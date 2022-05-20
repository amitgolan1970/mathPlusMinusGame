package com.golan.amit.plusminus;

import android.util.Log;

public class PlusMinusHelper {

    public static final int LOWER = 500;
    public static final int UPPER = 5000;
    public static final int ALLOWEDATTEMPTS = 3;
    public static final int ROUNDS = 10;
//    public static final int ROUNDS = 3;     //  for debug. TODO change


    private int operand1;
    private int operand2;
    private int attempts;
    private int attempts_total;
    private int score;

    private int currentRound;
    private int fails;
    private int success;

    public enum MathOperation {
        subtraction, sumation
    }

    private MathOperation mathOperation;

    public PlusMinusHelper() {
        attempts = 0;
        attempts_total = 0;
        score = 0;
        currentRound = 0;
        fails = 0;
        mathOperation = MathOperation.sumation;
    }

    public void generateOperands() {
        if (mathOperation.equals(MathOperation.subtraction)) {
            int tmp = (int) (Math.random() * 2000 + 500);
            operand2 = (int) (Math.random() * UPPER + LOWER);
            operand1 = operand2 + tmp;
            if (MainActivity.DEBUG)
                Log.i(MainActivity.DEBUGTAG, "generated for subtraction: operand1=" + operand1 + ", operand2=" + operand2);
        } else if (mathOperation.equals(MathOperation.sumation)) {
            operand1 = (int) (Math.random() * (UPPER) + LOWER);
            operand2 = (int) (Math.random() * (UPPER) + LOWER);
            if (MainActivity.DEBUG)
                Log.i(MainActivity.DEBUGTAG, "generated for summation: operand1=" + operand1 + ", operand2=" + operand2);
        } else {
            Log.e(MainActivity.DEBUGTAG, "generate: never should reach this section");
        }
    }

    public int result() {
        if (mathOperation.equals(MathOperation.subtraction)) {
            return operand1 - operand2;
        } else if (mathOperation.equals(MathOperation.sumation)) {
            return operand1 + operand2;
        } else {
            Log.e(MainActivity.DEBUGTAG, "result: never should reach this section");
            return -1;
        }
    }

    public void setToSumation() {
        mathOperation = MathOperation.sumation;
    }

    public void setToSubtraction() {
        mathOperation = MathOperation.subtraction;
    }

    /**
     * Getters & Setters
     * @return
     */

    public MathOperation getMathOperation() {
        return mathOperation;
    }

    public int getOperand1() {
        return operand1;
    }

    public int getOperand2() {
        return operand2;
    }

    public void increaseAttempts() {
        this.attempts++;
    }

    public void resetAttempts() {
        this.attempts = 0;
    }

    public int getAttempts() {
        return attempts;
    }

    public void increaseCurrentRound() {
        this.currentRound++;
    }

    public void resetCurrentRound() {
        this.currentRound=0;
    }

    public int getCurrentRound() {
        return currentRound;
    }

    public void increaseFails() {
        this.fails++;
    }

    public void resetFails() {
        this.fails=0;
    }

    public int getFails() {
        return fails;
    }

    public void increseSuccess() {
        this.success++;
    }

    public void resetSuccess() {
        this.success=0;
    }

    public int getSuccess() {
        return success;
    }

    public void increaseScoreByVal(int val) {
        this.score+=val;
    }

    public void decreaseScoreByVal(int val) {
        this.score-=val;
        if(this.score < 0)
            this.score = 0;
    }

    public int getScore() {
        return score;
    }

    public void increaseAttemptsTotal() {
        this.attempts_total++;
    }

    public int getAttempts_total() {
        return attempts_total;
    }
}
