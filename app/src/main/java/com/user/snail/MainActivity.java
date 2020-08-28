package com.user.snail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent=new Intent(MainActivity.this, TestForStudent.class);
        intent.putExtra("id", TestForStudent.TestType.TYPE_OF_THE_FUTURE_PROFESSION.toString());
        startActivity(intent);
    }
}