package bandm8s.hagenberg.fh.bandm8s.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Kevin on 26.01.2017.
 */

public class UserEntriesFragment extends EntryListFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("user-entries").orderByKey();
    }
}
