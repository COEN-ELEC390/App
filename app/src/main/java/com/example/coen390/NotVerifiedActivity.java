package com.example.coen390;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class NotVerifiedActivity extends AppCompatActivity {
    Button returnToLogin;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_verified);
        returnToLogin = findViewById(R.id.loginReturnBTN);
        returnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent;
                intent = new Intent(NotVerifiedActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
