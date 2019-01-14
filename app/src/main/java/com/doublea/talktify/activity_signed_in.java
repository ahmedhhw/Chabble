package com.doublea.talktify;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.doublea.talktify.backgroundTools.CompletionListener;
import com.doublea.talktify.backgroundTools.UserData;
import com.doublea.talktify.backgroundTools.FireBaseInteraction;
import com.google.firebase.auth.FirebaseUser;

public class activity_signed_in extends AppCompatActivity implements fragment_top_menu.OnFragmentInteractionListener, fragment_contact.OnFragmentInteractionListener {
    private LinearLayout contactsView;
    private FirebaseUser currentUser;
    private UserData userData;
    private FireBaseInteraction fireBase;
    private TextView displayName;
    private static final String TAG = "dbg";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signed_in);

        initializeVariables();
    }

    /**
     * @TODO onStart condition and onFail condition
     */
    private void initializeVariables() {

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
