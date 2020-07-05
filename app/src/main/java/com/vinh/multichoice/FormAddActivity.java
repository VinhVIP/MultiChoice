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
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

public class FormAddActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private LinearLayout mainLayout;

    private EditText edtQuestion, edtAnsA, edtAnsB, edtAnsC, edtAnsD, edtHd;
    private ImageView imgView, imgDeleteAttach;

    private Bitmap bitmap = null; // bitmap of imageView

    private Button btnAdd, btnSelectType, btnReset;
    private RadioButton radioA, radioB, radioC, radioD;

    private Database database;

    private String ques, ansA, ansB, ansC, ansD, hd;

    // default: Không xác định
    private int typeIDSelect = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_add);
        database = new Database(this);

        settingToolbar();

        mainLayout = findViewById(R.id.main_layout);

        edtQuestion = findViewById(R.id.edt_add_question);
        edtAnsA = findViewById(R.id.edt_add_ans_a);
        edtAnsB = findViewById(R.id.edt_add_ans_b);
        edtAnsC = findViewById(R.id.edt_add_ans_c);
        edtAnsD = findViewById(R.id.edt_add_ans_d);
        edtHd = findViewById(R.id.edt_hd);

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

        radioA = findViewById(R.id.radio_button_a);
        radioB = findViewById(R.id.radio_button_b);
        radioC = findViewById(R.id.radio_button_c);
        radioD = findViewById(R.id.radio_button_d);

        btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkRule()) {
                    Question q = new Question(typeIDSelect, ques, ansA, ansB, ansC, ansD, correctAnswer(), hd);
                    int id = database.addQuestion(q);
                    q.setId(id);

                    String imagePath = "";
                    if (bitmap != null) {
                        // example: 0_12.png
                        imagePath = "0" + "_" + id + ".png";
                        Helper.saveImageOnInternalStorage(FormAddActivity.this, imagePath, bitmap);
                        q.setImagePath(imagePath);

                        // update question
                        database.updateQuestion(q);
                    }
                    Snackbar snackbar = Snackbar.make(mainLayout, "Thêm thành công!", Snackbar.LENGTH_LONG)
                            .setAction("Reset", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    reset();
                                }
                            });
                    snackbar.show();
                } else {
                    Toast.makeText(FormAddActivity.this, "Chưa điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnReset = findViewById(R.id.btn_reset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });

        btnSelectType = findViewById(R.id.btn_select_type);
        btnSelectType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogType();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_image:
                getImageFromUri();
                break;
        }
        return super.onOptionsItemSelected(item);
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

    private void reset() {
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

    private void showDialogType() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn thể loại");

        final List<Type> listType = database.getAllTypes();
        final String[] list = new String[listType.size()];

        for (int i = 0; i < listType.size(); i++) {
            list[i] = listType.get(i).getName() + "(" + listType.get(i).getSize() + ")";
        }

        builder.setItems(list, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int index) {
                typeIDSelect = listType.get(index).getId();
                String s = list[index];
                btnSelectType.setText(listType.get(index).getName());
                Log.e("ID Type", typeIDSelect + "");
            }
        });
        builder.create();
        builder.show();
    }

}
