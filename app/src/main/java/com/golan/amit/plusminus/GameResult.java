package com.golan.amit.plusminus;

public class GameResult {

    private int id;
    private int wrong;
    private int correct;
    private int rounds;
    private int score;
    private String curr_datetime;

    public GameResult(int id, int correct, int wrong, int rounds, int score, String curr_datetime) {
        this.id = id;
        this.correct = correct;
        this.wrong = wrong;
        this.rounds = rounds;
        this.score = score;
        this.curr_datetime = curr_datetime;
    }

    public GameResult() {
    }

    public int getWrong() {
        return wrong;
    }

    public void setWrong(int wrong) {
        this.wrong = wrong;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getRounds() {
        return rounds;
    }

    public void setRounds(int rounds) {
        this.rounds = rounds;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCurr_datetime() {
        return curr_datetime;
    }

    public void setCurr_datetime(String curr_datetime) {
        this.curr_datetime = curr_datetime;
    }

    @Override
    public String toString() {
        return "GameResult{" +
                "id=" + id +
                ", wrong=" + wrong +
                ", correct=" + correct +
                ", rounds=" + rounds +
                ", score=" + score +
                ", curr_datetime='" + curr_datetime + '\'' +
                '}';
    }
}
