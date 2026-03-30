package com.goodcom.pos.emv;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.goodcom.smartpossdk.GcSmartPosUtils;
import com.goodcom.smartpossdk.PosConstants;
import com.goodcom.smartpossdk.entity.PinpadResult;
import com.goodcom.smartpossdk.entity.PosEmvCapk;
import com.goodcom.smartpossdk.listener.PosEmvCoreListener;
import com.goodcom.pos.MainApp;
import com.goodcom.pos.R;
import com.goodcom.pos.dialog.EnterDialog;
import com.goodcom.pos.pinpad.OnPinpadDialogListener;
import com.goodcom.pos.pinpad.PinpadDialog;
import com.goodcom.pos.utils.DoCardInfoHelper;
import com.goodcom.pos.utils.TlvData;
import com.goodcom.smartpossdk.utils.BCDHelper;
import com.goodcom.smartpossdk.utils.BerTag;
import com.goodcom.smartpossdk.utils.BerTlvBuilder;
import com.goodcom.smartpossdk.utils.HexUtil;
import com.goodcom.smartpossdk.utils.LogUtil;
import com.goodcom.smartpossdk.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class EmvLister extends PosEmvCoreListener.Stub {
    private static final String TAG = "EmvNewLister";

    private  Activity         mActivity = null;
    private       String                mCardNo = "";
    private final OnSwipeListerCallback mOnEmvListerCallback;
    private       int                   cardType;
    private       boolean               isAlreadyOnline = false;
    private       int                   pinType;

    public EmvLister(Activity activity, OnSwipeListerCallback onEmvListerCallback) {
        this.mActivity = activity;
        this.mOnEmvListerCallback = onEmvListerCallback;
    }

    @Override
    public void onEmvProcess(int type, Bundle bundle) throws RemoteException {
        LogUtil.se(getClass(), "onEmvProcess: " + type);
        Log.e(TAG, "onEmvProcess Detect Card");
        cardType = type;
        isAlreadyOnline = false;

        switch (type) {
            case PosConstants.EMV.EmvSupportCardType.DEVICE_CONTACT:
                MainApp.getInstance().getTransData().setEnterMode(TransData.EnterMode.INSERT);
                LogUtil.se(getClass(), "DEVICE_CONTACT ");
                break;
            case PosConstants.EMV.EmvSupportCardType.DEVICE_CONTACTLESS:
                MainApp.getInstance().getTransData().setEnterMode(TransData.EnterMode.PICC);
                LogUtil.se(getClass(), "DEVICE_CONTACTLESS:");
                break;
            case PosConstants.EMV.EmvSupportCardType.DEVICE_MAGSTRIPE:
                MainApp.getInstance().getTransData().setEnterMode(TransData.EnterMode.SWIPE);
                LogUtil.se(getClass(), "DEVICE_MAGSTRIPE:");

                byte[] buff;
                buff = bundle.getByteArray(PosConstants.EMV.EmvCardInfoConstraints.TRACK1);
                if (buff != null && buff.length > 0) {
                    LogUtil.se(getClass(), "TRACK1:" + new String(buff));
                    LogUtil.se(getClass(), "TRACK1:" + BCDHelper.bcdToString(buff, 0, buff.length));
                    MainApp.getInstance().getTransData().setTrack1(new String(buff));
                }
                buff = bundle.getByteArray(PosConstants.EMV.EmvCardInfoConstraints.TRACK2);
                if (buff != null && buff.length > 0) {
                    LogUtil.se(getClass(), "TRACK2:" + new String(buff));
                    LogUtil.se(getClass(), "TRACK2:" + BCDHelper.bcdToString(buff, 0, buff.length));
                    MainApp.getInstance().getTransData().setTrack2(new String(buff));

                    MainApp.getInstance().getTransData().setPan(DoCardInfoHelper.GetCardNumber(buff, buff.length));

                    MainApp.getInstance().getTransData().setExpDate(DoCardInfoHelper.getMagCardExtDate(buff));

                    String serverCode = DoCardInfoHelper.getMagCardServerCode(buff);
                    if (!StringUtils.isEmpty(serverCode)) {
                        MainApp.getInstance().getTransData().setCardSerialNo(serverCode);
                    }

                }
                buff = bundle.getByteArray(PosConstants.EMV.EmvCardInfoConstraints.TRACK3);
                if (buff != null && buff.length > 0) {
                    LogUtil.se(getClass(), "TRACK3:" + new String(buff));
                    LogUtil.se(getClass(), "TRACK3:" + BCDHelper.bcdToString(buff, 0, buff.length));
                    MainApp.getInstance().getTransData().setTrack3(new String(buff));
                }

                String pan = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "5A");
                LogUtil.se(getClass(), "pan:" + pan);

                String expData = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "5F24");
                LogUtil.se(getClass(), "expData:" + expData);

                String serviceCode = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "5F30");
                LogUtil.se(getClass(), "serviceCode:" + serviceCode);

                String track2 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "57");

                LogUtil.se(getClass(), "track2:" + track2);

                break;
            default:
                mOnEmvListerCallback.onError("Emv Error Code:"+String.valueOf(type));
                break;
        }


    }

    @Override
    public void onSelectApplication(List<String> list, boolean isFirstSelect) throws RemoteException {
        LogUtil.se(getClass(), "onSelectApplication: ");
        for(String str: list){
            LogUtil.se(getClass(), "onSelectApplication: "+str);
            byte[] temp = str.getBytes();
            LogUtil.se(getClass(), "onSelectApplication: "+BCDHelper.bcdToString(temp,0,temp.length));
        }


        Bundle bundle = new Bundle();
        bundle.putInt(PosConstants.EMV.EmvInfoConstraints.EMV_INFO_KEY, PosConstants.EMV.EmvInfoConstraints.INFO_TYPE_AID_KEY);
        GcSmartPosUtils.getInstance().emvGetInfo(MainApp.getInstance().getApplicationContext(), bundle);
        String aids = bundle.getString(PosConstants.EMV.EmvInfoConstraints.INFO_TYPE_AID_VALUE);
        LogUtil.se(getClass(), "onSelectApplication: "+aids);

        List<String> listAid = new ArrayList<>();
        int index = 0;
        int temp;
        LogUtil.se(getClass(), "onSelectApplication list.size(): "+list.size());
        for(int i=0 ; i< list.size()-1; i++){
            temp = aids.indexOf("|", index);
            listAid.add(aids.substring(index,temp));
            index = temp+1;

            if(i == list.size() -2){
                listAid.add(aids.substring(index));
                break;
            }
        }

        for(String str: listAid){
            LogUtil.se(getClass(), "onSelectApplication aid: "+str);
        }
        GcSmartPosUtils.getInstance().onSetSelectResponse(MainApp.getInstance().getApplicationContext(), 0);
    }

    @Override
    public void onConfirmCardInfo(int mode, Bundle bundle) throws RemoteException {
        LogUtil.se(getClass(), "onConfirmCardInfo: " + mode + "bundle" + bundle);

        String track2 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "57");

        LogUtil.se(getClass(), "track2:" + track2);



        Bundle outBundle = new Bundle();
        if (mode == PosConstants.EMV.EmvCallbackCommandType.CMD_CARD_NUMBER) {
            mCardNo = bundle.getString(PosConstants.EMV.EmvCardInfoConstraints.CARD);
            mOnEmvListerCallback.onConfirmCardNo(mCardNo);
        }
        if (TextUtils.isEmpty(mCardNo)) {
            mCardNo = null;
            LogUtil.se(getClass(), "onConfirmCardInfo: card number is null.");
            GcSmartPosUtils.getInstance().onSetCardInfoResponse(MainApp.getInstance().getApplicationContext(), outBundle);

            return;
        }

        if (cardType != PosConstants.EMV.EmvSupportCardType.DEVICE_CONTACT) {
            outBundle.putBoolean(PosConstants.EMV.EmvCardInfoConstraints.OUT_CONFIRM, true);
            GcSmartPosUtils.getInstance().onSetCardInfoResponse(MainApp.getInstance().getApplicationContext(), outBundle);
            return;
        }

        mActivity.runOnUiThread(() -> {
            new EnterDialog(mActivity).showConfirmDialog("please confirm cardno!", mCardNo, new EnterDialog.OnConfirmListener() {
                @Override
                public void onConfirm() {
                    try {
                        outBundle.putBoolean(PosConstants.EMV.EmvCardInfoConstraints.OUT_CONFIRM, true);
                        GcSmartPosUtils.getInstance().onSetCardInfoResponse(MainApp.getInstance().getApplicationContext(), outBundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancel() {
                    mOnEmvListerCallback.onError(mActivity.getString(R.string.error_cancel_trans));
                    outBundle.putBoolean(PosConstants.EMV.EmvCardInfoConstraints.OUT_CONFIRM, false);
                    GcSmartPosUtils.getInstance().onSetCardInfoResponse(MainApp.getInstance().getApplicationContext(), outBundle);
                }
            });
        });
    }

    public void onConfirmGeneralInfo(int mode, Bundle bundle) throws RemoteException {
        LogUtil.se(getClass(), "onConfirmGeneralInfo mode: " + mode);
        Bundle outBundle = new Bundle();
        Log.e(TAG, "onConfirmGeneralInfo: " + mode);
        if (mode == PosConstants.EMV.EmvCallbackCommandType.CMD_AMOUNT_CONFIG) {
            outBundle.putString(PosConstants.EMV.EmvCardInfoConstraints.OUT_AMOUNT, "11");
            outBundle.putString(PosConstants.EMV.EmvCardInfoConstraints.OUT_AMOUNT_OTHER, "22");
        } else if (mode == PosConstants.EMV.EmvCallbackCommandType.CMD_TRY_OTHER_APPLICATION) {
            outBundle.putBoolean(PosConstants.EMV.EmvCardInfoConstraints.OUT_CONFIRM, true);
        } else if (mode == PosConstants.EMV.EmvCallbackCommandType.CMD_ISSUER_REFERRAL) {
            outBundle.putBoolean(PosConstants.EMV.EmvCardInfoConstraints.OUT_CONFIRM, true);
        } else if (mode == PosConstants.EMV.EmvCallbackCommandType.CMD_AID_NAME) {
            String aid = bundle.getString(PosConstants.EMV.EmvCardInfoConstraints.DATA);
            Log.e(TAG, "onConfirmGeneralInfo: " + aid);
        }
        String tags[] = new String[]{"4F", "9F06", "50", "82"};
        String tlvs = GcSmartPosUtils.getInstance().EmvGetTlvDataList(MainApp.getInstance().getApplicationContext(), tags);
        if (tlvs != null) {
            Log.e(TAG, "tlvs: " + tlvs);
        } else {
            Log.e(TAG, "tlvs is null ");
        }
        GcSmartPosUtils.getInstance().onSetCardInfoResponse(MainApp.getInstance().getApplicationContext(), outBundle);
    }

    private byte[] onUpdateCapkItem() {
        try {
            PosEmvCapk capk = null;
            BerTlvBuilder tlvBuilder = new BerTlvBuilder();
            String rid = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F06");
            String index = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "8F");
            Log.e(TAG, "rid: " + rid + ",index= " + index);

            // TODO test
//            capk = new PosEmvCapk();
//            capk.RID = HexUtil.parseHex("A000000004");
//            capk.CapkIndex = (byte) 0x06;
//            capk.Module = HexUtil.parseHex("CB26FC830B43785B2BCE37C81ED334622F9622F4C89AAE641046B2353433883F307FB7C974162DA72F7A4EC75D9D657336865B8D3023D3D645667625C9A07A6B7A137CF0C64198AE38FC238006FB2603F41F4F3BB9DA1347270F2F5D8C606E420958C5F7D50A71DE30142F70DE468889B5E3A08695B938A50FC980393A9CBCE44AD2D64F630BB33AD3F5F5FD495D31F37818C1D94071342E07F1BEC2194F6035BA5DED3936500EB82DFDA6E8AFB655B1EF3D0D7EBF86B66DD9F29F6B1D324FE8B26CE38AB2013DD13F611E7A594D675C4432350EA244CC34F3873CBA06592987A1D7E852ADC22EF5A2EE28132031E48F74037E3B34AB747F");
//            capk.Exponent = HexUtil.parseHex("03");
//            capk.Checksum = HexUtil.parseHex("F910A1504D5FFB793D94F3B500765E1ABCAD72D9");
//            capk.AlgorithmInd = (byte) PosEmvCapk.ALGO_IND_RSA;
//            capk.HashInd = (byte) PosEmvCapk.HASH_IND_SHA1;

//                        capk = new PosEmvCapk();
//            capk.RID = HexUtil.parseHex("A000000333");
//            capk.CapkIndex = (byte) 0x08;
//            capk.Module = HexUtil.parseHex("B61645EDFD5498FB246444037A0FA18C0F101EBD8EFA54573CE6E6A7FBF63ED21D66340852B0211CF5EEF6A1CD989F66AF21A8EB19DBD8DBC3706D135363A0D683D046304F5A836BC1BC632821AFE7A2F75DA3C50AC74C545A754562204137169663CFCC0B06E67E2109EBA41BC67FF20CC8AC80D7B6EE1A95465B3B2657533EA56D92D539E5064360EA4850FED2D1BF");
//            capk.Exponent = HexUtil.parseHex("03");
//            capk.Checksum = HexUtil.parseHex("EE23B616C95C02652AD18860E48787C079E8E85A");
//            capk.AlgorithmInd = (byte) PosEmvCapk.ALGO_IND_RSA;
//            capk.HashInd = (byte) PosEmvCapk.HASH_IND_SHA1;

            capk = new PosEmvCapk();
            capk.RID = HexUtil.parseHex("A000000004");
            capk.CapkIndex = (byte) 0xF1;
            capk.Module = HexUtil.parseHex("A0DCF4BDE19C3546B4B6F0414D174DDE294AABBB828C5A834D73AAE27C99B0B053A90278007239B6459FF0BBCD7B4B9C6C50AC02CE91368DA1BD21AAEADBC65347337D89B68F5C99A09D05BE02DD1F8C5BA20E2F13FB2A27C41D3F85CAD5CF6668E75851EC66EDBF98851FD4E42C44C1D59F5984703B27D5B9F21B8FA0D93279FBBF69E090642909C9EA27F898959541AA6757F5F624104F6E1D3A9532F2A6E51515AEAD1B43B3D7835088A2FAFA7BE7");
            capk.Exponent = HexUtil.parseHex("03");
            capk.Checksum = HexUtil.parseHex("D8E68DA167AB5A85D8C3D55ECB9B0517A1A5B4BB");
            capk.AlgorithmInd = (byte) PosEmvCapk.ALGO_IND_RSA;
            capk.HashInd = (byte) PosEmvCapk.HASH_IND_SHA1;


            //capk.HashInd = (byte) PosEmvCapk.HASH_IND_NOT;
            // set capk format for kernel
            tlvBuilder.addBytes(new BerTag("9F01"), capk.RID);
            tlvBuilder.addBytes(new BerTag("9F02"), new byte[]{capk.CapkIndex});
            tlvBuilder.addBytes(new BerTag("9F03"), capk.Module);
            tlvBuilder.addBytes(new BerTag("9F04"), capk.Exponent);
            tlvBuilder.addBytes(new BerTag("9F05"), capk.Checksum);
            tlvBuilder.addBytes(new BerTag("9F06"), new byte[]{capk.AlgorithmInd});
            tlvBuilder.addBytes(new BerTag("9F07"), new byte[]{capk.HashInd});
            return tlvBuilder.buildArray();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new byte[0];
    }

    private byte[] onConfirmKernelMode() {
        String rid = null;
        rid = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F06");
        Log.d(TAG, "onConfirmKernelMode: "+rid);
        BerTlvBuilder tlvBuilder = new BerTlvBuilder();
        tlvBuilder.addBytes(new BerTag("9F2A"), new byte[]{PosConstants.EMV.EmvCardType.EMV_CARD_DISCOVER});
        return tlvBuilder.buildArray();
    }

    public byte[] updateContactlessParam() throws RemoteException {

        byte[] data =  new byte[0];
        String aidName = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F06");
        LogUtil.se(getClass(), "aidName: " + aidName);
        //visa
        if(aidName.startsWith("A000000003")){
            data = HexUtil.parseHex("9F66043640C000");
        }else if(aidName.startsWith("A000000333")){ //CUP
            data = HexUtil.parseHex("9F660436C0C080");
        }else if(aidName.startsWith("A000000004")){ //Master
            data = HexUtil.parseHex("DF811801FFDF811901FFDF811E01FFDF812C01FFDF811B01FF");
        }else if(aidName.startsWith("A000000152")){ //Discover
            data = HexUtil.parseHex("9F6604FFFFFFFF");
        }else if(aidName.startsWith("A000000025")){ //Amex
            data = HexUtil.parseHex("9F6E04FFFFFFFF");
        }else if(aidName.startsWith("A000000065")){ //JCB
            data = HexUtil.parseHex("9F5304000000FF");
        }else if(aidName.startsWith("A000000658")){ //JCB
            data = HexUtil.parseHex("DF7102FFFF");
        }else if(aidName.startsWith("D999999999")){ //JCB
            data = HexUtil.parseHex("DF3105FFFFFFFFFF");
        }
        return data;
    }

    @Override
    public void onExchangeData(int mode) throws RemoteException {
        LogUtil.se(getClass(), "onExchangeData mode: " + mode);

        byte[] data =  new byte[0];
        switch (mode) {
            case PosConstants.EMV.EmvCallbackCommandType.CMD_GPO_FILTER:
                String aidName = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F06");
                LogUtil.se(getClass(), "aidName: " + aidName);
                String temp = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F02");
                LogUtil.se(getClass(), "9F02:" + temp);
                data = updateContactlessParam();

                //data = HexUtil.parseHex("9F0206000000000001");
                break;
            case PosConstants.EMV.EmvCallbackCommandType.CMD_READ_RECORD_FILTER:
                String ddol = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F49");
                String tdol = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "97");
                LogUtil.se(getClass(), "ddol: " + ddol);
                LogUtil.se(getClass(), "tdol: " + tdol);

                //data = HexUtil.parseHex("9F49039F370497039F37041F0705D84000A8011F0905D84004F8021F080500000000031F0A04111111111F0501881F0601779F530100");
                break;
            case PosConstants.EMV.EmvCallbackCommandType.CMD_UPDATE_CAPK_FILTER:
                //data = onUpdateCapkItem();
                break;
            case PosConstants.EMV.EmvCallbackCommandType.CMD_CONFIRM_KERNEL_TYPE:
                data = onConfirmKernelMode();
                break;
            default:
                data = new byte[0];
                break;
        }
        int ret = GcSmartPosUtils.getInstance().EmvSetTlvData(MainApp.getInstance().getApplicationContext(), data);
        LogUtil.se(getClass(), "EmvSetTlvData ret: " + ret);
    }

    @Override
    public void onKernelType(int type) throws RemoteException {
        LogUtil.se(getClass(), "onKernelType: " + type);
        //PosConstants.EMV.EmvCardType.EMV_CARD_VISA;
        switch (type){
            case PosConstants.EMV.EmvCardType.EMV_CARD_VISA:
                break;
            case PosConstants.EMV.EmvCardType.EMV_CARD_MASTERCARD:
                break;
            case PosConstants.EMV.EmvCardType.EMV_CARD_AMEX:
                break;
            case PosConstants.EMV.EmvCardType.EMV_CARD_MIR:
                break;
            case PosConstants.EMV.EmvCardType.EMV_CARD_JCB:
                break;
            case PosConstants.EMV.EmvCardType.EMV_CARD_INTERAC:
                break;
            case PosConstants.EMV.EmvCardType.EMV_CARD_UNION_PAY:
                break;
            case PosConstants.EMV.EmvCardType.EMV_CARD_PURE:
                break;
        }
    }

    @Override
    public void onSecondTapCard() throws RemoteException {
        LogUtil.se(getClass(), "onSecondTapCard: ");
    }

    private Handler handlerPinpad = new Handler(MainApp.getInstance().getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            Bundle mBundle = new Bundle();
            PinpadDialog pinpadDialog = new PinpadDialog(mActivity, mCardNo, 20, 99, pinType, 60, new OnPinpadDialogListener() {
                @Override
                public void onError(int errCode, String msg) {
                    LogUtil.se(getClass(), "input online pin onError:" + errCode);
                    if(pinType == 3 || pinType == 4){
                        if(errCode >= 0x63C0 && errCode <= 0x63CF){

                            int retryTimes = errCode - 0x63C0;
                            LogUtil.se(getClass(), "retryTimes:" + retryTimes);
                            //Offline Pin verification failed, remaining retries
                            handlerPinpad.sendEmptyMessage(0);
                            return;
                        }
                    }

                    mOnEmvListerCallback.onError(mActivity.getString(R.string.error_pinpad) + ":" + errCode + " " + msg);

                    MainApp.getInstance().getTransData().setHasPin(false);
                    mBundle.putInt(PosConstants.EMV.EmvPinConstraints.OUT_PIN_VERIFY_RESULT, PosConstants.EMV.EmvPinConstraints.VERIFY_ERROR);
                    GcSmartPosUtils.getInstance().onSetPinResponse(MainApp.getInstance().getApplicationContext(), mBundle);
                }

                @Override
                public void onConfirm(byte[] data, boolean isNonePin) {
                    LogUtil.se(getClass(), "input online pin onConfirm isNonePin:" + isNonePin);
                    if (data != null) {
                        LogUtil.se(getClass(), "input online pin onConfirm data:" + new String(data));
                        MainApp.getInstance().getTransData().setPin(new String(data));
                        MainApp.getInstance().getTransData().setHasPin(true);
                    }

                    if((pinType == PosConstants.EMV.EmvPinType.PIN_ONLINE_PIN) && isNonePin){
                        boolean pinByPass = true;
                    }
                    mBundle.putInt(PosConstants.EMV.EmvPinConstraints.OUT_PIN_VERIFY_RESULT, PosConstants.EMV.EmvPinConstraints.VERIFY_SUCCESS);
                    mBundle.putByteArray(PosConstants.EMV.EmvPinConstraints.OUT_PIN_BLOCK, data);
                    GcSmartPosUtils.getInstance().onSetPinResponse(MainApp.getInstance().getApplicationContext(), mBundle);
                }

                @Override
                public void onCancel() {
                    LogUtil.se(getClass(), "input online pin onCancle");
                    try {
                        mOnEmvListerCallback.onError(mActivity.getString(R.string.error_cancel_trans));
                        mBundle.putInt(PosConstants.EMV.EmvPinConstraints.OUT_PIN_VERIFY_RESULT, PosConstants.EMV.EmvPinConstraints.VERIFY_CANCELED);
                        MainApp.getInstance().getTransData().setHasPin(false);
                        GcSmartPosUtils.getInstance().onSetPinResponse(MainApp.getInstance().getApplicationContext(), mBundle);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            pinpadDialog.show();
        }
    };


    @Override
    public void onRequestInputPin(Bundle bundle) throws RemoteException {
        LogUtil.se(getClass(), "onRequestInputPin: ");

        Bundle mBundle = new Bundle();
        Log.e(TAG, "onRequestInputPin: "+Build.VERSION.SDK_INT);

        int pinCounter = bundle.getInt(PosConstants.EMV.EmvPinConstraints.PIN_COUNTER);
        Log.e(TAG, "pinCounter: "+pinCounter);

        String tag9F17 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F17");
        Log.e(TAG, "tag9F17: "+tag9F17);

        pinType = bundle.getInt(PosConstants.EMV.EmvPinConstraints.PIN_TYPE);
        switch (pinType) {
            case PosConstants.EMV.EmvPinType.PIN_PLAIN_PIN:
                Log.e(TAG, "onRequestInputPin: " + "plain pin");
                pinType = 3;
                break;
            case PosConstants.EMV.EmvPinType.PIN_ENCIPHER_PIN:
                Log.e(TAG, "onRequestInputPin: " + "encipher pin");
                pinType = 4;
                break;
            case PosConstants.EMV.EmvPinType.PIN_ONLINE_PIN:
            default:
                Log.e(TAG, "onRequestInputPin: " + "online pin");
                pinType = 2;
                break;
        }

        handlerPinpad.sendEmptyMessage(0);

    }

    @Override
    public void onRequestOnlineProcess(Bundle bundle) throws RemoteException {
        Log.e(TAG, "onRequestOnlineProcess");
        LogUtil.se(getClass(), "onRequestOnlineProcess: ");
//todo
//        MainApp.app.mLedOpt.enableLedIndex(PosConstants.LedType.LED_BLUE, true);
//        MainApp.app.mLedOpt.enableLedIndex(PosConstants.LedType.LED_YELLOW, true);
//        MainApp.app.mLedOpt.enableLedIndex(PosConstants.LedType.LED_GREEN, true);
//        MainApp.app.mLedOpt.enableLedIndex(PosConstants.LedType.LED_RED, true);

        Log.d(TAG, "if declined: "+bundle.getBoolean(PosConstants.EMV.EmvOnlineConstraints.EMV_ONLINE_SCRIPT_WHEN_DECLINED));

        int cvm = bundle.getInt(PosConstants.EMV.EmvResultConstraints.CVM);
        Log.d(TAG, "cvm: " + cvm);

        isAlreadyOnline = true;
        if (cardType == PosConstants.EMV.EmvSupportCardType.DEVICE_MAGSTRIPE) {
            LogUtil.se(getClass(), "onRequestOnlineProcess: DEVICE_MAGSTRIPE");
            mOnEmvListerCallback.onTransactionAuthRequest(null);
            GcSmartPosUtils.getInstance().stopTransaction(MainApp.getInstance().getApplicationContext());
        } else {
            byte[] field55 = bundle.getByteArray(PosConstants.EMV.EmvOnlineConstraints.EMV_DATA);
            LogUtil.se(getClass(), "onRequestOnlineProcess: " + BCDHelper.bcdToString(field55, 0, field55.length));
            try {
                TlvData tlvData = new TlvData(field55);

                String filed55 = GcSmartPosUtils.getInstance().EmvGetTlvDataList(MainApp.getInstance().getApplicationContext(), new String[]{
                        "9F26", "9F27", "9F10", "9F37", "9F36", "95", "9A", "9C", "9F02", "5F2A",
                        "82", "9F1A", "9F33", "9F34", "9F35", "9F1E", "84", "9F09", "9F41"
                });
                LogUtil.se(getClass(), "filed55:" + filed55);


//                String expData = tlvData.getEmvExpData();
//                LogUtil.se(getClass(), "expData:" + expData);

                String pan = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "5A");
                LogUtil.se(getClass(), "pan:" + pan);

                String expData = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "5F24");
                LogUtil.se(getClass(), "expData:" + expData);

                String effectiveData  = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "5F25");
                LogUtil.se(getClass(), "effectiveData:" + effectiveData);

                String track2 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "57");

                LogUtil.se(getClass(), "track2:" + track2);

                String tag95 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "95");

                LogUtil.se(getClass(), "tag95:" + tag95);

                String tag9F45 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F45");

                LogUtil.se(getClass(), "tag9F45:" + tag9F45);
                String tag82 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "82");

                LogUtil.se(getClass(), "82:" + tag82);

                String tag8C = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "8C");

                LogUtil.se(getClass(), "tag8C:" + tag8C);

                String tag8D = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "8D");

                LogUtil.se(getClass(), "tag8D:" + tag8D);

                String tag90 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "90");

                LogUtil.se(getClass(), "tag90:" + tag90);

                String tag9F1D = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F1D");

                LogUtil.se(getClass(), "tag9F1D:" + tag9F1D);

                String tag9F33 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F33");

                LogUtil.se(getClass(), "tag9F33:" + tag9F33);
                String tag9F1A = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F1A");
                LogUtil.se(getClass(), "tag9F1A:" + tag9F1A);

                String tag9F15 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F15");
                LogUtil.se(getClass(), "tag9F15:" + tag9F15);

                String tag5F2A = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "5F2A");
                LogUtil.se(getClass(), "tag5F2A:" + tag5F2A);

                String tag9F35 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F35");
                LogUtil.se(getClass(), "tag9F35:" + tag9F35);

                String tag9F40 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F40");
                LogUtil.se(getClass(), "tag9F40:" + tag9F40);

                String tag9F66 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F66");
                LogUtil.se(getClass(), "tag9F66:" + tag9F66);

                String tag9F6E = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F6E");
                LogUtil.se(getClass(), "tag9F6E:" + tag9F6E);

                String tag9F53 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F53");
                LogUtil.se(getClass(), "tag9F53:" + tag9F53);

                String tag9F6C = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F6C");
                LogUtil.se(getClass(), "tag9F6C:" + tag9F6C);

                String tag9F02 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F02");
                LogUtil.se(getClass(), "tag9F02:" + tag9F02);
                try {
                    PinpadResult temp = GcSmartPosUtils.getInstance().encryptMagTrack(MainApp.getInstance().getApplicationContext(),20, 99, "5236497927826910D240120114350828");
                    LogUtil.se(getClass(), "temp:" + temp.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
//                if (track2 != null) {
//                    MainApp.getInstance().getTransData().setTrack2(track2);
//                    if (track2 != null) {
//                        String pan = DoCardInfoHelper.GetCardNumber(track2.getBytes(), track2.getBytes().length);
//                        LogUtil.se(getClass(), "pan:" + pan);
//                        MainApp.getInstance().getTransData().setPan(pan);
//                    }
//                }

                if (filed55 != null) {
                    MainApp.getInstance().getTransData().setSendIccData(filed55);
                }
                if (expData != null) {
                    MainApp.getInstance().getTransData().setExpDate(expData);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            MainApp.getInstance().getTransData().setSendIccData(BCDHelper.bcdToString(field55, 0, field55.length));
            mOnEmvListerCallback.onTransactionAuthRequest(field55);
        }

    }

    @Override
    public void onTransactionResult(int result, Bundle bundle) throws RemoteException {
        LogUtil.se(getClass(), "onTransactionResult: " + result);
//        if(isAlreadyOnline && MainApp.getInstance().isTestMode()){
//            result = PosConstants.EMV.PosEmvErrorCode.EMV_APPROVED;
//        }

        String tag9F02 = GcSmartPosUtils.getInstance().EmvGetTlvData(MainApp.getInstance().getApplicationContext(), "9F02");
        LogUtil.se(getClass(), "tag9F02:" + tag9F02);

        String apduResponse = bundle.getString(PosConstants.EMV.EmvResultConstraints.STATUS_WORD_IN_ERROR);
        LogUtil.se(getClass(), "apduResponse: " + apduResponse);




        Log.e(TAG, "isRequestSecondTap: "+bundle.getBoolean(PosConstants.EMV.EmvResultConstraints.IS_REQUEST_SECOND_TAP));
        int ret = 0;
        switch (result) {
            case PosConstants.EMV.PosEmvErrorCode.RUPAY_SECOND_TAP_DETECT_TIMEOUT:
                break;
            case PosConstants.EMV.PosEmvErrorCode.RUPAY_SECOND_TAP_NO_SAME_CARD:
                break;
            case PosConstants.EMV.PosEmvErrorCode.EMV_OK:
            case PosConstants.EMV.PosEmvErrorCode.EMV_APPROVED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_APPROVED_ONLINE:
            case PosConstants.EMV.PosEmvErrorCode.EMV_FORCE_APPROVED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_DELAYED_APPROVED:
                LogUtil.se(getClass(), "EMV_APPROVED_ONLINE");
                ret = 0;
                break;
            case PosConstants.EMV.PosEmvErrorCode.EMV_DECLINED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_CANCEL:
            case PosConstants.EMV.PosEmvErrorCode.EMV_TIMEOUT:
            case PosConstants.EMV.PosEmvErrorCode.EMV_COMMAND_FAIL:
            case PosConstants.EMV.PosEmvErrorCode.EMV_FALLBACK:
            case PosConstants.EMV.PosEmvErrorCode.EMV_MULTI_CONTACTLESS:
            case PosConstants.EMV.PosEmvErrorCode.EMV_OTHER_ICC_INTERFACE:
            case PosConstants.EMV.PosEmvErrorCode.EMV_APP_BLOCKED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_CARD_BLOCKED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_APP_EMPTY:
            case PosConstants.EMV.PosEmvErrorCode.EMV_NOT_ALLOWED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_NOT_ACCEPTED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_TERMINATED:
            case PosConstants.EMV.PosEmvErrorCode.EMV_SEE_PHONE:
            case PosConstants.EMV.PosEmvErrorCode.EMV_OTHER_INTERFACE:
            case PosConstants.EMV.PosEmvErrorCode.EMV_OTHER_ERROR:
            case PosConstants.EMV.PosEmvErrorCode.EMV_ENCRYPT_ERROR:
            case PosConstants.EMV.PosEmvErrorCode.EMV_UNENCRYPTED:
                LogUtil.se(getClass(), "EMV_UNENCRYPTED");
                String reason = bundle.getString(PosConstants.EMV.EmvResultConstraints.REASON_POSSIBLE_KERNEL);
                LogUtil.se(getClass(), "reason: " + reason);
                ret = result;
                break;
            default:
                ret = -1;
                break;
        }
        // TODO
        // 测试演示时,都按照成功处理,
        int cvmCode = bundle.getInt(PosConstants.EMV.EmvResultConstraints.CVM);
        LogUtil.se(getClass(), "cvmCode:" + cvmCode);
        byte[] buff;
        buff = bundle.getByteArray(PosConstants.EMV.EmvResultConstraints.EMV_DATA);
        if (buff != null && buff.length > 0) {
            LogUtil.se(getClass(), "EMV_DATA:" + BCDHelper.bcdToString(buff, 0, buff.length));
        }
        buff = bundle.getByteArray(PosConstants.EMV.EmvResultConstraints.SCRIPT_RESULT);
        if (buff != null && buff.length > 0) {
            LogUtil.se(getClass(), "SCRIPT_RESULT:" + new String(buff));
        }

        GcSmartPosUtils.getInstance().stopTransaction(MainApp.getInstance().getApplicationContext());
        mOnEmvListerCallback.onDisplayTransReport(ret);
    }
}
