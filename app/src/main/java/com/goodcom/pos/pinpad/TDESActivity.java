package com.goodcom.pos.pinpad;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.goodcom.smartpossdk.utils.BCDHelper;

public class TDESActivity extends Activity {
    private static final String TAG = "PinpadActivity";
    private TextView tv_display;
    private String str_display;
    private String tlk = "123456789012345678901234567890121234567890123456";
    private String tlk_kcv = "60C15261";
    private String tmk = "CF896E42A67B5E220EC6D2C9F394CA8FCF896E42A67B5E22";
    private String tmk_kcv = "60C15261";
    private String tak = "ABF803B6E79765C8024152A46359B433ABF803B6E79765C8";
    private String tak_kcv = "C68F798E";
    private String tpk = "F18E65C529BD733CABF803B6E79765C8F18E65C529BD733C";
    private String tpk_kcv = "38C8497E";
    private String tek = "9B196D071FA2631A150F5D704543FFA09B196D071FA2631A";
    private String tek_kcv = "E30F4AC3";

    private String tdkWithoutEnc = "123456789012345678901234567890121234567890123456";
    private String tdk_kcv = "60C15261";

    private String plaintext = "12345678901234567890123456789012";

    private String cipertext_tak = "389E0A86384F2D09B308CECC885229C6";
    private String cipertext_tpk = "99E52C669DE32DC0260BB3E00E54AD24";
    private String cipertext_tek = "5EF890A75C6C05BA4D26993755063225";
    private String cipertext_tdesk = "CF896E42A67B5E220EC6D2C9F394CA8F";

    private String cbccipertext_tak = "011F2A648EAABB63EEDC3CF12996B586";
    private String cbccipertext_tpk = "BF960A6AA594984F7630655ECCD0D362";
    private String cbccipertext_tek = "DA9C73EBE1E5EFA29934D92602AC6DE0";
    private String cbccipertext_tdesk = "B8776980CFEE241BCEB566CB9F078BA2";
    private String iv = "abcdef1234567890";

    private String mac_plaintext = "12345678901234567890123456789012abcdefabcdef12345678901234567890abcedf";

    private String magtrack_plaintext = "12345678901234567890123456789012abcdefabcdef1234567890";
    private String msgtrack_cipertest = "1864987E9AD43F157031F28F013E7F039210CDB30E05F3671B245F9D6F6538D6";

    private final static int area = 20;
    private final static int index = 99;

    private static final int PIN_KEY = 1;
    private static final int TRACK_KEY = 2;
    private static final int MAC_KEY = 3;
    private static final int PROTECT_KEY = 4;
    private static final int MAIN_KEY = 5;
    private static final int TDES_KEY = 6;

    private static final int MAC_MOD1 = 0;
    /**
     * block1跟block2异或再与block3做异或，知道最后8个字节，再进行DES/3DES
     */
    private static final int MAC_MOD2 = 1;
    /**
     * ANSIX9.19模式
     */
    private static final int MAC_MOD919 = 2;
    /**
     * 银联标准ECB模式
     */
    private static final int MAC_ECB = 3;

    private static final int MAC_MOD99 = 4;
    private PinpadDialog pinpadDialog;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            tv_display.setText(str_display);
        }
    };

    private void display_clear() {
        str_display = "";
        handler.sendEmptyMessage(0);
    }

    private void display_show(String str) {
        str_display = str_display + str + "\r\n";
        handler.sendEmptyMessage(0);
        Log.e(TAG, str);
    }

    public void clearDisplay(View view) {
        display_clear();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tdes);
        tv_display = findViewById(R.id.test_display);
        tv_display.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public void loadAllKeyWithTlk(View v) {
        try {
            display_clear();

            int ret = GcSmartPosUtils.getInstance().loadProtectKey(MainApp.getInstance().getApplicationContext(),area, tlk);
            display_show("loadProtectKey ret:" + ret);
            ret = GcSmartPosUtils.getInstance().loadMainKeyByEnc(MainApp.getInstance().getApplicationContext(),area, index, tmk, tmk_kcv);
            display_show("loadMainKeyByEnc ret:" + ret);
            ret = GcSmartPosUtils.getInstance().loadMacKey(MainApp.getInstance().getApplicationContext(),area, index, tak, tak_kcv);
            display_show("loadMacKey ret:" + ret);
            ret = GcSmartPosUtils.getInstance().loadPinKey(MainApp.getInstance().getApplicationContext(),area, index, tpk, tpk_kcv);
            display_show("loadPinKey ret:" + ret);
            ret = GcSmartPosUtils.getInstance().loadTrackKey(MainApp.getInstance().getApplicationContext(),area, index, tek, tek_kcv);
            display_show("loadTrackKey ret:" + ret);
            ret = GcSmartPosUtils.getInstance().loadTdesKeyByPlaintext(MainApp.getInstance().getApplicationContext(),area, index, tdkWithoutEnc);
            display_show("loadTdesKeyByPlaintext ret:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadAllKeyWithoutTlk(View v) {
        try {
            display_clear();

            int ret = GcSmartPosUtils.getInstance().loadMainKeyByPlaintext(MainApp.getInstance().getApplicationContext(),area, index, tlk);
            display_show("loadMainKeyByPlaintext ret:" + ret);
            ret = GcSmartPosUtils.getInstance().loadMacKey(MainApp.getInstance().getApplicationContext(),area, index, tak, tak_kcv);
            display_show("loadMacKey ret:" + ret);
            ret = GcSmartPosUtils.getInstance().loadPinKey(MainApp.getInstance().getApplicationContext(),area, index, tpk, tpk_kcv);
            display_show("loadPinKey ret:" + ret);
            ret = GcSmartPosUtils.getInstance().loadTrackKey(MainApp.getInstance().getApplicationContext(),area, index, tek, tek_kcv);
            display_show("loadTrackKey ret:" + ret);
            ret = GcSmartPosUtils.getInstance().loadTdesKeyByPlaintext(MainApp.getInstance().getApplicationContext(),area, index, tdkWithoutEnc);
            display_show("loadTdesKeyByPlaintext ret:" + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void calcKcv(View v) {

        display_clear();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    PinpadResult kcv = GcSmartPosUtils.getInstance().getTdesKeyKcv(MainApp.getInstance().getApplicationContext(),PosConstants.PinPad.KeyType.PROTECT_KEY, area, index);
                    display_show("Expected value:\r\n" + tlk_kcv);
                    display_show("Actual PROTECT_KEY kcv:\n" + kcv.toString());
                    kcv = GcSmartPosUtils.getInstance().getTdesKeyKcv(MainApp.getInstance().getApplicationContext(),PosConstants.PinPad.KeyType.MAIN_KEY, area, index);
                    display_show("Expected value:\r\n" + tmk_kcv);
                    display_show("Actual MAIN_KEY kcv:\n" + kcv.toString());
                    kcv = GcSmartPosUtils.getInstance().getTdesKeyKcv(MainApp.getInstance().getApplicationContext(),PosConstants.PinPad.KeyType.MAC_KEY, area, index);
                    display_show("Expected value:\r\n" + tak_kcv);
                    display_show("Actual MAC_KEY kcv:\n" + kcv.toString());
                    kcv = GcSmartPosUtils.getInstance().getTdesKeyKcv(MainApp.getInstance().getApplicationContext(),PosConstants.PinPad.KeyType.PIN_KEY, area, index);
                    display_show("Expected value:\r\n" + tpk_kcv);
                    display_show("Actual PIN_KEY kcv:\n" + kcv.toString());
                    kcv = GcSmartPosUtils.getInstance().getTdesKeyKcv(MainApp.getInstance().getApplicationContext(),PosConstants.PinPad.KeyType.TRACK_KEY, area, index);
                    display_show("Expected value:\r\n" + tek_kcv);
                    display_show("Actual TRACK_KEY kcv:\n" + kcv.toString());

                    kcv = GcSmartPosUtils.getInstance().getTdesKeyKcv(MainApp.getInstance().getApplicationContext(),PosConstants.PinPad.KeyType.TDES_KEY, area, index);
                    display_show("Expected value:\r\n" + tdk_kcv);
                    display_show("Actual TDES_KEY kcv:\n" + kcv.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    public void encryptAndDecrypt(View v) {
        display_clear();
        try {
            PinpadResult data = new PinpadResult();
            display_show("plaintext:" + plaintext);
            display_show("CBC iv:" + iv);
            data = GcSmartPosUtils.getInstance().encryptData(MainApp.getInstance().getApplicationContext(),area, index, PosConstants.PinPad.KeyType.MAC_KEY, plaintext, null);
            display_show("Expected value:\n" + cipertext_tak);
            display_show("ECB encryptData MAC_KEY:\n" + data.toString());
            data = GcSmartPosUtils.getInstance().encryptData(MainApp.getInstance().getApplicationContext(),area, index, PosConstants.PinPad.KeyType.PIN_KEY, plaintext, null);
            display_show("Expected value:\n" + cipertext_tpk);
            display_show("ECB encryptData PIN_KEY:\n" + data.toString());
            data = GcSmartPosUtils.getInstance().encryptData(MainApp.getInstance().getApplicationContext(),area, index, PosConstants.PinPad.KeyType.TRACK_KEY, plaintext, null);
            display_show("Expected value:\n" + cipertext_tek);
            display_show("ECB encryptData TRACK_KEY:\n" + data.toString());
            data = GcSmartPosUtils.getInstance().encryptData(MainApp.getInstance().getApplicationContext(),area, index, PosConstants.PinPad.KeyType.TDES_KEY, plaintext, null);
            display_show("Expected value:\n" + cipertext_tdesk);
            display_show("ECB encryptData TDES_KEY:\n" + data.toString());

            data = GcSmartPosUtils.getInstance().encryptData(MainApp.getInstance().getApplicationContext(),area, index, PosConstants.PinPad.KeyType.MAC_KEY, plaintext, iv);
            display_show("Expected value:\n" + cbccipertext_tak);
            display_show("CBC encryptData MAC_KEY:\n" + data.toString());
            data = GcSmartPosUtils.getInstance().encryptData(MainApp.getInstance().getApplicationContext(),area, index, PosConstants.PinPad.KeyType.PIN_KEY, plaintext, iv);
            display_show("Expected value:\n" + cbccipertext_tpk);
            display_show("CBC encryptData PIN_KEY:\n" + data.toString());
            data = GcSmartPosUtils.getInstance().encryptData(MainApp.getInstance().getApplicationContext(),area, index, PosConstants.PinPad.KeyType.TRACK_KEY, plaintext, iv);
            display_show("Expected value:\n" + cbccipertext_tek);
            display_show("CBC encryptData TRACK_KEY:\n" + data.toString());
            data = GcSmartPosUtils.getInstance().encryptData(MainApp.getInstance().getApplicationContext(),area, index, PosConstants.PinPad.KeyType.TDES_KEY, plaintext, iv);
            display_show("Expected value:\n" + cbccipertext_tdesk);
            display_show("CBC encryptData TDES_KEY:\n" + data.toString());

            data = GcSmartPosUtils.getInstance().decryptData(MainApp.getInstance().getApplicationContext(),area, index, PosConstants.PinPad.KeyType.MAC_KEY, cipertext_tak, null);
            display_show("Expected value:\n" + plaintext);
            display_show("ECB decryptData MAC_KEY:\n" + data.toString());
            data = GcSmartPosUtils.getInstance().decryptData(MainApp.getInstance().getApplicationContext(),area, index, PosConstants.PinPad.KeyType.TRACK_KEY, cipertext_tek, null);
            display_show("Expected value:\n" + plaintext);
            display_show("ECB decryptData TRACK_KEY:\n" + data.toString());
            data = GcSmartPosUtils.getInstance().decryptData(MainApp.getInstance().getApplicationContext(),area, index, PosConstants.PinPad.KeyType.TDES_KEY, cipertext_tdesk, null);
            display_show("Expected value:\n" + plaintext);
            display_show("ECB decryptData TDES_KEY:\n" + data.toString());

            data = GcSmartPosUtils.getInstance().decryptData(MainApp.getInstance().getApplicationContext(),area, index, PosConstants.PinPad.KeyType.MAC_KEY, cbccipertext_tak, iv);
            display_show("Expected value:\n" + plaintext);
            display_show("CBC decryptData MAC_KEY:\n" + data.toString());
            data = GcSmartPosUtils.getInstance().decryptData(MainApp.getInstance().getApplicationContext(),area, index, PosConstants.PinPad.KeyType.TRACK_KEY, cbccipertext_tek, iv);
            display_show("Expected value:\n" + plaintext);
            display_show("CBC decryptData TRACK_KEY:\n" + data.toString());
            data = GcSmartPosUtils.getInstance().decryptData(MainApp.getInstance().getApplicationContext(),area, index, PosConstants.PinPad.KeyType.TDES_KEY, cbccipertext_tdesk, iv);
            display_show("Expected value:\n" + plaintext);
            display_show("CBC decryptData TDES_KEY:\n" + data.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mac(View v) {
        display_clear();
        try {
            PinpadResult data = new PinpadResult();
            display_show("mac_plaintext:" + mac_plaintext);
            data = GcSmartPosUtils.getInstance().calcMAC(MainApp.getInstance().getApplicationContext(),area, index, mac_plaintext, PosConstants.PinPad.TdesMacKeyMode.MAC_MOD1);//OK
            display_show("Expected value:\n" + "0A2CC38A6CF6C415");
            display_show("calcMAC MAC_MOD1:\n" + data.toString());
            data = GcSmartPosUtils.getInstance().calcMAC(MainApp.getInstance().getApplicationContext(),area, index, mac_plaintext, PosConstants.PinPad.TdesMacKeyMode.MAC_MOD2);//OK
            display_show("Expected value:\n" + "F35024717F9F2B4D");
            display_show("calcMAC MAC_MOD2:\n" + data.toString());
            data = GcSmartPosUtils.getInstance().calcMAC(MainApp.getInstance().getApplicationContext(),area, index, mac_plaintext, PosConstants.PinPad.TdesMacKeyMode.MAC_MOD919);//OK
            display_show("Expected value:\n" + "1F2B21AAAC673D0C");
            display_show("calcMAC MAC_MOD919:\n" + data.toString());
            data = GcSmartPosUtils.getInstance().calcMAC(MainApp.getInstance().getApplicationContext(),area, index, mac_plaintext, PosConstants.PinPad.TdesMacKeyMode.MAC_ECB);//OK
            display_show("Expected value:\n" + "3545364246414642");
            display_show("calcMAC MAC_ECB:\n" + data.toString());
            data = GcSmartPosUtils.getInstance().calcMAC(MainApp.getInstance().getApplicationContext(),area, index, mac_plaintext, PosConstants.PinPad.TdesMacKeyMode.MAC_MOD99);//OK
            display_show("Expected value:\n" + "09046F3A5AE6939D");
            display_show("calcMAC MAC_MOD99:\n" + data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void encryptMagTrack(View v) {
        display_clear();
        try {
            PinpadResult data = new PinpadResult();
            display_show("magtrack_plaintext:" + magtrack_plaintext);
            data = GcSmartPosUtils.getInstance().encryptMagTrack(MainApp.getInstance().getApplicationContext(),area, index, magtrack_plaintext);
            display_show("Expected value:\r\n" + msgtrack_cipertest);
            display_show("encryptMagTrack:\r\n" + data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void inputonlinepin(View v) {
        pinpadDialog = new PinpadDialog(this, "6225760802344445", area, index, 2, 60 * 1000, new OnPinpadDialogListener() {
            @Override
            public void onError(int errCode, String msg) {
                Log.e(TAG, "inputonlinepin onError:" + errCode);
            }

            @Override
            public void onConfirm(byte[] data, boolean isNonePin) {
                Log.e(TAG, "inputonlinepin onConfirm isNonePin:" + isNonePin);
                if (data != null) {
                    Log.e(TAG, "inputonlinepin onConfirm data:" + BCDHelper.bcdToString(data, 0, data.length));
                }
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "inputonlinepin onCancle");
            }
        });
        pinpadDialog.show();
    }


}
