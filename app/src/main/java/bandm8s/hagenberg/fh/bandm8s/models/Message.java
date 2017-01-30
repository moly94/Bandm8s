package bandm8s.hagenberg.fh.bandm8s.models;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Felix on 24.01.2017.
 */

public class Message {
    public String author;
    public String uid;
    public String opponentId;
    public String opponentName;
    public String text;
    public int upvoteCount = 0;


    public Message() {

    }

    public Message(String author, String uid, String oid, String oName, String text) {
        this.author = author;
        this.uid = uid;
        this.opponentId=oid;
        this.opponentName=oName;
        this.text = text;
    }
}
