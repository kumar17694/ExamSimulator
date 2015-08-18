package com.example.prince.examsimulator;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements OnClickListener {
    RadioGroup rg;
//    CharSequence[] items = { "5", "10", "15" };
//    boolean[] itemsChecked = new boolean [items.length];
    int questionCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rg = (RadioGroup) findViewById(R.id.rg);
        Button start = (Button) findViewById(R.id.btn1);
        start.setOnClickListener(this);

        Button instructions = (Button)findViewById(R.id.instruct);
        instructions.setOnClickListener(this);

//        Dialog dialog = new Dialog(this);
//        dialog.setTitle("Instruction");
//        ScrollView sv = new ScrollView(this);
//        sv.setFillViewport(true);
//        LinearLayout ll = new LinearLayout(this);
//        ll.setOrientation(LinearLayout.VERTICAL);
//        ll.setPadding(10,10,10,10);
//        //LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        TextView tv = new TextView(this);
//        tv.setText("1. Every exam is based on objective pattern.Questions may have Multiple options as correct Answer."+"\n"+
//                "2. Once you selected a option for a question and move to next or previous,you can review question but can't change the ans."+"\n"+
//                "3. You can left a question un-attempted and latter you can attampt it."+"\n"+
//                "4. The exam will end automatically once the time is finished or you can manually end it via clicking Finish Exam from menu."+"\n"+
//                "5. Once a exam is finished, Statistics will be shown.From here you can review what you did and can check correct Answer as well."+"\n"+
//                "6. You can also finish the review manually Using Finish Review from menu."+"\n"+
//                "7. You can also see these instructions using Instructions option from menu."+"\n"+
//                "8. Whenever back button is pressed you will be directly taken to home.");
//        ll.addView(tv);
//        sv.addView(ll);
//        dialog.setContentView(sv);
//        dialog.show();

    }

    @Override
    public void onClick(View view) {
        if (view == findViewById(R.id.btn1)) {
            if (rg.getCheckedRadioButtonId() != -1) {
                RadioButton rb = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
                if (rb.getText().toString().contentEquals("General Knowledge")) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setTitle("Choose the Exam Duration : ");
                    final CharSequence[] items1 = { "10", "20", "30" };
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Toast.makeText(getBaseContext(), "OK clicked!", Toast.LENGTH_SHORT).show();
                            Intent start = new Intent(getBaseContext(), GeneralKnowledge.class);
                            start.putExtra("questionCount", questionCount);
                            startActivity(start);
                        }
                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Toast.makeText(getBaseContext(), "Cancel clicked!", Toast.LENGTH_SHORT).show();
                                }
                            })

                            .setSingleChoiceItems(items1, -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int pos) {
                                    questionCount = Integer.parseInt(items1[pos].toString());
                                    Toast.makeText(getApplicationContext(),
                                            "You Choose : " + items1[pos],
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                    android.support.v7.app.AlertDialog ad = builder.create();
                    ad.show();

                } else if (rb.getText().toString().contentEquals("Aptitude and Reasoning")) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setTitle("Choose the Exam Duration : ");
                    final CharSequence[] items2 = { "10", "20", "30" };
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Toast.makeText(getBaseContext(), "OK clicked!", Toast.LENGTH_SHORT).show();
                            Intent start = new Intent(getBaseContext(), AptitudeReasoning.class);
                            start.putExtra("questionCount", questionCount);
                            startActivity(start);
                        }
                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Toast.makeText(getBaseContext(), "Cancel clicked!", Toast.LENGTH_SHORT).show();
                                }
                            })

                            .setSingleChoiceItems(items2, -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int pos) {
                                    questionCount = Integer.parseInt(items2[pos].toString());
                                    Toast.makeText(getApplicationContext(),
                                            "You Choose : " + items2[pos],
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                    android.support.v7.app.AlertDialog ad = builder.create();
                    ad.show();
                } else {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                    builder.setIcon(R.mipmap.ic_launcher);
                    builder.setTitle("Choose the Exam Duration : ");
                    final CharSequence[] items = { "10", "20", "30" };
                    //builder.setMessage("Select the no of questionss you want to have in exam : ");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            //Toast.makeText(getBaseContext(), "OK clicked!", Toast.LENGTH_SHORT).show();
                            Intent start = new Intent(getBaseContext(), Programming.class);
                            start.putExtra("questionCount", questionCount);
                            startActivity(start);
                        }
                    })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    Toast.makeText(getBaseContext(), "Cancel clicked!", Toast.LENGTH_SHORT).show();
                                }
                            })

                            .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int pos) {
                                    questionCount = Integer.parseInt(items[pos].toString());
                                    Toast.makeText(getApplicationContext(),
                                            "You Choose : " + items[pos],
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                    android.support.v7.app.AlertDialog ad = builder.create();
                    ad.show();
                }
            } else {
                Toast.makeText(getBaseContext(), "no Option is selected. Please select a option.", Toast.LENGTH_SHORT).show();
            }
        }
        if (view == findViewById(R.id.instruct)){
            Intent intent = new Intent(getBaseContext(),Instructions.class);
            startActivity(intent);
        }

    }

}
