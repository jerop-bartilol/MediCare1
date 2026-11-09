package com.group11.medicare;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;
import com.google.firebase.firestore.*;
import java.text.SimpleDateFormat;
import java.util.*;
public class DashboardFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        View v = inf.inflate(R.layout.fragment_dashboard, c, false);
        ((TextView) v.findViewById(R.id.tvName)).setText(Session.name);
        ((TextView) v.findViewById(R.id.tvRole)).setText(Session.role);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        GridLayout stats = v.findViewById(R.id.gridStats), acts = v.findViewById(R.id.gridActions);
        stat(inf, stats, "Patients", db.collection("patients"));
        stat(inf, stats, "Today's visits", db.collection("appointments").whereEqualTo("date", today));
        stat(inf, stats, "Consultations", db.collection("consultations"));
        stat(inf, stats, "Pending visits", db.collection("appointments").whereEqualTo("status", "Scheduled"));
        stat(inf, stats, "Unpaid bills", db.collection("bills").whereIn("status", Arrays.asList("Unpaid", "Partially Paid")));
        stat(inf, stats, "Low stock ⚠", db.collection("stock").whereLessThanOrEqualTo("quantity", 10));
        boolean patient = Session.role.equals("Patient");
        for (Schema.E e : Schema.ALL.values()) {
            if (patient && !e.id.equals("appointments") && !e.id.equals("prescriptions")) continue;
            View t = tile(inf, acts, e.icon, e.title);
            t.setOnClickListener(x -> ((MainActivity) requireActivity()).show(ListFragment.of(e.id), true));
        }
        return v;
    }
    View tile(LayoutInflater inf, GridLayout g, String top, String label) {
        View t = inf.inflate(R.layout.item_tile, g, false);
        ((TextView) t.findViewById(R.id.tvTop)).setText(top); ((TextView) t.findViewById(R.id.tvLabel)).setText(label);
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams(GridLayout.spec(GridLayout.UNDEFINED, 1f), GridLayout.spec(GridLayout.UNDEFINED, 1f));
        lp.width = 0; lp.setMargins(8, 8, 8, 8); t.setLayoutParams(lp); g.addView(t); return t;
    }
    void stat(LayoutInflater inf, GridLayout g, String label, Query q) {
        TextView n = tile(inf, g, "…", label).findViewById(R.id.tvTop);
        q.count().get(AggregateSource.SERVER).addOnSuccessListener(r -> n.setText(String.valueOf(r.getCount()))).addOnFailureListener(x -> n.setText("–"));
    }
}
