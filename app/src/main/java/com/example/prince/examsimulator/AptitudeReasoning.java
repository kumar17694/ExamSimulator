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


public class AptitudeReasoning extends AppCompatActivity implements View.OnClickListener {
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
        setContentView(R.layout.activity_aptitude_reasoning);

        et = (TextView) findViewById(R.id.questionEdit3);

        optionsList = (ListView)findViewById(R.id.optionsList3);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked);
        optionsList.setAdapter(arrayAdapter);
        optionsList.setChoiceMode(optionsList.CHOICE_MODE_MULTIPLE);
        optionsList.setTextFilterEnabled(true);
        ans = new ArrayList<String>();
        selectpos = new ArrayList<Integer>();
        idlist = new ArrayList<>();
        backtrack = new ArrayList<>();
        next = (Button)findViewById(R.id.nextExam3);
        next.setOnClickListener(this);
        prev = (Button) findViewById(R.id.preExam3);
        prev.setOnClickListener(this);
        finish = (Button)findViewById(R.id.finishexam3);
        finish.setOnClickListener(this);
        count=1;
        Intent i = getIntent();
        int duration = i.getIntExtra("questionCount",0);
        questionCount = duration/2;

        final long startTime = 60 * 1000*duration;
        final long interval = 1000*1;

        timer = (TextView) findViewById(R.id.timerexam3);
        timer.setTextSize(20);
        timer.setTextColor(Color.rgb(102,102,255));

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
        if (view == findViewById(R.id.nextExam3)){
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

        if (view == findViewById(R.id.preExam3)){
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
        if (view == findViewById(R.id.finishexam3)){
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
                //Toast.makeText(getBaseContext(),"1 Minute remaining...",//Toast.LENGTH_LONG).show();
            }

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

        Questions = new String[] {" When a number is multiplied by 13, it becomes greater to 105 by an amount with which it is lesser to 105 by now. What is the number ",
                "Look at this series: 2, 1, (1/2), (1/4), ... What number should come next?",
                "Look at this series: 36, 34, 30, 28, 24, ... What number should come next?",
                " My successor's father is my fathers son. and i dont have any brothers or sons. Who is my successor?",
                "The length of the bridge, which a train 130 metres long and travelling at 45 km/hr can cross in 30 seconds, is: ",
                "Look at this series: 53, 53, 40, 40, 27, 27, ... What number should come next?",
                "Poles : Magnet :: ? : Battery",
                "If a person walks at 14 km/hr instead of 10 km/hr, he would have walked 20 km more. The actual distance travelled by him is: ",
                "10 cats caught 10 rats in 10 seconds. How many cats are required to catch 100 rats in 100 seconds",
                "Look at this series: 21, 9, 21, 11, 21, 13, 21, ... What number should come next?",
                "Peace : Chaos :: Creation : ?",
                "4 men & 6 women can complete a work in 8 days, while 3 men and 7 women can complete it in 10 days. In how many days will 10 women complete it? ",
                " A father is 30 years older than his son. He will be three times as old as his son after 5 years. What is the father's present age? ",
                "Horse : Mare ::",
                "A car travelling with of its actual speed covers 42 km in 1 hr 40 min 48 sec. Find the actual speed of the car. ",
                "A goat is tied to one corner of a square plot of side 12m by a rope 7m long.Find the area it can graze?",
                " Cricket : Pitch :: ",
                "Oceans : Deserts : : Waves : ?",
                "Cube is related to Square in the same way as Square is related to",
                "Three numbers are in the ratio of 3: 4 :5 respectively. If the sum of the first and third numbers is more than the second number by 52, then which will be the largest number?",
                " Of the following two statements, both of which cannot be true, but both can also be false. Which are these two statements ?",
                "If A is the son of Q, Q and Y are sisters, Z is the mother of Y, P is the son of Z, then which of the following statements is correct ?",
                "Find the odd one out",
                "What is 50% of 40% of Rs. 3,450?",
                "Find the odd one out",
                "Complete Series : SCD, TEF, UGH, ____, WKL",
                "Find the odd one out ",
                "A vendor bought toffees at 6 for a rupee. How many for a rupee must he sell to gain 20%? ",
                "The compound interest on Rs. 30,000 at 7% per annum is Rs. 4347. The period (in years) is: ",
                "Which word does NOT belong with the others?",
                "A alone can do a piece of work in 6 days and B alone in 8 days. A and B undertook to do it for Rs. 3200. With the help of C, they completed the work in 3 days. How much is to be paid to C? ",
                "Window is to pane as book is to",
                "The reflex angle between the hands of a clock at 10.25 is in degrees: "};

        String[] Option1 = new String[]{"10","1/3","20","nephew","200m","12","Energy","50 km","100","14","Manufacture","40","30"," Fox : Vixen","15 km/hr",
                "155 sq.m","Ship : Dock","Dust","Plane","52",
                "All machines make noise","P is the maternal uncle of A","crusade","690","Vapour","CMN","Mango","3","2","basil","Rs. 375","novel","180"};

        String[] Option2 = new String[]{"15","2/8","23","niece","225m","27","Power","56 km","10","15","Destruction","35","35","Duck : Geese","25 km/hr","144 sq.m",
                "Boat : Harbour", "Sand Dunes","Triangle","65"," Some machines are noisy",
                " P and Y are sisters","expedition","670","Mist","UJI"," Papaya","4","2.5","parsley","Rs. 400","glass","197"};

        String[] Option3 = new String[]{"30","1/8","26","daughter","250m","53"," Terminals","70 km","20","21","Build","30","40","Dog : Puppy","30 km/hr","19.25 sq.m",
                "Boxing : Ring","Ripples","LIne","67","No machine makes noise"," A and P are cousins","cruise","580",
                "Hailstone","VIJ","Apple","5","3","dill","Rs. 800","cover","197.5"};

        String[] Option4 = new String[]{"25","1/16","22","myself","245m","14","Cells","80 km","50","23","Construction","25","45","Donkey : Pony","35 km/hr","38.5 sq.m",
                "Wrestling : Track","Sea","Point","72","Some machines are not noisy","None of the above",
                "campaign","570","Fog","IJT","Orange","6","4","mayonnaise","Rs. 450","page","187.5"};

        String[] Answer = new String[]{"2","3","4","3","4","4","3","1","2","2","2","1","3","1","4","4","3","2","3","2","13","1","3","1","1","3","1","3","1","4","2","4","3"};


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

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem Item1 = menu.add(Menu.NONE,1,1,"Finish Exam");
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        CharSequence title = item.getTitle();
        if (title.equals("Finish Exam")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle("Alert Dialog");
            builder.setMessage("Are u sure you want to finish exam ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    ////Toast.makeText(getBaseContext(), "OK clicked!", //Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getBaseContext(), Statistics.class);
                    intent.putIntegerArrayListExtra("track",backtrack);
                    startActivity(intent);
                    finish();
                }
            })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //Toast.makeText(getBaseContext(), "No clicked! ", //Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
            AlertDialog ad = builder.create();
            ad.show();

        }
        return false;
    }
}
