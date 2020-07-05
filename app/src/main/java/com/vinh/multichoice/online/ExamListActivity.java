package com.vinh.multichoice.online;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.vinh.multichoice.Database;
import com.vinh.multichoice.FormTestActivity;
import com.vinh.multichoice.Helper;
import com.vinh.multichoice.Question;
import com.vinh.multichoice.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ExamListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Exam> examList = new ArrayList<>();    // the exams get from server (firebase)
    private List<Exam> oldExamList = new ArrayList<>();   // the exams in SQLite
    private List<Question> questions = new ArrayList<>();
    private ExamAdapter examAdapter;

    private int subjectId;

    private Database database;
    private Toolbar toolbar;
    private HashMap<String, Bitmap> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);

        database = new Database(this);
        settingToolbar();

        getData();
        settingList();

        if (Helper.isNetworkConnected(this)) {
            // if the internet connected, get exams from server
            getListExam();
        }
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

    private void getData() {
        subjectId = getIntent().getIntExtra("subject_id", -1);
        Subject subject = database.getSubject(subjectId);
        getSupportActionBar().setTitle(subject.getName());
    }

    private void settingList() {
        oldExamList = database.getListExams(subjectId);

        recyclerView = findViewById(R.id.recycler_view);
        examAdapter = new ExamAdapter(this, oldExamList, this);
        recyclerView.setAdapter(examAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void syncData() {
        // get data from server and sync with QSLite
        for (Exam exam : examList) {
            if (!database.isExamExists(exam.getId())) {
                // if not exists in SQLite, add it
                database.addExam(exam);
            } else {
                int id = exam.getId();
                Exam oldExam = database.getExam(exam.getId());
                if (exam.getUpdate() != oldExam.getUpdate()) {
                    // Exam updated, so reset the variable downloaded
                    exam.setDownloaded(false);
                    database.updateExam(exam);
                }
            }
        }
        settingList();
    }

    private void getListExam() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Exam");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                examList = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Exam exam = ds.getValue(Exam.class);

                    // many exams, so we find exams have subjectId we want
                    if (exam != null && exam.getSubject_id() == subjectId) {
                        examList.add(exam);
                    }
                }

                syncData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ExamListActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void analyzeDataZipFile(File zipFile, Exam exam) {
        hashMap = new HashMap<>();

        try {
            FileInputStream fin = new FileInputStream(zipFile);
            ZipInputStream zip = new ZipInputStream(fin);
            ZipEntry zipEntry;
            while ((zipEntry = zip.getNextEntry()) != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[2048];
                int length;
                while ((length = zip.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }
                byte[] bytes = baos.toByteArray();
                String fileName = zipEntry.getName();
                Log.e("File Name", fileName);

                if (fileName.equals("list.txt")) {
                    String listContent = new String(bytes);
                    questions = Helper.listQuestionImportFromFile(listContent);
                } else {
                    // this is image file, so we put them to HashMap
                    // image name example: 5.png ,  12.png
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    hashMap.put(fileName, bitmap);
                }
            }
            // add question to SQLite database
            for (Question q : questions) {
                q.setType(exam.getId());
                int ques_id = database.addOnlineQuestion(q);
                q.setId(ques_id);

                Log.e("path", q.getImagePath());

                if (q.getImagePath() != null && q.getImagePath().trim().length() > 0) {
                    if (hashMap.containsKey(q.getImagePath())) {
                        // online database: image name example: 1_1.png , 1_2.png
                        String nameBitmapSave = "1_" + ques_id + ".png";
                        Helper.saveImageOnInternalStorage(this, nameBitmapSave, hashMap.get(q.getImagePath()));

                        // update question
                        q.setImagePath(nameBitmapSave);
                        database.updateOnlineQuestion(q);

                        Log.e("path", q.getImagePath());
                    }
                }
            }
            Toast.makeText(this, "Đã thêm", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getFile(final Exam exam) {
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(exam.getUrl());
        File path = new File(getFilesDir(), "Vinh" + File.separator + "Zip");
        if (!path.exists()) path.mkdirs();

        final File file = new File(path, "exam.zip");
        if (file.exists()) file.delete();
        reference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                analyzeDataZipFile(file, exam);
                Log.e("Server", "ok duoc");
                Toast.makeText(ExamListActivity.this, "Đã tải xong, bây giờ bạn có thể làm offline", Toast.LENGTH_SHORT).show();

                // if exam has been downloaded, we can do the exam offline
                exam.setDownloaded(true);
                database.updateExam(exam);
                examAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Server", "K tai duoc");
            }
        });

/*
        reference.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Toast.makeText(ExamListActivity.this, "Đã tải xong", Toast.LENGTH_SHORT).show();
                String fileContent = new String(bytes);

                Helper.listTestQuestion = Helper.listQuestionImportFromFile(fileContent);

                for (Question q : Helper.listTestQuestion) {
                    if (q.getCorrect() == -1) {
                        Toast.makeText(ExamListActivity.this, "Câu hỏi lỗi", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    q.setType(exam.getId());
                    if (!database.isOnlineQuestionExists(q.getId(), exam.getId())) {
                        int quesid = database.addOnlineQuestion(q);
//                        Toast.makeText(ExamListActivity.this, "Đa them " + quesid, Toast.LENGTH_SHORT).show();
                    }
                }

                Toast.makeText(ExamListActivity.this, "Đã tải xong, bây giờ bạn có thể làm offline", Toast.LENGTH_SHORT).show();

                // if exam has been downloaded, we can do the exam offline
                exam.setDownloaded(true);
                database.updateExam(exam);
                examAdapter.notifyDataSetChanged();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ExamListActivity.this, "Đề thi đang bị lỗi", Toast.LENGTH_SHORT).show();

            }
        });
        */

    }

    public void downloadExam(Exam exam) {
        if (exam.isDownloaded()) {
            moveToFormTest(exam);
        } else if (Helper.isNetworkConnected(this)) {
            if (database.isExamExists(exam.getId())) {
                // clear all old question and add new question
                // because the exam updated
                List<Question> clearQuestions = database.getAllOnlineQuestionsByType(exam.getId());
                for (Question q : clearQuestions) {
                    database.deleteOnlineQuestion(q.getId());
                }
            }

            // then download and add new questions list
            Toast.makeText(this, "Đang tải đề thi", Toast.LENGTH_SHORT).show();
            getFile(exam);
        } else {
            Toast.makeText(this, "Không có kết nối mạng để tải", Toast.LENGTH_SHORT).show();
        }
    }

    private void moveToFormTest(Exam exam) {
        // TESTING NOW
        Helper.listTestQuestion = database.getAllOnlineQuestionsByType(exam.getId());

        if (Helper.listTestQuestion.size() == 0) {
            Toast.makeText(this, "Không có câu hỏi", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(ExamListActivity.this, FormTestActivity.class);
        // random answer index
        intent.putExtra(Helper.RANDOM_ANSWERS_INDEX, false);
        intent.putExtra(Helper.COUNT_DOWN_TIMER, exam.getTime());
        startActivity(intent);
    }
}

