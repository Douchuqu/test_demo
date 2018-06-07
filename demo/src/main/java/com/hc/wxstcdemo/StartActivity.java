package com.hc.wxstcdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StartActivity extends Activity {
    Button mainBtn;
    Button debugBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mainBtn= (Button) findViewById(R.id.main_btn);
        debugBtn= (Button) findViewById(R.id.debug_btn);
        mainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, MainActivity.class));
                finish();
            }
        });

        debugBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, BebugActivity.class));
                finish();
            }
        });
    }
}
