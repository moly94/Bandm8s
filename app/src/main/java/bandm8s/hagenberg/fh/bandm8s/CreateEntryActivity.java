package bandm8s.hagenberg.fh.bandm8s;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import bandm8s.hagenberg.fh.bandm8s.models.Entry;
import bandm8s.hagenberg.fh.bandm8s.models.User;

public class CreateEntryActivity extends AppCompatActivity {

    private static final String TAG = CreateEntryActivity.class.getSimpleName();

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String mUserId = getUid();

    private User mCurrentUser;

    //Database Reference
    private DatabaseReference mDataBase;

    //GUI
    private Spinner mGenres;
    private Spinner mSkill;
    private MultiSelectionSpinner mInstruments;
    private EditText mTitle, mLocation, mDescription;

    private TextView mlblInstruments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_entry);

        mGenres = (Spinner) findViewById(R.id.spinnerGenre);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> genreAdapter = ArrayAdapter.createFromResource(this,
                R.array.genres_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        genreAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mGenres.setAdapter(genreAdapter);

        mSkill = (Spinner) findViewById(R.id.spinnerSkill);
        ArrayAdapter<CharSequence> skillAdapter = ArrayAdapter.createFromResource(this,
                R.array.skillLevel_array, android.R.layout.simple_spinner_item);
        skillAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSkill.setAdapter(skillAdapter);

        mInstruments = (MultiSelectionSpinner) findViewById(R.id.multiSpinnerInstruments);
        mInstruments.setItems(getResources().getStringArray(R.array.instruments_array));

        mTitle = (EditText) findViewById(R.id.txtTitle);
        mLocation = (EditText) findViewById(R.id.txtLocation);
        mDescription = (EditText) findViewById(R.id.txtDescription);

        //  initialize_database_ref
        mDataBase = FirebaseDatabase.getInstance().getReference();

        //get Current User data
        mDataBase.child("users").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentUser = dataSnapshot.getValue(User.class);
                mlblInstruments = (TextView) findViewById(R.id.lblInstruments);

                Log.d(TAG, "User name: " + mCurrentUser.getmUsername() + ", email " + mCurrentUser.getmEmail());
                if (mCurrentUser.ismIsBand()) {
                    Log.d(TAG, "User is a band!");
                    mlblInstruments.append(" (we need)");

                }
                else {
                    Log.d(TAG, "User is NOT a band!");
                    mlblInstruments.append(" (I play)");
                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_create_entry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_confirm) {
            createEntry();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createEntry() {
        final String title = mTitle.getText().toString();
        final String location = mLocation.getText().toString();
        final String description = mDescription.getText().toString();

        final String genre = mGenres.getSelectedItem().toString();
        final String skill = mSkill.getSelectedItem().toString();
        final String instruments = mInstruments.getSelectedItemsAsString();

        final String userid = mUserId;
        final String author = mCurrentUser.getmUsername();
        final boolean isbandentry = mCurrentUser.ismIsBand();

        String key = mDataBase.child("entries").push().getKey();
        Entry entry = new Entry(userid, author, title, location, description, genre, skill, instruments, isbandentry);
        Map<String, Object> entryValues = entry.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/entries/" + key, entryValues);

        if(isbandentry) {
            //childUpdates.put("/band-entries/" + userid + "/" + key, entryValues);
            childUpdates.put("/band-entries/" + key + "/" , entryValues);
        }
        else {
            //childUpdates.put("/user-entries/" + userid + "/" + key, entryValues
            childUpdates.put("/user-entries/" + key + "/" , entryValues);
        }
        childUpdates.put("/user-chats-passive/"+getUid()+"/"+ key + "/", entryValues);

        mDataBase.updateChildren(childUpdates);
        finish();
    }

    static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
