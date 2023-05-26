package com.eot_app.nav_menu.client.clientlist.client_detail.contact.edit_contact.editmodel;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ubuntu on 8/6/18.
 */

public class ClientContactAddEdit_Model {

    int checkd;
    String compId;
    String cltId;
    String cnm;
    String email;
    String mob1;
    String mob2;
    String fax;
    String twitter;
    String skype;
    int def;
    private String tempId;
    private String extraField1;
    private String extraField2 ;
    private String extraField3;
    private String extraField4;
    private String notes;
    private String isactive;
    private Set<String> siteId = new HashSet<>();

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

    public ClientContactAddEdit_Model(String compId,
                                      String cltId, String cnm,
                                      String email, String mob,
                                      String alternate, String fax,
                                      String skype, String twitter,
                                      int i, int checked,
                                      Set<String> siteIds,
                                      String extraField1, String extraField2,
                                      String extraField3, String extraField4,
                                      String notes

    ) {
        this.compId = compId;
        this.cltId = cltId;
        this.cnm = cnm;
        this.email = email;
        this.mob1 = mob;
        this.mob2 = alternate;
        this.fax = fax;
        this.twitter = twitter;
        this.skype = skype;
        this.def = i;
        this.checkd = checked;
        this.siteId = siteIds;
        this.extraField1 = extraField1;
        this.extraField2 = extraField2;
        this.extraField3 = extraField3;
        this.extraField4 = extraField4;
        this.notes = notes;
        this.isactive = isactive;

    }

    public Set<String> getSiteId() {
        return siteId;
    }

    public String getTempId() {
        return tempId;
    }

    public void setTempId(String tempId) {
        this.tempId = tempId;
    }

    public int getCheckd() {
        return checkd;
    }

    public String getCompId() {
        return compId;
    }

    public String getCltId() {
        return cltId;
    }

    public String getCnm() {
        return cnm;
    }

    public String getEmail() {
        return email;
    }

    public String getMob1() {
        return mob1;
    }

    public String getMob2() {
        return mob2;
    }

    public String getFax() {
        return fax;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getSkype() {
        return skype;
    }

    public int getDef() {
        return def;
    }
}

class ClientContactEdit_Model {
    int checkd;
    String compId;
    String cltId;
    String conId;
    String cnm;
    String email;
    String mob1;
    String mob2;
    String fax;
    String twitter;
    String skype;
    int def;
    private Set<String> siteId = new HashSet<>();
    private String extraField1;
    private String extraField2 ;
    private String extraField3;
    private String extraField4;
    private String notes;
    private String isactive;

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

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public ClientContactEdit_Model(String cltId, String conId, String cnm,
                                   String email, String mob1, String mob2,
                                   String fax, String twitter, String skype,
                                   int def, int checkd, Set<String> siteId,
                                   String extraField1, String extraField2,
                                   String extraField3, String extraField4,
                                   String notes,String isactive) {
        this.cltId = cltId;
        this.conId = conId;
        this.cnm = cnm;
        this.email = email;
        this.mob1 = mob1;
        this.mob2 = mob2;
        this.fax = fax;
        this.skype = skype;
        this.twitter = twitter;
        this.def = def;
        this.checkd = checkd;
        this.siteId = siteId;
        this.extraField1 = extraField1;
        this.extraField2 = extraField2;
        this.extraField3 = extraField3;
        this.extraField4 = extraField4;
        this.notes = notes;
        this.isactive = isactive;
    }


    public int getCheckd() {
        return checkd;
    }

    public String getConId() {
        return conId;
    }

    public String getCnm() {
        return cnm;
    }

    public String getEmail() {
        return email;
    }

    public String getMob1() {
        return mob1;
    }

    public String getMob2() {
        return mob2;
    }

    public String getFax() {
        return fax;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getSkype() {
        return skype;
    }

    public int getDef() {
        return def;
    }

    public Set<String> getSiteId() {
        return siteId;
    }
}