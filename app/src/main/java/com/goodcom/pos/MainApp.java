package com.goodcom.pos;

import android.app.Application;
import android.os.RemoteException;
import android.util.Log;

import com.goodcom.administrator.generalprinter.constant.PosConstants;
import com.goodcom.administrator.generalprinter.entity.PinpadResult;
import com.goodcom.pos.emv.EmvConfig;
import com.goodcom.pos.emv.TransData;
import com.goodcom.smartpossdk.ConnectCallback;
import com.goodcom.smartpossdk.GcSmartPosUtils;
import com.goodcom.smartpossdk.utils.BCDHelper;


public class MainApp extends Application {
    private static final String TAG = "MyApplication";
    public static MainApp app;
    private TransData mTransData;

    public static MainApp getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        GcSmartPosUtils.getInstance().init(this, new ConnectCallback() {
            @Override
            public void onConnectPaySDK() {
                test();
                init();
            }
        });
    }

    private void test() {
        try {
            int area = 20;
            int index = 99;
            
            PinpadResult pinpadResult = GcSmartPosUtils.getInstance().getTdesKeyKcv(this, PosConstants.PinPad.KeyType.PIN_KEY, area, index);
            pinpadResult.setCode(-1);
            if (pinpadResult.getCode() != PosConstants.PinPad.PinpadErrorCode.SUCCESS) {


//                 String tlk = "11111111111111111111111111111111";
//                 String tmk = "F40379AB9E0EC533F40379AB9E0EC533";
//                 String tmk_kcv = "82E13665"; //82E13665
//                 String tak = "58D46F8C4CA35891C76595E92D499E0F";
//                 String tak_kcv = "B865B501"; //B865B501
//                 String tpk = "58D46F8C4CA35891C76595E92D499E0F";
//                 String tpk_kcv = "B865B501"; //B865B501
//                 String tdk = "58D46F8C4CA35891C76595E92D499E0F";
//                 String tdk_kcv = "B865B501"; //B865B501
//
//                int ret =  GcSmartPosUtils.getInstance().loadProtectKey(area,tlk);
//                Log.e(TAG, "loadProtectKey ret:" + ret);
//
//                ret = GcSmartPosUtils.getInstance().loadMainKeyByEnc(area, index, tmk,tmk_kcv);
//                Log.e(TAG, "loadMainKeyByEnc ret:" + ret);
//
//                ret = GcSmartPosUtils.getInstance().loadMacKey(area, index, tak, tak_kcv);
//                Log.e(TAG, "loadMacKey ret:" + ret);
//                ret = GcSmartPosUtils.getInstance().loadPinKey(area, index, tpk, tpk_kcv);
//                Log.e(TAG, "loadPinKey ret:" + ret);
//                ret = GcSmartPosUtils.getInstance().loadTrackKey(area, index, tdk =, tdk_kcv);
//                Log.e(TAG, "loadTrackKey ret:" + ret);

                String tlk = "11111111111111111111111111111111";
                String tak = "58D46F8C4CA35891C76595E92D499E0F";
                String tak_kcv = "B865B501"; //B865B501
                String tpk = "58D46F8C4CA35891C76595E92D499E0F";
                String tpk_kcv = "B865B501"; //B865B501
                String tdk = "58D46F8C4CA35891C76595E92D499E0F";
                String tdk_kcv = "B865B501"; //B865B501


                int ret = GcSmartPosUtils.getInstance().loadMainKeyByPlaintext(this, area, index, tlk);
                Log.e(TAG, "loadMainKeyByPlaintext ret:" + ret);

                ret = GcSmartPosUtils.getInstance().loadMacKey(this,area, index, tak, tak_kcv);
                Log.e(TAG, "loadMacKey ret:" + ret);
                ret = GcSmartPosUtils.getInstance().loadPinKey(this,area, index, tpk, tpk_kcv);
                Log.e(TAG, "loadPinKey ret:" + ret);
                ret = GcSmartPosUtils.getInstance().loadTrackKey(this,area, index, tdk, tdk_kcv);
                Log.e(TAG, "loadTrackKey ret:" + ret);

                String dukpt = "CF896E42A67B5E220EC6D2C9F394CA8F";
                String ksn = "FFFFFFFFFF1111100000";

                ret = GcSmartPosUtils.getInstance().loadDukptByIpek(this,2, BCDHelper.StrToBCD(dukpt), BCDHelper.StrToBCD(ksn), null);
                Log.e(TAG, "loadDukptByIpek ret:" + ret);

                //                int ret = GcSmartPosUtils.getInstance().loadMainKeyByPlaintext(area, index, tlk);
//                Log.e(TAG, "loadMainKeyByPlaintext ret:" + ret);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() {

        try {
            EmvConfig.loadAid();

            EmvConfig.loadCapk();
            EmvConfig.loadTerminal();
            EmvConfig.loadExceptionFile();
            EmvConfig.loadRevocationIPK();

            EmvConfig.loadVisa();
            EmvConfig.loadUnionPay();
            EmvConfig.loadMasterCard();
            EmvConfig.loadDiscover();
            EmvConfig.loadAmex();
            EmvConfig.loadMir();
            EmvConfig.loadVisaDRL();
            EmvConfig.loadAmexDRL();
            EmvConfig.loadService();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public TransData getTransData() {
        return mTransData;
    }

    public void setTransData(TransData transData) {
        this.mTransData = transData;
    }

    public boolean isTestMode() {
        return true;
    }
}
