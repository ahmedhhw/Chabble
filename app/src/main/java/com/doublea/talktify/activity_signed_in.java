package com.doublea.talktify;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doublea.talktify.backgroundTools.CompletionListener;
import com.doublea.talktify.backgroundTools.FirebaseReadListener;
import com.doublea.talktify.backgroundTools.UserData;
import com.doublea.talktify.backgroundTools.FireBaseInteraction;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class activity_signed_in extends AppCompatActivity implements fragment_top_menu.OnFragmentInteractionListener, fragment_contact.OnFragmentInteractionListener {
    private LinearLayout contactsView;
    private FirebaseUser currentUser;
    private FireBaseInteraction tempFB;
    private UserData userData;
    private FireBaseInteraction fireBase;
    private TextView displayName;
    public static HashMap<String,ArrayList<String>> allMessages;
    private static final String TAG = "dbg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signed_in);

        initializeVariables();
        detectNewMessages();
    }

    private void detectNewMessages() {
        tempFB = new FireBaseInteraction();
        tempFB.read("users/" + tempFB.getAuth().getUid() + "/contacts", new FirebaseReadListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                for (DataSnapshot contact: data.getChildren()){
                    listenForMessages(contact.getValue().toString());
                }
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void listenForMessages(final String userID) {
        allMessages.put(userID, new ArrayList<String>());
        tempFB.readOnUpdate("users/" + tempFB.getAuth().getUid() + "/messages/" + userID , new FirebaseReadListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                Log.d("dbg", "All messages: " + allMessages.toString());
                ArrayList<String> currentMessages = allMessages.get(userID);
                for (DataSnapshot message: data.getChildren()){
                    currentMessages.add(message.getValue().toString());
                }
                tempFB.write("users/" + tempFB.getAuth().getUid() + "/messages",null);
                if (activity_chat.isRunning)
                    activity_chat.addNewIncomingMessage(currentMessages, userID);
                else if (currentMessages.size() > 0){
                    //Send a notification with the latest message
                    String lastMessage = currentMessages.get(currentMessages.size() - 1);
                    displayNotification(lastMessage, userID);
                }
            }
            @Override
            public void onFailure() {

            }
        });
    }

    private static int stage; // 0: first read done, 1: second read done
    static class Notification {
        String title;
        String otherName;
        String message;
        public Notification(){
            title = "";
            otherName = "";
            message = "";
        }
        public Notification(String title, String otherName, String message) {
            this.title = title;
            this.otherName = otherName;
            this.message = message;
        }
    }
    private void displayNotification(String lastMessage, final String userID) {
        final FireBaseInteraction temp = new FireBaseInteraction();
        final Notification notification = new Notification();
        notification.message = lastMessage;
        stage = 0;
        temp.read("users/" + temp.getAuth().getUid() + "/displayName" , new FirebaseReadListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                notification.title = "Dear " + data.getValue().toString();
                if (stage == 1)
                    setNotification(notification,userID);
                else
                    stage++;
            }

            @Override
            public void onFailure() {

            }
        });
        temp.read("users/" + userID + "/displayName" , new FirebaseReadListener() {
            @Override
            public void onSuccess(DataSnapshot data) {
                notification.otherName = data.getValue().toString();
                if (stage == 1)
                    setNotification(notification,userID);
                else
                    stage++;

            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void setNotification(Notification notification, String userID) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "5231")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(notification.title)
                .setContentText(notification.otherName + " said: " + notification.message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        createNotificationChannel();
        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, activity_chat.class);
        Bundle b = new Bundle();
        b.putString("ID",userID);
        intent.putExtras(b);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setContentIntent(pendingIntent).setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(23141, mBuilder.build());
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = (CharSequence) "Chabble";
            String description = "Notifications from chabble";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("5231", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * @TODO onStart condition and onFail condition
     */
    private void initializeVariables() {

        // Initialize hashmap that has all the messages in it
        allMessages = new HashMap<String,ArrayList<String>>();

        // layout items
        contactsView = (LinearLayout) findViewById(R.id.ll_contacts_view);
        displayName = (TextView) findViewById(R.id.tv_display_name);

        //Setting up firebase
        fireBase = new FireBaseInteraction(new CompletionListener() {
            @Override
            public void onStart() {
                //Add loading screen
            }

            @Override
            public void onSuccess() {

                //Read data from firebase
                currentUser = fireBase.getCurrentUser();
                userData = fireBase.getCurrentUserData();
                displayName.setText(userData.getDisplayName());

                loadContacts();
            }

            @Override
            public void onFailure() {
                //Add failure conditions
            }
        });
    }

    public void loadContacts(){

        //Background set up
        FragmentManager fragmentManager = getSupportFragmentManager(); //Set up fragment manage
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //Set up new transaction

        //Create new contact fragment for every contact ID
        for (String contactID: userData.getContacts()){
            fragmentTransaction.add(R.id.ll_contacts_view,fragment_contact.newInstance(contactID));
        }

        fragmentTransaction.commit();
    }
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void openSearchView(View view) {
        startActivity(new Intent(this,activity_search_contacts.class));
    }

    public void signOutClicked(View view) {
        fireBase.getAuth().signOut();
        startActivity(new Intent(this,activity_sign_in.class));
    }
}
