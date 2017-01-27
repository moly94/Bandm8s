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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import bandm8s.hagenberg.fh.bandm8s.models.User;


public class UserProfile extends AppCompatActivity {

    private Spinner mGenres;
    private Spinner mSkill;
    private ImageButton changeProfilePicture;
    private ImageView profilePicture;
    private DatabaseReference mDataBase;
    private FirebaseUser mUser;
    private User mCurrentUser;
    private static final int SELECT_PICTURE = 0;
    private String mUserId = getUid();
    private EditText userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        mDataBase = FirebaseDatabase.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        userName = (EditText) findViewById(R.id.user_profile_name);
        userName.setFocusable(false);
        userName.clearFocus();
        mDataBase.child("users").child(mUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mCurrentUser = dataSnapshot.getValue(User.class);
                setUserName(mCurrentUser, userName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
        MultiSelectionSpinner mInstruments = (MultiSelectionSpinner) findViewById(R.id.multiSpinnerInstruments);
        mInstruments.setItems(getResources().getStringArray(R.array.instruments_array));
        profilePicture = (ImageView) findViewById(R.id.change_user_photo);
        changeProfilePicture = (ImageButton) findViewById(R.id.change_user_photo);
        changeProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imgIntent = new Intent();
                imgIntent.setType("image/*");
                imgIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(imgIntent, "Select Picture"), SELECT_PICTURE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override //Sollte eigentlich funktionieren, aber wenn ausgewählt wird stürtzt es ab oder macht nichts
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = getPath(data.getData());
            profilePicture.setImageBitmap(bitmap);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    static String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void setUserName(User user, EditText userName) {

        userName.setText(mCurrentUser.getmUsername());
    }
}

