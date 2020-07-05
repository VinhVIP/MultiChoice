package com.vinh.multichoice;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.kexanie.library.MathView;

public class FormTestActivity extends AppCompatActivity implements View.OnClickListener {


    private Button btnPrev, btnNext, btnComplete;
    private MathView mathView, mathViewA, mathViewB, mathViewC, mathViewD, mathViewHd;
    private ImageView imgView;
    private RelativeLayout layoutA, layoutB, layoutC, layoutD;
    private LinearLayout layoutSelectAnswer;

    private ImageView imgBack;
    private TextView tvProgress, tvSelectAnswer;
    private TextView titleA, titleB, titleC, titleD, txtTime;
    private CountDownTimer timer;
    private Database database;

    private int index = 0;
    private List<Question> list;
    private int[] answers;

    public boolean isTesting = true;

    private RecyclerView recyclerListQuestion;
    private ListQuestionAdapter listQuestionAdapter;
    private List<Integer> listQuesNumber;

    private int timeTest = 0;

    private Handler handlerNextQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_test);

        database = new Database(this);

        mathView = findViewById(R.id.mathView);
        mathViewA = findViewById(R.id.math_view_a);
        mathViewB = findViewById(R.id.math_view_b);
        mathViewC = findViewById(R.id.math_view_c);
        mathViewD = findViewById(R.id.math_view_d);
        mathViewHd = findViewById(R.id.math_view_hd);

        mathView.config(
                "MathJax.Hub.Config({\n" +
                        "  CommonHTML: { linebreaks: { automatic: true } }" +
                        "  \"HTML-CSS\": { linebreaks: { automatic: true } }" +
                        "         SVG: { linebreaks: { automatic: true } }" +
                        "});");

        imgView = findViewById(R.id.img_view);

        layoutSelectAnswer = findViewById(R.id.layout_select_answer);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTesting) {
                    showDialogConfirmBack();
                } else {
                    finish();
                }
            }
        });

        tvProgress = findViewById(R.id.question_process);
        tvSelectAnswer = findViewById(R.id.txt_select_answer);
        btnPrev = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnComplete = findViewById(R.id.btnComplete);
        btnPrev.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnComplete.setOnClickListener(this);

        layoutA = findViewById(R.id.layout_a);
        layoutB = findViewById(R.id.layout_b);
        layoutC = findViewById(R.id.layout_c);
        layoutD = findViewById(R.id.layout_d);
        layoutA.setOnClickListener(this);
        layoutB.setOnClickListener(this);
        layoutC.setOnClickListener(this);
        layoutD.setOnClickListener(this);

        titleA = findViewById(R.id.title_a);
        titleB = findViewById(R.id.title_b);
        titleC = findViewById(R.id.title_c);
        titleD = findViewById(R.id.title_d);

        titleA.setOnClickListener(this);
        titleB.setOnClickListener(this);
        titleC.setOnClickListener(this);
        titleD.setOnClickListener(this);

        getData();
        setDataIndex();

        listQuesNumber = new ArrayList<>();
        for (int i = 1; i <= list.size(); i++) {
            listQuesNumber.add(i);
        }


        listQuestionAdapter = new ListQuestionAdapter(this, listQuesNumber, this);
        recyclerListQuestion = findViewById(R.id.recycler_list_questions);
        recyclerListQuestion.setAdapter(listQuestionAdapter);
        recyclerListQuestion.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        startCountDownTimer();
    }

    private void startCountDownTimer() {
        txtTime = findViewById(R.id.txt_time);
        final int min = getIntent().getIntExtra(Helper.COUNT_DOWN_TIMER, 0);
        timeTest = min;

        if (min == 0) {
            txtTime.setText("Time sleep");
            return;
        }

        timer = new CountDownTimer(min * 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long t = millisUntilFinished / 1000;
                long minute = t / 60;
                long second = t % 60;
                txtTime.setText((minute < 10 ? "0" : "") + minute + ":" + (second < 10 ? "0" : "") + second);
            }

            @Override
            public void onFinish() {
                timer.cancel();
                testCompleted();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        if (isTesting) {
            showDialogConfirmBack();
        } else {
            finish();
        }

    }

    public int getQuestionIndex() {
        return index;
    }

    public int getAnswer(int ind) {
        return answers[ind];
    }

    public int getCorrectAnswer(int ind) {
        return list.get(ind).getCorrect();
    }

    public void changeQuestion(int newIndex) {
        index = newIndex;
        setDataIndex();
        invalidateRecyclerView();
        recyclerListQuestion.smoothScrollToPosition(index);
    }

    public void invalidateRecyclerView() {
        listQuestionAdapter.notifyDataSetChanged();
        recyclerListQuestion.invalidate();
    }

    private void getData() {
        list = Helper.listTestQuestion;
        answers = new int[list.size()];
        for (int i = 0; i < answers.length; i++) {
            answers[i] = -1;
        }
        boolean isRandomAnsIndex = getIntent().getBooleanExtra(Helper.RANDOM_ANSWERS_INDEX, false);
        if (isRandomAnsIndex) randomAnswersIndex();
    }

    private void randomAnswersIndex() {
        List<String> listAnswers, listAnswersAfter;

        for (int i = 0; i < list.size(); i++) {
            Question q = list.get(i);
            String correctAns = "";

            switch (q.getCorrect()) {
                case 0:
                    correctAns = q.getAnswerA();
                    break;
                case 1:
                    correctAns = q.getAnswerB();
                    break;
                case 2:
                    correctAns = q.getAnswerC();
                    break;
                case 3:
                    correctAns = q.getAnswerD();
                    break;
            }
            listAnswers = new ArrayList<>();
            listAnswersAfter = new ArrayList<>();

            listAnswers.add(q.getAnswerA());
            listAnswers.add(q.getAnswerB());
            listAnswers.add(q.getAnswerC());
            listAnswers.add(q.getAnswerD());

            while (!listAnswers.isEmpty()) {
                int ind = Helper.rand(listAnswers.size());
                listAnswersAfter.add(listAnswers.get(ind));
                listAnswers.remove(ind);
            }

            q.setAnswerA(listAnswersAfter.get(0));
            q.setAnswerB(listAnswersAfter.get(1));
            q.setAnswerC(listAnswersAfter.get(2));
            q.setAnswerD(listAnswersAfter.get(3));

            if (correctAns.equals(q.getAnswerA())) q.setCorrect(0);
            else if (correctAns.equals(q.getAnswerB())) q.setCorrect(1);
            else if (correctAns.equals(q.getAnswerC())) q.setCorrect(2);
            else q.setCorrect(3);

            list.set(i, q);
        }
    }

    private void setDataIndex() {
        Question currentQues = list.get(index);

        tvProgress.setText(index + 1 + "/" + list.size());

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

        if (isTesting) {
            mathViewHd.setVisibility(View.INVISIBLE);
            layoutSelectAnswer.setVisibility(View.GONE);
            // show the people's answer
            backgroundAnswers();
        } else {
            // show result
            if (answers[index] != -1)
                tvSelectAnswer.setText("Bạn chọn đáp án " + (char) ('A' + answers[index]));
            else tvSelectAnswer.setText("Bạn không chọn đáp án nào");
            layoutSelectAnswer.setVisibility(View.VISIBLE);
            mathViewHd.setVisibility(View.VISIBLE);
            result(currentQues);
        }
    }

    private void result(Question currentQues) {
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

    private void testCompleted() {
        isTesting = false;
        changeQuestion(0);
        btnComplete.setVisibility(View.INVISIBLE);

        showDialogResult();
    }

    private void stopHandler() {
        if (handlerNextQuestion != null) handlerNextQuestion.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View view) {
        stopHandler();

        switch (view.getId()) {
            case R.id.btnPrevious:
                if (index != Math.max(0, index - 1)) {
                    changeQuestion(Math.max(0, index - 1));
                    invalidateRecyclerView();
                    setDataIndex();
                }
                break;
            case R.id.btnNext:
                if (index != Math.min(list.size() - 1, index + 1)) {
                    changeQuestion(Math.min(list.size() - 1, index + 1));
                    invalidateRecyclerView();
                    setDataIndex();
                }
                break;
            case R.id.btnComplete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bạn muốn nộp bài bây giờ ?");
                builder.setPositiveButton("Nộp bài", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        timer.onFinish();
                    }
                });
                builder.setNegativeButton("Không", null);
                builder.create().show();
                break;
        }

        if (isTesting) {
            if (view.getId() == R.id.layout_a || view.getId() == R.id.title_a) {
                answers[index] = (answers[index] == 0 ? -1 : 0);
            } else if (view.getId() == R.id.layout_b || view.getId() == R.id.title_b) {
                answers[index] = (answers[index] == 1 ? -1 : 1);
            } else if (view.getId() == R.id.layout_c || view.getId() == R.id.title_c) {
                answers[index] = (answers[index] == 2 ? -1 : 2);
            } else if (view.getId() == R.id.layout_d || view.getId() == R.id.title_d) {
                answers[index] = (answers[index] == 3 ? -1 : 3);
            }

            backgroundAnswers();
            if (index + 1 < list.size() && answers[index] != -1) {
                handlerNextQuestion = new Handler();
                handlerNextQuestion.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        changeQuestion(index + 1);
                    }
                }, 500);
            }
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

    private int countNumQuestionsCorrect() {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (answers[i] == list.get(i).getCorrect()) count++;
        }
        return count;
    }

    private String timePerQuestion() {
        int numQuesAnswered = 0;
        for (int i = 0; i < list.size(); i++) {
            if (answers[i] != -1) numQuesAnswered++;
        }
        if (numQuesAnswered == 0) {
            return "0";
        }

        String[] strs = txtTime.getText().toString().split(":");
        int m = Integer.parseInt(strs[0]);
        int s = Integer.parseInt(strs[1]);
        int timeUsed = timeTest * 60 - m * 60 - s;

        int speed = timeUsed / numQuesAnswered;
        int min = speed / 60;
        int sec = speed % 60;
        if (min < 1) {
            return sec + "s/câu";
        } else {
            if (sec == 0) {
                return min + "p/câu";
            } else {
                sec = sec * 10 / 60;
                return min + "p" + sec + "s/câu";
            }
        }
    }

    private double mark() {
        double markPerQues = 10.0 / list.size();
        double mark = markPerQues * countNumQuestionsCorrect();
        int m = (int) (mark * 100);
        mark = m;
        mark /= 100;
        return mark;
    }

    private void showDialogResult() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_result);

        TextView txtNumCorrect = dialog.findViewById(R.id.txt_num_correct);
        TextView txtSpeed = dialog.findViewById(R.id.txt_speed);
        TextView txtMark = dialog.findViewById(R.id.txt_mark);

        txtMark.setText(String.valueOf(mark()));
        txtNumCorrect.setText("Đúng " + countNumQuestionsCorrect() + "/" + list.size() + " câu");
        txtSpeed.setText("Tốc độ trung bình: " + timePerQuestion());

        Button btnBack = dialog.findViewById(R.id.btn_back);
        Button btnReview = dialog.findViewById(R.id.btn_review);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                finish();
            }
        });

        btnReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        dialog.create();
        dialog.show();
    }

    private void showDialogConfirmBack() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Thoát kiểm tra?")
                .setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        timer.cancel();
                        finish();
                    }
                }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }
}
