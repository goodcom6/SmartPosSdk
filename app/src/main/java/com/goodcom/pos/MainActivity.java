
package com.goodcom.pos;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.widget.Toolbar;

import com.goodcom.pos.Iso8583.Iso8583TestActivity;
import com.goodcom.pos.bank.BankActivity;
import com.goodcom.pos.emv.EmvNewActivity;
import com.goodcom.pos.pinpad.SelectPinpadKeySystemActivity;


public class MainActivity extends BaseAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("GcSmartPosDemo");

        findViewById(R.id.card_view_bank_card).setOnClickListener(this);
        findViewById(R.id.card_view_pin_pad).setOnClickListener(this);
        findViewById(R.id.card_view_emv).setOnClickListener(this);
        findViewById(R.id.iso8583).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.card_view_bank_card:
                openActivity(BankActivity.class);
                break;
            case R.id.card_view_pin_pad:
                openActivity(SelectPinpadKeySystemActivity.class);
                break;
            case R.id.card_view_emv:
                openActivity(EmvNewActivity.class);
                break;
            case R.id.iso8583:
                openActivity(Iso8583TestActivity.class);
                break;
            default:
                break;
        }
    }
}
