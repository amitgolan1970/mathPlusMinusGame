package com.golan.amit.plusminus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HelpCalcActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnBack, btnBackNoAnswer;
    Button[] btnDigits;
    Bundle extras;
    int operand1, operand2, operation;
    TextView tvOperand1, tvOperand2;
    TextView[] tvDisplayDigits;
    Bitmap currBmOperationPtr;
    ImageView ivOperationHelpCalc;
    ImageButton ibDeleteBtn;

    int actualAnswer;
    int currIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_calc);

        init();

        setListener();

        setInitialRecieved();

        displayInitials();

    }

    private void displayInitials() {
        tvOperand1.setText(String.valueOf(operand1));
        tvOperand2.setText(String.valueOf(operand2));
        if (operation == PlusMinusActivity.PLUSOPERATION) {
            currBmOperationPtr = BitmapFactory.decodeResource(getResources(), R.mipmap.plussign);
        } else {
            currBmOperationPtr = BitmapFactory.decodeResource(getResources(), R.mipmap.minus);
        }
        ivOperationHelpCalc.setImageBitmap(currBmOperationPtr);
    }


    private void init() {
        operand1 = -1;
        operand2 = -1;
        operation = -1;

        actualAnswer = 0;
        currIndex = 0;

        btnBack = findViewById(R.id.btnBackFromHelpId);
        btnBackNoAnswer = findViewById(R.id.btnBackFromHelpNoAnswerId);
        btnDigits = new Button[] {
                findViewById(R.id.btnHelpNumBtnId0), findViewById(R.id.btnHelpNumBtnId1), findViewById(R.id.btnHelpNumBtnId2),
                findViewById(R.id.btnHelpNumBtnId3), findViewById(R.id.btnHelpNumBtnId4), findViewById(R.id.btnHelpNumBtnId5),
                findViewById(R.id.btnHelpNumBtnId6), findViewById(R.id.btnHelpNumBtnId7), findViewById(R.id.btnHelpNumBtnId8),
                findViewById(R.id.btnHelpNumBtnId9)
        };

        extras = getIntent().getExtras();
        tvOperand1 = findViewById(R.id.tvHelpOperand1Id);
        tvOperand2 = findViewById(R.id.tvHelpOperand2Id);

        tvDisplayDigits = new TextView[] {
                findViewById(R.id.tvHelpDisplayId0), findViewById(R.id.tvHelpDisplayId1), findViewById(R.id.tvHelpDisplayId2),
                findViewById(R.id.tvHelpDisplayId3), findViewById(R.id.tvHelpDisplayId4)
        };

        ivOperationHelpCalc = findViewById(R.id.ivHelpCalcPageOperator);
        ibDeleteBtn = findViewById(R.id.ibDeleteId);
    }

    private void setListener() {
        btnBack.setOnClickListener(this);
        btnBackNoAnswer.setOnClickListener(this);
        ibDeleteBtn.setOnClickListener(this);
        for(int i = 0; i < btnDigits.length; i++) {
            btnDigits[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        Button currDigitBtn = null;
        int currBtnIndex = -1;
        if (v == btnBackNoAnswer) {

            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return;
        } else if (v == btnBack) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tvDisplayDigits.length; i++) {
                if (!tvDisplayDigits[i].getText().toString().equalsIgnoreCase("")) {
                    sb.append(tvDisplayDigits[i].getText().toString());
                }
            }

            if (MainActivity.DEBUG)
                Log.i(MainActivity.DEBUGTAG, "sb is: {" + sb.toString() + "}");
            try {
                actualAnswer = Integer.parseInt(sb.toString());
            } catch (Exception e) {
                Log.e(MainActivity.DEBUGTAG, "exception while trying to parse replied answer");
                finish();
                return;
            }

            Intent returnIntent = new Intent();
            returnIntent.putExtra("answer", actualAnswer);
            setResult(RESULT_OK, returnIntent);
            finish();
            return;
        } else if (v == ibDeleteBtn) {
            decreaseCurrIndex();
            tvDisplayDigits[(tvDisplayDigits.length - 1) - getCurrIndex()].setText("");
            if (getCurrIndex() == (tvDisplayDigits.length - 1)) {
                enableDigitsButtons();
            }
            if (getCurrIndex() == 0) {
                ibDeleteBtn.setVisibility(View.INVISIBLE);
            }
            return;
        } else {
            for (int i = 0; i < btnDigits.length; i++) {
                if (v == btnDigits[i]) {
                    currDigitBtn = btnDigits[i];
                    currBtnIndex = i;
                    break;
                }
            }
        }
        if (currBtnIndex == -1 || currDigitBtn == null) {
            if (MainActivity.DEBUG)
                Log.i(MainActivity.DEBUGTAG, "no button digit was clicked");
            return;
        }


        if (MainActivity.DEBUG)
            Log.i(MainActivity.DEBUGTAG, "button digit " + currBtnIndex + " was clicked");
        ibDeleteBtn.setVisibility(View.VISIBLE);
        tvDisplayDigits[(tvDisplayDigits.length - 1) - getCurrIndex()].setText(String.valueOf(currBtnIndex));
        increaseCurrIndex();

        if (getCurrIndex() == tvDisplayDigits.length) {
            disableDigitsButtons();
        }
    }

    private void disableDigitsButtons() {
        for(int i = 0; i < btnDigits.length; i++) {
            btnDigits[i].setText("X");
            btnDigits[i].setClickable(false);
        }
    }

    private void enableDigitsButtons() {
        for(int i = 0; i < btnDigits.length; i++) {
            btnDigits[i].setText(String.valueOf(i));
            btnDigits[i].setClickable(true);
        }
    }

    private void setInitialRecieved() {
        if (extras != null) {
            try {
                operand1 = extras.getInt("operand1");
                operand2 = extras.getInt("operand2");
                operation = extras.getInt("math_operation");
                if (MainActivity.DEBUG)
                    Log.i(MainActivity.DEBUGTAG, "in help calc page. received: " + operand1 + "," + operand2 + " operation: " +
                            (operation == 0 ? "plus" : "minus"));
                return;
            } catch (Exception e) {
                Toast.makeText(HelpCalcActivity.this, "in help calc page. extras is null, exiting...", Toast.LENGTH_SHORT).show();
                Log.e(MainActivity.DEBUGTAG, "failed to get data in help page");
            }
        } else {
            Toast.makeText(HelpCalcActivity.this, "in help calc page. extras is null, exiting...", Toast.LENGTH_SHORT).show();
            Log.e(MainActivity.DEBUGTAG, "in help calc page extras is null, exiting...");
        }
        finish();
    }


    private void increaseCurrIndex() {
        this.currIndex++;
    }

    private void decreaseCurrIndex() {
        this.currIndex--;
    }

    public int getCurrIndex() {
        return currIndex;
    }
}
