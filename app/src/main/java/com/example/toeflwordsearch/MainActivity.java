package com.example.toeflwordsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

//TODO: "Appstudy" splash screen to "TOEFL WordSearch" splash screen (animation)
public class MainActivity extends AppCompatActivity {
    Button l0, l1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        l0 = findViewById(R.id.level0);
        l1 = findViewById(R.id.level1);
        SharedPreferences userData = getSharedPreferences("WordSearch", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = userData.edit();

        //TODO: in a loop
        l0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("level", "0");
                editor.apply();

                startActivity(new Intent(MainActivity.this, GameActivity.class));

            }
        });
        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("level", "1");
                editor.apply();
                startActivity(new Intent(MainActivity.this, GameActivity.class));
            }
        });
    }
}
