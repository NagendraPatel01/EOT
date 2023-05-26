package com.eot_app.nav_menu.jobs.job_detail.invoice.inventry_pkg;

import android.os.Parcel;
import android.os.Parcelable;

import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_db.tax_dao.TaxConverter;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_detail_pkg.inv_detail_model.Tax;

import java.util.List;

import androidx.room.TypeConverters;

public class ItemParts implements Parcelable {

    private String itemId;
    private String inm;
    private String ides;
    private String pno;
    private String qty;
    private String rate;
    private String discount;
    private String type;
    private String isactive;
    private String show_Invoice;
    private String lowStock;
    private String image;
    @TypeConverters(TaxConverter.class)
    private List<Tax> tax = null;
    private String unit;
    private String hsncode;
    private String supplierCost;
    private String searchKey;
    private String serialNo;
    private String isBillable;
    private String taxType;
    private String isBillableChange;
    private String ijmmId = "";
    private String tempNm = "";
    private String des;

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getIjmmId() {
        return ijmmId;
    }

    public void setIjmmId(String ijmmId) {
        this.ijmmId = ijmmId;
    }

    public String getTempNm() {
        return tempNm;
    }

    public void setTempNm(String tempNm) {
        this.tempNm = tempNm;
    }

    protected ItemParts(Parcel in) {
        itemId = in.readString();
        inm = in.readString();
        ides = in.readString();
        pno = in.readString();
        qty = in.readString();
        rate = in.readString();
        discount = in.readString();
        type = in.readString();
        isactive = in.readString();
        show_Invoice = in.readString();
        lowStock = in.readString();
        image = in.readString();
        tax = in.createTypedArrayList(Tax.CREATOR);
        unit = in.readString();
        hsncode = in.readString();
        supplierCost = in.readString();
        searchKey = in.readString();
        serialNo = in.readString();
        isBillable = in.readString();
        taxType = in.readString();
        isBillableChange = in.readString();
        ijmmId = in.readString();
        tempNm = in.readString();
        des = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemId);
        dest.writeString(inm);
        dest.writeString(ides);
        dest.writeString(pno);
        dest.writeString(qty);
        dest.writeString(rate);
        dest.writeString(discount);
        dest.writeString(type);
        dest.writeString(isactive);
        dest.writeString(show_Invoice);
        dest.writeString(lowStock);
        dest.writeString(image);
        dest.writeTypedList(tax);
        dest.writeString(unit);
        dest.writeString(hsncode);
        dest.writeString(supplierCost);
        dest.writeString(searchKey);
        dest.writeString(serialNo);
        dest.writeString(isBillable);
        dest.writeString(taxType);
        dest.writeString(isBillableChange);
        dest.writeString(ijmmId);
        dest.writeString(tempNm);
        dest.writeString(des);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ItemParts> CREATOR = new Creator<ItemParts>() {
        @Override
        public ItemParts createFromParcel(Parcel in) {
            return new ItemParts(in);
        }

        @Override
        public ItemParts[] newArray(int size) {
            return new ItemParts[size];
        }
    };

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getInm() {
        return inm;
    }

    public void setInm(String inm) {
        this.inm = inm;
    }

    public String getIdes() {
        return ides;
    }

    public void setIdes(String ides) {
        this.ides = ides;
    }

    public String getPno() {
        return pno;
    }

    public void setPno(String pno) {
        this.pno = pno;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIsactive() {
        return isactive;
    }

    public void setIsactive(String isactive) {
        this.isactive = isactive;
    }

    public String getShow_Invoice() {
        return show_Invoice;
    }

    public void setShow_Invoice(String show_Invoice) {
        this.show_Invoice = show_Invoice;
    }

    public String getLowStock() {
        return lowStock;
    }

    public void setLowStock(String lowStock) {
        this.lowStock = lowStock;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Tax> getTax() {
        return tax;
    }

    public void setTax(List<Tax> tax) {
        this.tax = tax;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getHsncode() {
        return hsncode;
    }

    public void setHsncode(String hsncode) {
        this.hsncode = hsncode;
    }

    public String getSupplierCost() {
        return supplierCost;
    }

    public void setSupplierCost(String supplierCost) {
        this.supplierCost = supplierCost;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getIsBillable() {
        return isBillable;
    }

    public void setIsBillable(String isBillable) {
        this.isBillable = isBillable;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }

    public String getIsBillableChange() {
        return isBillableChange;
    }

    public void setIsBillableChange(String isBillableChange) {
        this.isBillableChange = isBillableChange;
    }
}
