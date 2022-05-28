package com.example.projectMP3;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.projectMP3.Activity.MainActivity;

import java.util.Objects;

/** Данный класс формирует заставку при запуске приложения
 *  @author Mikhail */
public class SplashScreenActivity extends AppCompatActivity {

    /** Тип верстки */
    LinearLayout linearLayout;
    /** Toolbar */
    androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Размещаем пользовательский интерфейс на экране активности
        setContentView(R.layout.activity_splash_screen);

        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        linearLayout = findViewById(R.id.Splash_Activity);
        linearLayout.setOnClickListener(v -> {
            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        getWindow().setFlags(
                // Разрешаем выход окну за пределы экрана
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        Objects.requireNonNull(getSupportActionBar()).hide();

        Thread thread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashScreenActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        thread.start();
    }
}