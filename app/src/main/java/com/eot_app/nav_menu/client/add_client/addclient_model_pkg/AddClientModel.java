package com.eot_app.nav_menu.client.add_client.addclient_model_pkg;

/**
 * Created by ubuntu on 18/6/18.
 */

public class AddClientModel {
    private String tempId;
    String conId;
    String compId;
     String nm;
     int pymtType;
     String email;
     String mob1;
     String gstNo;
     String tinNo;
     int industry;
     String adr;
     String ctry;
     String state;
     String city;
     String zip;
     String note;
     String cltId;
    private String industryName;
    private String referral;
    /**
     * default site name
     **/
     String snm;
    /**
     * default contact name
     ***/
     String cnm;
     String lat;
     String lng;
    //  private int reference;


    public AddClientModel(String tempId, String compId, String nm, int pymtType, String email, String mob1, String gstNo, String tinNo,
                          int industry, String adr, String ctry, String state, String city, String zip,
                          String note, String cltId, String snm, String conId, String cnm, String lat, String lng, String industryName
            , String referral) {
        this.tempId = tempId;
        this.compId = compId;
        this.nm = nm;
        this.pymtType = pymtType;
        this.email = email;
        this.mob1 = mob1;
        this.gstNo = gstNo;
        this.tinNo = tinNo;
        this.industry = industry;
        this.adr = adr;
        this.ctry = ctry;
        this.state = state;
        this.city = city;
        this.zip = zip;
        this.note = note;
        this.cltId = cltId;
        this.snm = snm;
        this.conId = conId;
        this.cnm = cnm;
        this.lat = lat;
        this.lng = lng;
        this.industryName = industryName;
        this.referral = referral;
    }

    public String getReferral() {
        return referral;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public String getTempId() {
        return tempId;
    }

    public void setTempId(String tempId) {
        this.tempId = tempId;
    }

    public String getNm() {
        return nm;
    }

    public String getAdr() {
        return adr;
    }

    public String getCity() {
        return city;
    }

    public String getCltId() {
        return cltId;
    }

    public String getConId() {
        return conId;
    }

    public String getCompId() {
        return compId;
    }

    public int getPymtType() {
        return pymtType;
    }

    public String getEmail() {
        return email;
    }

    public String getMob1() {
        return mob1;
    }

    public String getGstNo() {
        return gstNo;
    }

    public String getTinNo() {
        return tinNo;
    }

    public int getIndustry() {
        return industry;
    }

    public String getCtry() {
        return ctry;
    }

    public String getState() {
        return state;
    }

    public String getZip() {
        return zip;
    }

    public String getNote() {
        return note;
    }

    public String getSnm() {
        return snm;
    }

    public String getCnm() {
        return cnm;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

}
