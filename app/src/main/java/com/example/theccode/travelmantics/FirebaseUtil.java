package com.example.theccode.travelmantics;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FirebaseUtil {
    public static FirebaseDatabase m_firebase_database;
    public static DatabaseReference m_database_ref;
    private static FirebaseUtil firebase_util;
    public static ArrayList<TravelDeal> m_deals;
    public static FirebaseAuth m_firebase_auth;
    public static FirebaseAuth.AuthStateListener m_auth_listener;
    private static ListActivity caller;
    private static final int RC_SIGN_IN = 123;
    public static boolean is_admin;
    public static FirebaseStorage firebase_storage;
    public static StorageReference storage_reference;

    private FirebaseUtil(){}

    public static void open_fb_ref (String ref, final ListActivity caller_activity){
        if ( firebase_util== null){
            firebase_util = new FirebaseUtil();
            m_firebase_database = FirebaseDatabase.getInstance();

            m_firebase_auth = FirebaseAuth.getInstance();
            caller = caller_activity;
            m_auth_listener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null){
                        FirebaseUtil.sign_in();
                    }
                    else{
                        String user_id = firebaseAuth.getUid();
                        check_admin(user_id);

                    }

                    Toast.makeText(caller_activity.getBaseContext(), "Welcome back!", Toast.LENGTH_LONG).show();
                }
            };
            connect_storage();
        }
        m_deals = new ArrayList<TravelDeal>();
        m_database_ref = m_firebase_database.getReference().child(ref);


    }

    private static void sign_in(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());


// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attach_auth_listener(){
        m_firebase_auth.addAuthStateListener(m_auth_listener);
    }

    public static void detach_auth_listener(){
        m_firebase_auth.removeAuthStateListener(m_auth_listener);
    }

    private static void check_admin(String uid){
        FirebaseUtil.is_admin = false;
        DatabaseReference ref = m_firebase_database.getReference().child("administrators").child(uid);

        ChildEventListener ad_listener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                FirebaseUtil.is_admin = true;
               // Log.d("Admin", "You are an administrators");
                caller.show_menu();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        ref.addChildEventListener(ad_listener);
    }

    public static void connect_storage(){
        firebase_storage = FirebaseStorage.getInstance();
        storage_reference = firebase_storage.getReference().child("deals_pictures");
    }

}
