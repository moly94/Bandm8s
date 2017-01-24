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
    public String text;
    public int upvoteCount = 0;


    public Message() {

    }

    public Message(String author, String uid, String text) {
        this.author = author;
        this.uid = uid;
        this.text = text;
    }
}
