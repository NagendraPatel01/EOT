package com.eot_app.nav_menu.quote.add_quotes_pkg.model_pkg;

import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_detail_pkg.inv_detail_model.Tax;

import java.util.List;

public class Update_Quote_ReQ {//var iqmmId: String?


//    var isInvOrNoninv: String?


     String invId;
     String type;
     String rate;
     String qty;
     String unit;
     String discount;
     String des;
     String totalAmount;//last
     List<Tax> taxData;
     String supplierCost;
    //  String isInvOrNoninv;
     String pno;
     String taxamnt;
     String jtId;
    //   String inm;
     String iqmmId;
     String itemId;
     String inm;
// String isInvOrNoninv,

    public Update_Quote_ReQ(String iqmmId, String invId, String type, String rate, String qty, String unit,
                            String discount, String des, String totalAmount, List<Tax> taxData,
                            String supplierCost, String pno, String taxamnt, String jtId, String itemId, String inm) {
        this.iqmmId = iqmmId;
        this.invId = invId;
        this.type = type;
        this.rate = rate;
        this.qty = qty;
        this.unit = unit;
        this.discount = discount;
        this.des = des;
        this.totalAmount = totalAmount;
        this.taxData = taxData;
        this.supplierCost = supplierCost;
       // this.isInvOrNoninv = isInvOrNoninv;
        this.pno = pno;
        this.taxamnt = taxamnt;
        this.jtId = jtId;
        this.itemId=itemId;
        this.inm = inm;

    }


}
