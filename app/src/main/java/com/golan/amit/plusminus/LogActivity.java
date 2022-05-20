package com.golan.amit.plusminus;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class LogActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, AdapterView.OnItemLongClickListener {

    Button btnBackToMainPage;
    PlusMinusDbHelper pmdh;
    ArrayList<GameResult> listOfGameResults;
    ListView lv;
    GameResultAdapter gameResultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        init();

        setListeners();
    }

    private void setListeners() {
        btnBackToMainPage.setOnClickListener(this);
        btnBackToMainPage.setOnLongClickListener(this);
        lv.setOnItemLongClickListener(this);
    }

    private void init() {
        btnBackToMainPage = findViewById(R.id.btnBackToMainPageId);
        lv = findViewById(R.id.lv);
        pmdh = new PlusMinusDbHelper(this);
        listOfGameResults = new ArrayList<GameResult>();

        pmdh.open();
        listOfGameResults = pmdh.getAllGameResults();
        pmdh.close();

        gameResultAdapter = new GameResultAdapter(this, 0, listOfGameResults);
        if(lv != null) {
            lv.setAdapter(gameResultAdapter);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == btnBackToMainPage) {
            Intent i = new Intent(this, PlusMinusActivity.class);
            startActivity(i);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (v == btnBackToMainPage) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        pmdh.open();
                        pmdh.resetTableToScratch();
                        listOfGameResults = pmdh.getAllGameResults();
                        pmdh.close();
                        refreshMyAdapter();
                        if (MainActivity.DEBUG)
                            Log.i(MainActivity.DEBUGTAG, "db reset");
                        Toast.makeText(LogActivity.this, "מסד הנתונים נמחק", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Log.e(MainActivity.DEBUGTAG, "exception when resetting db");
                    }
                }
            });

            builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            builder.setTitle("איפוס כל הרשומות");
            builder.setMessage("האם לאפס את הרשומות?");
            AlertDialog dlg = builder.create();
            dlg.show();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        pmdh.open();

        switch (item.getItemId()) {
            case R.id.menu_date_asc:
                listOfGameResults = pmdh.getAllGameResultsByFilters(null, "curr_datetime ASC");
                refreshMyAdapter();
                break;
            case R.id.menu_date_desc:
                listOfGameResults = pmdh.getAllGameResultsByFilters(null, "curr_datetime DESC");
                refreshMyAdapter();
                break;
            case R.id.menu_correct:
                listOfGameResults = pmdh.getAllGameResultsByFilters(null, "correct ASC");
                refreshMyAdapter();
                break;
            case R.id.menu_wrong:
                listOfGameResults = pmdh.getAllGameResultsByFilters(null, "wrong ASC");
                refreshMyAdapter();
                break;
            case R.id.menu_score:
                listOfGameResults = pmdh.getAllGameResultsByFilters(null, "score ASC");
                refreshMyAdapter();
                break;
        }
        pmdh.close();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (MainActivity.DEBUG)
            Log.v(MainActivity.DEBUGTAG, "raw details, position: " + position + ", id: " + id);
        GameResult gr = gameResultAdapter.getItem(position);
        final int realId = gr.getId();
        if (MainActivity.DEBUG)
            Log.i(MainActivity.DEBUGTAG, "game result object details:\n{" + gr.toString() + "}\n" +
                    "about to delete id: " + realId);


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("כן", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pmdh.open();
                pmdh.deleteRecordById(realId);
                listOfGameResults = pmdh.getAllGameResults();
                pmdh.close();
                refreshMyAdapter();
            }
        });
        builder.setNegativeButton("לא", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.setTitle("מחיקת רשומה");
        builder.setMessage("האם למחוק את הרשומה?");
        AlertDialog dlg = builder.create();
        dlg.show();
        return true;
    }

    public void refreshMyAdapter() {
        gameResultAdapter = new GameResultAdapter(this, 0, listOfGameResults);
        lv.setAdapter(gameResultAdapter);
    }
}
