package com.ownchan.miclock;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CustomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("原控件");
        setContentView(R.layout.activity_custom);
    }
}
