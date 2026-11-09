# MediCare – Mobile Clinic Management & Pharmacy System (Group 11, CMT 314)
1. Android Studio > New Project (Empty Views, package com.group11.medicare, Java) and copy `app/` over it.
2. Root build.gradle plugins: `id 'com.android.application' version '8.5.2' apply false` and `id 'com.google.gms.google-services' version '4.4.2' apply false`.
3. Firebase console: add the Android app, download google-services.json into app/, enable Email/Password Auth and Cloud Firestore.
4. Firestore rules (starter): allow read, write: if request.auth != null;
Collections: users, patients, appointments, consultations, prescriptions, bills, stock.
