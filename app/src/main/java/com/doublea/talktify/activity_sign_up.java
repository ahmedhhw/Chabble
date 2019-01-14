package com.doublea.talktify;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doublea.talktify.backgroundTools.UserData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * @Author Ahmed Ali
 */
public class activity_sign_up extends AppCompatActivity {
    private static final String TAG = "dbg";
    private FirebaseDatabase database;
    private EditText firstName;
    private EditText lastName;
    private EditText email;
    private EditText displayName;
    private EditText password;
    private TextView promptErrors; //Informs usersDatabaseRef of errors during sign up
    private DatabaseReference usersDatabaseRef;
    private DataSnapshot userSnapshotData;
    private UserData userData;
    private FirebaseAuth mAuth;
    private Button registerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Setup Views
        setupViews();
        database = FirebaseDatabase.getInstance();
        Log.d(TAG,database.toString());

        //Setup refrence and snapshot to read from database
        usersDatabaseRef = database.getReference("users");

        //readUsers();
        userData = null;
        mAuth = FirebaseAuth.getInstance(); //Authentication variable
    }

    //Sets up views needed to sign up user
    private void setupViews() {
        firstName = (EditText) findViewById(R.id.et_first_name);
        lastName = (EditText) findViewById(R.id.et_last_name);
        email = (EditText) findViewById(R.id.et_email);
        displayName = (EditText) findViewById(R.id.et_display_name);
        password = (EditText) findViewById(R.id.et_password);
        promptErrors = (TextView)findViewById(R.id.tv_errors);
        registerBtn = (Button) findViewById(R.id.btn_sign_up);
    }

    /**
     * Signs up user and stores their information into the database
     */
    private void signUpUser(){

        //Obtain information
        String email = this.email.getText().toString();
        String password = this.password.getText().toString();

        //Sign up user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(activity_sign_up.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });

    }

    /**
     * Link singed in activity
     * @param user
     */
    private void updateUI(FirebaseUser user) {

        //User is null
        if (user == null) return;

        //User is not null
        user.sendEmailVerification();
        userData = new UserData(firstName.getText().toString(),lastName.getText().toString(), displayName.getText().toString(), mAuth.getCurrentUser().getUid());
        usersDatabaseRef.child(user.getUid()).setValue(userData); // Store into database
        usersDatabaseRef.child(user.getUid()).child("contacts").setValue(""); // Contacts start out as empty

        // Resets current activity and switches to signed in activity
        clearTextBoxes();
        setEntryEnabled(true);
        startActivity(new Intent(this,activity_signed_in.class));
    }


    public void switchToLogIn(View view){
        startActivity(new Intent(this,activity_sign_in.class));
    }

    /**
     * Handles button clicked for "sign up" button
     * @param view
     */
    public void signUpClicked(View view) {
        registerBtn.setEnabled(false);

        //Check if all fields are filled
        promptErrors.setText("Please fill in data:");
        if (!isFilledOut()){
            return;
        }

        //disable user entry tools
        setEntryEnabled(false);

        //Register user
        signUpUser();

    }


    /**
     * Used to disable and enable user entry
     * @param value
     */
    private void setEntryEnabled(boolean value){
        firstName.setEnabled(value);
        lastName.setEnabled(value);
        email.setEnabled(value);
        displayName.setEnabled(value);
        password.setEnabled(value);
        registerBtn.setEnabled(value);
    }


    /**
     * data just for testing
     */
    private void dummyTestData() {
        firstName.setText("Ahmed");
        lastName.setText("Ali");
        email.setText("ahmedhhw@gmail.com");
        displayName.setText("Ahmed's account");
        password.setText("myPassword");
    }

    private boolean isFilledOut() {
        boolean filled = true;
        if (!isFilledOut(firstName))
            filled = false;
        if (!isFilledOut(lastName))
            filled = false;
        if (!isFilledOut(displayName))
            filled = false;
        if (!isFilledOut(email))
            filled = false;
        if (!isFilledOut(password))
            filled = false;
        return filled;
    }

    private boolean isFilledOut(EditText field) {
        if (field.getText().toString().equals("")){
            field.setBackgroundResource(R.drawable.text_error); //Highlight in red
            //Checks if this is the first occurance of this error
            if (promptErrors.getText().toString().equals("Please fill in data:")){
                promptErrors.setTextColor(Color.RED);
                promptErrors.setText("Please fill in " + field.getHint());
            }else{ // for all other occurances of such error
                promptErrors.setText(promptErrors.getText() + " and " + field.getHint());
            }
            return false;
        }else
            field.setBackgroundResource(R.drawable.text); //Highlight in blue
            return true;
    }

    private void clearTextBoxes() {
        firstName.setText("");
        lastName.setText("");
        displayName.setText("");
        email.setText("");
        password.setText("");
    }

}
