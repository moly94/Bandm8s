package bandm8s.hagenberg.fh.bandm8s;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.Spinner;

public class CreateEntryActivity extends AppCompatActivity {

    //GUI
    private Spinner mGenres;
    private Spinner mSkill;
    private MultiSelectionSpinner mInstruments;

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

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
