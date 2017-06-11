package com.jameshughes89.committracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Created by JamesHughes89 on 6/09/2017.
 *
 * A small app that will keep you informed about when your repos, or repos you're interested in
 * have updates. It will also say who updated them, and the commit message.
 *
 * WARNING: This uses HTML tags for parsing. This is probably a bad idea long term because
 *          they could change what their tags are.
 *
 * Credits:
 *  Creator - James Alexander Hughes
 *      Art - Matea Drljepan
 *
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
