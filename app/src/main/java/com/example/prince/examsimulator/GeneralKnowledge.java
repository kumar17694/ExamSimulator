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


public class GeneralKnowledge extends AppCompatActivity implements View.OnClickListener{
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
        setContentView(R.layout.activity_general_knowledge);

        et = (TextView) findViewById(R.id.questionEdit1);

        optionsList = (ListView)findViewById(R.id.optionsList1);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked);
        optionsList.setAdapter(arrayAdapter);
        optionsList.setChoiceMode(optionsList.CHOICE_MODE_MULTIPLE);
        optionsList.setTextFilterEnabled(true);
        ans = new ArrayList<String>();
        selectpos = new ArrayList<Integer>();
        idlist = new ArrayList<>();
        backtrack = new ArrayList<>();
        next = (Button)findViewById(R.id.nextExam1);
        next.setOnClickListener(this);
        prev = (Button) findViewById(R.id.preExam1);
        prev.setOnClickListener(this);
        finish = (Button)findViewById(R.id.finishexam1);
        finish.setOnClickListener(this);
        count=1;
        Intent i = getIntent();
        int duration = i.getIntExtra("questionCount", 0);
        questionCount = duration;

        final long startTime = 60 * 1000*duration;
        final long interval = 1000*1;

        timer = (TextView) findViewById(R.id.timerexam1);
        timer.setTextSize(20);
        timer.setTextColor(Color.rgb(102, 102, 255));

        countDownTimer = new MyCountDownTimer(startTime, interval);
        Long minits = (startTime/1000)/60;
        Long seconds = (startTime/1000) % 60;
        timer.setText(String.format("%d:%02d", minits, seconds));
        //timer.setText(String.valueOf(startTime / 1000));
        countDownTimer.start();

        AddData();

        loadData(0);

        optionsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                ListView l = (ListView) parent;
                if (l.isItemChecked(position)) {
                    selectpos.add(position + 1);
                    //Toast.makeText(getBaseContext(), "Option " + (position + 1) + " Selected .", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(getBaseContext(), "Option " + (position + 1) + " Deselected", Toast.LENGTH_SHORT).show();
                    selectpos.remove(selectpos.indexOf(position + 1));
                }
            }

        });

    }

    @Override
    public void onClick(View view) {
        if (view == findViewById(R.id.nextExam1)){
            // count < no of question
            if (count<questionCount){
                count+=1;
            }
            dbHelper = new MySQLiteHelper(getBaseContext(), "mydatabase1.db", null,1);
            db = dbHelper.getWritableDatabase();
            if (currentId == backtrack.get(backtrack.size()-1).intValue() ) {
                optionsList.setEnabled(true);
                Cursor cursor = db.rawQuery("Select answergiven, attampted from AnswerCollection where _id=?", new String[]{backtrack.get(backtrack.size()-1).toString()});
                if (cursor.getCount()==0 ) {
                    //Toast.makeText(getBaseContext(), "for next ques", Toast.LENGTH_SHORT).show();
                    ContentValues valus = new ContentValues();
                    valus.put("_id", backtrack.get(backtrack.size() - 1));
                    if (selectpos.size() > 1) {
                        sb.setLength(0);

                        char[] aansgiven = ans.get(0).toCharArray();
                        if (selectpos.size()!=aansgiven.length){
                            //Toast.makeText(getBaseContext(),"incorrect ans , lens not equal",Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < selectpos.size(); i++) {
                                sb.append(selectpos.get(i).toString());
                                optionsList.setItemChecked(selectpos.get(i) - 1, false);
                            }
                        }
                        else {
                            int chk;
                            chk = 0;
                            for (int i = 0; i < selectpos.size(); i++) {
                                sb.append(selectpos.get(i).toString());
                                optionsList.setItemChecked(selectpos.get(i) - 1, false);
                                if (selectpos.contains(Integer.parseInt(String.valueOf(aansgiven[i])))) {
                                    chk+=1;
                                }
                            }
                            if (chk==selectpos.size()){
                                //Toast.makeText(getBaseContext(),"ans is correct..",Toast.LENGTH_SHORT).show();
                                sb.setLength(0);
                                sb.append(ans.get(0));
                            }
                            else {
                                ///Toast.makeText(getBaseContext(),"Incorrect ans whaen lens are equal",Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                    else if (selectpos.size() == 0) {
                        valus.put("attampted", "no");
                        sb.setLength(0);
                        sb.append("");
                        valus.put("marks", 0);
                    } else {
                        if (selectpos.size() == 1) {
                            sb.setLength(0);
                            sb.append(selectpos.get(0).toString());
                            if (idlist.size()>Questions.length-questionCount) {
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
                    if (idlist.size()>Questions.length-questionCount){
                        selectpos.clear();
                        loadData(0);

                    }
                }else if (idlist.size()==(Questions.length-questionCount)){
                    Toast.makeText(getBaseContext(), "This is the last question", Toast.LENGTH_LONG).show();
                }
                else {
                    //Toast.makeText(getBaseContext(),"itis already in db",Toast.LENGTH_SHORT).show();
                    if (selectpos.size() > 1) {
                        sb.setLength(0);
                        char[] aansgiven = ans.get(0).toCharArray();
                        if (selectpos.size()!=aansgiven.length){
                            //Toast.makeText(getBaseContext(),"incorrect ans , lens not equal",Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < selectpos.size(); i++) {
                                sb.append(selectpos.get(i).toString());
                                optionsList.setItemChecked(selectpos.get(i) - 1, false);
                            }
                        }
                        else {
                            int chk;
                            chk = 0;
                            for (int i = 0; i < selectpos.size(); i++) {
                                sb.append(selectpos.get(i).toString());
                                optionsList.setItemChecked(selectpos.get(i) - 1, false);
                                if (selectpos.contains(Integer.parseInt(String.valueOf(aansgiven[i])))) {
                                    chk+=1;
                                }
                            }
                            if (chk==selectpos.size()){
                                //Toast.makeText(getBaseContext(),"ans is correct..",Toast.LENGTH_SHORT).show();
                                sb.setLength(0);
                                sb.append(ans.get(0));
                            }
                            else {
                                //Toast.makeText(getBaseContext(),"Incorrect ans whaen lens are equal",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else {
                        if(selectpos.size()==0){
                            for (int p =0;p<4;p++){
                                optionsList.setItemChecked(p, false);
                            }
                        }

                        if (selectpos.size() == 1) {
                            sb.setLength(0);
                            sb.append(selectpos.get(0).toString());

                            if (idlist.size()>Questions.length-questionCount) {
                                optionsList.setItemChecked(selectpos.get(0) - 1, false);
                            }
                        }
                    }
                    ContentValues cv = new ContentValues();
                    cv.put("answergiven",sb.toString());
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
                    if (selectpos.size()>0){
                        //Toast.makeText(getBaseContext(),"About to update on next ",Toast.LENGTH_SHORT).show();
                        db.update("AnswerCollection", cv, "_id=?", new String[]{uid.toString()});

                    }
                    selectpos.clear();
                    loadData(0);
                }
            }
            else{
                MySQLiteHelper helper = new MySQLiteHelper(getBaseContext(), "mydatabase1.db", null, 1);
                SQLiteDatabase db = helper.getReadableDatabase();


                if (selectpos.size() > 1) {
                    sb.setLength(0);

                    char[] aansgiven = ans.get(0).toCharArray();
                    if (selectpos.size()!=aansgiven.length){
                        //Toast.makeText(getBaseContext(),"incorrect ans , lens not equal",Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < selectpos.size(); i++) {
                            sb.append(selectpos.get(i).toString());
                            optionsList.setItemChecked(selectpos.get(i) - 1, false);
                        }
                    }
                    else {
                        int chk;
                        chk = 0;
                        for (int i = 0; i < selectpos.size(); i++) {
                            sb.append(selectpos.get(i).toString());
                            optionsList.setItemChecked(selectpos.get(i) - 1, false);
                            if (selectpos.contains(Integer.parseInt(String.valueOf(aansgiven[i])))) {
                                chk+=1;
                            }
                        }
                        if (chk==selectpos.size()){
                            //Toast.makeText(getBaseContext(),"ans is correct..",Toast.LENGTH_SHORT).show();
                            sb.setLength(0);
                            sb.append(ans.get(0));
                        }
                        else {
                            //Toast.makeText(getBaseContext(),"Incorrect ans whaen lens are equal",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    if (selectpos.size() == 1) {
                        sb.setLength(0);
                        sb.append(selectpos.get(0).toString());
                        if (idlist.size()>Questions.length-questionCount) {
                            optionsList.setItemChecked(selectpos.get(0) - 1, false);
                        }
                    }
                }
                ContentValues cv = new ContentValues();
                cv.put("answergiven",sb.toString());
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
                if (selectpos.size()>0){
                    //Toast.makeText(getBaseContext(),"About to update on next else main",Toast.LENGTH_SHORT).show();
                    db.update("AnswerCollection", cv, "_id=?", new String[]{uid.toString()});
                    selectpos.clear();
                }


                Integer bid = backtrack.get(backtrack.indexOf(currentId)+1);
                currentId = bid.intValue();
                loadData(1);
                Cursor cursor1 = db.rawQuery("Select answergiven, attampted from AnswerCollection where _id=?",new String[]{bid.toString()});
                //Toast.makeText(getBaseContext(),"cursor1 has : "+cursor1.getCount(),Toast.LENGTH_SHORT).show();
                while (cursor1.moveToNext()) {
                    if (cursor1.getString(1).contentEquals("yes")){
                        String ans = cursor1.getString(0);
                        ArrayList<Integer> ansgave =  new ArrayList<Integer>();
                        for (int i =0;i<ans.length();i++){
                            ansgave.add(Integer.parseInt(String.valueOf(ans.charAt(i))));
                        }

                        optionsList.setEnabled(false);
                        for(int p = 0;p<4;p++) {
                            if (ansgave.contains(p+1)) {
                                optionsList.setItemChecked(p, true);
                            }
                            else {
                                optionsList.setItemChecked(p,false);
                            }
                        }
                    }
                    else{
                        //Toast.makeText(getApplicationContext(),"On next click Not attamted",Toast.LENGTH_SHORT).show();
                        for (int p = 0;p<4;p++){
                            optionsList.setItemChecked(p, false);
                            optionsList.setEnabled(true);
                            selectpos.clear();
                        }

                    }
                }
                cursor1.close();
            }
        }

        if (view == findViewById(R.id.preExam1)){
            if (count>1){
                count-=1;
            }
            if(currentId == backtrack.get(0).intValue()){
                Toast.makeText(getBaseContext(),"This is the first question",Toast.LENGTH_SHORT).show();
            }
//
            else {
                MySQLiteHelper helper = new MySQLiteHelper(getBaseContext(), "mydatabase1.db", null, 1);
                SQLiteDatabase db = helper.getReadableDatabase();
                Integer lid = new Integer(currentId);
                Cursor cursor = db.rawQuery("Select answergiven, attampted from AnswerCollection where _id=?", new String[]{lid.toString()});
                if (cursor.getCount()==0){
                    ContentValues val = new ContentValues();
                    val.put("_id",backtrack.get(backtrack.size() - 1));


                    if (selectpos.size() > 1) {
                        sb.setLength(0);

                        char[] aansgiven = ans.get(0).toCharArray();
                        if (selectpos.size()!=aansgiven.length){
                            //Toast.makeText(getBaseContext(),"incorrect ans , lens not equal",Toast.LENGTH_SHORT).show();
                            for (int i = 0; i < selectpos.size(); i++) {
                                sb.append(selectpos.get(i).toString());
                                optionsList.setItemChecked(selectpos.get(i) - 1, false);
                            }
                        }
                        else {
                            int chk;
                            chk = 0;
                            for (int i = 0; i < selectpos.size(); i++) {
                                sb.append(selectpos.get(i).toString());
                                optionsList.setItemChecked(selectpos.get(i) - 1, false);
                                if (selectpos.contains(Integer.parseInt(String.valueOf(aansgiven[i])))) {
                                    chk+=1;
                                }
                            }
                            if (chk==selectpos.size()){
                                //Toast.makeText(getBaseContext(),"ans is correct..",Toast.LENGTH_SHORT).show();
                                sb.setLength(0);
                                sb.append(ans.get(0));
                            }
                            else {
                                //Toast.makeText(getBaseContext(),"Incorrect ans whaen lens are equal",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else if (selectpos.size() == 0) {
                        val.put("attampted", "no");
                        sb.setLength(0);
                        sb.append("");
                        val.put("marks", 0);
                    } else {
                        if (selectpos.size() == 1) {
                            sb.setLength(0);
                            sb.append(selectpos.get(0).toString());

                            if (idlist.size()>Questions.length-questionCount) {
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

                }
                else {
                    //Toast.makeText(getBaseContext(), "already in table",Toast.LENGTH_SHORT).show();
                }
                cursor.close();

                ////*****************************************************************************/////

                if (selectpos.size() > 1) {
                    sb.setLength(0);
                    char[] aansgiven = ans.get(0).toCharArray();
                    if (selectpos.size()!=aansgiven.length){
                        //Toast.makeText(getBaseContext(),"incorrect ans , lens not equal",Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < selectpos.size(); i++) {
                            sb.append(selectpos.get(i).toString());
                            optionsList.setItemChecked(selectpos.get(i) - 1, false);
                        }
                    }
                    else {
                        int chk;
                        chk = 0;
                        for (int i = 0; i < selectpos.size(); i++) {
                            sb.append(selectpos.get(i).toString());
                            optionsList.setItemChecked(selectpos.get(i) - 1, false);
                            if (selectpos.contains(Integer.parseInt(String.valueOf(aansgiven[i])))) {
                                chk+=1;
                            }
                        }
                        if (chk==selectpos.size()){
                            //Toast.makeText(getBaseContext(),"ans is correct..",Toast.LENGTH_SHORT).show();
                            sb.setLength(0);
                            sb.append(ans.get(0));
                        }
                        else {
                            //Toast.makeText(getBaseContext(),"Incorrect ans whaen lens are equal",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else {
                    if (selectpos.size() == 1) {
                        sb.setLength(0);
                        sb.append(selectpos.get(0).toString());
                        if (idlist.size()>Questions.length-questionCount) {
                            optionsList.setItemChecked(selectpos.get(0) - 1, false);
                        }
                    }
                }
                ContentValues cv = new ContentValues();
                cv.put("answergiven",sb.toString());
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
                if (selectpos.size()>0){
                    // Toast.makeText(getBaseContext(),"About to update",Toast.LENGTH_SHORT).show();
                    db.update("AnswerCollection", cv, "_id=?", new String[]{uid.toString()});
                    selectpos.clear();
                }

                Integer bid = backtrack.get(backtrack.indexOf(currentId)-1);
                currentId = bid.intValue();
                loadData(1);
                Cursor cursor1 = db.rawQuery("Select answergiven, attampted from AnswerCollection where _id=?",new String[]{bid.toString()});
                while (cursor1.moveToNext()) {
                    if (cursor1.getString(1).contentEquals("yes")){
                        String ans = cursor1.getString(0);

                        ArrayList<Integer> ansgave =  new ArrayList<Integer>();
                        for (int i =0;i<ans.length();i++){
                            ansgave.add(Integer.parseInt(String.valueOf(ans.charAt(i))));
                        }
                        optionsList.setEnabled(false);
                        for(int p = 0;p<4;p++) {
                            if (ansgave.contains(p+1)) {
                                optionsList.setItemChecked(p, true);
                            }
                            else {
                                optionsList.setItemChecked(p,false);
                            }
                        }
                    }
                    else{
                        // Toast.makeText(getApplicationContext(),"on back Not attampted",Toast.LENGTH_SHORT).show();
                        for (int p = 0;p<4;p++){
                            optionsList.setItemChecked(p, false);
                            optionsList.setEnabled(true);
                            selectpos.clear();
                        }

                    }
                }
                cursor1.close();
            }
        }
        if (view == findViewById(R.id.finishexam1)){
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle("Alert Dialog");
            builder.setMessage("Are u sure you want to finish exam ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Toast.makeText(getBaseContext(), "OK clicked!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getBaseContext(), Statistics.class);
                    intent.putIntegerArrayListExtra("track",backtrack);
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
                Toast.makeText(getBaseContext(),"1 Minute remaining...",Toast.LENGTH_LONG).show();
            }

//
            if (minits.intValue()==0 && seconds.intValue()==30){
                Toast.makeText(getBaseContext(),"Only 30 Seconds remaining...",Toast.LENGTH_LONG).show();
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
            //Toast.makeText(getBaseContext(),cursor.getCount()+"",Toast.LENGTH_LONG).show();
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
            //Toast.makeText(getBaseContext(),cursor.getCount()+"",Toast.LENGTH_LONG).show();

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

        Questions = new String[] {"The area in the vicinity of which among the following was earlier known as 'Jangal Desh'?","The Moon is a :","In which year India Joined the United Nations? ","Which language was patronised by the the rules of Delhi Sultanate? ","The Vijay Shahi coins were in use in which among the following areas of Rajasthan?","Who was the architect of modern Mumbai?",
                "Who receives Dronacharya Award? ","Who discovered magnetic field of electric current? ","Grand Central Terminal, Park Avenue, New York is the world\'s","Who was the first Indian to be elected to the British Parliament? ",
                "'International Yoga Day' celebrate on -","Which country leads in the production of rubber? "," Which team is the winner of 8th IPL league?","Entomology is the science that studies","The Panchayat Raj is a ","Which is the most irrigated State in India? ","A deadly virus suddenly appear in South Korea, the name of that virus is -",
                "Garampani sanctuary is located at","For which of the following disciplines is Nobel Prize awarded?","The nuclear force is -","Fastest shorthand writer was","Headquarters of Amnesty International is at ","The Yellow Stone National Park is in ","Which of the following animals was not native to India ?","Which of the following states is highly flood prone as well as drought prone ? ",
                "Days and Nights are equal throughout the globe when the sun is above ","Seasonal unemployment refers to ","Which is at the apex of the three tier system of Panchayati Raj system ?",
                " Which of the following gases is used for refrigeration ?","Aspirin is the common name of ","Radio Carbon Dating is use to estimate the age of -","Ink is prepared from -","National AIDS research Institute is at -","Who among the following was the 23rd Jain Tirthankara ?","Epsom (England) is the place associated with",
                "Who among the following succeeded Samudragupta as the next ruler of Gupta Dynasty","FFC stands for","The first metal used by the man was ", "Golf player Vijay Singh belongs to which country?","The equator passes through which of the following continents ? "," Which country unveiled the world\'s first facial recognition ATM?",
                "BRICS summit 2015 held at -","\"Love @ Facebook\" is written by -"," In Union Budget, GDP growth rate for the year 2015-16 has been projected at -","First Afghan War took place in"," World Milk Day is observed on which of the following dates?","The water in an open pond remains cool even in hot summer because ",
                " It is not advisable to sleep under a tree at night because of the "," Which one of the following is a fast growing tree ?","Which of the following is a balanced fertiliser for plants ? ","Gulf cooperation council was originally formed by","Who is the father of Geometry?","What is common between Kutty, Shankar, Laxman and Sudhir Dar?",
                "Who was known as Iron man of India?","The Indian to beat the computers in mathematical wizardry is","Jude Felix is a famous Indian player in which of the fields?","Prime Minister Narendra Modi launched India\'s first indigenously developed vaccine _________ on March 9, 2015.","Who has won the 62nd National Film Awards as best actor?",
                "Decrease in white blood cells results in ","Mouth and foot diseases in cattle are caused due to","Ajanta Caves are located in the State of ",". Which state has been ranked no.1 in the mutual fund landscape in the country?","Who is the chairman of the Panel on Net neutrality that submitted its report recently?","The first Asian Games were held at",
                "Which state is the winner of Ranji Trophy 2014-15?","Which one of the following pairs is not correctly matched?","Who among the following was an eminent painter?"," Professor Amartya Sen is famous in which of the fields?","The two highest gallantry awards in India are ","The case of dispute in the presidential election is referred to",
                "Which of the following is a dance- drama ?","Liberty, Equality and Fraternity, this inspiration was derived from ","Who was the Prime Minister of U.K. at the time of India\'s Independence ?","Who was the ICC Cricket world Cup 2015 ambassador?"};

        String[] Option1 = new String[] {"Jodhpur & Bikaner","setellite","1954","Hindi","Jaipur","Punjab Rao Deshmukh","Movie Actors","Ampere","Largest railway station","Motilal Nehru","5 June","India","Channai Super King","Behavior of human beings","One tier System",
                "Punjab","Ebola","Junagarh, Gujarat","Physics and Chemistry","Short range repulsive force","Dr. G. D. Bist","New York","Maldives","Rhinoceros","West Bengal",
                "Poles","Banks","Gram Sabha","Chlorine","salicylic acid","Monuments","Dye","Chennai","Nemi Natha","Horse racing","Chandragupta II","Foreign Finance Corporation","Iron",
                "USA","Africa","USA","Shanghai","Hindol Sengupta","8.0 to 8.5%","1839","1 June","Of continuous evaporation of water","Release of oxygen in lesser amount","Teak","Urea","Bahrain, Kuwait, Oman, Qatar, Saudi Arabia and United Arab Emirates","Aristotle","Film Direction",
                "Govind Ballabh Pant","Ramanujam","Volleyball","Rotavirus","Bobby Simhaa","Decrease in Antibodies","Bacteria","Maharashtra","Karnataka","A K Bhargava","New Delhi in 1950","Mumbai","Volkswagen : Germany",
                "Sarada Ukil","Biochemistry","Param Vir Chakra and Maha Vir Chakra","Chief Election Commissioner","Kathakali","American Revolution","Lord Attlee"," Riki Ponting"};

        String[] Option2 = new String[] {"Bhartpur & Alwar","combet","1955","Arebik","Jodhpur","Yashwantrao Chavan","Scientists","Faraday","Highest railway station","Dadabhai Naoroji","20 June","Australia","Kolkata Knight Rider",
                "Insects","Two tier System","Bihar","MERS", "Diphu, Assam","Literature, Peace and Economics","Short range attractive force","J.M. Tagore", "Washington","USA","Elephant","Madhya Pradesh",
                "Tropic of cancer","Agriculture", "Gram Panchayat","Phosphine"," acetyl salicylic acid","Soil","Starch","New Delhi","Mahaveer","Polo","Chandragupta I","Film Finance Corporation","Aluminium",
                "Fiji","Australia","China","Ufa","Anurag Mathur","7.0 to 7.5%","1835","1 May","Water radiates heat more rapidly than the atmosphere","Release of oxygen in larger amount","Eucalyptus","Ammonia sulphate","Second World Nations","Pythagoras","Drawing Cartoons",
                "Jawaharlal Nehru","Rina Panigrahi","Tennis","Oral Polio","Irfan Khan"," Increase in Antigens","Virus","Gujarat","Delhi","S K Gupta","Kuala Lumpur in 1952","Tamil Nadu","Nissan : Japan",
                "Uday Shanker","Econmics","Param Vir Chakra and Vir Chakra","Supreme Court","Bharatnatyam"," French Revolution","Winston Churchill","Sachin Tendulkar"};

        String[] Option3 = new String[]{"Jaipur & Ajmer","Star","1956","Persian","Ajmer","Jagannath (Nana) Sunkersett","Sportsmen","Fleming","Lowest railway station","Mahatma Gandhi","21 June","Malasia","Mumbai Indian",
                "The origin and history of technical and scientific terms","Three tier System","Uttr Pradesh","SERS","Kohima, Nagaland","Physiology or Medicine","Long range repulsive force","J.R.D. Tata",
                "London","France","Horse","Bihar","Equator","Public Sector","Zila Parishad","Ammonia","salicylate","Rocks","Tannin","Mumbai","Parshvanath","Shooting","Visnugupata","Federation of Football Council","Copper",
                "UK","Europ","Japan","Rio De janeiro","Chetan Bhagat","7.5 to 7.5%","1857","17 June","Water absorbs heat more rapidly than the atmosphere","Release of carbon monoxide","Banyan"," Nitrates","Third World Nations","Euclid","Instrumental Music",
                "Subhash Chandra Bose","Raja Ramanna","Football","Influenza","Vijay","ncrease in Antibodies","Fungi","Tamil Nadu","Maharashtra","P K Singh","Bangkok in 1952"," Karnataka","Hyundai : North Korea","" +
                "V. Shantaram","Electonics","Ashok Chakra and Maha Vir Chakra","Parliament","Odissi"," Russian Revolution","Lord Mountbatten"," M. S. Dhoni"};

        String[] Option4 = new String[]{"Nagaur & Sikar","Planet","1957","English","Kota","Vasantdada Patil","Sports Coaches","Edison","None of above","Gopalakrishna Gokhale","22 June","Myanmar","Royal Challengers Bangalore",
                "The formation of rocks","Four tier System","Andhra Pradesh", "influenza","Gangtok, Sikkim","All are correct","Long range attractive force","Khudada Khan","Berlin", "China","Tiger","Uttar pradesh",
                " Tropic of Capricorn","Private Sector","Panchayat Samiti","Sulphur dioxide","methyl salicylate","Fosills","Latex","Pune","None","Snooker","Mahendra","None of the above","Gold",
                "India","None","Russia","Moscow","Nikita Singh","8.5 to 9.0%","1847","15 March","Water absorbs heat less rapidly than the atmosphere","Release of carbon dioxide","Coconut","Compost","China,USA,France,UK","Kepler","Classical Dance",
                "Sardar Vallabhbhai Patel","Shakunthala Devi","Hockey"," Oral Typhoid","Amir Khan","None of the above"," Penicillium"," West Bengal","Punjab","S N Kumar","Singapore in 1952","West Bengal","Chevrolet : America",
                "Meherally","Geology","Param Vir Chakra and Ashok Chakra","None of the above","Manipuri","None of the above","Harold Wilson","AB de Villiers"};

        String[] Answer = new String[]{"1","1","2","3","2","3","4","2","1","2","3","3","3","2","3","2","2","2","4","2","1","3","2","3","3","3","2","3","3","2","4","1","4",
                "3","1","1","2","3","2","1","2","2","4","1","1","1","4","4","2","4","1","3","2","4","4","4","1","3","1","2","1","3","1","1","3","3","1","3","1","2","1","2",
                "1","2"};

        db.delete("QuestionCollection",null,null);
        db.delete("AnswerCollection",null,null);
        //db.execSQL("drop table if exists QuestionCollection");

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
        //Toast.makeText(getBaseContext(),"data Added",Toast.LENGTH_LONG).show();
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuItem Item1 = menu.add(Menu.NONE,1,1,"Finish Exam");
//        return true;
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        CharSequence title = item.getTitle();
//        if (title.equals("Finish Exam")) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setIcon(R.mipmap.ic_launcher);
//            builder.setTitle("Alert Dialog");
//            builder.setMessage("Are u sure you want to finish exam ?");
//            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    //Toast.makeText(getBaseContext(), "OK clicked!", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getBaseContext(), Statistics.class);
//                    intent.putIntegerArrayListExtra("track",backtrack);
//                    startActivity(intent);
//                    finish();
//                }
//            })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int whichButton) {
//                                    //Toast.makeText(getBaseContext(), "No clicked! ", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                    );
//            AlertDialog ad = builder.create();
//            ad.show();
//        }
//        return false;
//    }
}
