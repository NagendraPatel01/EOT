package com.eot_app.eoteditor;


import android.os.AsyncTask;

public class DownloadImageFromUrl extends AsyncTask<String, Void, String> {
    OnImageLoaded onImageLoaded;
    String inputText;

    public interface OnImageLoaded {
        void imagedLoadedRefreshText(String text);
    }

    public DownloadImageFromUrl(String text, OnImageLoaded onImageLoaded) {
        this.onImageLoaded = onImageLoaded;
        this.inputText = text;
    }

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }
}
