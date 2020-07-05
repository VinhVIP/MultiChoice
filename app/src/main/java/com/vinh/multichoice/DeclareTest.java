package com.vinh.multichoice;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DeclareTest extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private Button btnSelectType, btnAddToList, btnTest;
    private EditText edtNumQues, edtMinute;
    private CheckBox cbMixAns;

    private RecyclerView recyclerView;
    private List<Type> list;
    private TestTypeAdapter adapter;

    private Database database;

    private int typeIDSelect = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_declare_test);

        database = new Database(this);

        settingToolbar();

        btnSelectType = findViewById(R.id.btnSelectType);
        btnAddToList = findViewById(R.id.btnAddToList);
        btnTest = findViewById(R.id.btnTest);

        btnSelectType.setOnClickListener(this);
        btnAddToList.setOnClickListener(this);
        btnTest.setOnClickListener(this);

        edtNumQues = findViewById(R.id.edtNumQues);
        edtMinute = findViewById(R.id.edtMinute);

        cbMixAns = findViewById(R.id.checkMixAns);

        recyclerView = findViewById(R.id.recycler_type_select);
        list = new ArrayList<>();
        adapter = new TestTypeAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSelectType:
                showDialogType();
                break;
            case R.id.btnAddToList:
                if (!existsTypeInList()) {
                    Type type = database.getType(typeIDSelect);
                    list.add(type);
                    adapter.notifyItemInserted(list.size() - 1);
                } else {
                    Toast.makeText(this, "Đã thêm thể loại này!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnTest:
                if (!check()) {
                    Toast.makeText(this, "Số lượng câu hỏi không khớp", Toast.LENGTH_SHORT).show();
                } else if (!checkTime()) {
                    Toast.makeText(this, "Thời gian không hợp lệ", Toast.LENGTH_SHORT).show();
                } else {
                    prepareListQuestion();
                    Intent in = new Intent(this, FormTestActivity.class);
                    in.putExtra(Helper.RANDOM_ANSWERS_INDEX, cbMixAns.isChecked());

                    int min = Integer.parseInt(edtMinute.getText().toString());
                    in.putExtra(Helper.COUNT_DOWN_TIMER, min);

                    startActivity(in);
                }
                break;
        }
    }

    private boolean check() {
        if (edtNumQues.getText().toString().trim().length() == 0) return false;

        list = adapter.list;
        int totalQuestion = Integer.parseInt(edtNumQues.getText().toString());
        int sum = 0;
        for (Type type : list) {
            sum += type.getSelect();
        }
        return totalQuestion == sum && sum > 0;
    }

    private boolean checkTime() {
        if (edtMinute.getText().toString().trim().length() == 0) {
            return false;
        }
        int min = Integer.parseInt(edtMinute.getText().toString());
        return min > 0;
    }

    private void prepareListQuestion() {
        list = adapter.list;
        List<Question> listQues = new ArrayList<>();
        List<Question> listTmp = new ArrayList<>();

        for (Type type : list) {
            if (type.getSelect() > 0)
                listTmp.addAll(Helper.randomQuestions(database, type.getId(), type.getSelect()));
        }

        int numQues = listTmp.size();
        for (int i = 0; i < numQues; i++) {
            int ind = Helper.rand(listTmp.size());
            listQues.add(listTmp.get(ind));
            listTmp.remove(ind);
        }
        Helper.listTestQuestion = listQues;
    }

    public void removeTypeFromList(int position) {
        list.remove(position);
        adapter.notifyDataSetChanged();
    }

    private boolean existsTypeInList() {
        for (Type type : list) {
            if (type.getId() == typeIDSelect) return true;
        }
        return false;
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
//                Toast.makeText(FormAddActivity.this, typeIDSelect + "", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create();
        builder.show();
    }
}
