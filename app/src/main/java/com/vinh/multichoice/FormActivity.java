package com.vinh.multichoice;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import io.github.kexanie.library.MathView;

public class FormActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnPrev, btnNext;
    private MathView mathView, mathViewA, mathViewB, mathViewC, mathViewD, mathViewHd;
    private ImageView imgView;
    private TextView tvProgress, tvSelectAnswer;
    private TextView titleA, titleB, titleC, titleD;
    private RelativeLayout layoutA, layoutB, layoutC, layoutD;
    private LinearLayout layoutSelectAnswer;

    private Database database;

    private Question currentQues;
    private List<Question> listQuestions;
    private int[] answers;
    private int index = 0;

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        settingToolbar();

        database = new Database(this);

        mathView = findViewById(R.id.mathView);
        mathViewA = findViewById(R.id.math_view_a);
        mathViewB = findViewById(R.id.math_view_b);
        mathViewC = findViewById(R.id.math_view_c);
        mathViewD = findViewById(R.id.math_view_d);
        mathViewHd = findViewById(R.id.math_view_hd);

        mathViewA.setOnClickListener(this);
        mathViewB.setOnClickListener(this);
        mathViewC.setOnClickListener(this);
        mathViewD.setOnClickListener(this);

        imgView = findViewById(R.id.img_view);

        layoutA = findViewById(R.id.layout_a);
        layoutB = findViewById(R.id.layout_b);
        layoutC = findViewById(R.id.layout_c);
        layoutD = findViewById(R.id.layout_d);
        layoutSelectAnswer = findViewById(R.id.layout_select_answer);
        layoutA.setOnClickListener(this);
        layoutB.setOnClickListener(this);
        layoutC.setOnClickListener(this);
        layoutD.setOnClickListener(this);


        tvProgress = findViewById(R.id.question_process);
        tvSelectAnswer = findViewById(R.id.txt_select_answer);
        btnPrev = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        titleA = findViewById(R.id.title_a);
        titleB = findViewById(R.id.title_b);
        titleC = findViewById(R.id.title_c);
        titleD = findViewById(R.id.title_d);

        titleA.setOnClickListener(this);
        titleB.setOnClickListener(this);
        titleC.setOnClickListener(this);
        titleD.setOnClickListener(this);

        getQuestions();
    }

    private void settingToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void getQuestions() {
        Intent intent = getIntent();
        if (intent.hasExtra(Helper.DEF_TYPE_ID)) {
            int typeID = intent.getIntExtra(Helper.DEF_TYPE_ID, -1);
            listQuestions = database.getAllQuestionsByType(typeID);

            answers = new int[listQuestions.size()];
            for (int i = 0; i < listQuestions.size(); i++) {
                answers[i] = -1;
            }
        }
        setDataIndex();
    }

    public void changeQuestion(int newIndex) {
        index = newIndex;
        setDataIndex();
    }

    private void setDataIndex() {
        currentQues = listQuestions.get(index);

        tvProgress.setText(index + 1 + "/" + listQuestions.size());

        mathView.setText(currentQues.getQuestion());
        mathViewA.setText(currentQues.getAnswerA());
        mathViewB.setText(currentQues.getAnswerB());
        mathViewC.setText(currentQues.getAnswerC());
        mathViewD.setText(currentQues.getAnswerD());
        mathViewHd.setText(currentQues.getHd());

        if (currentQues.getImagePath() != null && currentQues.getImagePath().length() > 0) {
            imgView.setImageBitmap(Helper.getImageBitmapOnInternalStorage(this, currentQues.getImagePath()));
        } else {
            imgView.setImageBitmap(null);
        }


        backgroundAnswers();
        if (answers[index] == -1) {
            mathViewHd.setText("");
            layoutSelectAnswer.setVisibility(View.GONE);
            mathViewHd.setVisibility(View.INVISIBLE);
        } else {
            mathViewHd.setText("");
            tvSelectAnswer.setText("Bạn chọn đáp án " + (char) ('A' + answers[index]));
            layoutSelectAnswer.setVisibility(View.VISIBLE);
            mathViewHd.setVisibility(View.VISIBLE);
            result();
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPrevious:
                if (index != Math.max(0, index - 1)) {
                    changeQuestion(Math.max(0, index - 1));
                    setDataIndex();
                }
                break;
            case R.id.btnNext:
                if (index != Math.min(listQuestions.size() - 1, index + 1)) {
                    changeQuestion(Math.min(listQuestions.size() - 1, index + 1));
                    setDataIndex();
                }
                break;
        }

        if (answers[index] == -1) {
            if (view.getId() == R.id.layout_a || view.getId() == R.id.title_a) {
                answers[index] = 0;
            } else if (view.getId() == R.id.layout_b || view.getId() == R.id.title_b) {
                answers[index] = 1;
            } else if (view.getId() == R.id.layout_c || view.getId() == R.id.title_c) {
                answers[index] = 2;
            } else if (view.getId() == R.id.layout_d || view.getId() == R.id.title_d) {
                answers[index] = 3;
            }
        }

        if (answers[index] != -1) {
            mathViewHd.setVisibility(View.GONE);
            tvSelectAnswer.setText("Bạn chọn đáp án " + (char) ('A' + answers[index]));
            layoutSelectAnswer.setVisibility(View.VISIBLE);
            result();
        }
    }

    private void backgroundAnswers() {
        switch (answers[index]) {
            case 0:
                userAnswerTitle(true, false, false, false);
                break;
            case 1:
                userAnswerTitle(false, true, false, false);
                break;
            case 2:
                userAnswerTitle(false, false, true, false);
                break;
            case 3:
                userAnswerTitle(false, false, false, true);
                break;
            default:
                userAnswerTitle(false, false, false, false);
                break;
        }
    }

    private void userAnswerTitle(boolean a, boolean b, boolean c, boolean d) {
        setBackgroundAnswer(titleA, true, a);
        setBackgroundAnswer(titleB, true, b);
        setBackgroundAnswer(titleC, true, c);
        setBackgroundAnswer(titleD, true, d);
    }

    private void setBackgroundAnswer(TextView textView, boolean isTesting, boolean changeBackground) {
        if (isTesting) {
            textView.setBackgroundResource(changeBackground ? R.drawable.item_title_selected : R.drawable.item_title);
            textView.setTextColor(changeBackground ? Color.WHITE : Color.BLACK);
        } else {
            textView.setBackgroundResource(changeBackground ? R.drawable.item_question_correct : R.drawable.item_question_incorrect);
            textView.setTextColor(Color.WHITE);
        }
    }

    private void result() {
        int res = currentQues.getCorrect();

        userAnswerTitle(false, false, false, false);

        switch (answers[index]) {
            case 0:
                setBackgroundAnswer(titleA, false, false);
                break;
            case 1:
                setBackgroundAnswer(titleB, false, false);
                break;
            case 2:
                setBackgroundAnswer(titleC, false, false);
                break;
            case 3:
                setBackgroundAnswer(titleD, false, false);
                break;
            default:
                break;
        }

        switch (res) {
            case 0:
                setBackgroundAnswer(titleA, false, true);
                break;
            case 1:
                setBackgroundAnswer(titleB, false, true);
                break;
            case 2:
                setBackgroundAnswer(titleC, false, true);
                break;
            case 3:
                setBackgroundAnswer(titleD, false, true);
                break;
            default:
                break;
        }

    }

}
