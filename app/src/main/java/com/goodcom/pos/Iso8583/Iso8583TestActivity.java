package com.goodcom.pos.Iso8583;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.goodcom.administrator.generalprinter.constant.PosConstants;
import com.goodcom.administrator.generalprinter.entity.PinpadResult;
import com.goodcom.pos.MainApp;
import com.goodcom.pos.R;
import com.goodcom.smartpossdk.GcSmartPosUtils;
import com.goodcom.smartpossdk.packet8583.factory.Iso8583Manager;
import com.goodcom.smartpossdk.utils.BCDHelper;

import java.io.IOException;

public class Iso8583TestActivity extends Activity {
    private final static String TAG = "Iso8583TestActivity";
    Iso8583Manager mIso8583Manager = null;
    private TextView tv_display;
    private String str_display = "";
    private int area = 20, index = 88;
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_display.setText(str_display);
        }
    };

    private void display_clear(){
        str_display = "";
        handler.sendEmptyMessage(0);
    }

    private void display_show(String str){
        str_display = str_display + str + "\r\n";
        handler.sendEmptyMessage(0);
    }
    public void clearDisplay(View view){
        display_clear();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iso8583_test);
        tv_display = findViewById(R.id.test_display1);
        tv_display.setMovementMethod(ScrollingMovementMethod.getInstance());
        loadMacKey();
    }

    private void loadMacKey(){
        try{
            PinpadResult pinpadResult = GcSmartPosUtils.getInstance().getTdesKeyKcv(MainApp.getInstance().getApplicationContext(), PosConstants.PinPad.KeyType.MAC_KEY, area, index);

            if ( pinpadResult.getCode() != PosConstants.PinPad.PinpadErrorCode.SUCCESS) {
                String tlk = "123456789012345678901234567890121234567890123456";
                String tak = "ABF803B6E79765C8024152A46359B433ABF803B6E79765C8";
                String tak_kcv = "C68F798E";

                int ret = GcSmartPosUtils.getInstance().loadMainKeyByPlaintext(
                        MainApp.getInstance().getApplicationContext(), area, index, tlk);
                Log.e(TAG, "loadMainKeyByPlaintext ret:" + ret);
                ret = GcSmartPosUtils.getInstance().loadMacKey(MainApp.getInstance().getApplicationContext(),
                        area, index, tak, tak_kcv);
                Log.e(TAG, "loadMacKey ret:" + ret);
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }



    public void pack8583(View view) {
        try {
            mIso8583Manager = setTest8583value(mIso8583Manager);
            byte[] pack = mIso8583Manager.pack();

            display_show("pack :" + BCDHelper.bcdToString(pack,0,pack.length));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unpack8583(View view) {
        try {



            mIso8583Manager = setTest8583value(mIso8583Manager);
            byte[] pack = mIso8583Manager.pack();

            Iso8583Manager iso = new Iso8583Manager(this);
            iso.unpack(pack);
            display_show(getIsoMsgAll(iso));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Iso8583Manager setTest8583value(Iso8583Manager packManager) throws Exception {

        if (packManager == null) {
            //The 8583 package configuration file is  /assets/packet8583.xml
            packManager = new Iso8583Manager(this);
        }

        packManager.setBit("tpdu", "6000030000");
        packManager.setBit("header", "613100310003");
        packManager.setBit("preposition", "123456789012345678901234567890123456789012345678901234567890123456789");
        packManager.setBit("msgid", "0200");
        packManager.setBit(3, "000000");
        packManager.setBit(4, "000000000001");
        packManager.setBit(22, "051");
        packManager.setBit(25, "00");
        packManager.setBit(26, "12");
        packManager.setBit(37, "123456789012");
        packManager.setBit(49, "156");
        packManager.setBit(53, "2600000000000000");
        packManager.setBinaryBit(59, new byte[]{(byte) 0xef, 0x1e, 0x2f, 0x3d, (byte) 0xab, (byte) 0xcd, (byte) 0xef, (byte) 0xff});
        packManager.setBit(60, "22" + "000001" + "000" + "6" + "01");

        byte[] macInput = packManager.getMacData("msgid", "63");
        Log.e(TAG, "macInput:"+BCDHelper.bcdToString(macInput,0,macInput.length));
        PinpadResult pinpadResult = GcSmartPosUtils.getInstance().calcMAC(MainApp.getInstance().getApplicationContext(), area, index, BCDHelper.bcdToString(macInput,0,macInput.length), 2);
        Log.e(TAG,"PinpadResult:"+pinpadResult.toString());
        if(pinpadResult.getCode() == PosConstants.PinPad.PinpadErrorCode.SUCCESS) {
            packManager.setBinaryBit(64, BCDHelper.StrToBCD(pinpadResult.getData()));
        }
        return packManager;
    }

    private String getIsoMsgAll(Iso8583Manager iso8583Manager) {
        String msg = "";
        String tpdu = iso8583Manager.getBit("tpdu");
        String header = iso8583Manager.getBit("header");
        String msgid = iso8583Manager.getBit("msgid");
        msg += TextUtils.isEmpty(tpdu) ? "" : "tpdu:" + tpdu + " \n";
        msg += TextUtils.isEmpty(header) ? "" : "header:" + header + " \n";
        msg += TextUtils.isEmpty(msgid) ? "" : "msgid:" + msgid + " \n";
        String bit = null;
        byte[] array;
        for (int q = 1; q < 65; q++) {
            if(q == 52 || q == 55 || q==64){
                array = iso8583Manager.getBitBytes(q);
                if (array != null && array.length > 0) {
                    bit = BCDHelper.bcdToString(array,0,array.length);
                }
            }else {
                bit = iso8583Manager.getBit(q);
            }
            if (bit != null) {
                msg += q + ": " + bit + "\n";
            }
        }
        return msg;
    }

}
