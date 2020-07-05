package com.vinh.multichoice;

import android.support.annotation.NonNull;

public class Question {
    private int id, type;
    private String question, answerA, answerB, answerC, answerD;
    private int correct;
    private String hd;
    private String imagePath;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswerA() {
        return answerA;
    }

    public void setAnswerA(String answerA) {
        this.answerA = answerA;
    }

    public String getAnswerB() {
        return answerB;
    }

    public void setAnswerB(String answerB) {
        this.answerB = answerB;
    }

    public String getAnswerC() {
        return answerC;
    }

    public void setAnswerC(String answerC) {
        this.answerC = answerC;
    }

    public String getAnswerD() {
        return answerD;
    }

    public void setAnswerD(String answerD) {
        this.answerD = answerD;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public String getHd() {
        return hd;
    }

    public void setHd(String hd) {
        this.hd = hd;
    }

    public Question(String question, String answerA, String answerB, String answerC, String answerD, int correct, String hd) {
        this.question = question;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correct = correct;
        this.hd = hd;
    }

    public Question(String question, String imagePath, String answerA, String answerB, String answerC, String answerD, int correct, String hd) {
        this.question = question;
        this.imagePath = imagePath;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correct = correct;
        this.hd = hd;
    }

    public Question(int type, String question, String answerA, String answerB, String answerC, String answerD, int correct, String hd) {
        this.type = type;
        this.question = question;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correct = correct;
        this.hd = hd;
    }

    public Question(int id, int type, String question, String imagePath, String answerA, String answerB, String answerC, String answerD, int correct, String hd) {
        this.id = id;
        this.type = type;
        this.question = question;
        this.imagePath = imagePath;
        this.answerA = answerA;
        this.answerB = answerB;
        this.answerC = answerC;
        this.answerD = answerD;
        this.correct = correct;
        this.hd = hd;
    }

    @NonNull
    @Override
    public String toString() {
        return question
                + "\nA. " + answerA
                + "\nB. " + answerB
                + "\nC. " + answerC
                + "\nD. " + answerD
                + "\n" + "Đáp án: " + (char) ('A' + correct)
                + "\n" + hd;
    }
}
