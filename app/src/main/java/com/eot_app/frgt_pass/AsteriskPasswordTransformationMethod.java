package com.eot_app.frgt_pass;

import android.text.method.PasswordTransformationMethod;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Created by ubuntu on 18/7/18.
 */

public class AsteriskPasswordTransformationMethod extends PasswordTransformationMethod {
    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private static class PasswordCharSequence implements CharSequence {
        CharSequence mSource;

        public PasswordCharSequence(CharSequence source) {
            mSource = source; // Store char sequence
        }

        public char charAt(int index) {
            return '*'; // This is the important part
        }

        public int length() {
            return mSource.length(); // Return default
        }

        @NonNull
        public CharSequence subSequence(int start, int end) {
            return mSource.subSequence(start, end); // Return default
        }
    }
}


