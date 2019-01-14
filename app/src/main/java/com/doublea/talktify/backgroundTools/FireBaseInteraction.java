package com.doublea.talktify.backgroundTools;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FireBaseInteraction {
    private DatabaseReference userReference;
    private FirebaseDatabase database;
    private FirebaseAuth Auth;
    private FirebaseUser currentUser;
    private UserData userData;
    private CompletionListener listener;
    private DataSnapshot allUsersSnapshot;
    private static final String TAG = "dbg";
    /**
     * Basic constructor
     * @param listener completionListener
     */
    public FireBaseInteraction(CompletionListener listener){
        loadVariables(listener);
        userReference = database.getReference("users/" + currentUser.getUid());
        setUserData();
    }
    /**
     * Special contstuctor
     * @param ID ID to construct for
     * @param listener completionListener
     */
    public FireBaseInteraction(String ID, CompletionListener listener){
        loadVariables(listener);
        userReference = database.getReference("users/" + ID);
        setUserData();
    }
    /**
     * loads variables common to both constructors
     * @param listener
     */
    private void loadVariables(CompletionListener listener){
        Auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        this.listener = listener;
        currentUser = Auth.getCurrentUser();
    }
    /**
     * @TODO check if reading algorithm works
     */
    private void setUserData() {

        //Read in user data
        listener.onStart();
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Fill data into a UserData object
                userData = new UserData(dataSnapshot.child("firstName").getValue().toString(),dataSnapshot.child("lastName").getValue().toString(),dataSnapshot.child("displayName").getValue().toString(), dataSnapshot.child("uid").getValue().toString());

                //Read contacts if contacts is not empty
                if (!dataSnapshot.child("contacts").getValue().toString().equals("")){
                    for (DataSnapshot contact:dataSnapshot.child("contacts").getChildren()){
                        userData.getContacts().add(contact.getValue().toString());
                    }
                }
                listener.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"The read failed: " + databaseError.getCode());
                listener.onFailure();
            }
        });
    }
    public FirebaseUser getCurrentUser(){
        return currentUser;
    }
    public UserData getCurrentUserData(){
        return userData;
    }
    public FirebaseDatabase getDatabase(){
        return database;
    }
    public FirebaseAuth getAuth() {
        return Auth;
    }
    public void generateSnapShotOfAllUsers(final CompletionListener listener){
        listener.onStart();
        database.getReference("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                allUsersSnapshot = dataSnapshot;
                listener.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure();
            }
        });
    }
    public DataSnapshot getAllUsersSnapshot() {
        return allUsersSnapshot;
    }
    public DatabaseReference getUserReference() {
        return userReference;
    }
    public void setUserReference(DatabaseReference userReference) {
        this.userReference = userReference;
    }
    public FireBaseInteraction(){
        database = FirebaseDatabase.getInstance();
        Auth = FirebaseAuth.getInstance();
    }


    /**
     * Reads from firebase database
     * @param path Path to read from
     * @param listen Listener that allows data to be communicated back
     */
    public void read(String path, final FirebaseReadListener listen){
        DatabaseReference currentPath = database.getReference(path);
        currentPath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listen.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listen.onFailure();
            }
        });
    }

    /**
     * Reads when called and when change occurs at particular path
     * @param path
     * @param listen
     */
    public void readOnUpdate(String path, final FirebaseReadListener listen){
        DatabaseReference currentPath = database.getReference(path);
        currentPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listen.onSuccess(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listen.onFailure();;
            }
        });
    }
    /**
     * Writes strings to firebase
     * @param path Path of string
     * @param value String to be written
     */
    public void write(String path, String value){
        DatabaseReference currentPath = database.getReference(path);
        currentPath.setValue(value);
    }
    public void write(String path, Object value){
        DatabaseReference currentPath = database.getReference(path);
        currentPath.setValue(value);
    }

}
