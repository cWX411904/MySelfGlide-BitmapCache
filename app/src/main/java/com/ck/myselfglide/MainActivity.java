package com.ck.myselfglide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ck.myselfglide.glide.load.LoadTest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LoadTest.testsFindLoader(this);
    }
}
