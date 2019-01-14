package com.doublea.talktify;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.doublea.talktify.backgroundTools.FireBaseInteraction;
import com.doublea.talktify.backgroundTools.FirebaseReadListener;
import com.google.firebase.database.DataSnapshot;

public class activity_chat extends AppCompatActivity {

    private String otherUserID; //ID of other user
    private FireBaseInteraction firebase;
    private EditText message;
    private FragmentManager fragmentManager;
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
        firebase.readOnUpdate("users/" + firebase.getAuth().getUid() + "/messages", new FirebaseReadListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot message: data.getChildren()){
                   addNewIncomingMessage(message.getValue().toString());
                }
                firebase.write("users/" + firebase.getAuth().getUid() + "/messages",null);
            }

            private void addNewIncomingMessage(String msg) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.add(R.id.chat_ll_text_container,fragment_chat_to_others.newInstance(msg));
                fragmentTransaction.commit();
            }

            @Override
            public void onFailure() {

            }
        });
    }

    public void sendMessageToOther(View view) {
        final String msg = message.getText().toString();
        firebase.read("users/" + otherUserID + "/messages", new FirebaseReadListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                String index = Long.toString(data.getChildrenCount()); //Index of new message
                firebase.write("users/" + otherUserID + "/messages/" + index, msg); //Write messages indexed to the right location
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
}
