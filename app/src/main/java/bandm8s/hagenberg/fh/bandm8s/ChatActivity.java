package bandm8s.hagenberg.fh.bandm8s;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bandm8s.hagenberg.fh.bandm8s.models.Message;
import bandm8s.hagenberg.fh.bandm8s.models.Chat;
import bandm8s.hagenberg.fh.bandm8s.models.User;

@SuppressWarnings("LogConditional")
public class ChatActivity extends BaseActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = "ChatActivity";

    //stores the UID of the chat passed by the MainActivity
    public static final String EXTRA_CHAT_KEY = "chat_key";

    //firebase references
    private static DatabaseReference mChatReference;
    private static DatabaseReference mUserChatReference;
    private DatabaseReference mUserStarredReference;
    private static DatabaseReference mMessageReference;
    private ValueEventListener mChatListener;
    private String mChatKey;
    private MessageAdapter mAdapter;
    private static Chat mChat;

    private static ArrayList<String> commentOptions=new ArrayList<>();

    //UI
    private TextView mOpponentView;
    private TextView mTitleView;
    private TextView mDescriptionView;
    private TextView mStarNumView;
    private EditText mMessageField;
    private ImageView mStarView;
    private ImageView mChatAuthorPicView;
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
       /*
       mChatKey = getIntent().getStringExtra(EXTRA_CHAT_KEY);
       if (mChatKey == null) {
           throw new IllegalArgumentException("Must pass EXTRA_STRING_KEY");
       }*/

       mChatKey="UppEhGpzbcaju3yjOhNnBmrezOY2";
       // Initialize Database
       mChatReference = FirebaseDatabase.getInstance().getReference()
               .child("stories").child(mChatKey);

       mMessageReference = FirebaseDatabase.getInstance().getReference()
               .child("stories-comments").child(mChatKey);
       mUserStarredReference = FirebaseDatabase.getInstance().getReference().
               child("user-starred-stories").child(getUid()).child(mChatKey);

       //Init View
       mOpponentView = (TextView) findViewById(R.id.chat_opponent);
       mStarNumView = (TextView) findViewById(R.id.chat_detail_num_stars);
       mMessageField = (EditText) findViewById(R.id.field_comment_text);

       mMessageButton = (Button) findViewById(R.id.button_contribute);
       mMessagesRecycler = (RecyclerView) findViewById(R.id.recycler_comments);


       mStarView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onStarClicked(mUserChatReference);
               onStarClicked(mChatReference);
               onStarClicked(mUserStarredReference);
           }
       });

       mMessageButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (mMessageField.getText().length() >0)
                   postMessage();
           }
       });

       final GridLayoutManager manager = new GridLayoutManager(this, 6);
       final int multiplier=6;
       manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
           @Override
           public int getSpanSize(int position) {
               int length = mAdapter.mMessages.get(position).text.length();
               if (length < multiplier)
                   return 1;
               else if (length > multiplier * manager.getSpanCount())
                   return manager.getSpanCount();
               else
                   return (mAdapter.mMessages.get(position).text.length() / multiplier);
           }
       });

       mMessagesRecycler.setLayoutManager(manager);

   }

   @Override
   protected void onStart() {
       super.onStart();

       // Add value event listener to the post
       // [START post_value_event_listener]
       ValueEventListener chatListener = new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               // Get Post object and use the values to update the UI
               mChat = dataSnapshot.getValue(Chat.class);
               mUserChatReference = FirebaseDatabase.getInstance().getReference()
                       .child("user-stories").child(mChat.mUid).child(mChatKey);
               // [START_EXCLUDE]
               mOpponentView.setText(mChat.mOpponent);
               mTitleView.setText(mChat.mTitle);
               mDescriptionView.setText(mChat.mDescription);


               DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users");
               Query searchForUserPic = myRef.child(mChat.mUid).child("profilePic");
               searchForUserPic.addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       StorageReference profileReference = FirebaseStorage.getInstance().getReferenceFromUrl(String.valueOf(dataSnapshot.getValue()));


                       final long ONE_MEGABYTE = 1024 * 1024;
                       profileReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                           @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                           @Override
                           public void onSuccess(byte[] bytes) {
                               // Data for profilePic is returned
                               Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                               mChatAuthorPicView.setImageBitmap(getCroppedBitmap(bm, 200));
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                           @Override
                           public void onFailure(@NonNull Exception exception) {
                               // Handle any errors
                           }
                       });
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
               // [END_EXCLUDE]
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
       };
       mChatReference.addValueEventListener(chatListener);
       // [END post_value_event_listener]

       // Keep copy of post listener so we can remove it when app stops
       mChatListener = chatListener;

       // Listen for comments
       mAdapter = new MessageAdapter(this, mMessageReference);
       mMessagesRecycler.setAdapter(mAdapter);
   }



   private void onStarClicked(DatabaseReference storiesRef) {
       storiesRef.runTransaction(new Transaction.Handler() {
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

   private void postMessage() {
       final String uid = getUid();
       FirebaseDatabase.getInstance().getReference().child("users").child(uid)
               .addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(DataSnapshot dataSnapshot) {
                       //user info
                       User user = dataSnapshot.getValue(User.class);
                       String authorName = user.mUsername;

                       //new message object
                       String messageText = mMessageField.getText().toString();
                       Message message = new Message(authorName, uid, messageText);

                       //push comment
                       mMessageReference.push().setValue(message);

                       //clear field
                       mMessageField.setText(null);

                       mMessageButton.setEnabled(false);
                   }

                   @Override
                   public void onCancelled(DatabaseError databaseError) {

                   }
               });
   }

   private static class CommentViewHolder extends RecyclerView.ViewHolder {

       public TextView messageText;

       public CommentViewHolder(View itemView) {
           super(itemView);

           messageText = (TextView) itemView.findViewById(R.id.message_text);

       }
   }

   private static class MessageAdapter extends RecyclerView.Adapter<CommentViewHolder> {

       private Context mContext;
       private DatabaseReference mDatabaseReference;
       private ChildEventListener mChildEventListener;

       private List<String> mMessageIds = new ArrayList<>();
       private List<Message> mMessages = new ArrayList<>();

       public MessageAdapter(Context context, DatabaseReference ref) {
           this.mContext = context;
           this.mDatabaseReference = ref;

           // Create child event listener
           // [START child_event_listener_recycler]
           ChildEventListener childEventListener = new ChildEventListener() {
               @Override
               public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                   Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                   // A new comment has been added, add it to the displayed list
                   Message message = dataSnapshot.getValue(Message.class);



                   // [START_EXCLUDE]
                   // Update RecyclerView
                   mMessageIds.add(dataSnapshot.getKey());
                   mMessages.add(message);
                   notifyItemInserted(mMessages.size() - 1);
                   // [END_EXCLUDE]
               }

               @Override
               public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                   Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                   // A comment has changed, use the key to determine if we are displaying this
                   // comment and if so displayed the changed comment.
                   Message newMessage = dataSnapshot.getValue(Message.class);
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
                       if ((messageIndex==mMessageIds.size()-1) && mMessages.get(messageIndex).uid.equals(getUid()))
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
           ref.addChildEventListener(childEventListener);
           // [END child_event_listener_recycler]

           // Store reference to listener so it can be removed on app stop
           mChildEventListener = childEventListener;
       }

       @Override
       public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           LayoutInflater inflater = LayoutInflater.from(mContext);
           View view = inflater.inflate(R.layout.item_message, parent, false);
           return new CommentViewHolder(view);
       }

       @Override
       public void onBindViewHolder(CommentViewHolder holder, final int position) {
           final Message message = mMessages.get(position);
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
