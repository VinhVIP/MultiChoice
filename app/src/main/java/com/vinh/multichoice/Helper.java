package com.vinh.multichoice;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Helper {
    public static final String DEF_TYPE_ID = "typeID";
    public static final int MODE_PRACTICE = 102;
    public static final int MODE_TEST = 101;
    public static final int MODE_EDIT = 103;

    public static final int REQUEST_CODE_EDIT = 999;
    public static final int RESULT_CHANGE = 655;
    public static final int RESULT_CANCEL = 666;

    public static final int REQUEST_ASK_PERMISSIONS = 15069;

    public static final String RANDOM_ANSWERS_INDEX = "random_index";
    public static final String COUNT_DOWN_TIMER = "count_down";

    public static Random random = new Random();

    public static final int TYPE_ERR = -1302;

    public static int MODE;

    public static List<Question> listTestQuestion;

    public static void setMode(int mode) {
        MODE = mode;
    }


    public static List<Question> randomQuestions(Database database, int typeID, int num) {
        List<Question> allQuestions = database.getAllQuestionsByType(typeID);
        List<Question> list = new ArrayList<>();

        int[] randomIndex = new int[allQuestions.size()];
        for (int i = 0; i < randomIndex.length; i++) {
            randomIndex[i] = i;
        }

        for (int i = randomIndex.length - 1; i >= 0; i--) {
            int j = rand(i + 1);
            int tmp = randomIndex[i];
            randomIndex[i] = randomIndex[j];
            randomIndex[j] = tmp;
        }

        for (int i = 0; i < num; i++) {
            list.add(allQuestions.get(randomIndex[i]));
        }
        return list;
    }

    public static int rand(int limit) {
        return Math.abs(random.nextInt() % limit);
    }

    public static List<Question> listQuestionImportFromFile(String fileContent) {
        /* Format
        [CH] Question here
        [IMG] url image
        [A] Answer A
        [B] Answer B
        [C] Answer C
        [D] Answer D
        [TL] Here is correct answer
        [HD] Tutorials
         */
        // Note: Each part can contain multi lines

        List<Question> listQuestions = new ArrayList<>();
        String[] s = fileContent.split("\\[CH\\]");
        for (int i = 1; i < s.length; i++) {
            listQuestions.add(cut(s[i]));
        }
        return listQuestions;
    }

    public static Question cut(String str) {
        int len = str.length();

        int indexIMG = str.indexOf("[IMG]");
        int indexA = str.indexOf("[A]");
        int indexB = str.indexOf("[B]");
        int indexC = str.indexOf("[C]");
        int indexD = str.indexOf("[D]");
        int indexTL = str.indexOf("[TL]");
        int indexHD = str.indexOf("[HD]");

        if (indexA == -1 || indexB == -1 || indexC == -1 || indexD == -1 || indexTL == -1) {
            String err = "Lỗi!";
            Question question = new Question(str, err, err, err, err, -1, err);
            return question;
        }

        String question, imagePath;

        if (indexIMG == -1) {
            question = str.substring(0, indexA).trim();
            imagePath = "";
        } else {
            question = str.substring(0, indexIMG).trim();
            imagePath = str.substring(indexIMG + 5, indexA).trim();
        }

        String answerA = str.substring(indexA + 3, indexB).trim();
        String answerB = str.substring(indexB + 3, indexC).trim();
        String answerC = str.substring(indexC + 3, indexD).trim();
        String answerD = str.substring(indexD + 3, indexTL).trim();

        int correct;
        String hd, result;

        if (indexHD == -1) {
            hd = "";
            result = str.substring(indexTL + 4, len).trim();
        } else {
            hd = str.substring(indexHD + 4, len).trim();
            result = str.substring(indexTL + 4, indexHD).trim();
        }

        if (result.toUpperCase().equals("A")) correct = 0;
        else if (result.toUpperCase().equals("B")) correct = 1;
        else if (result.toUpperCase().equals("C")) correct = 2;
        else correct = 3;

        Question q = new Question(question, imagePath, answerA, answerB, answerC, answerD, correct, hd);
        return q;
    }

    public static String convertQuestion(Question question, int index) {
        String ans;
        switch (question.getCorrect()) {
            case 0:
                ans = "A";
                break;
            case 1:
                ans = "B";
                break;
            case 2:
                ans = "C";
                break;
            default:
                ans = "D";
                break;
        }
        String s = "[CH] " + question.getQuestion() + "\n"
                + "[IMG] " + (question.getImagePath() != null && question.getImagePath().length() > 0 ? (index + ".png") : ("")) + "\n"
                + "[A] " + question.getAnswerA() + "\n"
                + "[B] " + question.getAnswerB() + "\n"
                + "[C] " + question.getAnswerC() + "\n"
                + "[D] " + question.getAnswerD() + "\n"
                + "[TL] " + ans + "\n"
                + "[HD] " + question.getHd() + "\n";
        return s;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else if (Build.VERSION.SDK_INT >= 21) {
            Network[] info = connectivity.getAllNetworks();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i] != null && connectivity.getNetworkInfo(info[i]).isConnected()) {
                        return true;
                    }
                }
            }
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
            final NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                return true;
            }
        }
        return false;
    }


    // simple save image on internal storage
    public static void saveImageOnInternalStorage(Context context, String fileName, Bitmap bitmap) {
        File path = new File(context.getFilesDir(), "Vinh" + File.separator + "Images");
        if (!path.exists()) path.mkdirs();

        File f = new File(path, fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getImageBitmapOnInternalStorage(Context context, String fileName) {
        Bitmap bitmap = null;
        File path = new File(context.getFilesDir(), "Vinh" + File.separator + "Images");
        if (!path.exists()) path.mkdirs();

        File f = new File(path, fileName);
        try (FileInputStream fin = new FileInputStream(f)) {
            bitmap = BitmapFactory.decodeStream(fin);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static Bitmap getImageBitmapFromExternalStorage(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap b = BitmapFactory.decodeFile(photoPath, options);
        return b;
    }
}
