package com.thangld.managechildren.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.thangld.managechildren.R;
import com.thangld.managechildren.database.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new DatabaseHelper(this);


    }



}
