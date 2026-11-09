package com.group11.medicare;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.*;
import com.google.firebase.firestore.*;
import java.text.SimpleDateFormat;
import java.util.*;
public class FormFragment extends Fragment {
    public static FormFragment of(String id, String doc) { FormFragment f = new FormFragment(); Bundle b = new Bundle(); b.putString("e", id); b.putString("doc", doc); f.setArguments(b); return f; }
    Schema.E e; String docId; final Map<String, EditText> inputs = new LinkedHashMap<>(); final Map<String, TextInputLayout> layouts = new HashMap<>();
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle s) {
        e = Schema.ALL.get(requireArguments().getString("e")); docId = requireArguments().getString("doc");
        View v = inf.inflate(R.layout.fragment_form, c, false);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(e.icon + "  " + (docId == null ? "New " : "Edit ") + e.title);
        LinearLayout box = v.findViewById(R.id.fields);
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(new Date());
        for (Schema.F f : e.f) {
            TextInputLayout til = (TextInputLayout) inf.inflate(f.type.equals("drop") ? R.layout.item_dropdown : R.layout.item_input, box, false);
            til.setHint(f.label + (f.type.equals("note") ? " (optional)" : ""));
            EditText et = til.findViewById(R.id.et);
            switch (f.type) {
                case "drop": ((MaterialAutoCompleteTextView) et).setSimpleItems(f.opts); if (f.key.equals("status") && docId == null) et.setText(f.opts[0], false); break;
                case "num": et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL); break;
                case "note": et.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE); et.setMinLines(3); break;
                case "date": picker(et, true); if (docId == null && (f.key.equals("date") || f.key.equals("registered"))) et.setText(today); break;
                case "time": picker(et, false); break;
            }
            inputs.put(f.key, et); layouts.put(f.key, til); box.addView(til);
        }
        if (docId != null) {
            v.findViewById(R.id.btnDelete).setVisibility(View.VISIBLE);
            FirebaseFirestore.getInstance().collection(e.col).document(docId).get().addOnSuccessListener(d -> {
                if (d.getData() == null) return;
                for (Schema.F f : e.f) { EditText et = inputs.get(f.key); String val = Schema.str(d.get(f.key));
                    if (et instanceof MaterialAutoCompleteTextView) ((MaterialAutoCompleteTextView) et).setText(val, false); else et.setText(val); }
            });
            v.findViewById(R.id.btnDelete).setOnClickListener(x -> new MaterialAlertDialogBuilder(requireContext()).setTitle("Delete this record?")
                .setNegativeButton("Cancel", null).setPositiveButton("Delete", (dlg, w) -> FirebaseFirestore.getInstance().collection(e.col).document(docId).delete().addOnSuccessListener(r -> back())).show());
        }
        v.findViewById(R.id.btnSave).setOnClickListener(x -> save());
        return v;
    }
    void picker(EditText et, boolean date) {
        et.setFocusable(false); et.setClickable(true);
        et.setOnClickListener(x -> {
            if (date) { MaterialDatePicker<Long> p = MaterialDatePicker.Builder.datePicker().build();
                p.addOnPositiveButtonClickListener(sel -> { SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd", Locale.US); f.setTimeZone(TimeZone.getTimeZone("UTC")); et.setText(f.format(new Date(sel))); });
                p.show(getChildFragmentManager(), "d");
            } else { MaterialTimePicker p = new MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H).build();
                p.addOnPositiveButtonClickListener(y -> et.setText(String.format(Locale.US, "%02d:%02d", p.getHour(), p.getMinute())));
                p.show(getChildFragmentManager(), "t"); }
        });
    }
    void save() {
        Map<String, Object> data = new HashMap<>(); boolean ok = true;
        for (Schema.F f : e.f) {
            String val = inputs.get(f.key).getText().toString().trim(); TextInputLayout til = layouts.get(f.key); til.setError(null);
            if (val.isEmpty() && !f.type.equals("note")) { til.setError("Required"); ok = false; continue; }
            if (f.type.equals("num")) { try { data.put(f.key, Double.parseDouble(val)); } catch (Exception ex) { til.setError("Enter a number"); ok = false; } }
            else data.put(f.key, val);
        }
        if (!ok) return;
        data.put("updatedBy", Session.name); data.put("updatedAt", FieldValue.serverTimestamp());
        CollectionReference col = FirebaseFirestore.getInstance().collection(e.col);
        (docId == null ? col.add(data).continueWith(t -> null) : col.document(docId).set(data, SetOptions.merge()).continueWith(t -> null))
            .addOnSuccessListener(r -> { Toast.makeText(getContext(), "Saved ✓", Toast.LENGTH_SHORT).show(); back(); })
            .addOnFailureListener(ex -> Toast.makeText(getContext(), "Save failed: " + ex.getMessage(), Toast.LENGTH_LONG).show());
    }
    void back() { if (isAdded()) requireActivity().getSupportFragmentManager().popBackStack(); }
}
