package com.goodcom.pos.pinpad;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.goodcom.pos.BaseAppCompatActivity;
import com.goodcom.pos.R;

public class SelectPinpadKeySystemActivity extends BaseAppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinpad_select);
    }

    public void desKeySystem(View view){
        openActivity(TDESActivity.class);
    }

    public void dukptKeySystem(View view){
        openActivity(DukptActivity.class);
    }

    public void rsaKeySystem(View view){
        openActivity(RSAActivity.class);
    }
}
