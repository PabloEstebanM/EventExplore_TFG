package com.example.eventexplore_tfg.activitys;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventexplore_tfg.Data.User;
import com.example.eventexplore_tfg.R;


public class Register extends AppCompatActivity {

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Intent intent = getIntent();
        User userLogged = (User) intent.getSerializableExtra("usuario");

    }
}
