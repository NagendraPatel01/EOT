package com.eot_app.nav_menu.quote.quotes_add_item_pkg.item_model_pkg;

import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_detail_pkg.inv_detail_model.Tax;

import java.util.List;

public class AddItem_Model {
    String quotId;
    String itemId;
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
    String isInvOrNoninv;
    String pno;
    String taxamnt;
    String jtId;
    String inm;


    public AddItem_Model(String quotId, String itemId, String invId, String type, String rate,
                         String qty, String discount, String des, List<Tax> taxData, String unit,
                         String supplierCost, String isInvOrNoninv, String pno, String taxamnt, String totalAmount,
                         String jtId, String inm) {
        this.quotId = quotId;
        this.itemId = itemId;
        this.invId = invId;
        this.type = type;
        this.rate = rate;
        this.qty = qty;
        this.discount = discount;
        this.des = des;
        this.taxData = taxData;
        this.unit = unit;
        this.supplierCost = supplierCost;
        this.isInvOrNoninv = isInvOrNoninv;
        this.pno = pno;
        this.taxamnt = taxamnt;
        this.totalAmount = totalAmount;
        this.jtId = jtId;
        this.inm = inm;

    }

}
