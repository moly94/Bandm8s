package bandm8s.hagenberg.fh.bandm8s;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import bandm8s.hagenberg.fh.bandm8s.models.Band;
import bandm8s.hagenberg.fh.bandm8s.models.User;

import static android.Manifest.permission.READ_CONTACTS;
import static bandm8s.hagenberg.fh.bandm8s.R.string.error_registration_failed;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final String TAG = "RegisterActivity";

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */


    // UI references.
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private EditText mPasswordConfirmedView;
    private View mProgressView;
    private View mLoginFormView;

    private Switch mBandOrUser;

    //Firebase
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setupActionBar();

        //Set up values for Firebase

        mAuth= FirebaseAuth.getInstance();
        mDatabase=FirebaseDatabase.getInstance();

        mAuthListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    showProgress(false);
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Intent i= new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(i);
                } else {
                    // User is signed out
                    Log.d( TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        // Set up the login form.

        mBandOrUser = (Switch) findViewById(R.id.bandOrUser);
        mBandOrUser.setChecked(true);


        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordConfirmedView = (EditText) findViewById(R.id.password2);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_register_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                checkIfUserExists();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    private boolean validateEmailPassword () {
        View focusView = null;
        boolean valid=true;
        String email= mEmailView.getText().toString();
        String password= mPasswordView.getText().toString();
        String password2 = mPasswordConfirmedView.getText().toString();
        String username=mUsernameView.getText().toString();


        if (email.isEmpty()) {
            showProgress(false);
            mEmailView.setError(getString( R.string.error_field_required));
            mEmailView.requestFocus();
            valid = false;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            valid = false;
        }
        else if (password.isEmpty() || password.length() < 5) {
            showProgress(false);
            mPasswordView.setError(getString(R.string.error_invalid_password));
            mPasswordView.requestFocus();
            valid = false;
        } else if (!password.equals(password2)){
            showProgress(false);
            mPasswordConfirmedView.setError("Passwords don't match");
            mPasswordConfirmedView.requestFocus();
            valid=false;
        }

        else {
            mPasswordView.setError(null);
            mPasswordConfirmedView.setError(null);
        }
        return valid;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic

        return email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    }

    private void createUserwithEmail() {

        final String email= mEmailView.getText().toString();
        String password= mPasswordView.getText().toString();


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), error_registration_failed,
                                    Toast.LENGTH_SHORT).show();
                            showProgress(false);
                        } else {
                            writeNewUser();

                        }
                    }
                });

    }

    /**
     *Creates new User in the Firebase Database
     */
    private void writeNewUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if (user != null) {
            // Name, email address, and profile photo Url
            String email = user.getEmail();
            String username= mUsernameView.getText().toString();
            String uid = user.getUid();

            UserProfileChangeRequest setDisplayName= new UserProfileChangeRequest.Builder().setDisplayName(username).build();
            //TODO: Change to own profile pic!!!
            //UserProfileChangeRequest setDisplayName= new UserProfileChangeRequest.Builder()
            //        .setDisplayName(username).setPhotoUri(Uri.parse("gs://project-cow.appspot.com/testProfile.png")).build();

           // user.updateProfile(setDisplayName)
           //         .addOnCompleteListener(new OnCompleteListener<Void>() {
           //             @Override
           //             public void onComplete(@NonNull Task<Void> task) {
           //                 if (task.isSuccessful()) {
           //                     Log.d(TAG, "User profile updated.");
           //                 }
           //             }
           //         });

            if(mBandOrUser.isChecked()) {
                Band bandObject = new Band(username, email);
                DatabaseReference myRef = mDatabase.getReference("bands");
                myRef.child(uid).setValue(bandObject);
                showProgress(false);
            }
            else {
                User userObject = new User(username, email);
                DatabaseReference myRef = mDatabase.getReference("users");
                myRef.child(uid).setValue(userObject);
                showProgress(false);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Checks if user already exists in database (email and username are checked)
     * if it doesn't exist createUserWithEmail() is called
     */
    private void checkIfUserExists() {
        if (!validateEmailPassword()) {
            return;
        }
        String email=mEmailView.getText().toString();
        String username= mUsernameView.getText().toString();

        DatabaseReference myRef = mDatabase.getReference("users");

        Query searchForUserName=myRef.orderByChild("username").equalTo(username);
        searchForUserName.addListenerForSingleValueEvent(new ValueEventListener () {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    Toast.makeText(getApplicationContext(), "User already exists! (Username)",
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "User exists");
                    showProgress(false);
                } else {
                    DatabaseReference myRef = mDatabase.getReference("users");
                    Query searchForUserMail= myRef.orderByChild("email").equalTo(mEmailView.getText().toString());

                    showProgress(true);

                    searchForUserMail.addListenerForSingleValueEvent(new ValueEventListener () {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount() != 0) {
                                Toast.makeText(getApplicationContext(), "User already exists! (email)",
                                        Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "User exists");
                                showProgress(false);
                            } else {
                                createUserwithEmail();
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }


                    });
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}

