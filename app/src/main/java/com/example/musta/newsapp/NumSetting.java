package com.example.musta.newsapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class NumSetting extends AppCompatActivity {
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_num_setting);

        SharedPreferences sharedPreferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        int numArticles = sharedPreferences.getInt("savedNum", 10);
        editText = (EditText) findViewById(R.id.numArticles);
        editText.setText(Integer.toString(numArticles));
    }

//    @Override
//    public void onBackPressed() {
//
//        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//        intent.putExtra("number", Integer.valueOf(editText.getText().toString()));
//        setResult(Activity.RESULT_OK, intent);
//
//        super.onBackPressed();
//    }

    public void save(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("number", Integer.valueOf(editText.getText().toString()));
//        setResult(Activity.RESULT_OK, intent);
    }
}
