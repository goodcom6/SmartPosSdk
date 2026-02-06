package com.goodcom.pos.pinpad;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.goodcom.smartpossdk.entity.PinpadResult;
import com.goodcom.pos.MainApp;
import com.goodcom.pos.R;
import com.goodcom.smartpossdk.GcSmartPosUtils;
import com.goodcom.smartpossdk.utils.BCDHelper;

public class RSAActivity extends Activity {

    private static final String TAG = "RSAActivity";
    private TextView tv_display;
    private String str_display;

    private EditText et_index;

    private int mIndex = 5;

    private String text = "qewwdsdadqweqw123244567gsdfsdfgsffsdf23457fsfsdgff3445456";

    private String encDataPub = "";

    private String encDataPri = "";

    private String signData = "";

    private String pubKey = "30820221300D06092A864886F70D01010105000382020E00308202090282020053703F39EFDB779595D5BA644A1213E5824C1F5690140F655A139D444387911C9A8551C9EC5A05FE6C0CF1B73925B999DCDB992EBBA581F4BD491895EE4F3E51D78360E425A11F812678C32680B87E73DA8EFD534E29DCAB6C3AB6310BA974CD0970B21F7B35529FBB65E6DE227578C661351CCCB54F377A8DAF911FF19C0E4B2DD6420C5E936A9F62CB4B57FBDF4CA45B78A200AFBE485307A6E9E004723950D11AD3F34F20C8F8DEE3DFEA47E3BAA37AB6A9E44EB39D0DEDE2E700FF1BC59FBC9A4C77FA362D97F9F9B3B96BCF23B746F6D9DCF4604BC95C46320BFA8118093D50301C4E65D28E849AC3C3C30778557A063AF48699413DD6317EC19A77BD32C29C93BC692F0556C3C7247C40FF1E96CCF5C8F01AC5CF996A7CE916B9346F3B934BD2BAD5514648D9BE62AFE8C91D3D635371ED354A09F0B7393209E0CB6D9170F85E4C3EB52BE32D4A165CCAC3F75E2679440652FFFF76B3FC6A0FABC61539A6FC9028CF908EDE844A41CC055FCF23566D74FF84C5734DF5769E9AAD4CD7B898C2E52A788DE63C453AF960C332392750FAE5B293DF188482BB93F6FDB532CA0B7EF96549F7539D5016449CF154704EE3E20DD128ED2C9609FEA7AAE73CEC78A28720E0CBBCFA5D8B26B1CC3811920F2B16DFF377102AE114377CA0225C1A35E4F65BB6371D558A3959B3B996119A6264CB0CD58988FEBA3AD337B61B0C6D6B0203010001";

    private void setIndex() {
        String index = et_index.getText().toString();
        mIndex = Integer.valueOf(index);
    }

    private
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
        setContentView(R.layout.activity_rsa);
        tv_display = findViewById(R.id.test_display);
        tv_display.setMovementMethod(ScrollingMovementMethod.getInstance());

        et_index = findViewById(R.id.et_index);
    }

    public void GenerateRsaKeyPair(View view) {
        display_clear();
        setIndex();
        int ret = GcSmartPosUtils.getInstance().generateRsaKeyPair(MainApp.getInstance().getApplicationContext(), mIndex);
        display_show("generateRsaKeyPair ret:" + ret);
    }


    public void removeRSAKey(View view) {
        display_clear();
        setIndex();
        int ret = GcSmartPosUtils.getInstance().removeRSAKey(MainApp.getInstance().getApplicationContext(), mIndex);
        display_show("removeRSAKey ret:" + ret);
    }

    public void getRsaPublicKey(View view) {
        display_clear();
        setIndex();
        PinpadResult result = GcSmartPosUtils.getInstance().getRsaPublicKey(MainApp.getInstance().getApplicationContext(), mIndex);
        display_show("getRsaPublicKey ret:" + result.getCode());
        if (result.getCode() == 0 && result.getData() != null) {
            display_show("getRsaPublicKey Data:" + result.getData());
        }
    }

    public void setRsaAlgPaddingMode(View view) {
            setIndex();
            GcSmartPosUtils.getInstance().setRsaAlgPaddingMode(MainApp.getInstance().getApplicationContext(), "RSA/ECB/PKCS1Padding");
    }

    public void encrypByRsaPub(View view) {
            display_clear();
            setIndex();
            PinpadResult result = GcSmartPosUtils.getInstance().encrypByRsaPub(MainApp.getInstance().getApplicationContext(), mIndex, text.getBytes());
            display_show("encrypByRsaPub:" + result.toString());
            if (result.getCode() == 0 && result.getData() != null) {
                encDataPub = result.getData();
            }
    }

    public void encrypByRsaPri(View view) {
            display_clear();
            setIndex();
            PinpadResult result = GcSmartPosUtils.getInstance().encrypByRsaPri(MainApp.getInstance().getApplicationContext(),mIndex, text.getBytes());
            display_show("encrypByRsaPub:" + result.toString());
            if (result.getCode() == 0 && result.getData() != null) {
                encDataPri = result.getData();
            }
    }

    public void decryptByRsaPub(View view) {
        display_clear();
        setIndex();
        if (encDataPri != null) {
            PinpadResult result = GcSmartPosUtils.getInstance().decryptByRsaPub(MainApp.getInstance().getApplicationContext(), mIndex, BCDHelper.StrToBCD(encDataPri));
            display_show("encrypByRsaPub:" + result.toString());
        }
    }

    public void decryptByRsaPri(View view) {
        display_clear();
        setIndex();
        if (encDataPub != null) {
            PinpadResult result = GcSmartPosUtils.getInstance().decryptByRsaPri(MainApp.getInstance().getApplicationContext(), mIndex, BCDHelper.StrToBCD(encDataPub));
            display_show("encrypByRsaPub:" + result.toString());
        }
    }

    public void signBySha256WithRsa(View view) {
        display_clear();
        setIndex();
        PinpadResult result = GcSmartPosUtils.getInstance().signBySha256WithRsa(MainApp.getInstance().getApplicationContext(), mIndex, text.getBytes());
        display_show("encrypByRsaPub:" + result.toString());
        if (result.getCode() == 0 && result.getData() != null) {
            signData = result.getData();
        }
    }

    public void verifySignBySha256WithRsa(View view) {
        display_clear();
        setIndex();
        if (signData != null) {
            int ret = GcSmartPosUtils.getInstance().verifySignBySha256WithRsa(MainApp.getInstance().getApplicationContext(), mIndex, BCDHelper.StrToBCD(signData), text.getBytes());
            display_show("encrypByRsaPub:" + ret);
        }
    }


    public void storeRSAPubKey(View view) {
        display_clear();
        setIndex();
        if (signData != null) {
            Log.e(TAG, "pubKey:" + pubKey);
            int ret = GcSmartPosUtils.getInstance().loadRsaPublicKey(MainApp.getInstance().getApplicationContext(), mIndex, BCDHelper.StrToBCD(pubKey));
            display_show("loadRsaPublicKey:" + ret);
        }
    }


}
