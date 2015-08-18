package com.example.prince.examsimulator;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


public class Statistics extends AppCompatActivity {
    //SQLiteDatabase db;
    //MySQLiteHelper dbHelper;
    TextView et;
    //ListView optionsList;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> ans;
    ArrayList<Integer> track;
    int attamptcount,correctcount,incorrectcount,totalseen,incorrect;
    Button review,home;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        et = (TextView) findViewById(R.id.msgstst);
        et.setText("You have finished the exam. The Summery is displayed below.");

        final ListView optionsList = (ListView)findViewById(R.id.stats);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        optionsList.setAdapter(arrayAdapter);
        optionsList.setEnabled(false);
        optionsList.setTextFilterEnabled(true);

        Button review = (Button) findViewById(R.id.review);
        Button home = (Button) findViewById(R.id.home);

        track = new ArrayList<>();
        attamptcount=0;
        correctcount = 0;
        //incorrectcount = 0;
        totalseen = 0;
        incorrect =0;
        Intent i = getIntent();
        track = i.getIntegerArrayListExtra("track");

        loadData();
        arrayAdapter.add("Total seen : "+track.size());
        arrayAdapter.add("Total attampted questions : "+attamptcount);
        incorrectcount = attamptcount-correctcount;
        arrayAdapter.add("Total corrected questions : "+correctcount);
        arrayAdapter.add("Total incorrect questions : "+incorrectcount);
        arrayAdapter.add("Correct % over seen questions : "+(correctcount*100/track.size()));
//        if (incorrectcount==incorrect){
//            Toast.makeText(this,"everything is ok....",Toast.LENGTH_LONG).show();
//        }


        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getBaseContext(), Review.class);
                intent.putIntegerArrayListExtra("track1", track);
                startActivity(intent);
                finish();

            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void loadData(){
        MySQLiteHelper helper = new MySQLiteHelper(getBaseContext(), "mydatabase1.db", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select * from AnswerCollection",null); //new String[]{"ids"}
        //Toast.makeText(getBaseContext(), cursor.getCount() + "", Toast.LENGTH_LONG).show();
//        totalseen = cursor.getCount();
//        arrayAdapter.add("Total Seen Questions : " + totalseen);

        while (cursor.moveToNext()) {
            if(cursor.getString(3).contentEquals("yes")){
                attamptcount+=1;
                if (cursor.getInt(2)==0){
                      incorrect+=1;
                }
            }

            if (cursor.getInt(2)==1){
                correctcount+=1;
            }
        }

        cursor.close();
    }

//            Cursor cursor1 = db.rawQuery("Select answergiven, attampted from AnswerCollection where _id=?",new String[]{ids1.toString()});
//            while (cursor1.moveToNext()) {
//
//            }
//            cursor1.close();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
