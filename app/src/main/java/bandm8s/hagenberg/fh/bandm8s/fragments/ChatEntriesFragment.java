package bandm8s.hagenberg.fh.bandm8s.fragments;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Felix on 31.01.2017.
 */

public class ChatEntriesFragment extends ChatListFragment {

    private FirebaseAuth mAuth;

    @Override
    public Query getQuery(DatabaseReference databaseReference) {

        return databaseReference.child("user-chats").child(getUid());
    }
}
