package rs.aleph.android.example13.activities.db.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = Attraction.TABLE_NAME_ATTRACTION)
public class Attraction {


    public static final String TABLE_NAME_ATTRACTION = "attraction";
    public static final String FIELD_NAME_ID = "id";
    public static final String FIELD_NAME_NAME = "name";
    public static final String FIELD_NAME_DESCRIPTION = "description";
    public static final String FIELD_NAME_PICTURES = "pictures";
    public static final String FIELD_NAME_ADDRESS = "address";
    public static final String FIELD_NAME_PHONE = "phone";
    public static final String FIELD_NAME_WEB = "web";
    public static final String FIELD_NAME_TIME = "time";
    public static final String FIELD_NAME_PRICE = "price";
    public static final String FIELD_NAME_COMMENT = "comment";



    @DatabaseField(columnName = FIELD_NAME_ID, generatedId = true)
    private int mId;

    @DatabaseField(columnName = FIELD_NAME_NAME)
    private String mName;

    @DatabaseField(columnName = FIELD_NAME_DESCRIPTION)
    private String mDescription;

    @DatabaseField(columnName = FIELD_NAME_PICTURES)
    private String mPictures;

    @DatabaseField(columnName = FIELD_NAME_ADDRESS)
    private String mAddress;

    @DatabaseField(columnName = FIELD_NAME_PHONE)
    private int mPhone;

    @DatabaseField(columnName = FIELD_NAME_WEB)
    private String mWeb;

    @DatabaseField(columnName = FIELD_NAME_TIME)
    private String mTime;

    @DatabaseField(columnName = FIELD_NAME_PRICE)
    private double mPrice;

    @DatabaseField(columnName = FIELD_NAME_COMMENT)
    private String mComment;



    public Attraction() {
    }


    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmDescription() {
        return mDescription;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getmPictures() {
        return mPictures;
    }

    public void setmPictures(String mPictures) {
        this.mPictures = mPictures;
    }

    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public int getmPhone() {
        return mPhone;
    }

    public void setmPhone(int mPhone) {
        this.mPhone = mPhone;
    }

    public String getmWeb() {
        return mWeb;
    }

    public void setmWeb(String mWeb) {
        this.mWeb = mWeb;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public double getmPrice() {
        return mPrice;
    }

    public void setmPrice(double mPrice) {
        this.mPrice = mPrice;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }


    @Override
    public String toString() {
        return mName;
    }
}
