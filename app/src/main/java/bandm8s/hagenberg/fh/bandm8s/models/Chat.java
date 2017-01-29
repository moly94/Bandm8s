package bandm8s.hagenberg.fh.bandm8s.models;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Felix on 24.01.2017.
 */

public class Chat {
    public String mUid;
    public String mOpponent;
    public String mTitle;
    public String mDescription;
    public boolean friendsOnly;

    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();
    public ArrayList<String> tags = new ArrayList<>();

    public Chat() {
        // Default constructor required for calls to DataSnapshot.getValue(Story.class)
    }


    /**
     * @param userId  the unique ID of the mOpponent of this story
     * @param auth    Username of the mOpponent
     * @param t       mTitle of this story
     * @param b       short mDescription of the content
     * @param tagList some metatags
     * @param fOnly   is story public or friends-only?
     */
    public Chat(String userId, String auth, String t, String b, List<String> tagList, boolean fOnly) {
        mUid = userId;
        mOpponent = auth;
        mTitle = t;
        mDescription = b;
        tags = (ArrayList<String>) tagList;
        friendsOnly = fOnly;

    }



    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("mUid", mUid);
        result.put("mOpponent", mOpponent);
        result.put("mTitle", mTitle);
        result.put("mDescription", mDescription);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("friendsonly", friendsOnly);

        return result;
    }
    // [END post_to_map]
}
