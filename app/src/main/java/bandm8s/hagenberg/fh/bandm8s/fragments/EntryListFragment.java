package bandm8s.hagenberg.fh.bandm8s.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import bandm8s.hagenberg.fh.bandm8s.ChatActivity;
import bandm8s.hagenberg.fh.bandm8s.EntryDetailActivity;
import bandm8s.hagenberg.fh.bandm8s.R;
import bandm8s.hagenberg.fh.bandm8s.models.Entry;
import bandm8s.hagenberg.fh.bandm8s.viewholder.EntryViewHolder;

public abstract class EntryListFragment extends Fragment {

    private static final String TAG = "EntryListFragment";
    private FirebaseRecyclerAdapter<Entry, EntryViewHolder> mAdapter;

    private DatabaseReference mDatabase;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;


    public EntryListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_entry_list, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecycler = (RecyclerView) rootView.findViewById(R.id.entry_list);
        mRecycler.setHasFixedSize(true);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set up Layout Manager, reverse layout
        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);

        // Set up FirebaseRecyclerAdapter with the Query
        final Query entryQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Entry, EntryViewHolder>(Entry.class, R.layout.item_entry,
                EntryViewHolder.class, entryQuery) {
            @Override
            protected void populateViewHolder(final EntryViewHolder viewHolder, final Entry model, final int position) {

                final DatabaseReference entryRef = getRef(position);

                //Set onClick listener
                final String entryKey = entryRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Toast.makeText(getActivity(), entryKey, Toast.LENGTH_LONG).show();



                        Fragment f = getFragmentManager().findFragmentById(R.id.pager);

                        if (f instanceof ChatEntriesFragment || f instanceof ChatEntriesPassiveFragment){

                            entryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Entry e = dataSnapshot.getValue(Entry.class);

                                        Intent i = new Intent(getActivity(), ChatActivity.class);
                                        i.putExtra(EntryDetailActivity.EXTRA_ENTRY_KEY, entryKey);
                                        startActivity(i);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        else {
                            Intent i = new Intent(getActivity(), EntryDetailActivity.class);
                            i.putExtra(EntryDetailActivity.EXTRA_ENTRY_KEY, entryKey);
                            startActivity(i);
                        }

                    }
                });

                //bind entry to viewHolder
                viewHolder.bindToEntry(model);
            }
        };
        mRecycler.setAdapter(mAdapter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }


    /**
     * Get the unique ID from the currently signed in user
     * @return returns UID from current user
     */
    String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Define the DatabaseReference from where you want to get your entries to fill the fragment
     *
     * @param databaseReference The path of the entries to be displayed
     * @return the Query to be used for your fragment
     */
    protected abstract Query getQuery(DatabaseReference databaseReference);

}
