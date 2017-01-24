package bandm8s.hagenberg.fh.bandm8s.models;

/**
 * Created by Felix on 18.01.2017.
 */

public class User {

    public String mUsername;
    public String mEmail;
    public String mProfilePic;
    public boolean mIsBand;

    //TODO: Change to own profile pic!!!
    public User(String username, String email, boolean isBand){
        this(username, email, "gs://project-cow.appshot.com/testProfile.png", isBand);
    }

    public User(String username, String email, String profilePic, boolean isBand){
        mUsername=username;
        mEmail=email;
        mProfilePic=profilePic;
        mIsBand=isBand;
    }
}
