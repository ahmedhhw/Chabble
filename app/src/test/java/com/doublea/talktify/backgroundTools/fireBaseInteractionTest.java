package com.doublea.talktify.backgroundTools;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;

import static org.junit.Assert.*;

public class fireBaseInteractionTest {
    private FirebaseDatabase database;
    private static final String TAG = "dbg";
    private DatabaseReference ref;
    @Test
    public void getCurrentUser() {
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("users");
        ref.child("abc").setValue(new String("Your mom"));
    }

    @Test
    public void getCurrentUserData() {
    }

    @Test
    public void getDatabase() {
    }

    @Test
    public void storeNewUserData() {
    }
}