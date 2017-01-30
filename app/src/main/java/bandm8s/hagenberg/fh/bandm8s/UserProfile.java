package bandm8s.hagenberg.fh.bandm8s;


import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import bandm8s.hagenberg.fh.bandm8s.models.User;


public class UserProfile extends AppCompatActivity {

    private MultiSelectionSpinner mGenres;
    private Spinner mSkill;
    private MultiSelectionSpinner mInstruments;

    private DatabaseReference mDataBase;
    private FirebaseDatabase mFireBaseDataBase;
    private FirebaseUser mUser;
    private User mCurrentUser;

    private String mUserId = getUid();
    private EditText mUserName;
    private EditText mUserBiography;


    private ImageButton mChangeProfilePicture;
    private ImageButton mSaveProfile;
    private ImageView mProfilePicture;
    private static final int SELECT_PICTURE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        mFireBaseDataBase = FirebaseDatabase.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        startupProfile();

        mSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });
        mDataBase.child("users").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mCurrentUser = dataSnapshot.getValue(User.class);
                setProfile();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startupProfile() {
        mUserName = (EditText) findViewById(R.id.user_profile_name);
        mUserName.clearFocus();

        mGenres = (MultiSelectionSpinner) findViewById(R.id.multiSpinnerGenresProfile);
        mGenres.setItems(getResources().getStringArray(R.array.genres_array));

        mSkill = (Spinner) findViewById(R.id.spinnerSkill);
        ArrayAdapter<CharSequence> skillAdapter = ArrayAdapter.createFromResource(this,
                R.array.skillLevel_array, android.R.layout.simple_spinner_item);
        skillAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSkill.setAdapter(skillAdapter);

        mInstruments = (MultiSelectionSpinner) findViewById(R.id.multiSpinnerInstrumentsProfile);
        mInstruments.setItems(getResources().getStringArray(R.array.instruments_array));

        mUserBiography = (EditText) findViewById(R.id.user_biography);

        mProfilePicture = (ImageView) findViewById(R.id.user_profile_photo);
        mChangeProfilePicture = (ImageButton) findViewById(R.id.change_user_photo);
        mChangeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imgIntent = new Intent();
                imgIntent.setType("image/*");
                imgIntent.setAction(Intent.ACTION_GET_CONTENT);
                //startActivityForResult(Intent.createChooser(imgIntent, "Select Picture"), SELECT_PICTURE);
            }
        });
        mSaveProfile = (ImageButton) findViewById(R.id.saveChangedData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.save_changed_profile_data, menu);
        return true;
    }

    @Override
    //Sollte eigentlich funktionieren, aber wenn ausgewählt wird stürtzt es ab oder macht nichts
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = getPath(data.getData());
            mProfilePicture.setImageBitmap(bitmap);
        }
    }

    //Picaso https://github.com/square/picasso7
    private Bitmap getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        cursor.close();
        // Convert file path into bitmap image using below line.
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);

        return bitmap;
    }

    private void selectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.saveChangedData) {
            updateUser();
        }

        return super.onOptionsItemSelected(item);
    }

    static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void setProfile() {

        //set Name of User
        mUserName.setText(mCurrentUser.getmUsername());

        //set MultiSelectionSpinner Genres
        String genre = mCurrentUser.getmGenre();
        if (genre != null) {
            String[] genres = genre.split(", ");
            mGenres.setSelection(genres);
        }


        String skill = mCurrentUser.getmSkill();
        int i = 0;
        if (skill != null) {
            switch (skill) {
                case "Beginner":
                    i = 0;
                    break;
                case "Intermediate":
                    i = 1;
                    break;
                case "Experienced":
                    i = 2;
                    break;
                case "Professional":
                    i = 3;
                    break;
            }
            mSkill.setSelection(i);
        }

        //set MultiSelectionSpinner Instruments
        String instrument = mCurrentUser.getmInstruments();
        if (instrument != null) {
            String[] instruments = instrument.split(", ");
            mInstruments.setSelection(instruments);
        }

        //set Biography of User
        mUserBiography.setText(mCurrentUser.getmBiography());
    }

    private void updateUser() {
        String username = mUserName.getText().toString();
        String eMail = mUser.getEmail();
        String genre = mGenres.getSelectedItem().toString();
        String skill = mSkill.getSelectedItem().toString();
        String instruments = mInstruments.getSelectedItemsAsString();
        String biography = mUserBiography.getText().toString();

        User bandObject = new User(username, genre, skill, instruments, biography, eMail);
        DatabaseReference myRef = mFireBaseDataBase.getReference("users");
        myRef.child(mUser.getUid()).setValue(bandObject);
        Toast.makeText(UserProfile.this, "UserProfile Successfully updated",
                Toast.LENGTH_LONG).show();

    }
}