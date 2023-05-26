package com.eot_app.utility.language_support;

public interface Language_Preference_pi {
    String getBackendMsgsModel();

    void setBackendMsgsModel(String BACKEND_MSG_MODEL);

    String getMobileMsgsModel();

    void setMobileMsgsModel(String MOBILE_MSG_MODEL);

    String getStaticMsgsModel();

    void setStaticMsgsModel(String STATIC_MSG_MODEL);

    String getlanguageVersion();

    void setLanguageVersion(String version_no);

    String getlanguageFilename();

    void setLanguageFilename(String lang_filename);

    boolean isUserChangeLang();

    void setisUserChangeLang(boolean is_change);


    void clearPreference();

    void setDefaultPageView(int moduleCode);

     int getDefaultPageView();
}
