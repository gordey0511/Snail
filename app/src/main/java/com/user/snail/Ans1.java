package com.user.snail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Ans1 extends AppCompatActivity {

    static String name, text;
    static TestForStudent.TestType id;
    TextView textView;
    String ans1,ans2;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ans1);

        setTitle("Результат");

        textView = findViewById(R.id.textViewTextSection);
        floatingActionButton = findViewById(R.id.goAns1);

        id = TestForStudent.TestType.valueOf(getIntent().getStringExtra("id"));
        name = getIntent().getStringExtra("name");
        text = getIntent().getStringExtra("text");


        if(id== TestForStudent.TestType.TYPE_OF_THE_FUTURE_PROFESSION) {
            ans1 = getIntent().getStringExtra("ans1");
        }

        if(id== TestForStudent.TestType.INTERESTS_MAP) {
            ans1 = getIntent().getStringExtra("ans1");
            ans2 = getIntent().getStringExtra("ans2");
        }

        if(ans1!=null) {
            Log.e("onCreateAns1", ans1);
        }

        if(ans2!=null) {
            Log.e("onCreateAns2", ans2);
        }

        if(id!= TestForStudent.TestType.TYPE_OF_THE_FUTURE_PROFESSION) {
            textView.setText("Вам подходит предмет: " + name + "\n" + text);
        }else{
            textView.setText("Вы: " + name + "\n" + text);
        }
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(id== TestForStudent.TestType.TYPE_OF_THE_FUTURE_PROFESSION){
                    Intent intent = new Intent(Ans1.this, TestForStudent.class);
                    intent.putExtra("id", TestForStudent.TestType.INTERESTS_MAP.toString());
                    intent.putExtra("ans1", ans1);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(Ans1.this, ResultDepartment.class);
//                    intent.putExtra("ans1", ans1);
//                    intent.putExtra("ans2", ans2);
                    intent.putExtra("ans1","Здравоохранение и медицинские науки|Науки об обществе|Образование и педагогические науки|Гуманитарные науки");
                    intent.putExtra("ans2","История");
                    startActivity(intent);
                }
            }
        });
    }
}