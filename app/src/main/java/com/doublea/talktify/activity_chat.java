package com.doublea.talktify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.doublea.talktify.backgroundTools.FireBaseInteraction;
import com.doublea.talktify.backgroundTools.FirebaseReadListener;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import static com.doublea.talktify.activity_signed_in.allMessages;

public class activity_chat extends AppCompatActivity {

    private String otherUserID; //ID of other user
    private FireBaseInteraction firebase;
    private EditText message;
    private static FragmentManager fragmentManager;
    static boolean isRunning = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        instantiateVariables();
    }

    private void instantiateVariables() {
        Bundle b = getIntent().getExtras();
        if (b != null){
            otherUserID = b.getString("ID");
        }
        fragmentManager = getSupportFragmentManager();
        message = (EditText) findViewById(R.id.et_activity_chat_message);
        firebase = new FireBaseInteraction();
    }
    public static void addNewIncomingMessage(ArrayList<String> currentMessages, String userID) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            for (String msg : currentMessages) {
                fragmentTransaction.add(R.id.chat_ll_text_container, fragment_chat_to_others.newInstance(msg));
            }
            fragmentTransaction.commit();
            allMessages.put(userID, new ArrayList<String>());
    }


    public void sendMessageToOther(View view) {
        final String msg = message.getText().toString();
        firebase.read("users/" + otherUserID + "/messages/" + firebase.getAuth().getUid(), new FirebaseReadListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                String index = Long.toString(data.getChildrenCount()); //Index of new message
                firebase.write("users/" + otherUserID + "/messages/" + firebase.getAuth().getUid() + "/" + index, msg); //Write messages indexed to the right location
                message.setText("");
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.chat_ll_text_container,fragment_chat_to_me.newInstance(msg));
                fragmentTransaction.commit();
            }

            @Override
            public void onFailure() {

            }
        });
    }
    @Override
    public void onStart(){
        super.onStart();
        isRunning = true;
        addNewIncomingMessage(allMessages.get(otherUserID), otherUserID);
    }
    @Override
    public void onStop(){
        super.onStop();
        isRunning = false;
    }
}
