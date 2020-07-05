package com.vinh.multichoice;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ListTypeActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView recyclerViewType;
    private ListTypeAdapter typeAdapter;
    private FloatingActionButton fabMenu, fabAddQues, fabAddType;
    private TextView txtAddQues, txtAddType;
    private Database database;

    List<Type> listType;

    private int typeIDWrite;
    private boolean isRotate = false;
    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_type);

        database = new Database(this);

        settingToolbar();
        initDrawerLayout();

        listType = database.getAllTypes();
        recyclerViewType = findViewById(R.id.recycler_view_type);
        typeAdapter = new ListTypeAdapter(this, listType, this);
        recyclerViewType.setAdapter(typeAdapter);
        recyclerViewType.setLayoutManager(new LinearLayoutManager(this));

        txtAddQues = findViewById(R.id.txt_add_ques);
        txtAddType = findViewById(R.id.txt_add_type);

        fabAddType = findViewById(R.id.fab_add_type);
        fabAddType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFab();
                showDialogAddType();
            }
        });

        fabAddQues = findViewById(R.id.fab_add_ques);
        fabAddQues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFab();
                Intent in = new Intent(ListTypeActivity.this, FormAddActivity.class);
                startActivityForResult(in, Helper.REQUEST_CODE_EDIT);
            }
        });

        ViewAnimation.init(fabAddQues);
        ViewAnimation.init(fabAddType);
        ViewAnimation.init(txtAddQues);
        ViewAnimation.init(txtAddType);

        fabMenu = findViewById(R.id.fab_menu);
        fabMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRotate) {
                    hideFab();
                } else {
                    showFab();
                }
            }
        });

        fabMenu.setVisibility(Helper.MODE == Helper.MODE_EDIT ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_type, menu);
        return true;
    }

    private void showFab(){
        isRotate = ViewAnimation.rotateFab(fabMenu, true);
        ViewAnimation.showIn(fabAddQues);
        ViewAnimation.showIn(fabAddType);
        ViewAnimation.showIn(txtAddQues);
        ViewAnimation.showIn(txtAddType);
    }

    private void hideFab(){
        isRotate = ViewAnimation.rotateFab(fabMenu, false);
        ViewAnimation.showOut(fabAddQues);
        ViewAnimation.showOut(fabAddType);
        ViewAnimation.showOut(txtAddQues);
        ViewAnimation.showOut(txtAddType);
    }
    private void settingToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_change:
                if (Helper.MODE == Helper.MODE_EDIT) changeToPracticeMode();
                else changeToEditMode();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeToEditMode(){
        getSupportActionBar().setTitle("Chỉnh sửa");
        Helper.MODE = Helper.MODE_EDIT;
        fabMenu.setVisibility(View.VISIBLE);
        typeAdapter = new ListTypeAdapter(ListTypeActivity.this, listType, ListTypeActivity.this);
        recyclerViewType.setAdapter(typeAdapter);
    }

    private void changeToPracticeMode(){
        getSupportActionBar().setTitle("Luyện tập");
        Helper.MODE = Helper.MODE_PRACTICE;
        if (isRotate) hideFab();
        fabMenu.setVisibility(View.INVISIBLE);
        typeAdapter = new ListTypeAdapter(ListTypeActivity.this, listType, ListTypeActivity.this);
        recyclerViewType.setAdapter(typeAdapter);
    }

    private void initDrawerLayout() {
        drawerLayout = findViewById(R.id.drawer_layout_offline);
        NavigationView navigationView = findViewById(R.id.nav_view_offline);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                Intent in;
                switch (item.getItemId()) {
                    case R.id.nav_online:
                        finish();
                        break;
                    case R.id.nav_test:
                        in = new Intent(ListTypeActivity.this, DeclareTest.class);
                        startActivity(in);
                        break;
                    case R.id.nav_practice:
                        changeToPracticeMode();
                        break;
                    case R.id.nav_add_question:
                        in = new Intent(ListTypeActivity.this, FormAddActivity.class);
                        startActivityForResult(in, Helper.REQUEST_CODE_EDIT);
                        break;
                    case R.id.nav_add_from_file:
                        in = new Intent(ListTypeActivity.this, FileImportActivity.class);
                        startActivityForResult(in, Helper.REQUEST_CODE_EDIT);
                        break;
                    case R.id.nav_edit_question:
                        changeToEditMode();
                        break;
                }
                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void reLaunchRecyclerView() {
        listType = database.getAllTypes();
        typeAdapter = new ListTypeAdapter(this, listType, this);
        recyclerViewType.setAdapter(typeAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Helper.REQUEST_CODE_EDIT) {
            reLaunchRecyclerView();
//            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkWriteExternalStorage(int typeID) {
        this.typeIDWrite = typeID;

        int hasWriteExternalStorage = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Helper.REQUEST_ASK_PERMISSIONS);
        } else {
            // ok can write
            writeTypeToExternalStorage(typeID);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == Helper.REQUEST_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // action
                writeTypeToExternalStorage(typeIDWrite);
            } else {
                Toast.makeText(this, "Write External Storage denied!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void writeTypeToExternalStorage(int typeID) {
        if (!Helper.isExternalStorageAvailable() || Helper.isExternalStorageReadOnly()) {
            Toast.makeText(this, "Can't write file", Toast.LENGTH_SHORT).show();
            return;
        }

        Type type = database.getType(typeID);
        String filePath = "MultiChoice";   // directory on SDCard
        String fileName = type.getName();  // name of the zip file

        List<Question> list = database.getAllQuestionsByType(typeID);
        if (list.size() == 0){
            Toast.makeText(this, "Chưa có câu hỏi nào", Toast.LENGTH_SHORT).show();
            return;
        }

        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard + "/" + filePath);
        if (!dir.exists() || !dir.isDirectory()) dir.mkdirs();


        //// Create text file on Internal Storage --   Vinh/Texts/list.txt
        File path = new File(getFilesDir(), "Vinh" + File.separator + "Texts");
        if (!path.exists()) path.mkdirs();

        File f = new File(path, "list.txt");
        // delete old file
        if (f.exists()) f.delete();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            for (int i = 0; i < list.size(); i++) {
                String convert = Helper.convertQuestion(list.get(i), i);
                fos.write(convert.getBytes());
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //// Then create zip file on SDCard
        byte[] buffer = new byte[2048];
        File zipFile = new File(dir, fileName + ".zip");
        try {
            fos = new FileOutputStream(zipFile);
            ZipOutputStream zip = new ZipOutputStream(fos);

            // first, zip text file
            File txtFile = new File(path, "list.txt");
            FileInputStream fin = new FileInputStream(txtFile);
            zip.putNextEntry(new ZipEntry(txtFile.getName()));
            int length;
            while ((length = fin.read(buffer)) != -1) {
                zip.write(buffer, 0, length);
            }
            zip.closeEntry();
            fin.close();


            // now, add list images
            File imagePath = new File(getFilesDir(), "Vinh" + File.separator + "Images");
            if (!imagePath.exists()) imagePath.mkdirs();

            for (int i = 0; i < list.size(); i++) {
                Question q = list.get(i);
                if (q.getImagePath() != null && q.getImagePath().length() > 0) {
                    File imageFile = new File(imagePath, q.getImagePath());
                    fin = new FileInputStream(imageFile);
                    String name = i + ".png";
                    zip.putNextEntry(new ZipEntry(name));
                    while ((length = fin.read(buffer)) != -1) {
                        zip.write(buffer, 0, length);
                    }
                    zip.closeEntry();
                    fin.close();
                }
            }
            zip.close();
            Toast.makeText(this, "Tập tin được lưu tại " + zipFile.getPath(), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dialogConfirmDeleteType(final int typeID, final int position) {
        final Type type = database.getType(typeID);

        final Dialog dialog = new Dialog(this, R.style.Dialog);
        dialog.setTitle("Xóa " + type.getName() + "?");
        dialog.setContentView(R.layout.dialog_delete_type);

        final CheckBox checkBox = dialog.findViewById(R.id.checkbox_move_to_default);
        Button btnDelete = dialog.findViewById(R.id.btn_delete_type);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        if (typeID == -1) checkBox.setVisibility(View.GONE);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Question> removeList = database.getAllQuestionsByType(typeID);
                if (typeID == -1) {
                    // delete all question in Unknown Type
                    for (Question q : removeList) {
                        database.deleteQuestion(q.getId());
                    }
                } else {
                    if (checkBox.isChecked()) {
                        // di chuyển các câu hỏi về chủ đề Không xác định
                        for (Question q : removeList) {
                            q.setType(-1);
                            database.updateQuestion(q);
                        }
                        Toast.makeText(ListTypeActivity.this, "Đã di chuyển câu hỏi và xóa chủ dề", Toast.LENGTH_SHORT).show();
                    } else {
                        // change typeID to -1
                        for (Question q : removeList) {
                            database.deleteQuestion(q.getId());
                        }
                        Toast.makeText(ListTypeActivity.this, "Đã xóa câu hỏi và chủ dề", Toast.LENGTH_SHORT).show();
                    }

                }
                // re launch
                database.deleteType(typeID);
                listType.remove(position);
                typeAdapter.notifyItemRemoved(position);
//                reLaunchRecyclerView();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    // Move to EditActivity
    public void viewAllQuestionsInType(int typeID) {
        int typeSize = database.getTypeSize(typeID);
        if (typeSize == 0) {
            Toast.makeText(this, "Chưa có câu hỏi nào", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent in = new Intent(ListTypeActivity.this, EditActivity.class);
        in.putExtra(Helper.DEF_TYPE_ID, typeID);
        startActivityForResult(in, Helper.REQUEST_CODE_EDIT);
    }

    public void practiceOneType(int typeID) {
        int typeSize = database.getTypeSize(typeID);
        if (typeSize == 0) {
            Toast.makeText(this, "Chưa có câu hỏi nào để làm", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent in = new Intent(ListTypeActivity.this, FormActivity.class);
        in.putExtra(Helper.DEF_TYPE_ID, typeID);
        startActivity(in);
    }

    private void showDialogAddType() {
        final Dialog dialog = new Dialog(this, R.style.Dialog);
        dialog.setTitle("Thêm chủ đề");
        dialog.setContentView(R.layout.dialog_add_type);

        final EditText editText = dialog.findViewById(R.id.edt_add_type);
        Button btnAddType = dialog.findViewById(R.id.btn_add_type);
        btnAddType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typeName = editText.getText().toString().trim();
                if (typeName.length() < 1) {
                    Toast.makeText(ListTypeActivity.this, "Bạn chưa nhập tên thể loại", Toast.LENGTH_SHORT).show();
                } else {
                    Type type = new Type(typeName);
                    int id = database.addType(type);

//                    listType = database.getAllTypes();
//                    typeAdapter = new ListTypeAdapter(ListTypeActivity.this, listType, ListTypeActivity.this);
//                    recyclerViewType.setAdapter(typeAdapter);
//                    reLaunchRecyclerView();
                    type = database.getType(id);
                    listType.add(type);
                    typeAdapter.notifyItemInserted(listType.size()-1);

                    Toast.makeText(ListTypeActivity.this, " Thêm thành công! ", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
//                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    public void showDialogChangeTypeName(int typeID) {
        if (typeID == -1) {
            Toast.makeText(ListTypeActivity.this, "Không thể đổi tên chủ đề này", Toast.LENGTH_SHORT).show();
            return;
        }
        final Type type = database.getType(typeID);

        final Dialog dialog = new Dialog(this, R.style.Dialog);
        dialog.setTitle("Đổi tên");
        dialog.setContentView(R.layout.dialog_add_type);

        final EditText editText = dialog.findViewById(R.id.edt_add_type);
        editText.setText(type.getName());

        Button btnChange = dialog.findViewById(R.id.btn_add_type);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String typeName = editText.getText().toString().trim();

                if (typeName.length() < 1) {
                    Toast.makeText(ListTypeActivity.this, "Bạn chưa nhập tên thể loại", Toast.LENGTH_SHORT).show();
                } else {
                    type.setName(typeName);
                    database.updateType(type);
                    reLaunchRecyclerView();
                    Toast.makeText(ListTypeActivity.this, "Đổi tên thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }
}
