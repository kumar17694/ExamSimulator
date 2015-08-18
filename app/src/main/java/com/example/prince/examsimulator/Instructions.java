package com.example.prince.examsimulator;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class Instructions extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);
        TextView tv = (TextView) findViewById(R.id.tvinstruct);
        tv.setText(
                "1. Every exam is based on objective pattern.Questions may have Multiple options as correct Answer." + "\n" +
                "2. Once you selected a option for a question and move to next or previous,you can review question but can't change the ans." + "\n" +
                "3. You can left a question un-attempted and latter you can attampt it." + "\n" +
                "4. The exam will end automatically once the time is finished or you can manually end it via clicking Finish Exam" + "\n" +
                "5. Once a exam is finished, Statistics will be shown.From here you can review what you did and can check correct Answer as well." + "\n" +
                "\"6. Whenever back button is pressed you will be directly taken to home.\"");
    }

}
