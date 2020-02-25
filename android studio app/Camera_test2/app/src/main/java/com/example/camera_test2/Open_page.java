package com.example.camera_test2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Open_page extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_page);

    }

    public void startPLAY(View view)
    {
        startActivity(new Intent(this, MainActivity.class));
    }

    public void Setting(View view)
    {
        startActivity(new Intent(this, setting.class));
    }
}
