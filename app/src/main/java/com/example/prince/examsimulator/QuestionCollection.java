package com.example.prince.examsimulator;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Prince on 7/15/2015.
 */
public class QuestionCollection {
    //SQLiteDatabase db;
   // MySQLiteHelper dbHelper;
    public static void onCreate(SQLiteDatabase db){
        String query = "create table QuestionCollection("
                + "_id integer primary key null,"
                + "questions text not null,"
                + "option1 text not null,"
                + "option2 text not null,"                      ///+ "choicetype text not null,"
                + "option3 text not null,"
                + "option4 text not null,"
                + "answer text not null"
                + ")";
        db.execSQL(query);

        String query2 = "create table AnswerCollection("
                + "_id integer primary key null,"
                + "answergiven text not null,"
                + "marks Integer not null,"
                + "attampted text not null"
                + ")";

        db.execSQL(query2);

    }

//    SQLiteDatabase dbHelper = new MySQLiteHelper(this, "mydatabase.db", null, 1);
//    MySQLiteHelper db = dbHelper.getWritableDatabase();

    public static void onUpdate(SQLiteDatabase db, int oldVersion, int newVersion){
		/*
		 * Check here for old version and new version
		 * */
        String query1 = "drop table if exists QuestionCollection";
        db.execSQL(query1);
        String query2 = "drop table if exists AnswerCollection";
        db.execSQL(query2);
        onCreate(db);
    }
}

