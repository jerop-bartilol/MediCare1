package com.group11.medicare;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
public class MainActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle b) {
        super.onCreate(b); setContentView(R.layout.activity_main);
        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u == null) { startActivity(new Intent(this, LoginActivity.class)); finish(); return; }
        BottomNavigationView nav = findViewById(R.id.nav);
        FirebaseFirestore.getInstance().collection("users").document(u.getUid()).get().addOnCompleteListener(t -> {
            DocumentSnapshot d = t.isSuccessful() ? t.getResult() : null;
            boolean ok = d != null && d.exists();
            Session.email = u.getEmail();
            Session.name = ok && d.getString("fullName") != null ? d.getString("fullName") : u.getEmail();
            Session.role = ok && d.getString("role") != null ? d.getString("role") : "Clinic Staff";
            boolean patient = Session.role.equals("Patient");   // role-based access
            nav.getMenu().findItem(R.id.nav_patients).setVisible(!patient);
            nav.getMenu().findItem(R.id.nav_stock).setVisible(!patient);
            nav.setOnItemSelectedListener(i -> {
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                int id = i.getItemId();
                show(id == R.id.nav_home ? new DashboardFragment() : id == R.id.nav_patients ? ListFragment.of("patients")
                   : id == R.id.nav_appts ? ListFragment.of("appointments") : id == R.id.nav_stock ? ListFragment.of("stock")
                   : new ProfileFragment(), false);
                return true;
            });
            if (b == null) show(new DashboardFragment(), false);
        });
    }
    public void show(Fragment f, boolean back) {
        var t = getSupportFragmentManager().beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.container, f);
        if (back) t.addToBackStack(null);
        t.commit();
    }
}
