package com.goodcom.pos.bank;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.goodcom.smartpossdk.PosConstants;
import com.goodcom.smartpossdk.listener.BankCardReaderListener;
import com.goodcom.pos.BaseAppCompatActivity;
import com.goodcom.pos.MainApp;
import com.goodcom.pos.R;
import com.goodcom.smartpossdk.GcSmartPosUtils;
import com.goodcom.smartpossdk.utils.BCDHelper;

public class BankActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card);
        initView();
    }

    private void initView() {
        findViewById(R.id.bank_card).setOnClickListener(this);
    }

    byte[] apdu = new byte[] {
            (byte) 0x00,
            (byte) 0xA4,
            (byte) 0x00,
            (byte) 0x00,
            (byte) 0x02,
            (byte) 0x3F,
            (byte) 0x00
    };

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.bank_card:
                try {
                    int type = PosConstants.ReadCardType.IC_CARD | PosConstants.ReadCardType.MAG_CARD |
                            PosConstants.ReadCardType.RF_CARD;
                    
                    GcSmartPosUtils.getInstance().checkCard(MainApp.getInstance().getApplicationContext(), type, new BankCardReaderClass(),30);
                    break;
                }catch (Exception e){
                    e.printStackTrace();
                }
        }
    }


    private static class BankCardReaderClass extends BankCardReaderListener.Stub {

        @Override
        public void findMagCard(Bundle info) throws RemoteException {
            String track1 = info.getString("TRACK1");
            String track2 = info.getString("TRACK2");
            String track3 = info.getString("TRACK3");

            Log.e(TAG,"track1:"+track1);
            Log.e(TAG,"track2:"+track2);
            Log.e(TAG,"track3:"+track3);
        }

        @Override
        public void findICCard(Bundle info) throws RemoteException {
            int  cardType = info.getInt("cardType");
            String ATR = info.getString("ATR");
            Log.e(TAG,"cardType:"+cardType);
            Log.e(TAG,"ATR:"+ ATR);
        }

        @Override
        public void findRFCard(Bundle info) throws RemoteException {
            int  cardType = info.getInt("cardType");
            byte[] atqa = info.getByteArray("atqa");
            Log.e(TAG,"cardType:"+cardType);
            Log.e(TAG,"atqa:"+ BCDHelper.bcdToString(atqa,0,atqa.length));
        }

        @Override
        public void onCheckError(Bundle info) throws RemoteException {
            int  code = info.getInt("code");
            String message = info.getString("message");
            Log.e(TAG,"code:"+code);
            Log.e(TAG,"message:"+message);
        }
    }


}
