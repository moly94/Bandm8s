package bandm8s.hagenberg.fh.bandm8s.models;

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
    public String mGenre;
    public String mSkill;
    public String mInstruments;
    public String mBiography;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    //TODO: Change to own profile pic!!!
    public User(String username, String email, boolean isBand) {
        this(username, "Rock", "Beginner", "Guitar", "Nix", email, "gs://project-cow.appshot.com/testProfile.png", isBand);

    }

    public User(String username, String email, String profilePic, boolean isBand) {
        mUsername = username;
        mEmail = email;
        mProfilePic = profilePic;
        mIsBand = isBand;
    }




    public User(String username, String genre, String skill, String instruments, String biography, String email, String profilePic) {
        mUsername = username;
        mGenre = genre;
        mSkill = skill;
        mInstruments = instruments;
        mBiography = biography;
        mEmail = email;
        mProfilePic = profilePic;
    }

    public User(String username, String genre, String skill, String instruments, String biography, String email, String profilePic, boolean isBand) {
        mUsername = username;
        mGenre = genre;
        mSkill = skill;
        mInstruments = instruments;
        mBiography = biography;
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

    public String getmGenre() {
        return mGenre;
    }

    public void setmGenre(String mGenre) {
        this.mGenre = mGenre;
    }

    public String getmSkill() {
        return mSkill;
    }

    public void setmSkill(String mSkill) {
        this.mSkill = mSkill;
    }

    public String getmInstruments() {
        return mInstruments;
    }

    public void setmInstruments(String mInstruments) {
        this.mInstruments = mInstruments;
    }

    public String getmBiography() {
        return mBiography;
    }

    public void setmBiography(String mBiography) {
        this.mBiography = mBiography;
    }
}
