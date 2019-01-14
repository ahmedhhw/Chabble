package com.doublea.talktify;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * @Author Ahmed Ali
 * Allows registered user to log in using email and password
 */
public class activity_sign_in extends AppCompatActivity/* implements LoaderCallbacks<Cursor> */{

    private static final String TAG = "dbg";
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateActivityToSignedIn(currentUser);
    }

    /**
     * updates UI to signed in activity
     */
    private void updateActivityToSignedIn(FirebaseUser user) {
       if (user == null) return;
        startActivity(new Intent(this,activity_signed_in.class));
    }

    public void updateActivityToSignUp(View view) {
        startActivity(new Intent(this, activity_sign_up.class));
    }

    public void signInUser(View view) {
        //Load variables
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        //Sign in action event
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateActivityToSignedIn(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(activity_sign_in.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateActivityToSignedIn(null);
                        }

                        // ...
                    }
                });
    }

}
