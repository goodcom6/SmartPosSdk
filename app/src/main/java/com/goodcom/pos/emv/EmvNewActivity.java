package com.goodcom.pos.emv;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.goodcom.smartpossdk.PosConstants;
import com.goodcom.pos.MainApp;
import com.goodcom.pos.R;
import com.goodcom.smartpossdk.GcSmartPosUtils;
import com.goodcom.smartpossdk.utils.BCDHelper;

public class EmvNewActivity extends Activity {
    private static final String TAG = "EmvNewActiity";
    private EditText mEditAmount;
    private TextView mTvShowInfo;
    private StringBuffer str_display = new StringBuffer();
    private String mAmount;

    private Handler handlerEmv = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            transactProcess("123");
        }
    };


    public void StartEmv(View view) {
        try {
            clearDisplay();
            mAmount = mEditAmount.getText().toString();
            Long parseLong = Long.parseLong(mAmount);
            if (parseLong > 0) {
                transactProcess(mAmount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView((R.layout.activity_emv_layout));
        mEditAmount = findViewById(R.id.edit_amount);
        mTvShowInfo = findViewById(R.id.tv_info);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            mTvShowInfo.setText(str_display.toString());
        }
    };

    private void appendDisplay(String msg) {
        str_display.append(msg + "\r\n");
        handler.sendEmptyMessage(0);
    }

    private void clearDisplay() {
        str_display = new StringBuffer();
        str_display.append("");
        handler.sendEmptyMessage(0);
    }

    void transactProcess(String amount) {
        Log.e(TAG, "transactProcess: ");
        appendDisplay("Please Insert/Tap/Swipe Card");

        TransData transData = new TransData();
        MainApp.getInstance().setTransData(transData);

        Bundle bundle = new Bundle();
        bundle.putInt(PosConstants.EMV.EmvTransDataConstraints.TRANS_TYPE, PosConstants.EMV.EmvTransType.EMV_GOODS);
        bundle.putLong(PosConstants.EMV.EmvTransDataConstraints.TRANS_AMOUNT, Long.parseLong(amount));
        bundle.putLong(PosConstants.EMV.EmvTransDataConstraints.TRANS_AMOUNT_OTHER, 0);
        int mode = PosConstants.EMV.EmvSupportCardType.DEVICE_CONTACT | PosConstants.EMV.EmvSupportCardType.DEVICE_CONTACTLESS | PosConstants.EMV.EmvSupportCardType.DEVICE_MAGSTRIPE;
        // int mode = PosConstants.EMV.EmvSupportCardType.DEVICE_CONTACT | PosConstants.EMV.EmvSupportCardType.DEVICE_MAGSTRIPE;
        bundle.putInt(PosConstants.EMV.EmvTransDataConstraints.TRANS_MODE, mode);
        bundle.putInt(PosConstants.EMV.EmvTransDataConstraints.TRANS_TIMEOUT, 60);
        bundle.putBoolean(PosConstants.EMV.EmvTransDataConstraints.SPECIAL_CONTACT, false);
        bundle.putBoolean(PosConstants.EMV.EmvTransDataConstraints.SPECIAL_MAGSTRIPE, false);
        bundle.putBoolean(PosConstants.EMV.EmvTransDataConstraints.USE_ENCRYPTED_TRACK, false); //Open track encryption

        //bundle.putInt(PosConstants.EMV.EmvTransDataConstraints.ENC_TRACK_KEY_AREA_INDEX, 20);
        //bundle.putInt(PosConstants.EMV.EmvTransDataConstraints.ENC_TRACK_KEY_INDEX, 99);  //Key Index
        bundle.putInt(PosConstants.EMV.EmvTransDataConstraints.ENC_TRACK_KEY_INDEX, 2);  //Key Index
        bundle.putInt(PosConstants.EMV.EmvTransDataConstraints.ENC_TRACK_KEY_ALGO,
                PosConstants.PinPad.TrackAlgoType.TRACK_ALGO_DUKPT);      //Use Dukpt Encryp
        bundle.putString(PosConstants.EMV.EmvTransDataConstraints.ENC_TRACK_IV, "0000000000000000");  //Key Index
        bundle.putByte(PosConstants.EMV.EmvTransDataConstraints.ENC_TRACK_PADDING, (byte) 0xFF);  //Key Index

        GcSmartPosUtils.getInstance().startTransaction(MainApp.getInstance().getApplicationContext(),
                bundle, new EmvLister(this, onEmvListerCallback));
    }

    private final OnSwipeListerCallback onEmvListerCallback = new OnSwipeListerCallback() {
        @Override
        public void onConfirmCardNo(String cardNo) {
            appendDisplay("Detect Card:" + cardNo);
        }

        @Override
        public void onTransactionAuthRequest(byte[] data) {
            appendDisplay("onTransactionAuthRequest");

            if (data != null) {
                Log.e(TAG, "onTransactionAuthRequest data:" + BCDHelper.bcdToString(data, 0, data.length));
            }
            Bundle outBundle = new Bundle();
            outBundle.putByteArray(PosConstants.EMV.EmvOnlineConstraints.OUT_AUTH_DATA, BCDHelper.StrToBCD("1A1B1C1D1E1F2A2B3030"));
            outBundle.putByteArray(PosConstants.EMV.EmvOnlineConstraints.OUT_AUTH_CODE, BCDHelper.StrToBCD("123456"));
            outBundle.putInt(PosConstants.EMV.EmvOnlineConstraints.OUT_AUTH_RESP_CODE, PosConstants.EMV.EmvOnlineConstraints.EMV_ONLINE_APPROVE);
            outBundle.putByteArray(PosConstants.EMV.EmvOnlineConstraints.OUT_ISSUER_SCRIPT, BCDHelper.StrToBCD("72219F180411223344860D8424"));
            outBundle.putByteArray(PosConstants.EMV.EmvOnlineConstraints.OUT_SPECIAL_AUTH_RESP_CODE, "00".getBytes());

            //outBundle.putByteArray(PosConstants.EMV.EmvOnlineConstraints.OUT_SPECIAL_AUTH_RESP_CODE, HexUtil.parseHex("3030"));
            GcSmartPosUtils.getInstance().onSetOnlineResponse(MainApp.getInstance().getApplicationContext(), outBundle);
        }

        @Override
        public void onDisplayTransReport(int code) {
            //ActionTransOnline
            appendDisplay("onDisplayTransReport");
            switch (code) {
                case PosConstants.EMV.PosEmvErrorCode.EMV_OTHER_ICC_INTERFACE:
                    stopTrans();

                    handlerEmv.sendEmptyMessage(0);
                    break;
            }
        }

        @Override
        public void onDisplayMsg(String msg) {
            appendDisplay(msg);
        }

        @Override
        public void onError(String msg) {
            appendDisplay("onError:" + msg);
            stopTrans();
        }
    };


    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        stopTrans();
    }

    private void stopTrans() {
        try {
            GcSmartPosUtils.getInstance().stopTransaction(MainApp.getInstance().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

