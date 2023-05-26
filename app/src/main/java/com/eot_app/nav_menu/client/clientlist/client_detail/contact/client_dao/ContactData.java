package com.eot_app.nav_menu.client.clientlist.client_detail.contact.client_dao;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.eot_app.nav_menu.client.clientlist.client_detail.contact.edit_contact.editmodel.SiteId;
import com.eot_app.nav_menu.client.clientlist.client_detail.contact.edit_contact.editmodel.SiteidConverter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ubuntu on 7/6/18.
 */
@Entity(indices = {@Index(value = "conId", unique = true)})
public class ContactData implements Parcelable {

    public static final Creator<ContactData> CREATOR = new Creator<ContactData>() {
        @Override
        public ContactData createFromParcel(Parcel in) {
            return new ContactData(in);
        }

        @Override
        public ContactData[] newArray(int size) {
            return new ContactData[size];
        }
    };
    @PrimaryKey
    @NonNull
    private String conId;
    private String cltId;
    private String cnm;
    private String email;
    private String mob1;
    private String mob2;
    private String fax;
    private String twitter;
    private String skype;
    private String def;

    public String getExtraField1() {
        return extraField1;
    }

    public void setExtraField1(String extraField1) {
        this.extraField1 = extraField1;
    }

    public String getExtraField2() {
        return extraField2;
    }

    public void setExtraField2(String extraField2) {
        this.extraField2 = extraField2;
    }

    private String tempId;
    private String extra;
    private String isdelete;
    private String isactive="1";
    private String conExtraField3Label;
    private String conExtraField4Label ;

    private String extraField3;
    private String extraField4;
    private String notes;
    private String extraField1;
    private String extraField2;

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getConExtraField3Label() {
        return conExtraField3Label;
    }

    public void setConExtraField3Label(String conExtraField3Label) {
        this.conExtraField3Label = conExtraField3Label;
    }

    public String getConExtraField4Label() {
        return conExtraField4Label;
    }

    public void setConExtraField4Label(String conExtraField4Label) {
        this.conExtraField4Label = conExtraField4Label;
    }

    public String getExtraField3() {
        return extraField3;
    }

    public void setExtraField3(String extraField3) {
        this.extraField3 = extraField3;
    }

    public String getExtraField4() {
        return extraField4;
    }

    public void setExtraField4(String extraField4) {
        this.extraField4 = extraField4;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @TypeConverters(SiteidConverter.class)
    private List<SiteId> siteId = new ArrayList<>();

    public ContactData() {
    }

    protected ContactData(Parcel in) {
        conId = in.readString();
        cltId = in.readString();
        cnm = in.readString();
        email = in.readString();
        mob1 = in.readString();
        mob2 = in.readString();
        fax = in.readString();
        twitter = in.readString();
        skype = in.readString();
        def = in.readString();
        tempId = in.readString();
        extra = in.readString();
        isdelete = in.readString();
        isactive = in.readString();
        conExtraField3Label = in.readString();
        conExtraField4Label = in.readString();
        extraField3 = in.readString();
        extraField4 = in.readString();
        notes = in.readString();
        extraField1 = in.readString();
        extraField2 = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(conId);
        dest.writeString(cltId);
        dest.writeString(cnm);
        dest.writeString(email);
        dest.writeString(mob1);
        dest.writeString(mob2);
        dest.writeString(fax);
        dest.writeString(twitter);
        dest.writeString(skype);
        dest.writeString(def);
        dest.writeString(tempId);
        dest.writeString(extra);
        dest.writeString(isdelete);
        dest.writeString(isactive);
        dest.writeString(conExtraField3Label);
        dest.writeString(conExtraField4Label);
        dest.writeString(extraField3);
        dest.writeString(extraField4);
        dest.writeString(notes);
        dest.writeString(extraField1);
        dest.writeString(extraField2);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public List<SiteId> getSiteId() {
        return siteId;
    }

    public void setSiteId(List<SiteId> siteId) {
        this.siteId = siteId;
    }

    public String getTempId() {
        return tempId;
    }

    public void setTempId(String tempId) {
        this.tempId = tempId;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(String isdelete) {
        this.isdelete = isdelete;
    }

    public String getCltId() {
        return cltId;
    }

    public void setCltId(String cltId) {
        this.cltId = cltId;
    }

    @NonNull
    public String getConId() {
        return conId;
    }

    public void setConId(@NonNull String conId) {
        this.conId = conId;
    }

    public String getCnm() {
        return cnm;
    }

    public void setCnm(String cnm) {
        this.cnm = cnm;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMob1() {
        return mob1;
    }

    public void setMob1(String mob1) {
        this.mob1 = mob1;
    }

    public String getMob2() {
        return mob2;
    }

    public void setMob2(String mob2) {
        this.mob2 = mob2;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }


}
