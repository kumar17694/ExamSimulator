package com.example.prince.examsimulator;

/**
 * Created by Prince on 7/15/2015.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public MySQLiteHelper(Context context, String dbName, SQLiteDatabase.CursorFactory factory,	int version) {
        super(context, dbName, factory, version);
		/*
		 * CursorFactory is used to create Cursor Objects with customized implementation.
		 * Use it when you want Cursor from a custom factory
		 * Pass null if you want to use default factory.
		 * */
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //db.execSQL("drop table if exists QuestionCollection");
        QuestionCollection.onCreate(db);
//        String query = "create table PersonInfo("
//                + "_id integer primary key autoincrement,"
//                + "pname text not null,"
//                + "dob text not null"
//                + ")";
//        db.execSQL(query);
//        String query2 = "create table AnswerCollection("
//                + "_id integer primary key null,"
//                + "questions text not null,"
//                + "answergiven text not null,"
//                + "marks Integer not null,"
//                + "attampted text not null"
//                + ")";
//
//        db.execSQL(query2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        QuestionCollection.onUpdate(db, oldVersion, newVersion);
//        String query = "drop table if exists PersonInfo";
//        db.execSQL(query);
//        onCreate(db);
    }

}