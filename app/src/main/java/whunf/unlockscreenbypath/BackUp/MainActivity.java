package com.example.whunf.phonelock;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyPhoneLocker myPhoneLocker=new MyPhoneLocker(this);
        myPhoneLocker.setListener(new CallBack() {
            @Override
            public void shutDown() {
                finish();
            }
        });
        setContentView(myPhoneLocker);
    }



}
