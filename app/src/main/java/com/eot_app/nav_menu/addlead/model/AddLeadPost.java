package com.eot_app.nav_menu.addlead.model;



import java.util.ArrayList;

import java.util.HashSet;
import java.util.Set;

public class AddLeadPost {
    String cltId="";
    String siteId="";
    String conId="";

    Set<String> jtId= new HashSet<>();
    String des="";
    int type;
    int afId;
    int source;
    int createdby;


    public int getAfId() {
        return afId;
    }

    public void setAfId(int afId) {
        this.afId = afId;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getCreatedby() {
        return createdby;
    }

    public void setCreatedby(int createdby) {
        this.createdby = createdby;
    }

    public AddLeadPost() {
    }

    String status="";

    String leadEndDate="";
    String inst="";
    String nm="";
    String cnm="";
    String snm="";
    String email="";
    String mob1="";
    String mob2="";
    String adr="";
    String city="";
    String referenceName = "";

    String state="";
    String ctry="";
    String zip="";

    int clientForFuture=0;
    int siteForFuture=0;
    int contactForFuture=0;

    String remiDatetime="";

    String lat="";
    String lng="";
    String landmark="";



    //add param to create sub job of contract under contract
    ArrayList<QuestionListPostLead> answerArray;

    String leadStartDate="";

    public String getLeadStartDate() {
        return leadStartDate;
    }

    public void setLeadStartDate(String leadStartDate) {
        this.leadStartDate = leadStartDate;
    }

    public String getLeadEndDate() {
        return leadEndDate;
    }

    public void setLeadEndDate(String leadEndDate) {
        this.leadEndDate = leadEndDate;
    }

    public ArrayList<QuestionListPostLead> getAnswerArray() {
        return answerArray;
    }

    public void setAnswerArray(ArrayList<QuestionListPostLead> answerArray) {
        this.answerArray = answerArray;
    }



    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
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


    public String getRemiDatetime() {
        return remiDatetime;
    }

    public void setRemiDatetime(String remiDatetime) {
        this.remiDatetime = remiDatetime;
    }



    public ArrayList<QuestionListPostLead> getAnswerArrayList() {
        return answerArray;
    }

    public void setAnswerArrayList(ArrayList<QuestionListPostLead> answerArrayList) {
        this.answerArray = answerArrayList;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getAdr() {
        return adr;
    }

    public void setAdr(String adr) {
        this.adr = adr;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getCltId() {
        return cltId;
    }

    public void setCltId(String cltId) {
        this.cltId = cltId;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }


    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getConId() {
        return conId;
    }

    public void setConId(String conId) {
        this.conId = conId;
    }


    public Set<String> getJtId() {
        return jtId;
    }

    public void setJtId(Set<String> jtId) {
        this.jtId = jtId;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getInst() {
        return inst;
    }

    public void setInst(String inst) {
        this.inst = inst;
    }

    public String getNm() {
        return nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public String getCnm() {
        return cnm;
    }

    public void setCnm(String cnm) {
        this.cnm = cnm;
    }

    public String getSnm() {
        return snm;
    }

    public void setSnm(String snm) {
        this.snm = snm;
    }


    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCtry() {
        return ctry;
    }

    public void setCtry(String ctry) {
        this.ctry = ctry;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public int getClientForFuture() {
        return clientForFuture;
    }

    public void setClientForFuture(int clientForFuture) {
        this.clientForFuture = clientForFuture;
    }

    public int getSiteForFuture() {
        return siteForFuture;
    }

    public void setSiteForFuture(int siteForFuture) {
        this.siteForFuture = siteForFuture;
    }

    public int getContactForFuture() {
        return contactForFuture;
    }

    public void setContactForFuture(int contactForFuture) {
        this.contactForFuture = contactForFuture;
    }


    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }


}
