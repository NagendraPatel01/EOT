package com.eot_app.nav_menu.jobs.job_detail.invoice.inventry_pkg;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_db.tax_dao.TaxConverter;
import com.eot_app.nav_menu.jobs.job_detail.invoice.invoice_detail_pkg.inv_detail_model.Tax;
import com.eot_app.utility.DropdownListBean;

import java.util.List;

@Entity(tableName = "Inventry_ReS_Model")
public class Inventry_ReS_Model implements Parcelable, DropdownListBean {
    public static final Creator<Inventry_ReS_Model> CREATOR = new Creator<Inventry_ReS_Model>() {
        @Override
        public Inventry_ReS_Model createFromParcel(Parcel in) {
            return new Inventry_ReS_Model(in);
        }

        @Override
        public Inventry_ReS_Model[] newArray(int size) {
            return new Inventry_ReS_Model[size];
        }
    };
    @PrimaryKey
    @NonNull
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

    private String warrantyType;
    private String warrantyValue;
    private String isGrouped;

    public String getIsGrouped() {
        return isGrouped;
    }

    public void setIsGrouped(String isGrouped) {
        this.isGrouped = isGrouped;
    }

    public String getWarrantyType() {
        return warrantyType;
    }

    public void setWarrantyType(String warrantyType) {
        this.warrantyType = warrantyType;
    }

    public String getWarrantyValue() {
        return warrantyValue;
    }

    public void setWarrantyValue(String warrantyValue) {
        this.warrantyValue = warrantyValue;
    }

    @TypeConverters(PartConverter.class)
    private List<ItemParts> parts = null;

    public List<ItemParts> getParts() {
        return parts;
    }

    public void setParts(List<ItemParts> parts) {
        this.parts = parts;
    }

    public Inventry_ReS_Model() {
    }

    protected Inventry_ReS_Model(Parcel in) {
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
        isBillable = in.readString();
        serialNo = in.readString();
        isBillableChange = in.readString();
        taxType = in.readString();
        parts = in.createTypedArrayList(ItemParts.CREATOR);
        warrantyType = in.readString();
        warrantyValue = in.readString();
        isGrouped = in.readString();
    }

    public static Creator<Inventry_ReS_Model> getCREATOR() {
        return CREATOR;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Inventry_ReS_Model octClone = (Inventry_ReS_Model) super.clone();
        return octClone;
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
        dest.writeString(isBillable);
        dest.writeString(searchKey);
        dest.writeString(isBillableChange);
        dest.writeString(serialNo);
        dest.writeString(taxType);
        dest.writeTypedList(parts);
        dest.writeString(warrantyType);
        dest.writeString(warrantyValue);
        dest.writeString(isGrouped);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @NonNull
    public String getItemId() {
        return itemId;
    }

    public void setItemId(@NonNull String itemId) {
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

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
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

    public String getIsBillableChange() {
        return isBillableChange;
    }

    public void setIsBillableChange(String isBillableChange) {
        this.isBillableChange = isBillableChange;
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
}
