package bandm8s.hagenberg.fh.bandm8s.models;

/**
 * Created by Felix on 18.01.2017.
 */

public class Band {
    public String mUsername;
    public String mEmail;
    public String mProfilePic;

    //TODO: Change to own profile pic!!!
    public Band(String username, String email){
        this(username, email, "gs://project-cow.appshot.com/testProfile.png");
    }

    public Band(String username, String email, String profilePic){
        mUsername=username;
        mEmail=email;
        mProfilePic=profilePic;
    }
}
