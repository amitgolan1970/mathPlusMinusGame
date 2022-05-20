package com.golan.amit.plusminus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String DEBUGTAG = "AMGO";
    public static final boolean DEBUG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        redirect();

        /**
         * For debug & dev phase:
         */

//        playDbForDebugAndDev();

//        redirectToLog();

    }

    private void playDbForDebugAndDev() {
        PlusMinusDbHelper pmdh = new PlusMinusDbHelper(this);
        pmdh.open();
        try {
            for(int i = 0; i < 10; i++) {
                pmdh.insertGameResult("3", "7", "10", "300");
                pmdh.insertGameResult("8", "2", "10", "500");
                pmdh.insertGameResult("9", "1", "10", "900");
            }
            if (DEBUG)
                Log.i(DEBUGTAG, "inserted to db");
        } catch (Exception exins) {
            Log.e(DEBUGTAG, "exception inserting to db: " + exins);
        }
        pmdh.close();
    }

    private void redirect() {
        Intent i = new Intent(this, PlusMinusActivity.class);
        startActivity(i);
    }

    private void redirectToLog() {
        Intent i = new Intent(this, LogActivity.class);
        startActivity(i);
    }
}
