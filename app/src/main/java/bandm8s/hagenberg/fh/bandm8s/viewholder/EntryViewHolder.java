package bandm8s.hagenberg.fh.bandm8s.viewholder;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import bandm8s.hagenberg.fh.bandm8s.R;
import bandm8s.hagenberg.fh.bandm8s.models.Entry;

public class EntryViewHolder extends RecyclerView.ViewHolder{

    //UI
    private TextView mTitleView;
    private TextView mAuthorView;
    private TextView mGenreView;
    private TextView mLocationView;
    private ImageView mProfileView;

    public EntryViewHolder(View itemView) {
        super(itemView);

        mTitleView = (TextView) itemView.findViewById(R.id.entry_title);
        mAuthorView = (TextView) itemView.findViewById(R.id.entry_author);
        mGenreView = (TextView) itemView.findViewById(R.id.entry_genre);
        mLocationView = (TextView) itemView.findViewById(R.id.entry_location);
        mProfileView = (ImageView) itemView.findViewById(R.id.entry_author_profile_pic);
    }

    public void bindToEntry(Entry entry) {
        mTitleView.setText(entry.getmTitle());
        mAuthorView.setText(entry.getmAuthor());
        mGenreView.setText(entry.getmGenre());
        mLocationView.setText(entry.getmLocation());

        /// TODO: 26.01.2017 add profile picture

    }
}
