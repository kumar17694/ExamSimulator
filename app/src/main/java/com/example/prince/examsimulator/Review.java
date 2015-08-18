package com.example.prince.examsimulator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Review extends AppCompatActivity implements View.OnClickListener {
    TextView et;
    ArrayAdapter<String> arrayAdapter;
    //ArrayList<String> ans;
    ArrayList<Integer> ids,ansgiven,correctans,track;
    int currentId,count;
    Button pre,nxt,ans,finish;
    ListView optionsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        et = (TextView) findViewById(R.id.questionrv);

        optionsList = (ListView)findViewById(R.id.optionsrv);
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_checked);
        optionsList.setAdapter(arrayAdapter);
        //optionsList.setEnabled(false);
        optionsList.setTextFilterEnabled(true);
        optionsList.setChoiceMode(optionsList.CHOICE_MODE_MULTIPLE);
        count = 1;

        pre = (Button) findViewById(R.id.prerv);
        pre.setOnClickListener(this);
        nxt = (Button) findViewById(R.id.nextrv);
        nxt.setOnClickListener(this);
        ans = (Button) findViewById(R.id.ans);
        ans.setOnClickListener(this);
        finish = (Button)findViewById(R.id.finishrv);
        finish.setOnClickListener(this);

        ids = new ArrayList<>();
        ansgiven = new ArrayList<>();
        correctans = new ArrayList<>();
        track = new ArrayList<>();

        Intent it = getIntent();
        track = it.getIntegerArrayListExtra("track1");

        //cursor.close();
        currentId = track.get(0).intValue();
        loadData();
        int pos = ansgiven.get(0).intValue()-1;
        if (pos ==4){
            Toast.makeText(getBaseContext(),"this was unattampted question",Toast.LENGTH_SHORT).show();
        }
        for (int p = 0;p<4;p++){
            if (p == pos) {
                optionsList.setItemChecked(pos, true);
            }
            else {
                optionsList.setItemChecked(p,false);
            }
        }

    }

    @Override
    public void onClick(View view) {
        if (view == nxt){
            if (currentId == track.get(track.size()-1).intValue()) {
                Toast.makeText(getBaseContext(), "this is the last question you saw", Toast.LENGTH_SHORT).show();
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setIcon(R.mipmap.ic_launcher);
                builder.setTitle("Alert Dialog");
                builder.setMessage("You have reviewed all the questions." + "\n" + "Do you want to go Home screen?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Toast.makeText(getBaseContext(), "OK clicked!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        //intent.putIntegerArrayListExtra("track",backtrack);
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
            } else {
                count += 1;
                int nid = track.get(track.indexOf(currentId) + 1).intValue();
                currentId = nid;
                loadData();
                optionsList.setEnabled(false);
                //////////////////////////////////////////////////////
                /////////////////////////////////////////////////////13/8/15

                //int pos = ansgiven.get(0).intValue()-1;
                //Toast.makeText(getBaseContext(),"pos is : " +pos,Toast.LENGTH_SHORT).show();
                if (ansgiven.get(0).intValue()-1 == 4){
                    Toast.makeText(getBaseContext(),"this was unattampted question",Toast.LENGTH_SHORT).show();
                }
                for (int p = 0;p<4;p++){
                    if (ansgiven.contains(p+1)) {
                        optionsList.setItemChecked(p, true);
                    }
                    else {
                        optionsList.setItemChecked(p,false);
                    }
                }

                //int pos = ansgiven.get(ids.indexOf(currentId)).intValue()-1;
                //optionsList.setItemChecked(pos,true);
            }

        }
        else if (view == pre){
            if (currentId == track.get(0).intValue()){
                Toast.makeText(getBaseContext(),"This was the first question",Toast.LENGTH_SHORT).show();
            }
            else {
                count-=1;
                int pid = track.get(track.indexOf(currentId)-1).intValue();
                currentId = pid;
                loadData();
                optionsList.setEnabled(false);
                //int pos = ansgiven.get(0).intValue()-1;
                //Toast.makeText(getBaseContext(),"pos is : " +pos,Toast.LENGTH_SHORT).show();
                if (ansgiven.get(0).intValue()-1 ==4){
                    Toast.makeText(getBaseContext(),"this was unattampted question",Toast.LENGTH_SHORT).show();
                }
                for (int p = 0;p<4;p++){
                    if (ansgiven.contains(p+1)) {
                        optionsList.setItemChecked(p, true);
                    }
                    else {
                        optionsList.setItemChecked(p,false);
                    }
                }


                //int pos = ansgiven.get(ids.indexOf(currentId)).intValue()-1;
                //optionsList.setItemChecked(pos,true);
            }
        }
        else if (view == ans){
            Dialog dialog = new Dialog(this);
            dialog.setTitle("Correct Answer");
            LinearLayout ll = new LinearLayout(this);
            ll.setOrientation(LinearLayout.VERTICAL);
            ll.setPadding(10, 10, 10, 10);
            //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            TextView tv = new TextView(this);
            tv.setText("The Correct Ans is : ");
            ll.addView(tv);
            TextView tv2 = new TextView(this);
            tv2.setText("Option " + correctans.get(0));
            ll.addView(tv2);
            dialog.setContentView(ll);
            dialog.show();

            //Toast.makeText(getBaseContext(),"correct  ans is : option "+correctans.get(0),Toast.LENGTH_SHORT).show();
        }
        else if (view == finish){
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setTitle("Alert Dialog");
            builder.setMessage("Are u sure you want to end Reviewing ?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //Toast.makeText(getBaseContext(), "OK clicked!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    //intent.putIntegerArrayListExtra("track",backtrack);
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

    private void loadData(){
        MySQLiteHelper helper = new MySQLiteHelper(getBaseContext(), "mydatabase1.db", null, 1);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.rawQuery("Select questions, option1, option2, option3, option4, answer from QuestionCollection where _id=?",new String[]{new Integer(currentId).toString()}); //new String[]{"ids"}
        //Toast.makeText(getBaseContext(),cursor.getCount()+"",Toast.LENGTH_LONG).show();

        while (cursor.moveToNext()) {
            et.setText("Question " + count + " : " + cursor.getString(0));
            arrayAdapter.clear();
            arrayAdapter.add(cursor.getString(1));
            arrayAdapter.add(cursor.getString(2));
            arrayAdapter.add(cursor.getString(3));
            arrayAdapter.add(cursor.getString(4));
            correctans.clear();
            correctans.add(Integer.parseInt(cursor.getString(5)));
            //arrayAdapter.add("You Answered : Option "+ansgiven.get(ids.indexOf(currentId)));
        }
        cursor.close();

        Cursor cursor1 = db.rawQuery("Select attampted, answergiven from AnswerCollection where _id=?",new String[]{new Integer(currentId).toString()});
        if (cursor1.getCount()==0){
            ansgiven.clear();
            ansgiven.add(5);
        }
        while (cursor1.moveToNext()){
            if (cursor1.getString(0).contentEquals("yes")){
                ansgiven.clear();
                String ans = cursor1.getString(1);
                ///////////////////// 13/8/15
                //ArrayList<Integer> ansgave =  new ArrayList<Integer>();
                for (int i =0;i<ans.length();i++){
                    ansgiven.add(Integer.parseInt(String.valueOf(ans.charAt(i))));
                }
                //ansgiven.add(Integer.parseInt(cursor1.getString(1)));
            }
            else {
                ansgiven.clear();
                ansgiven.add(5);
            }
        }
        cursor1.close();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        //getMenuInflater().inflate(R.menu.menu_exam2, menu);
//        MenuItem Item1 = menu.add(Menu.NONE,1,1,"Finish Review");
//        return true;
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        //int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        CharSequence title = item.getTitle();
//        if (title.equals("Finish Review")) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setIcon(R.mipmap.ic_launcher);
//            builder.setTitle("Alert Dialog");
//            builder.setMessage("Are u sure you want to end Reviewing ?");
//            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int whichButton) {
//                    //Toast.makeText(getBaseContext(), "OK clicked!", Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
//                    //intent.putIntegerArrayListExtra("track",backtrack);
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
//            //Intent intent = new Intent(getBaseContext(),Statistics.class);
//            //startActivity(intent);
//        }
//        return false;
//    }
}
