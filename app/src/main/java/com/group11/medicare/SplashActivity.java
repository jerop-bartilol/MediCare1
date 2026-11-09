package com.group11.medicare;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
public class SplashActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b); setContentView(R.layout.activity_splash);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            boolean in = FirebaseAuth.getInstance().getCurrentUser() != null;
            startActivity(new Intent(this, in ? MainActivity.class : LoginActivity.class)); finish();
        }, 1800);
    }
}
