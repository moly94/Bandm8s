package bandm8s.hagenberg.fh.bandm8s.fragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;



public class BandEntriesFragment extends EntryListFragment {
    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        return databaseReference.child("band-entries").orderByKey();
    }
}
