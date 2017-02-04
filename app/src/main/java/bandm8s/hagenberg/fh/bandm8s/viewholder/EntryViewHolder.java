package bandm8s.hagenberg.fh.bandm8s.viewholder;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import bandm8s.hagenberg.fh.bandm8s.R;
import bandm8s.hagenberg.fh.bandm8s.models.Entry;

public class EntryViewHolder extends RecyclerView.ViewHolder {

    //UI
    private TextView mTitleView;
    private TextView mAuthorView;
    private TextView mGenreView;
    private TextView mLocationView;
    private ImageView mProfileView;

    public EntryViewHolder(View itemView) {
        super(itemView);

        mTitleView = (TextView) itemView.findViewById(R.id.entry_title);
        mAuthorView = (TextView) itemView.findViewById(R.id.entry_author);
        mGenreView = (TextView) itemView.findViewById(R.id.entry_genre);
        mLocationView = (TextView) itemView.findViewById(R.id.entry_location);
        mProfileView = (ImageView) itemView.findViewById(R.id.entry_author_profile_pic);
    }

    public void bindToEntry(Entry entry) {
        mTitleView.setText(entry.getmTitle());
        // mAuthorView.setText(entry.getmAuthor());
        mGenreView.setText(entry.getmGenre());
        mLocationView.setText(entry.getmLocation());


        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = mDatabase.getReference("users");
        Query searchForUserName = myRef.child(entry.getmUid()).child("mUsername");
        searchForUserName.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.getValue().toString();
                mAuthorView.setText(userName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Query searchForUserPic = myRef.child(entry.getmUid()).child("mProfilePic");
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


    private void getCroppedBitmap(Bitmap bitmap) {


        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(Resources.getSystem(), bitmap);

        //roundedBitmapDrawable.setCornerRadius(20.0f);
        roundedBitmapDrawable.setCircular(true);
        roundedBitmapDrawable.setAntiAlias(true);
        mProfileView.setImageDrawable(roundedBitmapDrawable);
    }


}
