package com.group11.medicare;
import java.util.*;
/** Describes every Firestore entity once; list + form screens are generated from it. */
public class Schema {
    public static class F { String key, label, type; String[] opts;
        F(String k, String l, String t, String... o) { key = k; label = l; type = t; opts = o; } }
    public static class E { String id, title, col, icon, titleKeys, subKeys; F[] f;
        E(String id, String title, String col, String icon, String tk, String sk, F... f) {
            this.id = id; this.title = title; this.col = col; this.icon = icon; titleKeys = tk; subKeys = sk; this.f = f; } }
    public static final Map<String, E> ALL = new LinkedHashMap<>();
    static void add(E e) { ALL.put(e.id, e); }
    static {
        add(new E("patients", "Patients", "patients", "🧑", "firstName,lastName", "patientId,phone",
            new F("patientId", "Patient ID", "text"), new F("firstName", "First name", "text"), new F("lastName", "Last name", "text"),
            new F("dob", "Date of birth", "date"), new F("gender", "Gender", "drop", "Female", "Male", "Other"),
            new F("phone", "Phone number", "text"), new F("address", "Address", "text"), new F("emergency", "Emergency contact", "text"),
            new F("blood", "Blood group", "drop", "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"), new F("registered", "Registration date", "date")));
        add(new E("appointments", "Appointments", "appointments", "📅", "date,time", "patientId,doctor",
            new F("patientId", "Patient ID", "text"), new F("doctor", "Doctor", "text"), new F("date", "Date", "date"), new F("time", "Time", "time"),
            new F("reason", "Reason for visit", "text"), new F("status", "Status", "drop", "Scheduled", "Completed", "Cancelled")));
        add(new E("consultations", "Consultations", "consultations", "🩺", "patientId,date", "doctor,diagnosis",
            new F("patientId", "Patient ID", "text"), new F("doctor", "Doctor", "text"), new F("appointmentId", "Appointment ID", "text"),
            new F("date", "Date", "date"), new F("symptoms", "Symptoms", "note"), new F("diagnosis", "Diagnosis", "text"),
            new F("treatment", "Treatment", "note"), new F("notes", "Clinical notes", "note")));
        add(new E("prescriptions", "Prescriptions", "prescriptions", "💊", "medicine,dosage", "patientId,frequency,duration",
            new F("consultationId", "Consultation ID", "text"), new F("patientId", "Patient ID", "text"), new F("medicine", "Medicine name", "text"),
            new F("dosage", "Dosage", "text"), new F("frequency", "Frequency", "text"), new F("duration", "Duration", "text"), new F("instructions", "Instructions", "note")));
        add(new E("bills", "Billing", "bills", "💳", "billNo,patientId", "amount,method",
            new F("billNo", "Bill number", "text"), new F("patientId", "Patient ID", "text"), new F("consultationId", "Consultation ID", "text"),
            new F("services", "Services", "note"), new F("amount", "Amount", "num"),
            new F("method", "Payment method", "drop", "Cash", "M-Pesa", "Card", "Insurance"),
            new F("status", "Payment status", "drop", "Paid", "Unpaid", "Partially Paid"), new F("date", "Date", "date")));
        add(new E("stock", "Pharmacy Stock", "stock", "🧪", "medicine", "category,quantity,expiry",
            new F("medicine", "Medicine", "text"), new F("category", "Category", "text"), new F("quantity", "Quantity in stock", "num"),
            new F("reorder", "Reorder level", "num"), new F("price", "Unit price", "num"), new F("expiry", "Expiry date", "date")));
    }
    public static String str(Object o) {
        if (o == null) return "";
        if (o instanceof Double && (Double) o == Math.floor((Double) o)) return String.valueOf(((Double) o).longValue());
        if (o instanceof Long) return String.valueOf(o);
        return o.toString();
    }
    public static String join(String keys, Map<String, Object> d, String sep) {
        StringBuilder sb = new StringBuilder();
        for (String k : keys.split(",")) { String v = str(d.get(k)); if (!v.isEmpty()) { if (sb.length() > 0) sb.append(sep); sb.append(v); } }
        return sb.toString();
    }
}
