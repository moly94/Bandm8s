package bandm8s.hagenberg.fh.bandm8s;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import bandm8s.hagenberg.fh.bandm8s.models.User;


public class OtherUserProfile extends AppCompatActivity {

    public static final String EXTRA_USER_KEY = "user_key";

    private DatabaseReference mDataBase;
    private FirebaseDatabase mFireBaseDataBase;
    private DatabaseReference mEntryReference;

    private FirebaseUser mUser;
    private User mCurrentUser;

    private TextView mUserName;
    private TextView mGenres;
    private TextView mSkill;
    private TextView mInstruments;
    private TextView mUserBiography;
    private ImageView mProfilePicture;
    private String mUserID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_user_profile);
        Intent intent = getIntent();
        mUserID = intent.getExtras().getString(EXTRA_USER_KEY);
        mFireBaseDataBase = FirebaseDatabase.getInstance();
        mDataBase = FirebaseDatabase.getInstance().getReference();


        startupProfile();


    }

    /**
     * Method to get the data from the Firebase Database loaded into the User Profile
     */
    @Override
    protected void onStart() {
        super.onStart();

        mDataBase.child("users").child(mUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mCurrentUser = dataSnapshot.getValue(User.class);


                mUserName.setText(mCurrentUser.getmUsername());
                mGenres.setText(mCurrentUser.getmGenre());
                mSkill.setText(mCurrentUser.getmSkill());
                mInstruments.setText(mCurrentUser.getmInstruments());
                mUserBiography.setText(mCurrentUser.getmBiography());

                Query searchForUserPic = mDataBase.child("users").child(mUserID).child("mProfilePic");
                searchForUserPic.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() != null) {
                            String profilePic = dataSnapshot.getValue().toString();
                            Log.d("LUL", "Profilepic: " + profilePic);

                            byte[] decodedString = Base64.decode(profilePic.getBytes(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                            getCroppedBitmap(decodedByte);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * Sets up the Activity and connects the TextViews to the global variables
     */
    private void startupProfile() {
        mUserName = (TextView) findViewById(R.id.other_user_profile_name);
        mGenres = (TextView) findViewById(R.id.GenresString);
        mSkill = (TextView) findViewById(R.id.SkillString);
        mInstruments = (TextView) findViewById(R.id.InstrumentsString);
        mUserBiography = (TextView) findViewById(R.id.user_biography);
        mProfilePicture = (ImageView) findViewById(R.id.user_profile_photo);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.startChat) {

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method uses the given Image and cuts it out circular and also sets the circular Image as the profile Picture
     *
     * @param bitmap square Profile Picture
     */
    private void getCroppedBitmap(Bitmap bitmap) {


        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap);

        //roundedBitmapDrawable.setCornerRadius(20.0f);
        roundedBitmapDrawable.setCircular(true);
        roundedBitmapDrawable.setAntiAlias(true);
        mProfilePicture.setImageDrawable(roundedBitmapDrawable);
    }
}