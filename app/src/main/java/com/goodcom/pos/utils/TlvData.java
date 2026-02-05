package com.goodcom.pos.utils;

import com.goodcom.smartpossdk.utils.AppUtil;
import com.goodcom.smartpossdk.utils.BCDHelper;
import com.goodcom.smartpossdk.utils.tlv.BerTag;
import com.goodcom.smartpossdk.utils.tlv.BerTlv;
import com.goodcom.smartpossdk.utils.tlv.BerTlvParser;
import com.goodcom.smartpossdk.utils.tlv.BerTlvs;
import com.goodcom.smartpossdk.utils.LogUtil;

public class TlvData {
    private BerTlvs mDataTlv;

    public TlvData(byte[] mData){
        BerTlvParser mBerTlvParser = new BerTlvParser();
        mDataTlv = mBerTlvParser.parse(mData);
    }

    public String getEmvTlvData(String tag) {
        if (mDataTlv == null) {
            return null;
        }
        BerTlv ctlv = mDataTlv.find(new BerTag(tag));

        if (ctlv != null)
        {
            LogUtil.si(getClass(),"BerTlv{" + "theTag=" + ctlv.getTag()
                    + ", theValue=" + ctlv.getHexValue() + '}');
            return BCDHelper.bcdToString(ctlv.getBytesValue(), 0,
                    ctlv.getBytesValue().length);
        }

        return null;
    }

    public byte[] getEmvTlvArrayData(String tag) {
        if (mDataTlv == null) {
            return null;
        }
        BerTlv ctlv = mDataTlv.find(new BerTag(tag));

        if (ctlv != null)
        {
            LogUtil.si(getClass(),"BerTlv{" + "theTag=" + ctlv.getTag()
                    + ", theValue=" + ctlv.getHexValue() + '}');
            return ctlv.getBytesValue();
        }

        return null;
    }


    public String getEmvExpData(){
        String track2 = getEmvTlvData("57");
        if(track2 != null) {
            return getCardExpData(track2.getBytes());
        }
        return null;
    }

    public String getF55Filed(int[] tags) throws Exception{
        byte[] DataOut;
        byte[] buffer = new byte[512];
        int offset = 0;

        if(null == tags || tags.length == 0) {
            return null;
        }

        for(int i=0; i<tags.length; i++){
            DataOut = getEmvTlvArrayData(Integer.toHexString((int)tags[i]));
           // LogUtil.si(getClass(), "tag:" + Integer.toHexString(tags[i]));
            if (DataOut != null) {
               // LogUtil.si(getClass(),"DataOut:"+ BCDHelper.bcdToString(DataOut, 0, DataOut.length));
                offset = AppUtil.TLVAppend((short) tags[i], DataOut, 0, buffer, offset, DataOut.length);
            }
        }

        if(offset <=0 ) {
            return null;
        }

        byte[] Field55 = new byte[offset];
        System.arraycopy(buffer, 0, Field55, 0, Field55.length);
        LogUtil.si(getClass(), "Field55: " + BCDHelper.hex2DebugHexString(Field55, Field55.length));

        return BCDHelper.bcdToString(Field55, 0, Field55.length);
    }



    private  String getCardExpData(byte[] tr2data) {

        String tr2_str = new String(tr2data);
        int startIndex = 0;
        if (tr2_str == null) {
            return "0000";
        }
        if(tr2_str.contains("=")) {
            startIndex = tr2_str.indexOf("=", 0);
        } else if (tr2_str.contains("D")) {
            startIndex = tr2_str.indexOf("D",0);
        }
        try{
            return tr2_str.substring(startIndex + 3, startIndex + 5) + tr2_str.substring(startIndex + 1, startIndex + 3);
        }catch(Exception e){
            e.printStackTrace();
        }
        return "0000";
    }

    @Override
    public String toString() {
        String result = "";
        for(BerTlv tlv: mDataTlv.getList()){
            LogUtil.si(getClass(), "tag:" + tlv.getTag().toString());
            LogUtil.si(getClass(), "value:" + tlv.getHexValue());
        }
        return result;
    }

    /*
    public static String _22(Card card) {
        StringBuilder code = new StringBuilder();
        switch (card.type) {
            case CardType.MAG_CARD:
                code.append("02");
                break;
            case CardType.RF_CARD:
                code.append("07");
                break;
            case CardType.IC_CARD:
                code.append("05");
                break;
            case 1:
                code.append("01");
                break;
            default:
                code.append("00");
                break;
        }
        if (card.password == null) {
            code.append("2");
        } else {
            code.append("1");
        }
        return code.toString();
    }
    */

}
