package com.vinh.multichoice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vinh.multichoice.online.Exam;
import com.vinh.multichoice.online.Subject;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MULTI_CHOICE_DATABASE";

    // Question Table
    private static final String QUESTION_TABLE = "QUESTION";
    private static final String COLUMN_QUES_ID = "id";
    private static final String COLUMN_QUES_TYPE = "type";
    private static final String COLUMN_QUES_QUESTION = "question";
    private static final String COLUMN_QUES_IMAGE_PATH = "image";
    private static final String COLUMN_QUES_ANSWER_A = "A";
    private static final String COLUMN_QUES_ANSWER_B = "B";
    private static final String COLUMN_QUES_ANSWER_C = "C";
    private static final String COLUMN_QUES_ANSWER_D = "D";
    private static final String COLUMN_QUES_CORRECT = "Correct";
    private static final String COLUMN_QUES_HD = "hd";

    // QUESTION ONLINE TABLE
    private static final String QUESTION_ONLINE_TABLE = "QUESTION_ONLINE";

    // Type Table
    private static final String TYPE_TABLE = "TYPE";
    private static final String COLUMN_TYPE_ID = "id";
    private static final String COLUMN_TYPE_NAME = "name";

    // Exam Table
    private static final String EXAM_TABLE = "EXAM";
    private static final String COLUMN_EXAM_ID = "id";
    private static final String COLUMN_EXAM_SUBJECT_ID = "subject_id";
    private static final String COLUMN_EXAM_NAME = "name";
    private static final String COLUMN_EXAM_AMOUNT = "amount";
    private static final String COLUMN_EXAM_TIME = "time";
    private static final String COLUMN_EXAM_URL = "url";
    private static final String COLUMN_EXAM_DOWNLOADED = "downloaded";
    private static final String COLUMN_EXAM_UPDATED = "updated";

    // Subject Table
    private static final String SUBJECT_TABLE = "SUBJECT";
    private static final String COLUMN_SUBJECT_ID = "id";
    private static final String COLUMN_SUBJECT_NAME = "name";
    private static final String COLUMN_SUBJECT_ICON = "icon";
    private static final String COLUMN_SUBJECT_UPDATED = "updated";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + QUESTION_TABLE + " ( "
                + COLUMN_QUES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUES_TYPE + " INTEGER, "
                + COLUMN_QUES_QUESTION + " TEXT, "
                + COLUMN_QUES_IMAGE_PATH + " TEXT, "
                + COLUMN_QUES_ANSWER_A + " TEXT, "
                + COLUMN_QUES_ANSWER_B + " TEXT, "
                + COLUMN_QUES_ANSWER_C + " TEXT, "
                + COLUMN_QUES_ANSWER_D + " TEXT, "
                + COLUMN_QUES_CORRECT + " INTEGER, "
                + COLUMN_QUES_HD + " TEXT "
                + ")";
        db.execSQL(query);

        query = "CREATE TABLE " + TYPE_TABLE + " ( "
                + COLUMN_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TYPE_NAME + " TEXT "
                + ")";
        db.execSQL(query);

        query = "CREATE TABLE " + QUESTION_ONLINE_TABLE + " ( "
                + COLUMN_QUES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_QUES_TYPE + " INTEGER, "       // type is exam_id
                + COLUMN_QUES_QUESTION + " TEXT, "
                + COLUMN_QUES_IMAGE_PATH + " TEXT, "
                + COLUMN_QUES_ANSWER_A + " TEXT, "
                + COLUMN_QUES_ANSWER_B + " TEXT, "
                + COLUMN_QUES_ANSWER_C + " TEXT, "
                + COLUMN_QUES_ANSWER_D + " TEXT, "
                + COLUMN_QUES_CORRECT + " INTEGER, "
                + COLUMN_QUES_HD + " TEXT "
                + ")";
        db.execSQL(query);

        query = "CREATE TABLE " + SUBJECT_TABLE + " ( "
                + COLUMN_SUBJECT_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_SUBJECT_NAME + " TEXT, "
                + COLUMN_SUBJECT_ICON + " TEXT, "
                + COLUMN_SUBJECT_UPDATED + " INTEGER "
                + ")";
        db.execSQL(query);

        query = "CREATE TABLE " + EXAM_TABLE + " ( "
                + COLUMN_EXAM_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_EXAM_SUBJECT_ID + " INTEGER, "
                + COLUMN_EXAM_NAME + " TEXT, "
                + COLUMN_EXAM_AMOUNT + " INTEGER, "
                + COLUMN_EXAM_TIME + " INTEGER, "
                + COLUMN_EXAM_URL + " TEXT, "
                + COLUMN_EXAM_DOWNLOADED + " INTEGER, "
                + COLUMN_EXAM_UPDATED + " INTEGER "
                + ")";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DROP TABLE IF EXISTS " + QUESTION_TABLE;
        db.execSQL(query);

        query = "DROP TABLE IF EXISTS " + TYPE_TABLE;
        db.execSQL(query);

        query = "DROP TABLE IF EXISTS " + QUESTION_ONLINE_TABLE;
        db.execSQL(query);

        query = "DROP TABLE IF EXISTS " + SUBJECT_TABLE;
        db.execSQL(query);

        query = "DROP TABLE IF EXISTS " + EXAM_TABLE;
        db.execSQL(query);

        onCreate(db);
    }

    /*
    Subject Query
     */

    public int addSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SUBJECT_ID, subject.getId());
        values.put(COLUMN_SUBJECT_NAME, subject.getName());
        values.put(COLUMN_SUBJECT_ICON, subject.getIconPath());
        values.put(COLUMN_SUBJECT_UPDATED, subject.getUpdate());

        long id = db.insert(SUBJECT_TABLE, null, values);
        db.close();
        return (int) id;
    }

    public Subject getSubject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + SUBJECT_TABLE + " WHERE " + COLUMN_SUBJECT_ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            String name = cursor.getString(1);
            String iconPath = cursor.getString(2);
            int update = cursor.getInt(3);
            return new Subject(id, name, iconPath, update);
        } else {
            return new Subject(-1, "Không biết", "", -1);
        }
    }

    public void updateSubject(Subject subject) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SUBJECT_ID, subject.getId());
        values.put(COLUMN_SUBJECT_NAME, subject.getName());
        values.put(COLUMN_SUBJECT_ICON, subject.getIconPath());
        values.put(COLUMN_SUBJECT_UPDATED, subject.getUpdate());

        db.update(SUBJECT_TABLE, values, COLUMN_SUBJECT_ID + " = ?", new String[]{subject.getId() + ""});
        db.close();
    }

    public boolean isSubjectExists(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(SUBJECT_TABLE, new String[]{COLUMN_SUBJECT_ID, COLUMN_SUBJECT_NAME}, COLUMN_SUBJECT_ID + "=?", new String[]{id + ""}, null, null, null, null);

        return cursor.moveToFirst();
    }

    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<Subject>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + SUBJECT_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String iconPath = cursor.getString(2);
                int update = cursor.getInt(3);

                Subject subject = new Subject(id, name, iconPath, update);
                list.add(subject);
            } while (cursor.moveToNext());
        }
        return list;
    }

    /*
    Exam Query
     */

    public int addExam(Exam exam) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_EXAM_ID, exam.getId());
        values.put(COLUMN_EXAM_SUBJECT_ID, exam.getSubject_id());
        values.put(COLUMN_EXAM_NAME, exam.getName());
        values.put(COLUMN_EXAM_AMOUNT, exam.getAmount());
        values.put(COLUMN_EXAM_TIME, exam.getTime());
        values.put(COLUMN_EXAM_URL, exam.getUrl());
        values.put(COLUMN_EXAM_DOWNLOADED, exam.isDownloaded() ? 1 : 0);
        values.put(COLUMN_EXAM_UPDATED, exam.getUpdate());

        long id = db.insert(EXAM_TABLE, null, values);
        db.close();
        return (int) id;
    }

    public Exam getExam(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + EXAM_TABLE + " WHERE " + COLUMN_EXAM_ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        Exam exam = new Exam();

        if (cursor.moveToFirst()) {
//            int id = cursor.getInt(0);
            int subject_id = cursor.getInt(1);
            String name = cursor.getString(2);
            int amount = cursor.getInt(3);
            int time = cursor.getInt(4);
            String url = cursor.getString(5);
            boolean downloaded = cursor.getInt(6) == 1;
            int update = cursor.getInt(7);

            exam = new Exam(id, subject_id, name, amount, time, url, update);
        }
        cursor.close();
        db.close();
        return exam;
    }

    public List<Exam> getListExams(int subject_id) {
        List<Exam> list = new ArrayList<Exam>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + EXAM_TABLE + " WHERE " + COLUMN_EXAM_SUBJECT_ID + " = " + subject_id;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
//                int subject_id = cursor.getInt(1);
                String name = cursor.getString(2);
                int amount = cursor.getInt(3);
                int time = cursor.getInt(4);
                String url = cursor.getString(5);
                boolean downloaded = cursor.getInt(6) == 1;
                int update = cursor.getInt(7);

                Exam exam = new Exam(id, subject_id, name, amount, time, url, update);
                exam.setDownloaded(downloaded);
                list.add(exam);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Exam> getAllExams() {
        List<Exam> list = new ArrayList<Exam>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + EXAM_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                int subject_id = cursor.getInt(1);
                String name = cursor.getString(2);
                int amount = cursor.getInt(3);
                int time = cursor.getInt(4);
                String url = cursor.getString(5);
                boolean downloaded = cursor.getInt(6) == 1;
                int update = cursor.getInt(7);

                Exam exam = new Exam(id, subject_id, name, amount, time, url, update);
                exam.setDownloaded(downloaded);
                list.add(exam);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public void updateExam(Exam exam) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_EXAM_ID, exam.getId());
        values.put(COLUMN_EXAM_SUBJECT_ID, exam.getSubject_id());
        values.put(COLUMN_EXAM_NAME, exam.getName());
        values.put(COLUMN_EXAM_AMOUNT, exam.getAmount());
        values.put(COLUMN_EXAM_TIME, exam.getTime());
        values.put(COLUMN_EXAM_URL, exam.getUrl());
        values.put(COLUMN_EXAM_DOWNLOADED, exam.isDownloaded() ? 1 : 0);
        values.put(COLUMN_EXAM_UPDATED, exam.getUpdate());

        db.update(EXAM_TABLE, values, COLUMN_EXAM_ID + "=?", new String[]{exam.getId() + ""});
        db.close();
    }

    public boolean isExamExists(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + EXAM_TABLE + " WHERE " + COLUMN_EXAM_ID + " = " + id;
        Cursor cursor = db.rawQuery(query, null);
        return cursor.moveToFirst();
    }


    /*
     Online question query
     */

    public int addOnlineQuestion(Question q) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

//        values.put(COLUMN_QUES_ID, q.getId());
        values.put(COLUMN_QUES_TYPE, q.getType());
        values.put(COLUMN_QUES_QUESTION, q.getQuestion());
        values.put(COLUMN_QUES_IMAGE_PATH, q.getImagePath());
        values.put(COLUMN_QUES_ANSWER_A, q.getAnswerA());
        values.put(COLUMN_QUES_ANSWER_B, q.getAnswerB());
        values.put(COLUMN_QUES_ANSWER_C, q.getAnswerC());
        values.put(COLUMN_QUES_ANSWER_D, q.getAnswerD());
        values.put(COLUMN_QUES_CORRECT, q.getCorrect());
        values.put(COLUMN_QUES_HD, q.getHd());

        long id = db.insert(QUESTION_ONLINE_TABLE, null, values);
        db.close();
        return (int) id;
    }

    public boolean isOnlineQuestionExists(int id, int exam_id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + QUESTION_ONLINE_TABLE + " WHERE " + COLUMN_QUES_ID + " = " + id + " AND " + COLUMN_EXAM_ID + " = " + exam_id;
        Cursor cursor = db.rawQuery(query, null);

        return cursor.moveToFirst();
    }

    public Question getOnlineQuestion(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(QUESTION_ONLINE_TABLE,
                new String[]{COLUMN_QUES_ID, COLUMN_QUES_TYPE, COLUMN_QUES_QUESTION, COLUMN_QUES_IMAGE_PATH, COLUMN_QUES_ANSWER_A, COLUMN_QUES_ANSWER_B, COLUMN_QUES_ANSWER_C, COLUMN_QUES_ANSWER_D, COLUMN_QUES_CORRECT, COLUMN_QUES_HD},
                COLUMN_QUES_ID + " = ?",
                new String[]{id + ""},
                null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        else return null;

        // ques_id = id : because of found
        // int ques_id = cursor.getInt(0);
        int type = cursor.getInt(1);
        String question = cursor.getString(2);
        String imagePath = cursor.getString(3);
        String ansA = cursor.getString(4);
        String ansB = cursor.getString(5);
        String ansC = cursor.getString(6);
        String ansD = cursor.getString(7);
        int correct = cursor.getInt(8);
        String hd = cursor.getString(9);

        Question q = new Question(id, type, imagePath, question, ansA, ansB, ansC, ansD, correct, hd);
        cursor.close();
        return q;
    }

    public void updateOnlineQuestion(Question q) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

//        values.put(COLUMN_QUES_ID, q.getId());
        values.put(COLUMN_QUES_TYPE, q.getType());
        values.put(COLUMN_QUES_QUESTION, q.getQuestion());
        values.put(COLUMN_QUES_IMAGE_PATH, q.getImagePath());
        values.put(COLUMN_QUES_ANSWER_A, q.getAnswerA());
        values.put(COLUMN_QUES_ANSWER_B, q.getAnswerB());
        values.put(COLUMN_QUES_ANSWER_C, q.getAnswerC());
        values.put(COLUMN_QUES_ANSWER_D, q.getAnswerD());
        values.put(COLUMN_QUES_CORRECT, q.getCorrect());
        values.put(COLUMN_QUES_HD, q.getHd());

        db.update(QUESTION_ONLINE_TABLE, values, COLUMN_QUES_ID + " = ?", new String[]{q.getId() + ""});
        db.close();
    }

    public List<Question> getAllOnlineQuestions() {
        List<Question> list = new ArrayList<Question>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + QUESTION_ONLINE_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                int type = cursor.getInt(1);
                String question = cursor.getString(2);
                String imagePath = cursor.getString(3);
                String ansA = cursor.getString(4);
                String ansB = cursor.getString(5);
                String ansC = cursor.getString(6);
                String ansD = cursor.getString(7);
                int correct = cursor.getInt(8);
                String hd = cursor.getString(9);

                Question q = new Question(id, type, question, imagePath, ansA, ansB, ansC, ansD, correct, hd);
                list.add(q);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Question> getAllOnlineQuestionsByType(int type) {
        List<Question> list = new ArrayList<Question>();
        List<Question> all = getAllOnlineQuestions();

        for (Question q : all) {
            if (q.getType() == type) list.add(q);
        }
        return list;
    }

    public void deleteOnlineQuestion(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(QUESTION_ONLINE_TABLE, COLUMN_QUES_ID + " = ?", new String[]{id + ""});
        db.close();
    }


    // offline question query
    public int addQuestion(Question q) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

//        values.put(COLUMN_QUES_ID, q.getId());
        values.put(COLUMN_QUES_TYPE, q.getType());
        values.put(COLUMN_QUES_QUESTION, q.getQuestion());
        values.put(COLUMN_QUES_IMAGE_PATH, q.getImagePath());
        values.put(COLUMN_QUES_ANSWER_A, q.getAnswerA());
        values.put(COLUMN_QUES_ANSWER_B, q.getAnswerB());
        values.put(COLUMN_QUES_ANSWER_C, q.getAnswerC());
        values.put(COLUMN_QUES_ANSWER_D, q.getAnswerD());
        values.put(COLUMN_QUES_CORRECT, q.getCorrect());
        values.put(COLUMN_QUES_HD, q.getHd());

        long id = db.insert(QUESTION_TABLE, null, values);
        db.close();
        return (int) id;
    }

    public Question getQuestion(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(QUESTION_TABLE,
                new String[]{COLUMN_QUES_ID, COLUMN_QUES_TYPE, COLUMN_QUES_QUESTION, COLUMN_QUES_IMAGE_PATH, COLUMN_QUES_ANSWER_A, COLUMN_QUES_ANSWER_B, COLUMN_QUES_ANSWER_C, COLUMN_QUES_ANSWER_D, COLUMN_QUES_CORRECT, COLUMN_QUES_HD},
                COLUMN_QUES_ID + " = ?",
                new String[]{id + ""},
                null, null, null, null);
        if (cursor != null) cursor.moveToFirst();
        else return null;

        // ques_id = id : because of found
        // int ques_id = cursor.getInt(0);
        int type = cursor.getInt(1);
        String question = cursor.getString(2);
        String imagePath = cursor.getString(3);
        String ansA = cursor.getString(4);
        String ansB = cursor.getString(5);
        String ansC = cursor.getString(6);
        String ansD = cursor.getString(7);
        int correct = cursor.getInt(8);
        String hd = cursor.getString(9);

        Question q = new Question(id, type, question, imagePath, ansA, ansB, ansC, ansD, correct, hd);
        cursor.close();
        return q;
    }

    public List<Question> getAllQuestions() {
        List<Question> list = new ArrayList<Question>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + QUESTION_TABLE;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                int type = cursor.getInt(1);
                String question = cursor.getString(2);
                String imagePath = cursor.getString(3);
                String ansA = cursor.getString(4);
                String ansB = cursor.getString(5);
                String ansC = cursor.getString(6);
                String ansD = cursor.getString(7);
                int correct = cursor.getInt(8);
                String hd = cursor.getString(9);

                Question q = new Question(id, type, question, imagePath, ansA, ansB, ansC, ansD, correct, hd);
                list.add(q);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public List<Question> getAllQuestionsByType(int type) {
        List<Question> list = new ArrayList<Question>();
        List<Question> all = getAllQuestions();

        for (Question q : all) {
            if (q.getType() == type) list.add(q);
        }
        return list;
    }

    public void updateQuestion(Question q) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

//        values.put(COLUMN_QUES_ID, q.getId());
        values.put(COLUMN_QUES_TYPE, q.getType());
        values.put(COLUMN_QUES_QUESTION, q.getQuestion());
        values.put(COLUMN_QUES_IMAGE_PATH, q.getImagePath());
        values.put(COLUMN_QUES_ANSWER_A, q.getAnswerA());
        values.put(COLUMN_QUES_ANSWER_B, q.getAnswerB());
        values.put(COLUMN_QUES_ANSWER_C, q.getAnswerC());
        values.put(COLUMN_QUES_ANSWER_D, q.getAnswerD());
        values.put(COLUMN_QUES_CORRECT, q.getCorrect());
        values.put(COLUMN_QUES_HD, q.getHd());

        db.update(QUESTION_TABLE, values, COLUMN_QUES_ID + " = ?", new String[]{q.getId() + ""});
        db.close();
    }

    public void deleteQuestion(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(QUESTION_TABLE, COLUMN_QUES_ID + " = ?", new String[]{id + ""});
        db.close();
    }


    //
    // Type Query
    //

    public int addType(Type type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // nếu là Type mới thì để database auto increment id.
        // trường hợp duy nhất có id là type "Không xác định"
        if (type.getId() != Helper.TYPE_ERR) values.put(COLUMN_TYPE_ID, type.getId());
        values.put(COLUMN_TYPE_NAME, type.getName());

        long id = db.insert(TYPE_TABLE, null, values);
        db.close();
        return (int) id;
    }

    public Type getType(int typeID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TYPE_TABLE + " WHERE " + COLUMN_TYPE_ID + " = " + typeID;
        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();
        int id = cursor.getInt(0);
        String name = cursor.getString(1);
        Type type = new Type(id, name);
        type.setSize(getTypeSize(type.getId()));
        db.close();
        return type;
    }

    public List<Type> getAllTypes() {
        // insert basic data
        if (!isExistsType(-1)) addType(new Type(-1, "Không xác định"));

        List<Type> list = new ArrayList<Type>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TYPE_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                Type type = new Type(id, name);
                type.setSize(getTypeSize(type.getId()));
                list.add(type);
            } while (cursor.moveToNext());
        }
        db.close();
        return list;
    }


    public int getTypeSize(int typeID) {
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, QUESTION_TABLE, COLUMN_QUES_TYPE + "=?", new String[]{typeID + ""});

    }

    public boolean isExistsType(int typeID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TYPE_TABLE + " WHERE " + COLUMN_TYPE_ID + " = " + typeID;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) return true;
        return false;
    }

    public void deleteType(int typeID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TYPE_TABLE, COLUMN_TYPE_ID + " = ?", new String[]{typeID + ""});
        db.close();
    }

    public void updateType(Type type) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_TYPE_NAME, type.getName());
        db.update(TYPE_TABLE, values, COLUMN_TYPE_ID + " = ?", new String[]{type.getId() + ""});
        db.close();
    }
}
