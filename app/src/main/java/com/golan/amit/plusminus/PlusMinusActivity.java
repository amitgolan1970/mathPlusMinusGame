package com.golan.amit.plusminus;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlusMinusActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener, View.OnLongClickListener {

    TextView tvUp, tvDown;
    TextView tvLine;
    TextView tvGenInfo, tvTimer;
    ImageView iv, ivFeedback;
    EditText etAnswer;
    ImageButton ibEqual, ibHelp;
    Spinner spinner;
    PlusMinusHelper pmh;
    private static final int REQUESTCODEOM = 1;
    private static final int REQUESTCODEHELP = 2;

    public static final int PLUSOPERATION = 0;
    public static final int MINUSOPERATION = 1;

    public static final int POINTSPANNELTY = 5;

    Bitmap currBmPtr;

    /**
     * Timers
     */
    private static final int TIMER = 5 * 60 * 1000;
//    private static final int TIMER = 1 * 60 * 1000;
    private int countDownInterval;
    private long timeToRemain;
    CountDownTimer cTimer;

    private static final int TIMER_FEEDBACK = 2 * 1000;
    CountDownTimer cTimerFeedback;

    /**
     * Background Sound
     */
    MediaPlayer mp;
    SeekBar sb;
    AudioManager am;

    /**
     * Animation
     */
    Animation[] animRotate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus_minus);

        init();

        setListeners();

        displayInitials();

        enableElements();

    }

    private void displayInitials() {
        tvUp.setText(String.valueOf(pmh.getOperand1()));
        tvDown.setText(String.valueOf(pmh.getOperand2()));
    }

    private void setListeners() {
        ibEqual.setOnClickListener(this);
        ibEqual.setOnLongClickListener(this);
        ibHelp.setOnClickListener(this);
        ibHelp.setOnLongClickListener(this);
        spinner.setOnItemSelectedListener(this);
    }

    private void init() {
        tvUp = findViewById(R.id.tvUpOperand);
        tvDown = findViewById(R.id.tvDownOperand);
        tvLine = findViewById(R.id.tvLindId);
        tvGenInfo = findViewById(R.id.tvGeneralInfoId);
        tvTimer = findViewById(R.id.tvTimerDisplayId);
        iv = findViewById(R.id.ivOperator);
        ivFeedback = findViewById(R.id.ivFeedbackId);
        etAnswer = findViewById(R.id.etAnswerId);
        etAnswer.requestFocus();
        ibEqual = findViewById(R.id.btnImgEqualId);
        ibHelp = findViewById(R.id.btnHelpId);
        spinner = findViewById(R.id.spinnerId);

        List<String> categories = new ArrayList<String>();
        categories.add("  חיבור  ");
        categories.add("  חיסור  ");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

//        List<String> ls = Arrays.asList(getResources().getStringArray(R.array.operations));
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ls);

        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        pmh = new PlusMinusHelper();

        cTimer = null;
        cTimerFeedback = null;
        timeToRemain = TIMER;

        sb = findViewById(R.id.sb);
        mp = MediaPlayer.create(this, R.raw.good_bad_ugly);
        mp.start();

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int max = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sb.setMax(max);
        sb.setProgress(max / 4);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, max / 4, 0);
        sb.setOnSeekBarChangeListener(this);

        /**
         * Animation
         */
        animRotate = new Animation[]{
                AnimationUtils.loadAnimation(this, R.anim.anim_rotate_right),
                AnimationUtils.loadAnimation(this, R.anim.anim_rotate_left)
        };

    }

    @Override
    public void onClick(View v) {
        if (v == ibHelp) {
            int operation = PLUSOPERATION;
            if (pmh.getMathOperation().equals(PlusMinusHelper.MathOperation.subtraction)) {
                operation = MINUSOPERATION;
            }
            //  TODO pass operands and math operation
            Intent i = new Intent(this, HelpCalcActivity.class);
            i.putExtra("operand1", pmh.getOperand1());
            i.putExtra("operand2", pmh.getOperand2());
            i.putExtra("math_operation", operation);
            startActivityForResult(i, REQUESTCODEHELP);

        } else if (v == ibEqual) {

            if (etAnswer.getText() == null || etAnswer.getText().toString().equalsIgnoreCase("")) {
                etAnswer.setText("");
                Toast.makeText(this, "הוקלד ערך ריק", Toast.LENGTH_SHORT).show();
                Log.e(MainActivity.DEBUGTAG, "empty value entered");
                return;
            }
            String tmpAnswerStr = etAnswer.getText().toString();
            int tmpAnswerInt = -1;
            try {
                tmpAnswerInt = Integer.parseInt(tmpAnswerStr);
            } catch (Exception e) {
                etAnswer.setText("");
                Toast.makeText(this, "הוקלד ערך בלתי חוקי", Toast.LENGTH_SHORT).show();
                Log.e(MainActivity.DEBUGTAG, "Illegal value entered");
                return;
            }

            /**
             * won the round - correct answer
             */

            if (MainActivity.DEBUG)
                Log.i(MainActivity.DEBUGTAG, "attempt: " + pmh.getAttempts() + ", round: " + pmh.getCurrentRound());
            iv.startAnimation(animRotate[(int) (Math.random() * animRotate.length)]);
            if (tmpAnswerInt < 0) {
                Toast.makeText(PlusMinusActivity.this, "ללא מספרים שליליים", Toast.LENGTH_SHORT).show();
            } else if (tmpAnswerStr.contains(".")) {
                Toast.makeText(PlusMinusActivity.this, "רק מספרים שלמים", Toast.LENGTH_SHORT).show();
            }
            if (tmpAnswerInt == pmh.result()) {
                cTimer.cancel();
                Log.e(MainActivity.DEBUGTAG, "Correct answer in " + pmh.getCurrentRound() + " round");
                pmh.increseSuccess();
                pmh.resetAttempts();
                pmh.increaseCurrentRound();
                pmh.increaseScoreByVal((int)(timeToRemain / 10000));
                if (pmh.getCurrentRound() == PlusMinusHelper.ROUNDS) {
                    pmh.decreaseScoreByVal((int)(pmh.getAttempts_total() * POINTSPANNELTY));
                    PlusMinusDbHelper pmdh = new PlusMinusDbHelper(this);
                    pmdh.open();
                    pmdh.insertGameResult(String.valueOf((PlusMinusHelper.ROUNDS - pmh.getFails())),
                            String.valueOf(pmh.getFails()), String.valueOf(PlusMinusHelper.ROUNDS),
                            String.valueOf(pmh.getScore()));
                    pmdh.close();
                    Toast.makeText(this, "המשחק נגמר", Toast.LENGTH_SHORT).show();
                    Log.e(MainActivity.DEBUGTAG, "game over");
                    disableElements();
                    redirectToEndActivity();
                    return;
                } else {
                    Toast.makeText(this, "תשובה נכונה", Toast.LENGTH_SHORT).show();
                    timerFeedback(TIMER_FEEDBACK);
                    tvGenInfo.setText("תשובה נכונה");

                    currBmPtr = BitmapFactory.decodeResource(getResources(), R.mipmap.emojy_happy);
                    ivFeedback.setVisibility(View.VISIBLE);
                    ivFeedback.setImageBitmap(currBmPtr);
                }
                pmh.generateOperands();
                displayInitials();
                etAnswer.setText("");
                timeToRemain = TIMER;
                timerDemo(timeToRemain);
                return;
            } else {
                /**
                 * Lost this attempt - wrong answer
                 */
                pmh.increaseAttempts();
                pmh.increaseAttemptsTotal();
                if (pmh.getAttempts() == PlusMinusHelper.ALLOWEDATTEMPTS) {
                    /**
                     * Lost this round
                     */
                    cTimer.cancel();
                    if (MainActivity.DEBUG)
                        Log.i(MainActivity.DEBUGTAG, "no more attempts");
                    pmh.increaseFails();
                    pmh.resetAttempts();
                    pmh.increaseCurrentRound();
                    if (pmh.getCurrentRound() == PlusMinusHelper.ROUNDS) {
                        pmh.decreaseScoreByVal((int)(pmh.getAttempts_total() * POINTSPANNELTY));
                        PlusMinusDbHelper pmdh = new PlusMinusDbHelper(this);
                        pmdh.open();
                        pmdh.insertGameResult(String.valueOf((PlusMinusHelper.ROUNDS - pmh.getFails())),
                                String.valueOf(pmh.getFails()), String.valueOf(PlusMinusHelper.ROUNDS),
                                String.valueOf(pmh.getScore()));
                        pmdh.close();
                        Toast.makeText(this, "המשחק נגמר", Toast.LENGTH_SHORT).show();
                        Log.e(MainActivity.DEBUGTAG, "game over");
                        disableElements();
                        redirectToEndActivity();
                        return;
                    } else {
                        Toast.makeText(this, "נגמרו הנסיונות", Toast.LENGTH_SHORT).show();
                        timerFeedback(TIMER_FEEDBACK);
                        String wrongAnswer = "התשובה הנכונה הייתה ";
                        wrongAnswer += pmh.result();
                        tvGenInfo.setText(wrongAnswer);

                        currBmPtr = BitmapFactory.decodeResource(getResources(), R.mipmap.emojy_sad);
                        ivFeedback.setVisibility(View.VISIBLE);
                        ivFeedback.setImageBitmap(currBmPtr);
                    }
                    pmh.generateOperands();
                    displayInitials();
                    etAnswer.setText("");
                    timeToRemain = TIMER;
                    timerDemo(timeToRemain);
                    return;
                }
                Toast.makeText(this, "תשובה לא נכונה", Toast.LENGTH_SHORT).show();
                Log.e(MainActivity.DEBUGTAG, "Wrong answer, attempt: " + pmh.getAttempts());
                timerFeedback(TIMER_FEEDBACK);
                tvGenInfo.setText("תשובה לא נכונה");
                etAnswer.setText("");
            }
        }
    }

    private void redirectToEndActivity() {
        if(mp != null) {
            mp.stop();
            try {
                mp.release();
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "release media player exception");
            }
        }
        Intent i = new Intent(this, EndGameActivity.class);
        i.putExtra("success", pmh.getSuccess());
        i.putExtra("totalRounds", PlusMinusHelper.ROUNDS);
        i.putExtra("score", pmh.getScore());
        startActivityForResult(i, REQUESTCODEOM);
    }

    private void disableElements() {
        etAnswer.setEnabled(false);
        ibEqual.setClickable(false);
        spinner.setVisibility(View.INVISIBLE);
    }

    private void enableElements() {
        etAnswer.setEnabled(true);
        ibEqual.setClickable(true);
        spinner.setVisibility(View.VISIBLE);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (MainActivity.DEBUG)
            Log.i(MainActivity.DEBUGTAG, "in onItemSelected");
        int pos = parent.getSelectedItemPosition();
        String item = parent.getItemAtPosition(position).toString();
        if (pos == PLUSOPERATION) {
            pmh.setToSumation();
            iv.setImageResource(R.mipmap.plussign);
        } else if (pos == MINUSOPERATION) {
            pmh.setToSubtraction();
            iv.setImageResource(R.mipmap.minus);
        } else {
            Log.e(MainActivity.DEBUGTAG, "none spinner selected. can't be");
            return;
        }
        pmh.generateOperands();
        displayInitials();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void timerDemo(final long millisInFuture) {
        countDownInterval = 1000;
        cTimer = new CountDownTimer(millisInFuture, countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeToRemain = millisUntilFinished;
                long Minutes = timeToRemain / (60 * 1000) % 60;
                long Seconds = timeToRemain / 1000 % 60;

                if(Minutes == 1 && Seconds == 0) {
                    Toast.makeText(PlusMinusActivity.this, "הזמן אוזל...", Toast.LENGTH_LONG).show();
                }

                String tmpTime = String.format("%02d:%02d", Minutes, Seconds);
                tvTimer.setText(tmpTime);
            }

            @Override
            public void onFinish() {
                if (MainActivity.DEBUG)
                    Log.i(MainActivity.DEBUGTAG, "count down finished");

                cTimer.cancel();

                pmh.increaseFails();
                pmh.resetAttempts();
                pmh.increaseCurrentRound();
                if (pmh.getCurrentRound() == PlusMinusHelper.ROUNDS) {
                    pmh.decreaseScoreByVal((int)(pmh.getAttempts_total() * POINTSPANNELTY));
                    PlusMinusDbHelper pmdh = new PlusMinusDbHelper(PlusMinusActivity.this);
                    pmdh.open();
                    pmdh.insertGameResult(String.valueOf((PlusMinusHelper.ROUNDS - pmh.getFails())),
                            String.valueOf(pmh.getFails()), String.valueOf(PlusMinusHelper.ROUNDS),
                            String.valueOf(pmh.getScore()));
                    pmdh.close();
                    Toast.makeText(PlusMinusActivity.this, "המשחק נגמר", Toast.LENGTH_SHORT).show();
                    Log.e(MainActivity.DEBUGTAG, "round ended");
                    tvGenInfo.setText("הזמן אזל. המשחק הסתיים");
                    disableElements();
                    redirectToEndActivity();
                    return;
                } else {
                    iv.startAnimation(animRotate[(int) (Math.random() * animRotate.length)]);
                    Toast.makeText(PlusMinusActivity.this, "נגמר הזמן", Toast.LENGTH_SHORT).show();
                    String wrongAnswer = "הזמן נגמר. התשובה הנכונה הייתה ";
                    wrongAnswer += pmh.result();
                    timerFeedback(TIMER_FEEDBACK);
                    tvGenInfo.setText(wrongAnswer);

                    currBmPtr = BitmapFactory.decodeResource(getResources(), R.mipmap.emojy_sad);
                    ivFeedback.setVisibility(View.VISIBLE);
                    ivFeedback.setImageBitmap(currBmPtr);
                }
                pmh.generateOperands();
                displayInitials();
                etAnswer.setText("");
                timeToRemain = TIMER;
                timerDemo(timeToRemain);
                return;
            }
        }.start();
    }


    private void timerFeedback(long millisInFuture) {
        countDownInterval = 1000;
        cTimerFeedback = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                if (tvGenInfo != null) {
                    try {
                        tvGenInfo.setText("");
                    } catch (Exception eti) {
                    }
                }
                if (ivFeedback != null) {
                    try {
                        ivFeedback.setVisibility(View.INVISIBLE);
                    } catch (Exception ep) {
                    }
                }
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MainActivity.DEBUG)
            Log.i(MainActivity.DEBUGTAG, "in on resume");
        timerDemo(timeToRemain);
        if (mp != null) {
            try {
                mp.start();
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "on resume, media player start exception");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp != null) {
            try {
                mp.pause();
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "on pause, media player pause exception");
            }
        }
        if (MainActivity.DEBUG)
            Log.i(MainActivity.DEBUGTAG, "in on pause");
        cTimer.cancel();
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
    public boolean onLongClick(View v) {
        if(v == ibEqual) {
            timerFeedback(TIMER_FEEDBACK);
            String tmpInfo = "round: " + pmh.getCurrentRound() + ", attempt: " + pmh.getAttempts() + ", fails: " +
                    pmh.getFails() + ", success: " + pmh.getSuccess() + ", result: " + pmh.result();

            tmpInfo = "סיבוב ";
            tmpInfo += pmh.getCurrentRound();
            tmpInfo += ", ";
            tmpInfo += "נסיון ";
            tmpInfo += pmh.getAttempts();
            tmpInfo += ", ";
            tmpInfo += "מספר נסיונות כולל ";
            tmpInfo += pmh.getAttempts_total();
            tmpInfo += ", ";
            tmpInfo += "תשובות נכונות ";
            tmpInfo += pmh.getSuccess();
            tmpInfo += ", ";
            tmpInfo += "תשובות שגויות ";
            tmpInfo += pmh.getFails();
            tmpInfo += ", ";
            tmpInfo += "ניקוד ";
            tmpInfo += pmh.getScore();
            tmpInfo += ", ";
            tmpInfo += "תוצאה: ";
            tmpInfo += pmh.result();

            tvGenInfo.setText(tmpInfo);
        } else if(v == ibHelp) {
            Toast.makeText(v.getContext(), "help long clicked", Toast.LENGTH_SHORT).show();
            reverseAnswer();
        }
        return true;
    }

    private void reverseAnswer() {
        if (MainActivity.DEBUG)
            Log.i(MainActivity.DEBUGTAG, "in reverse answer");
        if (etAnswer.getText() == null || etAnswer.getText().toString().equalsIgnoreCase("")) {
            return;
        }
        int answerInt = -1;
        try {
            answerInt = Integer.parseInt(etAnswer.getText().toString());
            if (answerInt <= 0) {
                return;
            }

            StringBuilder sb = new StringBuilder(etAnswer.getText().toString());
            etAnswer.setText(sb.reverse().toString());
        } catch (Exception exr) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(MainActivity.DEBUGTAG, "in page - when coming back requescode: " + requestCode + ", resultCode: " + resultCode);
        if (resultCode == RESULT_OK && requestCode == REQUESTCODEHELP) {
            if (data.hasExtra("answer")) {
                try {
                    int theRecievedAnswer = data.getExtras().getInt("answer");
                    if (MainActivity.DEBUG)
                        Log.i(MainActivity.DEBUGTAG, "recieved back: " + theRecievedAnswer);
                    etAnswer.setText(String.valueOf(theRecievedAnswer));
                } catch (Exception e) {
                    Log.e(MainActivity.DEBUGTAG, "exception while trying to receive back the answer");
                }
            } else {
                if (MainActivity.DEBUG)
                    Log.i(MainActivity.DEBUGTAG, "recieved back - extras has no data");
            }
        }
    }
}
