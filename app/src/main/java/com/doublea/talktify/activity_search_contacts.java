package com.doublea.talktify;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.doublea.talktify.backgroundTools.CompletionListener;
import com.doublea.talktify.backgroundTools.FireBaseInteraction;
import com.doublea.talktify.backgroundTools.UserData;
import com.doublea.talktify.backgroundTools.ContactAddListener;
import com.google.firebase.database.DataSnapshot;

public class activity_search_contacts extends AppCompatActivity implements fragment_contact_search_view.OnFragmentInteractionListener {
    private static final String TAG = "dbg";
    private FireBaseInteraction fireBase;
    private Button btnSearch;
    private fragment_contact_search_view contact_search_view = null;
    private EditText etSearch;
    private DataSnapshot userDataSnapshot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contacts);
        loadUIVariables();
        generateFireBase();
    }

    private void generateFireBase() {
        fireBase = new FireBaseInteraction(new CompletionListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess() {
                fireBase.generateSnapShotOfAllUsers(new CompletionListener() {
                    @Override
                    public void onStart() {
                        etSearch.setHint("Loading...");
                        etSearch.setEnabled(false);
                        btnSearch.setEnabled(false);
                    }

                    @Override
                    public void onSuccess() {
                        etSearch.setHint("Search...");
                        etSearch.setEnabled(true);
                        btnSearch.setEnabled(true);
                        userDataSnapshot = fireBase.getAllUsersSnapshot();
                    }

                    @Override
                    public void onFailure() {

                    }
                });
            }

            @Override
            public void onFailure() {

            }
        });
    }

    private void loadUIVariables() {
        btnSearch = (Button) findViewById(R.id.btn_search);
        etSearch = (EditText) findViewById(R.id.et_search);
    }

    /**
     * @TODO make contacts disappear for different searches
     * @TODO make view switch back to previous view
     * @param view
     */
    public void Search(View view) {

        //Initialize variables
        UserData userData = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        String userEntry = etSearch.getText().toString();

        //Search for users
        for (DataSnapshot userSnapshot: userDataSnapshot.getChildren()){
            userData = new UserData(userSnapshot.child("firstName").getValue().toString(),userSnapshot.child("lastName").getValue().toString(),userSnapshot.child("displayName").getValue().toString(),userSnapshot.child("uid").getValue().toString());

            if (userData.matches(userEntry)){

                //Create new fragment object for user up on match
                contact_search_view = fragment_contact_search_view.newInstance(userData, new ContactAddListener() {

                    @Override
                    public void onSuccess(String userID) {

                        //Add new contact to database
                        int numberOfContacts = (fireBase.getCurrentUserData().getContacts()== null)?0:fireBase.getCurrentUserData().getContacts().size();
                        fireBase.getUserReference().child("contacts").child(Integer.toString(numberOfContacts)).setValue(userID);

                    }
                });

                //Add newly created fragment to fragment transaction
                fragmentTransaction.add(R.id.ll_contact_View_search,contact_search_view);
            }
        }
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
