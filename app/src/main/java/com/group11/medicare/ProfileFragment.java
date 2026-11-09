package com.group11.medicare;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
public class ProfileFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        View v = inf.inflate(R.layout.fragment_profile, c, false);
        ((TextView) v.findViewById(R.id.tvName)).setText(Session.name);
        ((TextView) v.findViewById(R.id.tvRole)).setText(Session.role);
        ((TextView) v.findViewById(R.id.tvEmail)).setText("✉️  " + Session.email);
        v.findViewById(R.id.btnLogout).setOnClickListener(x -> {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(requireContext(), LoginActivity.class); i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); startActivity(i);
        });
        return v;
    }
}
