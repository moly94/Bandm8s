package bandm8s.hagenberg.fh.bandm8s;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bandm8s.hagenberg.fh.bandm8s.models.Chat;
import bandm8s.hagenberg.fh.bandm8s.models.Entry;
import bandm8s.hagenberg.fh.bandm8s.models.Message;
import bandm8s.hagenberg.fh.bandm8s.models.User;

import static bandm8s.hagenberg.fh.bandm8s.EntryDetailActivity.EXTRA_ENTRY_KEY;


/**
 * Activity for Group Chats with other users depending on their Entries
 */

@SuppressWarnings("LogConditional")
public  class ChatActivity extends BaseActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = "ChatActivity";

    //stores the UID of the chat passed by the MainActivity
    public static final String EXTRA_CHAT_KEY = "chat_key";

    //firebase references
    private static DatabaseReference mChatReference;
    private static DatabaseReference mUserChatReference;
    private DatabaseReference mUserStarredReference;
    private static DatabaseReference mMessageReference;
    private static DatabaseReference mEntryReference;
    private ValueEventListener mChatListener;
    private DatabaseReference mDatabaseReference;
    private String mChatKey;
    private MessageAdapter mAdapter;
    private static Chat mChat;
    private static Entry mEntry;

    private static ArrayList<String> commentOptions = new ArrayList<>();

    //UI
    private TextView mOpponentView;
    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mStarNumView;
    private EditText mMessageField;
    private ImageView mStarView;
    private ImageView mProfilePicture;
    private RecyclerView mMessagesRecycler;
    private static Button mMessageButton;

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //TODO: RE-Write after finished Beitrag

        // Get post key from intent
        mChatKey = getIntent().getStringExtra(EXTRA_ENTRY_KEY);
        if (mChatKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_STRING_KEY");
        }



        // Initialize Database
        mChatReference = FirebaseDatabase.getInstance().getReference()
                .child("entries").child(mChatKey);

        mMessageReference = FirebaseDatabase.getInstance().getReference()
                .child("messages").child(mChatKey);



        //Init View
        mOpponentView = (TextView) findViewById(R.id.chat_opponent);
        mTitleView = (TextView) findViewById(R.id.chat_title);
       // mDescriptionView = (TextView) findViewById(R.id.chat_description);
        mMessageField = (EditText) findViewById(R.id.field_comment_text);

        mMessageButton = (Button) findViewById(R.id.button_contribute);
        mMessagesRecycler = (RecyclerView) findViewById(R.id.recycler_comments);
        mProfilePicture = (ImageView) findViewById(R.id.chat_opponent_profile_pic);


        mMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMessageField.getText().length() > 0)
                    postMessage();
            }
        });

        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(getApplicationContext());
        mMessagesRecycler.setLayoutManager(mLayout);
        mMessagesRecycler.setItemAnimator(new DefaultItemAnimator());
        mMessagesRecycler.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReference.child("entries").child(mChatKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mEntry = dataSnapshot.getValue(Entry.class);
                mOpponentView.setText(mEntry.getmAuthor());
                mTitleView.setText(mEntry.getmTitle());
                //mDescriptionView.setText(mEntry.getmDescription());



                DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
                Query searchForUserPic = myRef.child(mEntry.getmUid()).child("mProfilePic");
                searchForUserPic.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getValue() != null) {
                            String profilePic = dataSnapshot.getValue().toString();
                            Log.d("LUL", "Profilepic: " + profilePic);

                            byte[] decodedString = Base64.decode(profilePic.getBytes(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                            getCroppedBitmap(decodedByte);
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(ChatActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]

            }
        });


        // Listen for comments
        mAdapter = new MessageAdapter(this, mMessageReference);

        mMessagesRecycler.setAdapter(mAdapter);

        ViewGroup.LayoutParams params = mMessagesRecycler.getLayoutParams();
        params.height = 43;
        mMessagesRecycler.setLayoutParams(params);

    }

    public void getCroppedBitmap(Bitmap bitmap) {


        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);

        //roundedBitmapDrawable.setCornerRadius(100.0f);
        roundedBitmapDrawable.setCircular(true);
        roundedBitmapDrawable.setAntiAlias(true);
        mProfilePicture.setImageDrawable(roundedBitmapDrawable);
    }

    private void onStarClicked(DatabaseReference chatsRef) {
        chatsRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Chat s = mutableData.getValue(Chat.class);
                if (s == null) {
                    return Transaction.success(mutableData);
                }


                //Set value
                mutableData.setValue(s);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(TAG, "chatTransaction complete:" + databaseError);
            }
        });
    }

    /**
     * Posts a message to the firebase
     */
    private void postMessage() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //user info
                        final User user = dataSnapshot.getValue(User.class);

                        FirebaseDatabase.getInstance().getReference().child("entries").child(mChatKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String authorName = user.mUsername;
                                Entry entry = dataSnapshot.getValue(Entry.class);
                                //new message object
                                final String messageText = mMessageField.getText().toString();
                                Message message = new Message(authorName, uid, entry.getmUid(), entry.getmAuthor(), messageText);

                                //push comment
                                mMessageReference.push().setValue(message);
                                String key = mDatabaseReference.child("user-chats").child(mMessageReference.getKey()).getKey();
                                Map<String, Object> entryValues = entry.toMap();

                                Map<String, Object> childUpdates = new HashMap<>();
                                //post to User Chats or user Chats passive depending on the UID
                                if(!uid.equals(entry.getmUid()))
                                    childUpdates.put("/user-chats/"+uid+"/" + key, entryValues);
                                childUpdates.put("/user-chats-passive/"+entry.getmUid()+"/"+key, entryValues);
                                //mDatabaseReference.child("user-chats-passive").child(entry.getmUid()).push().child(key).setValue(entryValues);
                                mDatabaseReference.updateChildren(childUpdates);

                                //clear field
                                mMessageField.setText(null);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Messageviewholder is populated by the Messages
     */
    private static class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText, messageAuthor;

        public MessageViewHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.message_text);
            messageAuthor = (TextView) itemView.findViewById(R.id.message_author);

        }
    }

    /**
     * MessageAdapter is for correctly displaying the messages
     */
    private static class MessageAdapter extends RecyclerView.Adapter<MessageViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private DatabaseReference mEntryReference;
        private ChildEventListener mChildEventListener;

        private List<String> mMessageIds = new ArrayList<>();
        private List<Message> mMessages = new ArrayList<>();

        public MessageAdapter(Context context, DatabaseReference ref) {
            this.mContext = context;
            this.mDatabaseReference = ref;

            loadMessages(FirebaseAuth.getInstance().getCurrentUser().getUid());

        }

        /**
         * Method loads Messages
         * @param messageKey ID of the entry the messages belong to
         */
        public void loadMessages(final String messageKey){
            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    Message message = dataSnapshot.getValue(Message.class);

                    //if(message.opponentId.equals(messageKey)||message.uid.equals(messageKey)) {

                        // [START_EXCLUDE]
                        // Update RecyclerView
                        mMessageIds.add(dataSnapshot.getKey());
                        mMessages.add(message);
                        notifyItemInserted(mMessages.size() - 1);
                        //notifyDataSetChanged();
                        // [END_EXCLUDE]
                    //}

                }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());


                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                Message newMessage = dataSnapshot.getValue(Message.class);

                //if(newMessage.opponentId.equals(messageKey)||newMessage.uid.equals(messageKey)) {

                    String messageKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mMessageIds.indexOf(messageKey);
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mMessages.set(commentIndex, newMessage);


                        // Update the RecyclerView
                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + messageKey);
                    }
                    // [END_EXCLUDE]
               // }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                String messageKey = dataSnapshot.getKey();

                // [START_EXCLUDE]
                int messageIndex = mMessageIds.indexOf(messageKey);
                if (messageIndex > -1) {

                    //enable commentButton if comment is not from the user
                    if ((messageIndex == mMessageIds.size() - 1) && mMessages.get(messageIndex).uid.equals(getUid()))
                        mMessageButton.setEnabled(true);

                    // Remove data from the list
                    mMessageIds.remove(messageIndex);
                    mMessages.remove(messageIndex);

                    // Update the RecyclerView
                    notifyItemRemoved(messageIndex);
                } else {
                    Log.w(TAG, "onChildRemoved:unknown_child:" + messageKey);
                }
                // [END_EXCLUDE]
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                //TODO:moving Comments needed?
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                Toast.makeText(mContext, "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mDatabaseReference.addChildEventListener(childEventListener);
        // [END child_event_listener_recycler]

        // Store reference to listener so it can be removed on app stop
        mChildEventListener = childEventListener;
        }

        @Override
        public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MessageViewHolder holder, final int position) {
            final Message message = mMessages.get(position);
            holder.messageAuthor.setText(message.author+": ");
            holder.messageText.setText(message.text);

        }

        @Override
        public int getItemCount() {
            return mMessages.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }
}
