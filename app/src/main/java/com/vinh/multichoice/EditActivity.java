package com.vinh.multichoice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private TextView txtQuesProcess;
    private EditText edtQuestion, edtAnsA, edtAnsB, edtAnsC, edtAnsD, edtHd;
    private ImageView imgView, imgDeleteAttach;
    private Bitmap bitmap = null;
    private Button btnEdit, btnPrevious, btnNext, btnReset;
    private RadioButton radioA, radioB, radioC, radioD;

    private Database database;

    private String ques, ansA, ansB, ansC, ansD, hd;
    private int typeIDSelect = -1, index = 0;
    private boolean isChanged = false;

    private List<Question> listQuestions;
    private Type currentType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        database = new Database(this);

        settingToolbar();

        imgView = findViewById(R.id.img_view);
        imgDeleteAttach = findViewById(R.id.btn_delete_image_attach);
        setImageAttach();
        imgDeleteAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bitmap != null) bitmap = null;
                setImageAttach();
            }
        });

        txtQuesProcess = findViewById(R.id.question_process);
        edtQuestion = findViewById(R.id.edt_add_question);
        edtAnsA = findViewById(R.id.edt_add_ans_a);
        edtAnsB = findViewById(R.id.edt_add_ans_b);
        edtAnsC = findViewById(R.id.edt_add_ans_c);
        edtAnsD = findViewById(R.id.edt_add_ans_d);
        edtHd = findViewById(R.id.edt_hd);

        radioA = findViewById(R.id.radio_button_a);
        radioB = findViewById(R.id.radio_button_b);
        radioC = findViewById(R.id.radio_button_c);
        radioD = findViewById(R.id.radio_button_d);

        btnReset = findViewById(R.id.btn_reset);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnEdit = findViewById(R.id.btn_add);
        btnEdit.setText("Sửa");

        btnPrevious.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        getListQuestionFromType();
        setDataIndex();
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
                if (isChanged) {
                    setResult(Helper.RESULT_CHANGE);
                } else {
                    setResult(Helper.RESULT_CANCEL);
                }
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_image:
                getImageFromUri();
                break;
            case R.id.action_delete_question:
                Question question = listQuestions.get(index);
                showDialogConfirmDelete(question.getId());
                break;
            case R.id.action_move_question:
                showDialogType();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 696:
                if (resultCode == RESULT_OK && data != null) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        setImageAttach();
                        Log.e("Add Image", "Successfully");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getImageFromUri() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 696);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setImageAttach() {
        if (bitmap == null) {
            imgDeleteAttach.setVisibility(View.GONE);
            imgView.setVisibility(View.GONE);
        } else {
            imgDeleteAttach.setVisibility(View.VISIBLE);
            imgView.setVisibility(View.VISIBLE);
            imgView.setImageBitmap(bitmap);
        }
    }

    private void getListQuestionFromType() {
        Intent in = getIntent();
        listQuestions = new ArrayList<>();

        if (in.hasExtra(Helper.DEF_TYPE_ID)) {
            int typeID = in.getIntExtra(Helper.DEF_TYPE_ID, -1);
            typeIDSelect = typeID;
            currentType = database.getType(typeID);
            listQuestions = database.getAllQuestionsByType(typeID);

            getSupportActionBar().setTitle(currentType.getName());
        }
    }

    private void setDataIndex() {
        Question currentQues = listQuestions.get(index);

        txtQuesProcess.setText(index + 1 + "/" + listQuestions.size());

        if (currentQues.getImagePath() != null && currentQues.getImagePath().length() > 0) {
            bitmap = Helper.getImageBitmapOnInternalStorage(this, currentQues.getImagePath());
        } else {
            bitmap = null;
        }

        setImageAttach();

        edtQuestion.setText(currentQues.getQuestion());
        edtAnsA.setText(currentQues.getAnswerA());
        edtAnsB.setText(currentQues.getAnswerB());
        edtAnsC.setText(currentQues.getAnswerC());
        edtAnsD.setText(currentQues.getAnswerD());

        switch (currentQues.getCorrect()) {
            case 0:
                radioA.setChecked(true);
                break;
            case 1:
                radioB.setChecked(true);
                break;
            case 2:
                radioC.setChecked(true);
                break;
            default:
                radioD.setChecked(true);
                break;
        }

        edtHd.setText(currentQues.getHd());
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnPrevious:
                index = Math.max(index - 1, 0);
                setDataIndex();
                break;
            case R.id.btnNext:
                index = Math.min(index + 1, listQuestions.size() - 1);
                setDataIndex();
                break;
            case R.id.btn_add:
                // update Question
                if (checkRule()) {
                    int id = listQuestions.get(index).getId();
                    String imagePath = "";

                    Question q = new Question(id, typeIDSelect, ques, imagePath, ansA, ansB, ansC, ansD, correctAnswer(), hd);

                    if (bitmap != null) {
                        // example: 0_12.png
                        imagePath = "0" + "_" + id + ".png";
                        Helper.saveImageOnInternalStorage(EditActivity.this, imagePath, bitmap);
                        q.setImagePath(imagePath);
                    }

                    database.updateQuestion(q);
                    listQuestions.set(index, q);
                    Toast.makeText(EditActivity.this, "Sửa thành công ", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditActivity.this, "Chưa điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_reset:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Reset các ô về mặc định ?")
                        .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                edtQuestion.setText("");
                                edtAnsA.setText("");
                                edtAnsB.setText("");
                                edtAnsC.setText("");
                                edtAnsD.setText("");
                                edtHd.setText("");
                                radioA.setChecked(true);
                                bitmap = null;
                                setImageAttach();
                            }
                        }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
                break;
        }
    }

    private boolean checkRule() {
        if (edtQuestion.getText().toString().length() == 0
                || edtAnsA.getText().toString().length() == 0
                || edtAnsB.getText().toString().length() == 0
                || edtAnsC.getText().toString().length() == 0
                || edtAnsD.getText().toString().length() == 0
        ) {
            return false;
        }

        ques = edtQuestion.getText().toString().trim();
        ansA = edtAnsA.getText().toString().trim();
        ansB = edtAnsB.getText().toString().trim();
        ansC = edtAnsC.getText().toString().trim();
        ansD = edtAnsD.getText().toString().trim();
        hd = edtHd.getText().toString().trim();

        return true;
    }

    private int correctAnswer() {
        if (radioA.isChecked()) return 0;
        if (radioB.isChecked()) return 1;
        if (radioC.isChecked()) return 2;
        return 3;
    }

    private void showDialogType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn thể loại");

        final List<Type> listType = database.getAllTypes();
        for (int i = 0; i < listType.size(); i++) {
            Type type = listType.get(i);
            if (type.getId() == currentType.getId()) {
                listType.remove(i);
                break;
            }
        }

        final String[] list = new String[listType.size()];

        for (int i = 0; i < listType.size(); i++) {
            list[i] = listType.get(i).getName() + "(" + listType.get(i).getSize() + ")";
        }

        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {
                int typeIDMove = listType.get(index).getId();
                showDialogConfirmMove(typeIDMove);
                typeIDSelect = listType.get(index).getId();
            }
        });
        builder.create();
        builder.show();
    }

    private void showDialogConfirmMove(final int typeIDMove) {
        Type typeMove = database.getType(typeIDMove);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Di chuyển tới " + typeMove.getName());
        builder.setPositiveButton("Di chuyển", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Question currentQuestion = listQuestions.get(index);
                currentQuestion.setType(typeIDMove);
                database.updateQuestion(currentQuestion);
                listQuestions.remove(index);
                updateScreen();
                Toast.makeText(EditActivity.this, "Đã di chuyển", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.create();
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (isChanged) {
            setResult(Helper.RESULT_CHANGE);
        } else {
            setResult(Helper.RESULT_CANCEL);
        }
        finish();
        super.onBackPressed();
    }

    private void updateScreen() {
        if (listQuestions == null || listQuestions.size() == 0) {
            setResult(Helper.RESULT_CHANGE);
            finish();
        } else {
            if (listQuestions.size() <= index) {
                index = listQuestions.size() - 1;
            }
            setDataIndex();
        }
    }

    private void showDialogConfirmDelete(final int ques_id) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Xác nhận xóa?");

        builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                database.deleteQuestion(ques_id);
                listQuestions.remove(index);
                isChanged = true;
                updateScreen();
                Toast.makeText(EditActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create();
        builder.show();
    }
}
