package bandm8s.hagenberg.fh.bandm8s.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@IgnoreExtraProperties
public class Entry {
    private String mUid;
    private String mAuthor;
    private String mTitle;
    private String mLocation;
    private String mDescription;

    private String mGenre;
    private String mSkill;
    private String mInstruments;

    private boolean mIsBandEntry;

    public Entry () {
        // Default constructor required for calls to DataSnapshot.getValue(Entry.class)

    }

    public Entry(String mUid, String mAuthor, String mTitle, String mLocation, String mDescription, String mGenre, String mSkill, String mInstruments, boolean mIsBandEntry) {
        this.mUid = mUid;
        this.mAuthor = mAuthor;
        this.mTitle = mTitle;
        this.mLocation = mLocation;
        this.mDescription = mDescription;
        this.mGenre = mGenre;
        this.mSkill = mSkill;
        this.mInstruments = mInstruments;
        this.mIsBandEntry = mIsBandEntry;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("mUid", mUid);
        result.put("mAuthor", mAuthor);
        result.put("mTitle", mTitle);
        result.put("mLocation", mLocation);
        result.put("mDescription", mDescription);
        result.put("mGenre", mGenre);
        result.put("mSkill", mSkill);
        result.put("mInstruments", mInstruments);
        result.put("mIsBandEntry", mIsBandEntry);

        return result;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getmAuthor() {
        return mAuthor;
    }

    public void setmAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmLocation() {
        return mLocation;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmGenre() {
        return mGenre;
    }

    public void setmGenre(String mGenre) {
        this.mGenre = mGenre;
    }

    public String getmSkill() {
        return mSkill;
    }

    public void setmSkill(String mSkill) {
        this.mSkill = mSkill;
    }

    public String getmInstruments() {
        return mInstruments;
    }

    public void setmInstruments(String mInstruments) {
        this.mInstruments = mInstruments;
    }

    public boolean ismIsBandEntry() {
        return mIsBandEntry;
    }

    public void setmIsBandEntry(boolean mIsBandEntry) {
        this.mIsBandEntry = mIsBandEntry;
    }
}
