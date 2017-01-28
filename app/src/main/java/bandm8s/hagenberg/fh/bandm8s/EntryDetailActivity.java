package bandm8s.hagenberg.fh.bandm8s;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import bandm8s.hagenberg.fh.bandm8s.models.User;

public class EntryDetailActivity extends AppCompatActivity {

    //stores the UID of the story passed by the MainActivity
    public static final String EXTRA_ENTRY_KEY = "entry_key";

    private static final String TAG = EntryDetailActivity.class.getSimpleName();

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private String mUserId = getUid();

    private User mCurrentUser;
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
        }

        else {
            Log.d(TAG, "Given Entry Key: " +  mEntryKey);
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


    static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
