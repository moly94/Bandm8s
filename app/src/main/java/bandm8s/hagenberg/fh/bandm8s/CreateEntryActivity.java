package bandm8s.hagenberg.fh.bandm8s;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateEntryActivity extends AppCompatActivity {


    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    //Database Reference
    private DatabaseReference mDataBase;

    //GUI
    private Spinner mGenres;
    private Spinner mSkill;
    private MultiSelectionSpinner mInstruments;
    private EditText mTitle, mLocation, mDescription;

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
}
