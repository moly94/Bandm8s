package bandm8s.hagenberg.fh.bandm8s;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import bandm8s.hagenberg.fh.bandm8s.models.Entry;
import bandm8s.hagenberg.fh.bandm8s.models.User;

public class EntryDetailActivity extends AppCompatActivity {

    //stores the UID of the story passed by the MainActivity
    public static final String EXTRA_ENTRY_KEY = "entry_key";

    private static final String TAG = EntryDetailActivity.class.getSimpleName();

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String mUserId = getUid();

    private User mCurrentUser;
    private Entry mCurrentEntry;
    private String mEntryKey;

    //Database Reference
    private DatabaseReference mDataBase;
    private DatabaseReference mEntryReference;

    //UI
    private TextView mAuthor;
    private TextView mTitle;
    private TextView mGenre;
    private TextView mSkill;
    private TextView mLocation;
    private TextView mInstruments;
    private TextView mDescription;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_detail);

        // Get post key from intent
        mEntryKey = getIntent().getStringExtra(EXTRA_ENTRY_KEY);
        if (mEntryKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_STRING_KEY");
        } else {
            Log.d(TAG, "Given Entry Key: " + mEntryKey);
        }

        //  initialize_database_ref
        mDataBase = FirebaseDatabase.getInstance().getReference();

        // initialize entry reference
        mEntryReference = mDataBase.child("entries").child(mEntryKey);

        // initialize UI elements
        mAuthor = (TextView) findViewById(R.id.lblAuthorContent_detail);
        mTitle = (TextView) findViewById(R.id.lblTitleContent_detail);
        mGenre = (TextView) findViewById(R.id.lblGenreContent_detail);
        mSkill = (TextView) findViewById(R.id.lblSkillContent_detail);
        mLocation = (TextView) findViewById(R.id.lblLocationContent_detail);
        mInstruments = (TextView) findViewById(R.id.lblInstrumentsContent_detail);
        mDescription = (TextView) findViewById(R.id.lblDescription_detail);


    }

    @Override
    protected void onStart() {
        super.onStart();

        mEntryReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentEntry = dataSnapshot.getValue(Entry.class);
                Log.d(TAG, mCurrentEntry.getmAuthor() + mCurrentEntry.getmGenre() + mCurrentEntry.getmLocation());
                mAuthor.setText(mCurrentEntry.getmAuthor());
                mTitle.setText(mCurrentEntry.getmTitle());
                mGenre.setText(mCurrentEntry.getmGenre());
                mSkill.setText(mCurrentEntry.getmSkill());
                mLocation.setText(mCurrentEntry.getmLocation());
                mInstruments.setText(mCurrentEntry.getmInstruments());
                mDescription.setText(mCurrentEntry.getmDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_detail_entry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_startChat) {
            //Start chat behaviour
            Intent i = new Intent(getApplicationContext(), ChatActivity.class);
            i.putExtra(EXTRA_ENTRY_KEY, mEntryKey);
            startActivity(i);
        } else if (id == R.id.action_showProfile) {
            Intent i = new Intent(this, OtherUserProfile.class);
            i.putExtra(EXTRA_ENTRY_KEY, mEntryKey);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
