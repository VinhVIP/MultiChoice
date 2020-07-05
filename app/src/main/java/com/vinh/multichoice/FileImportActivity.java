package com.vinh.multichoice;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileImportActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;

    private Button btnSelectFile, btnImport, btnSelectType;
    private TextView textView;

    private Database database;

    private int typeIDSelect = -1;
    private boolean allCorrect = true, isDefaultContent = false;
    private String convertContent, defaultContent;

    private List<Question> list;
    HashMap<String, Bitmap> hashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_import);

        database = new Database(this);
        settingToolbar();

        btnSelectFile = findViewById(R.id.btn_select_file);
        btnSelectType = findViewById(R.id.btn_select_type);
        btnImport = findViewById(R.id.btn_import);
        textView = findViewById(R.id.txt_preview);

        btnSelectFile.setOnClickListener(this);
        btnSelectType.setOnClickListener(this);
        btnImport.setOnClickListener(this);
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_select_file:
                checkReadExternalStorage();
                break;
            case R.id.btn_select_type:
                showDialogType();
                break;
            case R.id.btn_import:
                if (!allCorrect) {
                    Toast.makeText(this, "Có câu hỏi sai định dạng", Toast.LENGTH_SHORT).show();
                } else {
                    for (Question q : list) {
                        q.setType(typeIDSelect);
                        int ques_id = database.addQuestion(q);
                        q.setId(ques_id);

                        Log.e("path", q.getImagePath());

                        if (q.getImagePath() != null && q.getImagePath().trim().length() > 0) {
                            if (hashMap.containsKey(q.getImagePath())) {
                                String nameBitmapSave = "0_" + ques_id + ".png";
                                Helper.saveImageOnInternalStorage(this, nameBitmapSave, hashMap.get(q.getImagePath()));

                                // update question
                                q.setImagePath(nameBitmapSave);
                                database.updateQuestion(q);

                                Log.e("path", q.getImagePath());
                            }
                        }
                    }
                    Toast.makeText(this, "Đã thêm", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 69:
                if (resultCode == RESULT_OK && data != null) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();
                    analyzeDataZipFile(uri);
                    textView.setText(defaultContent);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void checkReadExternalStorage() {
        int hasWriteExternalStorage = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Helper.REQUEST_ASK_PERMISSIONS);
        } else {
            // ok can get content
            getFileFromUri();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Helper.REQUEST_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // action
                getFileFromUri();
            } else {
                Toast.makeText(this, "Quyền đọc ghi tập tin bị từ chối!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void getFileFromUri() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/zip");

        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Upload"), 69);
//            startActivityForResult(intent, 69);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    public void analyzeDataZipFile(Uri uri) {
        File zipFile = new File(uri.getPath());
        Log.e("Fake", uri.getPath());

        try {
            zipFile = new File(getFilePath(this, uri));
            btnSelectFile.setText(zipFile.getName());
            Toast.makeText(FileImportActivity.this, getFilePath(this, uri), Toast.LENGTH_SHORT).show();
            Log.e("File Zip real", getFilePath(this, uri));

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Log.e("File Zip", zipFile.getName());
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
                    defaultContent = listContent;

                    String message = convertFileContent(listContent);
                    if (!allCorrect) {
                        Toast.makeText(FileImportActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // this is image file, so we put them to HashMap
                    // image name example: 5.png ,  12.png
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    hashMap.put(fileName, bitmap);
                }
            }
            // add question to SQLite database

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String convertFileContent(String content) {
        list = Helper.listQuestionImportFromFile(content);
        StringBuilder sb = new StringBuilder();
        String errorIndex = "";

        for (int i = 0; i < list.size(); i++) {
            Question q = list.get(i);
            if (q.getCorrect() == -1) {
                allCorrect = false;
                errorIndex += (i + 1) + ", ";
                sb.append("Câu " + (i + 1) + ": \n" + "CÂU HỎI NÀY BỊ LỖI ĐỊNH DẠNG\n" + q.getQuestion() + "\n--------\n");
            } else {
                sb.append("Câu " + (i + 1) + ": \n" + q.toString() + "\n--------\n");
            }
        }

        if (!allCorrect) {
            Toast.makeText(this, "Có câu hỏi sai định dạng: " + errorIndex, Toast.LENGTH_SHORT).show();
        }
        return sb.toString();
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
            }
        });
        builder.create();
        builder.show();
    }


    public String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {


            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
