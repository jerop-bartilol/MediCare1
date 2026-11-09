package com.group11.medicare;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.google.firebase.firestore.*;
import java.util.*;
public class ListFragment extends Fragment {
    public static ListFragment of(String id) { ListFragment f = new ListFragment(); Bundle b = new Bundle(); b.putString("e", id); f.setArguments(b); return f; }
    Schema.E e; List<DocumentSnapshot> all = new ArrayList<>(), shown = new ArrayList<>(); RecyclerView.Adapter<VH> ad;
    ListenerRegistration reg; EditText search; TextView empty;
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        e = Schema.ALL.get(requireArguments().getString("e"));
        View v = inf.inflate(R.layout.fragment_list, c, false);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(e.icon + "  " + e.title);
        search = v.findViewById(R.id.etSearch); empty = v.findViewById(R.id.tvEmpty);
        RecyclerView rv = v.findViewById(R.id.rv); rv.setLayoutManager(new LinearLayoutManager(getContext()));
        ad = new RecyclerView.Adapter<VH>() {
            public VH onCreateViewHolder(ViewGroup p, int t) { return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_row, p, false)); }
            public int getItemCount() { return shown.size(); }
            public void onBindViewHolder(VH h, int i) { h.bind(shown.get(i)); }
        };
        rv.setAdapter(ad);
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence a, int b, int c, int d) {} public void onTextChanged(CharSequence a, int b, int c, int d) {}
            public void afterTextChanged(Editable ed) { filter(); } });
        v.findViewById(R.id.fab).setOnClickListener(x -> ((MainActivity) requireActivity()).show(FormFragment.of(e.id, null), true));
        reg = FirebaseFirestore.getInstance().collection(e.col).addSnapshotListener((snap, err) -> { if (snap != null) { all = snap.getDocuments(); filter(); } });
        return v;
    }
    void filter() {
        String q = search.getText().toString().toLowerCase().trim(); shown = new ArrayList<>();
        for (DocumentSnapshot d : all) { if (q.isEmpty() || Schema.join(allKeys(), d.getData(), " ").toLowerCase().contains(q)) shown.add(d); }
        empty.setVisibility(shown.isEmpty() ? View.VISIBLE : View.GONE); ad.notifyDataSetChanged();
    }
    String allKeys() { StringBuilder sb = new StringBuilder(); for (Schema.F f : e.f) sb.append(f.key).append(","); return sb.toString(); }
    @Override public void onDestroyView() { super.onDestroyView(); if (reg != null) reg.remove(); }
    class VH extends RecyclerView.ViewHolder {
        TextView icon, title, sub, badge;
        VH(View v) { super(v); icon = v.findViewById(R.id.tvIcon); title = v.findViewById(R.id.tvTitle); sub = v.findViewById(R.id.tvSub); badge = v.findViewById(R.id.tvBadge); }
        void bind(DocumentSnapshot d) {
            Map<String, Object> m = d.getData();
            icon.setText(e.icon); title.setText(Schema.join(e.titleKeys, m, " · ")); sub.setText(Schema.join(e.subKeys, m, "  •  "));
            String b = null; int col = Color.parseColor("#D97706");
            if (m.get("status") != null) { b = Schema.str(m.get("status"));
                col = Color.parseColor(b.equals("Paid") || b.equals("Completed") ? "#16A34A" : b.equals("Cancelled") || b.equals("Unpaid") ? "#DC2626" : "#0EA5E9"); }
            if (e.id.equals("stock")) { double q = m.get("quantity") instanceof Number ? ((Number) m.get("quantity")).doubleValue() : 0, r = m.get("reorder") instanceof Number ? ((Number) m.get("reorder")).doubleValue() : 10;
                b = q <= r ? "LOW" : "OK"; col = Color.parseColor(q <= r ? "#DC2626" : "#16A34A"); }
            badge.setVisibility(b == null ? View.GONE : View.VISIBLE);
            if (b != null) { badge.setText(b); GradientDrawable g = new GradientDrawable(); g.setCornerRadius(40f); g.setColor(col); badge.setBackground(g); }
            itemView.setOnClickListener(x -> ((MainActivity) requireActivity()).show(FormFragment.of(e.id, d.getId()), true));
        }
    }
}
