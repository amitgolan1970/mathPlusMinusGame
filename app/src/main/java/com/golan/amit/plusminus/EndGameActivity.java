package com.golan.amit.plusminus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class EndGameActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, View.OnLongClickListener {

    TextView tvInfo;
    ImageView ivMainPic;
    Button btnGoBack, btnExit;
    Bundle extras;
    boolean isWon;

    Bitmap currBmPtr;
    private int pic_ptr = -1;
    private int anim_ptr = -1;
    private int[] currWonPicPtr;
    private int[] currLostPicPtr;

    /**
     * Background Sound Music
     */
    MediaPlayer mp;
    SeekBar skb;
    AudioManager am;

    /**
     * Animation
     */
    Animation[] animRotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        init();

        setListeners();

        isWon = hasWon();

        if (isWon) {
            pic_ptr = (int) (Math.random() * currWonPicPtr.length);
            currBmPtr = BitmapFactory.decodeResource(getResources(), currWonPicPtr[pic_ptr]);
            anim_ptr = 0;
        } else {
            pic_ptr = (int) (Math.random() * currLostPicPtr.length);
            currBmPtr = BitmapFactory.decodeResource(getResources(), currLostPicPtr[pic_ptr]);
            anim_ptr = 1;
        }
        ivMainPic.setImageBitmap(currBmPtr);
        ivMainPic.startAnimation(animRotate[anim_ptr]);
    }

    private boolean hasWon() {
        boolean hasWon = false;
        if (extras != null) {
            try {
                int success = extras.getInt("success");
                int rounds = extras.getInt("totalRounds");
                int score = extras.getInt("score");
                if (success >= (rounds / 2))
                    hasWon = true;

                String tmpInfo = "תשובות נכונות: ";
                tmpInfo += String.valueOf(success);
                tmpInfo += "\n";
                tmpInfo += "תשובות שגויות: ";
                tmpInfo += String.valueOf ((rounds - success));
                tmpInfo += "\n";
                tmpInfo += "ניקוד: ";
                tmpInfo += String.valueOf(score);

                tvInfo.setText(tmpInfo);
                Log.d(MainActivity.DEBUGTAG, "in end game activity - received incoming data: ");
                Log.d(MainActivity.DEBUGTAG, tmpInfo);
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "in end game activity - failed to receive incoming data");
                tvInfo.setText("חריגה בפענוח הנתונים שהועברו");
            }
        }
        return hasWon;
    }

    private void setListeners() {
        btnGoBack.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnExit.setOnLongClickListener(this);
    }

    private void init() {
        isWon = true;

        currWonPicPtr = new int[]{
                R.mipmap.aquaman, R.mipmap.ariel, R.mipmap.lior_ariel, R.mipmap.liori_love, R.mipmap.superman, R.mipmap.cake_birthday_five
        };
        currLostPicPtr = new int[] {
                R.mipmap.maleficent, R.mipmap.pissed_witch, R.mipmap.ursula, R.mipmap.shirkhan
        };


        tvInfo = findViewById(R.id.tvEndGameInfoId);
        ivMainPic = findViewById(R.id.ivMainPicId);
        btnGoBack = findViewById(R.id.btnGoBackId);
        btnExit = findViewById(R.id.btnExitId);
        extras = getIntent().getExtras();

        skb = findViewById(R.id.sbEndGame);
        mp = MediaPlayer.create(this, R.raw.superman);
        mp.start();

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        skb.setMax(max);
        skb.setProgress(max / 4);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, max / 4, 0);
        skb.setOnSeekBarChangeListener(this);

        animRotate = new Animation[]{
                AnimationUtils.loadAnimation(this, R.anim.anim_scale_inout),
                AnimationUtils.loadAnimation(this, R.anim.anim_slideup)
        };
    }

    @Override
    public void onClick(View v) {
        if(v == btnGoBack) {
            Intent i = new Intent(this, PlusMinusActivity.class);
            startActivity(i);
        } else if (v == btnExit) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                Log.i(MainActivity.DEBUGTAG, "finish all affinity");
                finishAffinity();
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mp != null) {
            try {
                mp.pause();
            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mp != null) {
            try {
                mp.start();
            } catch (Exception e) {

            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(v == btnExit) {
            Intent i = new Intent(this, LogActivity.class);
            startActivity(i);
        }
        return true;
    }
}
