package bandm8s.hagenberg.fh.bandm8s.models;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Felix on 18.01.2017.
 */

@IgnoreExtraProperties
public class User {

    public String mUsername;
    public String mEmail;
    public String mProfilePic;
    public boolean mIsBand;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    //TODO: Change to own profile pic!!!
    public User(String username, String email, boolean isBand) {
        this(username, email, "gs://project-cow.appshot.com/testProfile.png", isBand);
    }

    public User(String username, String email, String profilePic, boolean isBand) {
        mUsername = username;
        mEmail = email;
        mProfilePic = profilePic;
        mIsBand = isBand;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmProfilePic() {
        return mProfilePic;
    }

    public void setmProfilePic(String mProfilePic) {
        this.mProfilePic = mProfilePic;
    }

    public boolean ismIsBand() {
        return mIsBand;
    }

    public void setmIsBand(boolean mIsBand) {
        this.mIsBand = mIsBand;
    }
}
