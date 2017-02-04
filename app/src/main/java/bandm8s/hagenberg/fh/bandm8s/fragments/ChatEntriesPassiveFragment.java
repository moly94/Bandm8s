package bandm8s.hagenberg.fh.bandm8s.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Felix on 02.02.2017.
 */

public class ChatEntriesPassiveFragment extends ChatListFragment {

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("user-chats-passive").child(getUid());
    }
}
