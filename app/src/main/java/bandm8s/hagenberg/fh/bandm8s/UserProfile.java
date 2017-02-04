package bandm8s.hagenberg.fh.bandm8s;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
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

import java.io.ByteArrayOutputStream;

import bandm8s.hagenberg.fh.bandm8s.models.User;


public class UserProfile extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;


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

    /**
     * Sets up the Activity and connects the Edit Texts and Spinners to the global variables
     */
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

        mSaveProfile = (ImageButton) findViewById(R.id.saveChangedData);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
//                    String profilePic = mCurrentUser.getmProfilePic();
//                    byte[] decodedString = Base64.decode(profilePic.getBytes(), Base64.DEFAULT);
//                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                    getCroppedBitmap(decodedByte);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
            Bitmap bitmap = getPath(data.getData());
            getCroppedBitmap(bitmap);
        }
    }

    /**
     * This method uses the given Image and cuts it out circular and also sets the circular Image as the profile Picture
     *
     * @param bitmap square Profile Picture
     */
    public void getCroppedBitmap(Bitmap bitmap) {


        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);

        //roundedBitmapDrawable.setCornerRadius(100.0f);
        roundedBitmapDrawable.setCircular(true);
        roundedBitmapDrawable.setAntiAlias(true);
        mProfilePicture.setImageDrawable(roundedBitmapDrawable);
    }


    //Picaso https://github.com/square/picasso7

    /**
     * Gets the path of the choosen Image form the phone Storage and returns the image
     * @param uri link to the picture
     * @return the path of the image as a bitmap
     */
    private Bitmap getPath(Uri uri) {

        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(column_index);
        //mProfilePicture.setImageBitmap(BitmapFactory.decodeFile(filePath));
        //cursor.close();
        // Convert file path into bitmap image using below line.
        return BitmapFactory.decodeFile(filePath);
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
            onBackPressed();
        } else if (id == R.id.change_user_photo) {
            Intent imgIntent = new Intent();
            imgIntent.setType("image/*");
            imgIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(imgIntent, "Select Picture"), SELECT_PICTURE);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * get id of the user
     * @return userID as string
     */
    static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * when the Activity stars this method gets called, and sets everything stored in the Database to the users profile
     */
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

        //get Profile Picture from User
        String profilePic = mCurrentUser.getmProfilePic();
        byte[] decodedString = Base64.decode(profilePic.getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        getCroppedBitmap(decodedByte);

        //set MultiSelectionSpinner Instruments
        String instrument = mCurrentUser.getmInstruments();
        if (instrument != null) {
            String[] instruments = instrument.split(", ");
            mInstruments.setSelection(instruments);
        }

        //set Biography of User
        mUserBiography.setText(mCurrentUser.getmBiography());
    }

    /**
     * when the user saves the profile everything gets updated to the Firebase database
     */
    private void updateUser() {
        String username = mUserName.getText().toString();
        String eMail = mUser.getEmail();
        String genre = mGenres.getSelectedItem().toString();
        String skill = mSkill.getSelectedItem().toString();
        String instruments = mInstruments.getSelectedItemsAsString();
        String biography = mUserBiography.getText().toString();

        mProfilePicture.buildDrawingCache();
        Bitmap bitmap = mProfilePicture.getDrawingCache();
        String profilePicture = getEncoded64ImageStringFromBitmap(bitmap);


        User bandObject = new User(username, genre, skill, instruments, biography, eMail, profilePicture);
        DatabaseReference myRef = mFireBaseDataBase.getReference("users");
        myRef.child(mUser.getUid()).setValue(bandObject);
        Toast.makeText(UserProfile.this, "UserProfile Successfully updated",
                Toast.LENGTH_LONG).show();

    }

    /**
     * This method gets called to form the given Bitmap into a String
     * @param bitmap Image which gets transformed
     * @return transformed string from bitmap
     */
    public String getEncoded64ImageStringFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        return Base64.encodeToString(byteFormat, Base64.NO_WRAP);
    }

    @Override
    public void onBackPressed() {
        updateUser();
        super.onBackPressed();
    }
}