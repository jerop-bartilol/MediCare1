package com.group11.medicare;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.*;
public class LoginActivity extends AppCompatActivity {
    boolean register = false;
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b); setContentView(R.layout.activity_login);
        EditText name = findViewById(R.id.etName), email = findViewById(R.id.etEmail), pass = findViewById(R.id.etPass);
        MaterialAutoCompleteTextView role = findViewById(R.id.etRole);
        role.setSimpleItems(new String[]{"Doctor", "Patient", "Clinic Staff", "Admin"}); role.setText("Clinic Staff", false);
        View lName = findViewById(R.id.lName), lRole = findViewById(R.id.lRole);
        TextView head = findViewById(R.id.tvHead);
        MaterialButton go = findViewById(R.id.btnGo), sw = findViewById(R.id.btnSwitch);
        sw.setOnClickListener(v -> {
            register = !register; int vis = register ? View.VISIBLE : View.GONE;
            lName.setVisibility(vis); lRole.setVisibility(vis);
            head.setText(register ? "Create account" : "Welcome back");
            go.setText(register ? "Register" : "Sign in");
            sw.setText(register ? "Already registered? Sign in" : "New here? Create an account");
        });
        go.setOnClickListener(v -> {
            String e = email.getText().toString().trim(), p = pass.getText().toString();
            if (e.isEmpty() || p.length() < 6) { toast("Enter a valid email and a password of 6+ characters"); return; }
            if (register && name.getText().toString().trim().isEmpty()) { toast("Enter your full name"); return; }
            go.setEnabled(false);
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (register) auth.createUserWithEmailAndPassword(e, p).addOnCompleteListener(t -> {
                if (!t.isSuccessful()) { fail(go, t.getException()); return; }
                Map<String, Object> u = new HashMap<>();
                u.put("fullName", name.getText().toString().trim()); u.put("email", e); u.put("role", role.getText().toString());
                FirebaseFirestore.getInstance().collection("users").document(t.getResult().getUser().getUid()).set(u)
                    .addOnCompleteListener(x -> enter());
            });
            else auth.signInWithEmailAndPassword(e, p).addOnCompleteListener(t -> { if (t.isSuccessful()) enter(); else fail(go, t.getException()); });
        });
    }
    void enter() { startActivity(new Intent(this, MainActivity.class)); finish(); }
    void fail(View btn, Exception ex) { btn.setEnabled(true); toast(ex == null ? "Failed" : ex.getMessage()); }
    void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_LONG).show(); }
}
