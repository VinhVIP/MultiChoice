package com.vinh.multichoice.online;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.GridView;
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
import com.vinh.multichoice.Helper;
import com.vinh.multichoice.InfoActivity;
import com.vinh.multichoice.ListTypeActivity;
import com.vinh.multichoice.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuActivity extends AppCompatActivity {

    Database database;

    GridView gridView;
    GridViewCustomAdapter arrayAdapter;
    List<Subject> subjectList = new ArrayList<>();
    List<Subject> oldSubjectList = new ArrayList<>();

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        database = new Database(this);

        settingToolbar();
        initDrawerLayout();
        settingGridView();

        if (Helper.isNetworkConnected(this)) {
            getListSubject();
        }
    }

    private void settingToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                switch (item.getItemId()) {
                    case R.id.nav_offline:
                        Intent in = new Intent(MenuActivity.this, ListTypeActivity.class);
                        Helper.MODE = Helper.MODE_EDIT;
                        startActivity(in);
                        break;
                    case R.id.nav_fanpage:
                        getLink("https://www.facebook.com/it.multimedia.club/");
                        break;
                    case R.id.nav_web:
                        getLink("https://itmc-ptithcm.github.io/");
                        break;
                    case R.id.nav_info:
                        showDialogInfo();
                        break;
                    case R.id.nav_exit:
                        finish();
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void settingGridView() {
        Log.e("Set Grid Adapter", "OK");
        oldSubjectList = database.getAllSubjects();
        gridView = findViewById(R.id.grid_view);
        arrayAdapter = new GridViewCustomAdapter(this, oldSubjectList, this);
        gridView.setAdapter(arrayAdapter);
    }

    private void notifyAllGridData() {
        arrayAdapter.notifyDataSetChanged();
    }

    private void syncData() {
        Log.e("Sync Data", "OK");

        for (Subject subject : subjectList) {
            if (!database.isSubjectExists(subject.getId())) {
                if (subject.getIconPath() != null && subject.getIconPath().length() > 0) {
                    subject.setIconPath(getImageIcon(subject));
                }
                database.addSubject(subject);
                Log.e("Subject", "add " + subject.getIconPath());

                oldSubjectList.add(subject);
                notifyAllGridData();
            } else {
                int id = subject.getId();
                Subject oldSubject = database.getSubject(id);
                if (subject.getUpdate() != oldSubject.getUpdate()) {
                    // update subject
                    // download image icon
                    if (subject.getIconPath() != null && subject.getIconPath().length() > 0) {
                        subject.setIconPath(getImageIcon(subject));
                    }
                    database.updateSubject(subject);
                    int position = oldSubjectList.indexOf(oldSubject);
                    oldSubjectList.set(position, subject);
                    notifyAllGridData();
                    Log.e("Subject", "update " + subject.getIconPath());
                }
            }
        }

    }

    private String getImageIcon(final Subject subject) {
        StorageReference reference = FirebaseStorage.getInstance().getReference().child(subject.getIconPath());
        File path = new File(getFilesDir(), "Vinh" + File.separator + "Images");
        if (!path.exists()) path.mkdirs();

        final File file = new File(path, "iconPath.png");
        if (file.exists()) file.delete();

        reference.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
                if (bitmap == null) Log.e("bitmap", "null");
                else Log.e("bitmap", "ok");
                Helper.saveImageOnInternalStorage(MenuActivity.this, "subject_" + subject.getId() + ".png", bitmap);
                Log.e("Download icon", "Ok");

                notifyAllGridData();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Download icon", "Tach");
            }
        });
        return "subject_" + subject.getId() + ".png";
    }

    private void getListSubject() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = firebaseDatabase.getReference().child("Subject");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                subjectList = new ArrayList<>();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Subject subject = ds.getValue(Subject.class);
                    subjectList.add(subject);
                }
                syncData();
//                Toast.makeText(MenuActivity.this, "Các môn học vừa được cập nhật thêm!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MenuActivity.this, "Can't load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getListExamFromSubject(int subjectId) {
        Intent in = new Intent(MenuActivity.this, ExamListActivity.class);
        in.putExtra("subject_id", subjectId);
        startActivity(in);
    }

    private void getLink(String url) {
        Intent in = new Intent(Intent.ACTION_VIEW);
        in.setData(Uri.parse(url));
        startActivity(in);
    }

    private void showDialogInfo() {
        Intent in = new Intent(this, InfoActivity.class);
        startActivity(in);
    }
}
