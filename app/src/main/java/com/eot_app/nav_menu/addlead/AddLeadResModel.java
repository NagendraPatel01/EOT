package com.eot_app.nav_menu.addlead;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class AddLeadResModel implements Serializable {

        @SerializedName("isDefault")
        private String isDefault;
        @SerializedName("afType")
        private String afType;
        @SerializedName("afJson")
        private String afJson;
        @SerializedName("afName")
        private String afName;
        @SerializedName("afId")
        private String afId;
        private boolean isSelected=false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(String isDefault) {
            this.isDefault = isDefault;
        }

        public String getAfType() {
            return afType;
        }

        public void setAfType(String afType) {
            this.afType = afType;
        }

        public String getAfJson() {
            return afJson;
        }

        public void setAfJson(String afJson) {
            this.afJson = afJson;
        }

        public String getAfName() {
            return afName;
        }

        public void setAfName(String afName) {
            this.afName = afName;
        }

        public String getAfId() {
            return afId;
        }

        public void setAfId(String afId) {
            this.afId = afId;
        }

}
