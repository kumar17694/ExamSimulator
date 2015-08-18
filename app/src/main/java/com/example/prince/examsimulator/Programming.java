package com.example.prince.examsimulator;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


public class Programming extends AppCompatActivity implements View.OnClickListener {
    SQLiteDatabase db;
    MySQLiteHelper dbHelper;
    CountDownTimer countDownTimer;
    TextView timer;

    TextView et;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<Integer> selectpos,idlist,backtrack;
    ArrayList<String> ans;
    StringBuilder sb = new StringBuilder();
    int currentId,count;
    Button prev,next,finish;
    int questionCount;
    Long currentmilis;
    String Questions[];
    ListView optionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programming);

        et = (TextView) findViewById(R.id.questionEdit2);

        optionsList = (ListView) findViewById(R.id.optionsList2);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked);
        optionsList.setAdapter(arrayAdapter);
        optionsList.setChoiceMode(optionsList.CHOICE_MODE_MULTIPLE);
        optionsList.setTextFilterEnabled(true);
        ans = new ArrayList<String>();
        selectpos = new ArrayList<Integer>();
        idlist = new ArrayList<>();
        backtrack = new ArrayList<>();
        next = (Button)findViewById(R.id.nextExam2);
        next.setOnClickListener(this);
        prev = (Button) findViewById(R.id.preExam2);
        prev.setOnClickListener(this);
        finish = (Button)findViewById(R.id.finishexam2);
        finish.setOnClickListener(this);
        count = 1;
        Intent i = getIntent();
        int duration = i.getIntExtra("questionCount", 0);
        questionCount = duration;

        final long startTime = 60 * 1000 * duration;
        final long interval = 1000 * 1;

        AddData();

        timer = (TextView) findViewById(R.id.timerexam2);
        timer.setTextSize(20);
        timer.setTextColor(Color.rgb(102, 102, 255));

        countDownTimer = new MyCountDownTimer(startTime, interval);
        Long minits = (startTime / 1000) / 60;
        Long seconds = (startTime / 1000) % 60;
        timer.setText(String.format("%d:%02d", minits, seconds));
        //timer.setText(String.valueOf(startTime / 1000));
        countDownTimer.start();

        loadData(0);

        optionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                ListView l = (ListView) parent;
                if (l.isItemChecked(position)) {
                    selectpos.add(position + 1);
                    //Toast.makeText(getBaseContext(), "Option " + (position + 1) + " Selected .", //Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getBaseContext(), "Option " + (position + 1) + " Deselected", //Toast.LENGTH_SHORT).show();
                    selectpos.remove(selectpos.indexOf(position + 1));
                }
            }

        });

    }

    @Override
    public void onClick(View view) {
        if (view == findViewById(R.id.nextExam2)) {
            // count < no of question
            if (count < questionCount) {
                count += 1;
            }
            dbHelper = new MySQLiteHelper(getBaseContext(), "mydatabase1.db", null, 1);
            db = dbHelper.getWritableDatabase();
            if (currentId == backtrack.get(backtrack.size() - 1).intValue()) {
                optionsList.setEnabled(true);
                Cursor cursor = db.rawQuery("Select answergiven, attampted from AnswerCollection where _id=?", new String[]{backtrack.get(backtrack.size() - 1).toString()});
                if (cursor.getCount() == 0) {
                    //Toast.makeText(getBaseContext(), "for next ques", Toast.LENGTH_SHORT).show();
                    ContentValues valus = new ContentValues();
                    valus.put("_id", backtrack.get(backtrack.size() - 1));
                    if (selectpos.size() > 1) {
                        sb.setLength(0);

                        char[] aansgiven = ans.get(0).toCharArray();
                        if (selectpos.size() != aansgiven.length) {
                            //Toast.makeText(getBaseContext(),"incorrect ans , lens not equal",Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < selectpos.size(); i++) {
                                sb.append(selectpos.get(i).toString());
                                optionsList.setItemChecked(selectpos.get(i) - 1, false);
                            }
                        } else {
                            int chk;
                            chk = 0;
                            for (int i = 0; i < selectpos.size(); i++) {
                                sb.append(selectpos.get(i).toString());
                                optionsList.setItemChecked(selectpos.get(i) - 1, false);
                                if (selectpos.contains(Integer.parseInt(String.valueOf(aansgiven[i])))) {
                                    chk += 1;
                                }
                            }
                            if (chk == selectpos.size()) {
                                //Toast.makeText(getBaseContext(),"ans is correct..",Toast.LENGTH_SHORT).show();
                                sb.setLength(0);
                                sb.append(ans.get(0));
                            } else {
                                ///Toast.makeText(getBaseContext(),"Incorrect ans whaen lens are equal",Toast.LENGTH_SHORT).show();
                            }
                        }

                    } else if (selectpos.size() == 0) {
                        valus.put("attampted", "no");
                        sb.setLength(0);
                        sb.append("");
                        valus.put("marks", 0);
                    } else {
                        if (selectpos.size() == 1) {
                            sb.setLength(0);
                            sb.append(selectpos.get(0).toString());
                            if (idlist.size() > Questions.length - questionCount) {
                                optionsList.setItemChecked(selectpos.get(0) - 1, false);
                            }
                        }
                    }
                    valus.put("answergiven", sb.toString());
                    if (ans.get(0).contentEquals(sb.toString())) {
                        ///Toast.makeText(getBaseContext(), "Your ans is correct after next main if", Toast.LENGTH_SHORT).show();
                        valus.put("marks", 1);
                        valus.put("attampted", "yes");
                    } else if (selectpos.size() != 0) {
                        //Toast.makeText(getBaseContext(), "incorrect ans", Toast.LENGTH_SHORT).show();
                        valus.put("marks", 0);
                        valus.put("attampted", "yes");
                    }
                    db.insert("AnswerCollection", null, valus);
                    if (idlist.size() > Questions.length - questionCount) {
                        selectpos.clear();
                        loadData(0);

                    }
                } else if (idlist.size() == (Questions.length - questionCount)) {
                    Toast.makeText(getBaseContext(), "This is the last question", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getBaseContext(),"itis already in db",Toast.LENGTH_SHORT).show();
                    if (selectpos.size() > 1) {
                        sb.setLength(0);
                        char[] aansgiven = ans.get(0).toCharArray();
                        if (selectpos.size() != aansgiven.length) {
                            //Toast.makeText(getBaseContext(),"incorrect ans , lens not equal",Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < selectpos.size(); i++) {
                                sb.append(selectpos.get(i).toString());
                                optionsList.setItemChecked(selectpos.get(i) - 1, false);
                            }
                        } else {
                            int chk;
                            chk = 0;
                            for (int i = 0; i < selectpos.size(); i++) {
                                sb.append(selectpos.get(i).toString());
                                optionsList.setItemChecked(selectpos.get(i) - 1, false);
                                if (selectpos.contains(Integer.parseInt(String.valueOf(aansgiven[i])))) {
                                    chk += 1;
                                }
                            }
                            if (chk == selectpos.size()) {
                                //Toast.makeText(getBaseContext(),"ans is correct..",Toast.LENGTH_SHORT).show();
                                sb.setLength(0);
                                sb.append(ans.get(0));
                            } else {
                                //Toast.makeText(getBaseContext(),"Incorrect ans whaen lens are equal",Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        if (selectpos.size() == 0) {
                            for (int p = 0; p < 4; p++) {
                                optionsList.setItemChecked(p, false);
                            }
                        }

                        if (selectpos.size() == 1) {
                            sb.setLength(0);
                            sb.append(selectpos.get(0).toString());

                            if (idlist.size() > Questions.length - questionCount) {
                                optionsList.setItemChecked(selectpos.get(0) - 1, false);
                            }
                        }
                    }
                    ContentValues cv = new ContentValues();
                    cv.put("answergiven", sb.toString());
                    if (ans.get(0).contentEquals(sb.toString())) {
                        //Toast.makeText(getBaseContext(), "Your ans is correct inside main if --else", Toast.LENGTH_SHORT).show();
                        cv.put("marks", 1);
                        cv.put("attampted", "yes");
                    } else if (selectpos.size() != 0) {
                        //Toast.makeText(getBaseContext(), "incorrect ans", Toast.LENGTH_SHORT).show();
                        cv.put("marks", 0);
                        cv.put("attampted", "yes");
                    }

                    Integer uid = new Integer(currentId);
                    if (selectpos.size() > 0) {
                        //Toast.makeText(getBaseContext(),"About to update on next ",Toast.LENGTH_SHORT).show();
                        db.update("AnswerCollection", cv, "_id=?", new String[]{uid.toString()});

                    }
                    selectpos.clear();
                    loadData(0);
                }
            } else {
                MySQLiteHelper helper = new MySQLiteHelper(getBaseContext(), "mydatabase1.db", null, 1);
                SQLiteDatabase db = helper.getReadableDatabase();


                if (selectpos.size() > 1) {
                    sb.setLength(0);

                    char[] aansgiven = ans.get(0).toCharArray();
                    if (selectpos.size() != aansgiven.length) {
                        //Toast.makeText(getBaseContext(),"incorrect ans , lens not equal",Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < selectpos.size(); i++) {
                            sb.append(selectpos.get(i).toString());
                            optionsList.setItemChecked(selectpos.get(i) - 1, false);
                        }
                    } else {
                        int chk;
                        chk = 0;
                        for (int i = 0; i < selectpos.size(); i++) {
                            sb.append(selectpos.get(i).toString());
                            optionsList.setItemChecked(selectpos.get(i) - 1, false);
                            if (selectpos.contains(Integer.parseInt(String.valueOf(aansgiven[i])))) {
                                chk += 1;
                            }
                        }
                        if (chk == selectpos.size()) {
                            //Toast.makeText(getBaseContext(),"ans is correct..",Toast.LENGTH_SHORT).show();
                            sb.setLength(0);
                            sb.append(ans.get(0));
                        } else {
                            //Toast.makeText(getBaseContext(),"Incorrect ans whaen lens are equal",Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (selectpos.size() == 1) {
                        sb.setLength(0);
                        sb.append(selectpos.get(0).toString());
                        if (idlist.size() > Questions.length - questionCount) {
                            optionsList.setItemChecked(selectpos.get(0) - 1, false);
                        }
                    }
                }
                ContentValues cv = new ContentValues();
                cv.put("answergiven", sb.toString());
                if (ans.get(0).contentEquals(sb.toString())) {
                    //Toast.makeText(getBaseContext(), "Your ans is correct inside main else ", Toast.LENGTH_SHORT).show();
                    cv.put("marks", 1);
                    cv.put("attampted", "yes");
                } else if (selectpos.size() != 0) {
                    //Toast.makeText(getBaseContext(), "incorrect ans", Toast.LENGTH_SHORT).show();
                    cv.put("marks", 0);
                    cv.put("attampted", "yes");
                }
                Integer uid = new Integer(currentId);
                if (selectpos.size() > 0) {
                    //Toast.makeText(getBaseContext(),"About to update on next else main",Toast.LENGTH_SHORT).show();
                    db.update("AnswerCollection", cv, "_id=?", new String[]{uid.toString()});
                    selectpos.clear();
                }


                Integer bid = backtrack.get(backtrack.indexOf(currentId) + 1);
                currentId = bid.intValue();
                loadData(1);
                Cursor cursor1 = db.rawQuery("Select answergiven, attampted from AnswerCollection where _id=?", new String[]{bid.toString()});
                //Toast.makeText(getBaseContext(),"cursor1 has : "+cursor1.getCount(),Toast.LENGTH_SHORT).show();
                while (cursor1.moveToNext()) {
                    if (cursor1.getString(1).contentEquals("yes")) {
                        String ans = cursor1.getString(0);
                        ArrayList<Integer> ansgave = new ArrayList<Integer>();
                        for (int i = 0; i < ans.length(); i++) {
                            ansgave.add(Integer.parseInt(String.valueOf(ans.charAt(i))));
                        }

                        optionsList.setEnabled(false);
                        for (int p = 0; p < 4; p++) {
                            if (ansgave.contains(p + 1)) {
                                optionsList.setItemChecked(p, true);
                            } else {
                                optionsList.setItemChecked(p, false);
                            }
                        }
                    } else {
                        //Toast.makeText(getApplicationContext(),"On next click Not attamted",Toast.LENGTH_SHORT).show();
                        for (int p = 0; p < 4; p++) {
                            optionsList.setItemChecked(p, false);
                            optionsList.setEnabled(true);
                            selectpos.clear();
                        }

                    }
                }
                cursor1.close();
            }
        }

        if (view == findViewById(R.id.preExam2)) {
            if (count > 1) {
                count -= 1;
            }
            if (currentId == backtrack.get(0).intValue()) {
                Toast.makeText(getBaseContext(), "This is the first question", Toast.LENGTH_SHORT).show();
            }
//
            else {
                MySQLiteHelper helper = new MySQLiteHelper(getBaseContext(), "mydatabase1.db", null, 1);
                SQLiteDatabase db = helper.getReadableDatabase();
                Integer lid = new Integer(currentId);
                Cursor cursor = db.rawQuery("Select answergiven, attampted from AnswerCollection where _id=?", new String[]{lid.toString()});
                if (cursor.getCount() == 0) {
                    ContentValues val = new ContentValues();
                    val.put("_id", backtrack.get(backtrack.size() - 1));


                    if (selectpos.size() > 1) {
                        sb.setLength(0);

                        char[] aansgiven = ans.get(0).toCharArray();
                        if (selectpos.size() != aansgiven.length) {
                            //Toast.makeText(getBaseContext(),"incorrect ans , lens not equal",Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < selectpos.size(); i++) {
                                sb.append(selectpos.get(i).toString());
                                optionsList.setItemChecked(selectpos.get(i) - 1, false);
                            }
                        } else {
                            int chk;
                            chk = 0;
                            for (int i = 0; i < selectpos.size(); i++) {
                                sb.append(selectpos.get(i).toString());
                                optionsList.setItemChecked(selectpos.get(i) - 1, false);
                                if (selectpos.contains(Integer.parseInt(String.valueOf(aansgiven[i])))) {
                                    chk += 1;
                                }
                            }
                            if (chk == selectpos.size()) {
                                //Toast.makeText(getBaseContext(),"ans is correct..",Toast.LENGTH_SHORT).show();
                                sb.setLength(0);
                                sb.append(ans.get(0));
                            } else {
                                //Toast.makeText(getBaseContext(),"Incorrect ans whaen lens are equal",Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else if (selectpos.size() == 0) {
                        val.put("attampted", "no");
                        sb.setLength(0);
                        sb.append("");
                        val.put("marks", 0);
                    } else {
                        if (selectpos.size() == 1) {
                            sb.setLength(0);
                            sb.append(selectpos.get(0).toString());

                            if (idlist.size() > Questions.length - questionCount) {
                                optionsList.setItemChecked(selectpos.get(0) - 1, false);
                            }
                        }
                    }
                    val.put("answergiven", sb.toString());
                    if (ans.get(0).contentEquals(sb.toString())) {
                        //Toast.makeText(getBaseContext(), "Your ans is correct after next main if", Toast.LENGTH_SHORT).show();
                        val.put("marks", 1);
                        val.put("attampted", "yes");
                    } else if (selectpos.size() != 0) {
                        //Toast.makeText(getBaseContext(), "incorrect ans", Toast.LENGTH_SHORT).show();
                        val.put("marks", 0);
                        val.put("attampted", "yes");
                    }
                    db.insert("AnswerCollection", null, val);
                    selectpos.clear();
                    //Toast.makeText(getBaseContext(),"Inserted on back press",Toast.LENGTH_SHORT).show();

                } else {
                    //Toast.makeText(getBaseContext(), "already in table",Toast.LENGTH_SHORT).show();
                }
                cursor.close();

                ////*****************************************************************************/////

                if (selectpos.size() > 1) {
                    sb.setLength(0);
                    char[] aansgiven = ans.get(0).toCharArray();
                    if (selectpos.size() != aansgiven.length) {
                        //Toast.makeText(getBaseContext(),"incorrect ans , lens not equal",Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < selectpos.size(); i++) {
                            sb.append(selectpos.get(i).toString());
                            optionsList.setItemChecked(selectpos.get(i) - 1, false);
                        }
                    } else {
                        int chk;
                        chk = 0;
                        for (int i = 0; i < selectpos.size(); i++) {
                            sb.append(selectpos.get(i).toString());
                            optionsList.setItemChecked(selectpos.get(i) - 1, false);
                            if (selectpos.contains(Integer.parseInt(String.valueOf(aansgiven[i])))) {
                                chk += 1;
                            }
                        }
                        if (chk == selectpos.size()) {
                            //Toast.makeText(getBaseContext(),"ans is correct..",Toast.LENGTH_SHORT).show();
                            sb.setLength(0);
                            sb.append(ans.get(0));
                        } else {
                            //Toast.makeText(getBaseContext(),"Incorrect ans whaen lens are equal",Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    if (selectpos.size() == 1) {
                        sb.setLength(0);
                        sb.append(selectpos.get(0).toString());
                        if (idlist.size() > Questions.length - questionCount) {
                            optionsList.setItemChecked(selectpos.get(0) - 1, false);
                        }
                    }
                }
                ContentValues cv = new ContentValues();
                cv.put("answergiven", sb.toString());
                if (ans.get(0).contentEquals(sb.toString())) {
                    //Toast.makeText(getBaseContext(), "Your ans is correct", Toast.LENGTH_SHORT).show();
                    cv.put("marks", 1);
                    cv.put("attampted", "yes");
                } else if (selectpos.size() != 0) {
                    // Toast.makeText(getBaseContext(), "incorrect ans", Toast.LENGTH_SHORT).show();
                    cv.put("marks", 0);
                    cv.put("attampted", "yes");
                }

                Integer uid = new Integer(currentId);
                if (selectpos.size() > 0) {
                    // Toast.makeText(getBaseContext(),"About to update",Toast.LENGTH_SHORT).show();
                    db.update("AnswerCollection", cv, "_id=?", new String[]{uid.toString()});
                    selectpos.clear();
                }

                Integer bid = backtrack.get(backtrack.indexOf(currentId) - 1);
                currentId = bid.intValue();
                loadData(1);
                Cursor cursor1 = db.rawQuery("Select answergiven, attampted from AnswerCollection where _id=?", new String[]{bid.toString()});
                while (cursor1.moveToNext()) {
                    if (cursor1.getString(1).contentEquals("yes")) {
                        String ans = cursor1.getString(0);

                        ArrayList<Integer> ansgave = new ArrayList<Integer>();
                        for (int i = 0; i < ans.length(); i++) {
                            ansgave.add(Integer.parseInt(String.valueOf(ans.charAt(i))));
                        }
                        optionsList.setEnabled(false);
                        for (int p = 0; p < 4; p++) {
                            if (ansgave.contains(p + 1)) {
                                optionsList.setItemChecked(p, true);
                            } else {
                                optionsList.setItemChecked(p, false);
                            }
                        }
                    } else {
                        // Toast.makeText(getApplicationContext(),"on back Not attampted",Toast.LENGTH_SHORT).show();
                        for (int p = 0; p < 4; p++) {
                            optionsList.setItemChecked(p, false);
                            optionsList.setEnabled(true);
                            selectpos.clear();
                        }

                    }
                }
                cursor1.close();
            }
        }
        if (view == findViewById(R.id.finishexam2)) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle("Alert Dialog");
            builder.setMessage("Are u sure you want to finish exam ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Toast.makeText(getBaseContext(), "OK clicked!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getBaseContext(), Statistics.class);
                    intent.putIntegerArrayListExtra("track", backtrack);
                    startActivity(intent);
                    finish();
                }
            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //Toast.makeText(getBaseContext(), "No clicked! ", Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
            android.support.v7.app.AlertDialog ad = builder.create();
            ad.show();
        }
    }

        public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            Intent intent = new Intent(getBaseContext(), Statistics.class);
            intent.putIntegerArrayListExtra("track",backtrack);
            startActivity(intent);
            finish();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            Long minits = (millisUntilFinished/1000)/60;
            Long seconds = (millisUntilFinished/1000) % 60;
            currentmilis = millisUntilFinished;
            timer.setText(String.format("%d:%02d", minits, seconds));
            if (minits.intValue()==1 && seconds.intValue()==0){
                //Toast.makeText(getBaseContext(),"1 Minute remaining...",//Toast.LENGTH_LONG).show();
            }
//
            if (minits.intValue()==0 && seconds.intValue()==30){
                //Toast.makeText(getBaseContext(),"Only 30 Seconds remaining...",//Toast.LENGTH_LONG).show();
            }
            //timer.setText("" + millisUntilFinished / 1000);
        }
    }


    private void loadData(int value){
        MySQLiteHelper helper = new MySQLiteHelper(getBaseContext(), "mydatabase1.db", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Integer ids =  new Integer(new Random().nextInt(Questions.length));
        while (!idlist.contains(ids)){
            ids =  new Integer(new Random().nextInt(Questions.length));
        }

        if(value == 0) {
            Cursor cursor = db.rawQuery("Select questions, option1, option2, option3, option4, answer from QuestionCollection where _id=?",new String[]{ids.toString()}); //new String[]{"ids"}
            //Toast.makeText(getBaseContext(),cursor.getCount()+"",//Toast.LENGTH_LONG).show();
            backtrack.add(idlist.get(idlist.indexOf(ids)));
            currentId = ids;
            idlist.remove(idlist.indexOf(ids));
            while (cursor.moveToNext()) {
                et.setText("Question "+count+" : " + cursor.getString(0));
                arrayAdapter.clear();
                arrayAdapter.add(cursor.getString(1));
                arrayAdapter.add(cursor.getString(2));
                arrayAdapter.add(cursor.getString(3));
                arrayAdapter.add(cursor.getString(4));
                ans.clear();
                ans.add(cursor.getString(5));
            }
            cursor.close();
        }
        // value 1 imply backtrack set
        else if(value == 1){

            Integer ids1 = new Integer(currentId);
            Cursor cursor = db.rawQuery("Select questions, option1, option2, option3, option4, answer from QuestionCollection where _id=?",new String[]{ids1.toString()}); //new String[]{"ids"}
            //Toast.makeText(getBaseContext(),cursor.getCount()+"",//Toast.LENGTH_LONG).show();

            while (cursor.moveToNext()) {
                et.setText("Question "+count+" : " + cursor.getString(0));
                arrayAdapter.clear();
                arrayAdapter.add(cursor.getString(1));
                arrayAdapter.add(cursor.getString(2));
                arrayAdapter.add(cursor.getString(3));
                arrayAdapter.add(cursor.getString(4));
                ans.clear();
                ans.add(cursor.getString(5));

            }
            cursor.close();
        }
    }
    private void AddData(){

        dbHelper = new MySQLiteHelper(this, "mydatabase1.db", null, 1);
        db = dbHelper.getWritableDatabase();

        Questions = new String[] {"Which one of these lists contains only Java programming language keywords?","Which will legally declare, construct, and initialize an array?",
                "Which is a reserved word in the Java programming language?","Which one of the following will declare an array and initialize it with five numbers?",
                "Which is the valid declarations within an interface definition?","A constructor","What is byte code in the context of Java?",
                "What output is displayed as the result of executing the following statement?\n" +
                        "System.out.println(\"// Looks like a comment.\");","Which one is a valid declaration of a boolean?","Which is a valid declarations of a String?","What is the numerical range of a char?",
                "You want subclasses in any package to have access to members of a superclass. Which is the most restrictive access that accomplishes this objective?","Which cause a compiler error?",
                "Which one creates an instance of an array?","Which of the following class level (nonlocal) variable declarations will not compile?","Which is a valid declaration within an interface?",
                "Which of the following are legal lines of code?","Which two statements are equivalent?","Which statement is true?","Which statement is true?","Which of the following would compile without error?",
                "What is the name of the method used to start a thread execution?","Which of the following will directly stop the execution of a Thread?","Which of the following special symbol allowed in a variable name in C?",
                "How would you round off a value from 1.66 to 2.0 in C?","Is the following statement a declaration or definition?\n" + "extern int i;"};

        String[] Option1 = new String[]{"class, if, void, long, Int, continue","int [] myList = {\"1\", \"2\", \"3\"};","method","Array a = new Array(5);","public double methoda();","must have the same name as the class it is declared within.",
                "The type of code generated by a Java compiler","// Looks like a comment","boolean b1 = 0;","String s1 = null;","-128 to 127","public","int[ ] scores = {3, 5, 7};",
                "int[ ] ia = new int[15];","protected int a;","public static short stop = 23;","int w = (int)888.8;","16*4","Assertions can be enabled or disabled on a class-by-class basis.",
                "catch(X x) can catch subclasses of X where X is a subclass of Exception.","int a = Math.abs(-5);","init();","notify();","* (asterisk)","ceil(1.66)","Error"};

        String[] Option2 = new String[]{"goto, instanceof, native, finally, default, throws","int [] myList = (5, 8, 2);","native","int [] a = {23,22,21,20,19};","public final double methoda();","is used to create objects.",
                "The type of code generated by a Java Virtual Machine","The statement results in a compilation error","boolean b2 = 'false';","String s2 = 'null';","(215) to (215) - 1","private","int [ ][ ] scores = {2,7,6}, {9,3,45};",
                "float fa = new float[20];","transient int b = 3;","protected short stop = 23;","byte x = (byte)1000L;","16>>2","Conditional compilation is used to allow tested classes to run at full speed.",
                "The Error class is a RuntimeException.","int b = Math.abs(5.0);","start();","exits synchronized code","- (hyphen)","floor(1.66)","definition"};

        String[] Option3 = new String[]{"try, virtual, throw, final, volatile, transient","int myList [] [] = {4,9,7,0};","subclasses","int a [] = new int[5];","static void methoda(double d1);","may be declared private",
                "It is another name for a Java source file","Looks like a comment","boolean b3 = false;","String s3 = (String) 'abc';","0 to 32767","protected","boolean results[ ] = new boolean [] {true, false, true};",
                "char[ ] ca = \"Some String\";","private synchronized int e;","transient short stop = 23;","long y = (byte)100;","16/2^2","Assertions are appropriate for checking the validity of arguments in a method.",
                "Any statement that can throw an Error must be enclosed in a try block.","int c = Math.abs(5.5F);","run();","notifyall()","_ (underscore)","roundto(1.66)","function"};

        String[] Option4 = new String[]{"byte, break, assert, switch, include","int myList [] = {4, 3, 7};","reference","int [5] array;","protected void methoda(double d1);","A, B and C",
                "It is the code written within the instance methods of a class.","No output is displayed","boolean b4 = Boolean.false();","String s4 = (String) '\\ufeed';","0 to 65535","transient","String cats[ ] = {\"Fluffy\", \"Spot\", \"Zeus\"};",
                "int ia[ ] [ ] = { 4, 5, 6 }, { 1,2,3 };","volatile int d;","final void madness(short stop);","All A,B,and C","16<<<2","As of Java version 1.4, assertion statements are compiled by default.",
                "Any statement that can throw an Exception must be enclosed in a try block.","int d = Math.abs(5L);","resume();","wait()","| (pipeline)","roundup(1.66)","declaration"};

        String[] Answer = new String[]{"2","4","2","2","1","4","1","1","3","1","4","3","2","1","3","1","4","2","3","1","1","3","4","3","1","4"};

        db.delete("QuestionCollection",null,null);
        db.delete("AnswerCollection",null,null);

        int i;

        DatabaseUtils.InsertHelper ih = new DatabaseUtils.InsertHelper(db,"QuestionCollection");
        final int col1 = ih.getColumnIndex("questions");
        final int col2 = ih.getColumnIndex("option1");
        final int col3 = ih.getColumnIndex("option2");
        final int col4 = ih.getColumnIndex("option3");
        final int col5 = ih.getColumnIndex("option4");
        final int col6 = ih.getColumnIndex("answer");
        final int col8 = ih.getColumnIndex("_id");
        for (i=0;i<Questions.length;i++){

            ih.prepareForInsert();
            ih.bind(col1, Questions[i]);
            ih.bind(col2,Option1[i]);
            ih.bind(col3,Option2[i]);
            ih.bind(col4,Option3[i]);
            ih.bind(col5,Option4[i]);
            ih.bind(col6,Answer[i]);
            ih.bind(col8,i);
            ih.execute();

            idlist.add(i);
        }

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        MenuItem Item1 = menu.add(Menu.NONE,1,1,"Finish Exam");
//        return true;
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        CharSequence title = item.getTitle();
//        if (title.equals("Finish Exam")) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setIcon(R.mipmap.ic_launcher);
//            builder.setTitle("Alert Dialog");
//            builder.setMessage("Are u sure you want to finish exam ?");
//            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    ////Toast.makeText(getBaseContext(), "OK clicked!", //Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getBaseContext(), Statistics.class);
//                    intent.putIntegerArrayListExtra("track",backtrack);
//                    startActivity(intent);
//                    finish();
//                }
//            })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    //Toast.makeText(getBaseContext(), "No clicked! ", //Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                    );
//            AlertDialog ad = builder.create();
//            ad.show();
//        }
//        return false;
//    }
}
