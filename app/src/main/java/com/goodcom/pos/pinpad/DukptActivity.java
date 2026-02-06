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

import com.goodcom.smartpossdk.entity.DukptInfo;
import com.goodcom.pos.MainApp;
import com.goodcom.pos.R;
import com.goodcom.smartpossdk.GcSmartPosUtils;
import com.goodcom.smartpossdk.utils.BCDHelper;

public class DukptActivity extends Activity {
    private final static String TAG = "DukptActivity";
    private TextView tv_display;
    private String str_display = "";

    private String dukpt = "CF896E42A67B5E220EC6D2C9F394CA8F";
    private String ksi = "FFFFFFFFFF";
    private String did = "11111111";
    private String kcv = "00730FBD";
    private String data = "12345678901234566543210123456789";
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
        setContentView(R.layout.activity_dukpt);
        tv_display = findViewById(R.id.test_display);
        tv_display.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    public void loadDUKPTBDK(View view){
        int ret = -1;
        try {
            ret = GcSmartPosUtils.getInstance().loadDukptByBdk(MainApp.getInstance().getApplicationContext(), 1, BCDHelper.StrToBCD(dukpt),BCDHelper.StrToBCD(ksi+did+"01"),null);
            Log.e(TAG,"loadDUKPTBDK Ret:"+ret);
            display_show("loadDUKPTBDK Ret:"+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDUKPTIPEK(View view){
        int ret = -1;
        try {
            ret = GcSmartPosUtils.getInstance().loadDukptByIpek(MainApp.getInstance().getApplicationContext(),1, BCDHelper.StrToBCD(dukpt),BCDHelper.StrToBCD(ksi+did+"01"),null);
            Log.e(TAG,"loadDukptByIpek Ret:"+ret);
            display_show("loadDukptByIpek Ret:"+ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void CalDukpt(View view){
        int  ret = -1;
        DukptInfo dukptInfo = new DukptInfo();
        try {
            Log.e(TAG,"calMacForDukpt");
            display_show("calMacForDukpt");
            ret = GcSmartPosUtils.getInstance().calMacForDukpt(MainApp.getInstance().getApplicationContext(), 1, 2, BCDHelper.stringToBcd(data, data.length()), dukptInfo);
            Log.e(TAG,"calMacForDukpt ret:"+ret);
            display_show("calMacForDukpt ret:"+ret);
            if(ret != 0){
                // break;
            }else{
                Log.e(TAG,"calMacForDukpt mac:"+dukptInfo.getData());
                Log.e(TAG,"calMacForDukpt ksn:"+dukptInfo.getKsn());
                display_show("calMacForDukpt mac:"+dukptInfo.getData());
                display_show("calMacForDukpt ksn:"+dukptInfo.getKsn());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void DesDukpt(View view){
        int  ret = -1;
        DukptInfo dukptInfo = new DukptInfo();
        try {
            Log.e(TAG,"calDesForDukpt");
            display_show("calDesForDukpt");
            ret = GcSmartPosUtils.getInstance().calDesForDukpt(MainApp.getInstance().getApplicationContext(),1, 1, null, 1, BCDHelper.stringToBcd(data, data.length()), dukptInfo);
            Log.e(TAG,"calDesForDukpt ret:"+ret);
            display_show("calDesForDukpt ret:"+ret);
            if(ret != 0){
                // break;
            }else{
                Log.e(TAG,"calDesForDukpt mac:"+dukptInfo.getData());
                Log.e(TAG,"calDesForDukpt ksn:"+dukptInfo.getKsn());
                display_show("calDesForDukpt mac:"+dukptInfo.getData());
                display_show("calDesForDukpt ksn:"+dukptInfo.getKsn());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void InputDukptPin(View view){
                PinpadDialog pinpadDialog = new PinpadDialog(this, "5236497927826910", 1, 1, 1, 60, new OnPinpadDialogListener() {
                    @Override
                    public void onError(int errCode, String msg) {
                        Log.e(TAG, "inputonlinepin onError:" + errCode);
                        display_show("inputonlinepin onError:" + errCode);
                    }

                    @Override
                    public void onConfirm(byte[] data, boolean isNonePin) {
                        Log.e(TAG, "inputonlinepin onConfirm isNonePin:" + isNonePin);
                        display_show("inputonlinepin onConfirm isNonePin:" + isNonePin);
                        if (data != null) {
                            Log.e(TAG, "inputonlinepin onConfirm data:" + BCDHelper.bcdToString(data, 0, data.length));
                            display_show("inputonlinepin onConfirm data:" + BCDHelper.bcdToString(data, 0, data.length));
                        }
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG, "inputonlinepin onCancle");
                        display_show("inputonlinepin onCancle");
                    }
                });
                pinpadDialog.show();
    }
}
