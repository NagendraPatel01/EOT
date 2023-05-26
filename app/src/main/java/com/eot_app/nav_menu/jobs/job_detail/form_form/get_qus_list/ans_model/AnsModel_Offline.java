package com.eot_app.nav_menu.jobs.job_detail.form_form.get_qus_list.ans_model;

import java.util.ArrayList;
import java.util.List;

public class AnsModel_Offline extends Ans_Req {

     Ans_Req ans_req;
      List<String> dosanspath;
      List<String> signanspath;
      ArrayList<String> signQueIdArray;
      ArrayList<String> docQueIdArray;

    public AnsModel_Offline( Ans_Req ans_req,List<String> docAns, List<String> signAns, ArrayList<String> signQueIdArray, ArrayList<String> docQueIdArray) {
        this.ans_req=ans_req;
        this.dosanspath = docAns;
        this.signanspath = signAns;
        this.signQueIdArray = signQueIdArray;
        this.docQueIdArray = docQueIdArray;
    }

    public Ans_Req getAns_req() {
        return ans_req;
    }

    public void setAns_req(Ans_Req ans_req) {
        this.ans_req = ans_req;
    }

    public List<String> getDosanspath() {
        return dosanspath;
    }

    public void setDosanspath(List<String> dosanspath) {
        this.dosanspath = dosanspath;
    }

    public List<String> getSignanspath() {
        return signanspath;
    }

    public void setSignanspath(List<String> signanspath) {
        this.signanspath = signanspath;
    }

    public ArrayList<String> getSignQueIdArray() {
        return signQueIdArray;
    }

    public void setSignQueIdArray(ArrayList<String> signQueIdArray) {
        this.signQueIdArray = signQueIdArray;
    }

    public ArrayList<String> getDocQueIdArray() {
        return docQueIdArray;
    }

    public void setDocQueIdArray(ArrayList<String> docQueIdArray) {
        this.docQueIdArray = docQueIdArray;
    }
}
