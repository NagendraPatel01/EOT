package com.eot_app.nav_menu.audit.audit_list.documents;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.eot_app.R;
import com.eot_app.utility.AppConstant;
import com.eot_app.utility.AppUtility;
import com.eot_app.utility.CompressImageInBack;
import com.eot_app.utility.language_support.LanguageController;
import com.eot_app.utility.util_interfaces.OnImageCompressed;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;
import ja.burhanrashid52.photoeditor.SaveSettings;


public class ActivityEditImageDialog extends AppCompatActivity implements View.OnClickListener, CropImageView.OnSetImageUriCompleteListener, CropImageView.OnGetCroppedImageCompleteListener {
    public static final String FIXED_ASPECT_RATIO = "extra_fixed_aspect_ratio";
    public static final String EXTRA_ASPECT_RATIO_X = "extra_aspect_ratio_x";
    public static final String EXTRA_ASPECT_RATIO_Y = "extra_aspect_ratio_y";
    private static final String ASPECT_RATIO_X = "ASPECT_RATIO_X";
    private static final String ASPECT_RATIO_Y = "ASPECT_RATIO_Y";
    private static final int DEFAULT_ASPECT_RATIO_VALUES = 100;
    private Bitmap imageBitmap;
    private PhotoEditorView photo_editor_view;
    private PhotoEditor photoEditor;
    private EditText edt_file_name;
    private File file;
    ImageView image_paint, image_back, image_undo, image_save, image_green, image_red, image_blue, image_crop,image_rotate;
    private String defaultFileName = "";
    private boolean colorButtonFlag = false;
    //  private DocumentsFragment documentsFragment;
    private CropImageView cropImageView;
    private int mAspectRatioX = DEFAULT_ASPECT_RATIO_VALUES;
    private int mAspectRatioY = DEFAULT_ASPECT_RATIO_VALUES;
    boolean isFixedAspectRatio = false;
    private TextView cancel_txt, crop_txt;
    private ConstraintLayout constraintLayout_editImage;
    private RelativeLayout cropImageViewLayout;
    private Uri uri;
    private boolean allowInOffline = false;
    private final boolean isImage = false;
    private int rotation;
    Bitmap scaledBitmap;


    public ActivityEditImageDialog() {

    }

    public void setBitmapImage(Bitmap bitmap, Uri uri) {
        this.imageBitmap = bitmap;
        this.uri = uri;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_image_dialog_layout);

        if (getIntent() != null && getIntent().hasExtra("allowOffline")) {
            allowInOffline = getIntent().getBooleanExtra("allowOffline", false);
        }
        findViews();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("uri")) {
            try {
                uri = intent.getParcelableExtra("uri");

                new CompressImageInBack(ActivityEditImageDialog.this, new OnImageCompressed() {
                    @Override
                    public void onImageCompressed(Bitmap bitmap) {
                        imageBitmap = bitmap;
                        photo_editor_view.getSource().setImageBitmap(imageBitmap);
                        getVisibleViews();
                    }
                }, uri).compressImageInBckg();//.execute(uri);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void getCropImageRatios() {
        isFixedAspectRatio = getIntent().getBooleanExtra(FIXED_ASPECT_RATIO, false);
        mAspectRatioX = getIntent().getIntExtra(EXTRA_ASPECT_RATIO_X, DEFAULT_ASPECT_RATIO_VALUES);
        mAspectRatioY = getIntent().getIntExtra(EXTRA_ASPECT_RATIO_Y, DEFAULT_ASPECT_RATIO_VALUES);

        hideEditImageView();

        // Initialize components of the app
        // If you want to fix the aspect ratio, set it to 'true'

        cropImageView.setFixedAspectRatio(isFixedAspectRatio);

        /**load bitmap from uri */
        cropImageView.setImageUriAsync(uri);
    }

    @Override
    public void onStart() {
        super.onStart();
        cropImageView.setOnSetImageUriCompleteListener(this);
        cropImageView.setOnGetCroppedImageCompleteListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        cropImageView.setOnSetImageUriCompleteListener(null);
        cropImageView.setOnGetCroppedImageCompleteListener(null);
    }

    private void findViews() {

        image_back = findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        image_paint = findViewById(R.id.image_paint);
        image_paint.setOnClickListener(this);
        image_undo = findViewById(R.id.image_undo);
        image_undo.setOnClickListener(this);
        image_save = findViewById(R.id.image_save);
        image_save.setOnClickListener(this);
        image_green = findViewById(R.id.image_green);
        image_blue = findViewById(R.id.image_blue);
        image_red = findViewById(R.id.image_red);

        image_crop = findViewById(R.id.image_crop);
        image_crop.setOnClickListener(this);

        image_rotate=findViewById(R.id.image_rotate);
        image_rotate.setOnClickListener(this);

        constraintLayout_editImage = findViewById(R.id.constraintLayout_editImage);

        intializedCropviews();


        image_green.setOnClickListener(this);
        image_blue.setOnClickListener(this);
        image_red.setOnClickListener(this);

        edt_file_name = findViewById(R.id.edt_file_name);
        edt_file_name.setHint(LanguageController.getInstance().getMobileMsgByKey(AppConstant.enter_file_name));
        photo_editor_view = findViewById(R.id.photo_editor_view);
        photo_editor_view.getSource().setImageBitmap(imageBitmap);

        if (allowInOffline)
            edt_file_name.setVisibility(View.INVISIBLE);


        photoEditor = new PhotoEditor.Builder(this, photo_editor_view)
                .setPinchTextScalable(true)
                .build();

        //getExternalFilesDir(), getExternalCacheDir(), or getExternalMediaDirs()
        //(Environment.DIRECTORY_PICTURES);// return path
        //file = new File(Environment.getExternalStorageDirectory()

        file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                + File.separator + "image_"
                + System.currentTimeMillis() + ".png");

        try {
            file.createNewFile();
            String fname = file.getName();
            if (fname.contains(".")) {
                fname = fname.substring(0, fname.lastIndexOf("."));
                defaultFileName = fname;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void intializedCropviews() {
        cropImageViewLayout = findViewById(R.id.cropImageViewLayout);
        cropImageView = findViewById(R.id.cropImageView);
        cancel_txt = findViewById(R.id.cancel_txt);
        cancel_txt.setOnClickListener(this);
        crop_txt = findViewById(R.id.crop_txt);
        crop_txt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                onBackPressed();
                break;
            case R.id.image_paint:
                if (!colorButtonFlag) {
                    showColorButton();
                    photoEditor.setBrushDrawingMode(true);
                } else {
                    hideColorButton();
                }
                image_undo.setVisibility(View.VISIBLE);
                break;
            case R.id.image_save:
                //  AppUtility.hideSoftKeyboard(getActivity());
                String fileName = edt_file_name.getText().toString();
                if (!fileName.equals("")) {
                    defaultFileName = fileName;
                }
                Log.v("Image File Name ","Image Save :::::");

                saveImage(defaultFileName);
                break;
            case R.id.image_undo:
                photoEditor.undo();
                break;
            case R.id.image_red:
                photoEditor.setBrushColor(getResources().getColor(R.color.red_color));
                image_paint.setColorFilter(getResources().getColor(R.color.red_color));
                break;
            case R.id.image_green:
                photoEditor.setBrushColor(getResources().getColor(R.color.color_green));
                image_paint.setColorFilter(getResources().getColor(R.color.color_green));
                break;
            case R.id.image_blue:
                photoEditor.setBrushColor(getResources().getColor(R.color.color_blue));
                image_paint.setColorFilter(getResources().getColor(R.color.color_blue));
                break;
            case R.id.image_crop:
                getCropImageRatios();
                break;
            case R.id.cancel_txt:
                cropiExit();
                break;
            case R.id.image_rotate:
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                final int width = metrics.widthPixels;
                final int height = metrics.heightPixels;
                scaledBitmap= Bitmap.createScaledBitmap(imageBitmap, width, height, true);
                photo_editor_view.getSource().setImageBitmap(getRotatedBitmap(scaledBitmap));
                imagerotateview();
                break;
            case R.id.crop_txt:
                cropImageView.getCroppedImageAsync(cropImageView.getCropShape(), 0, 0);
                break;
        }
    }

    private Bitmap getRotatedBitmap(Bitmap bitmap) {
        if (rotation % 360 == 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation, bitmap.getWidth(),
                bitmap.getHeight());
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight() , matrix, true);
    }

    private void imagerotateview()
    {
        hiderotate();
        rotation += 90;
        rotation %= 360;
        Bitmap bitmap = getRotatedBitmap(scaledBitmap);
        photo_editor_view.getSource().setImageBitmap(bitmap);
    }

    private void cropiExit() {
        image_crop.setVisibility(View.VISIBLE);
        cropImageView.setVisibility(View.GONE);
        image_rotate.setVisibility(View.VISIBLE);
        photo_editor_view.setVisibility(View.VISIBLE);
        cancel_txt.setVisibility(View.GONE);
        crop_txt.setVisibility(View.GONE);
        image_paint.setVisibility(View.VISIBLE);
        constraintLayout_editImage.setVisibility(View.VISIBLE);
    }

    private void hideEditImageView() {
        cropImageViewLayout.setVisibility(View.VISIBLE);
        cancel_txt.setVisibility(View.VISIBLE);
        crop_txt.setVisibility(View.VISIBLE);
        cropImageView.setVisibility(View.VISIBLE);
        image_rotate.setVisibility(View.VISIBLE);
        image_crop.setVisibility(View.GONE);
        image_paint.setVisibility(View.GONE);
        photo_editor_view.setVisibility(View.GONE);
        constraintLayout_editImage.setVisibility(View.GONE);
    }

    private void hiderotate() {
        cropImageViewLayout.setVisibility(View.GONE);
        cancel_txt.setVisibility(View.GONE);
        crop_txt.setVisibility(View.GONE);
        cropImageView.setVisibility(View.GONE);
        image_crop.setVisibility(View.VISIBLE);
        image_rotate.setVisibility(View.VISIBLE);
        image_paint.setVisibility(View.VISIBLE);
        photo_editor_view.setVisibility(View.VISIBLE);
        constraintLayout_editImage.setVisibility(View.VISIBLE);
    }

    private void showColorButton() {
        colorButtonFlag = true;
        image_red.setVisibility(View.VISIBLE);
        image_green.setVisibility(View.VISIBLE);
        image_blue.setVisibility(View.VISIBLE);
    }

    private void hideColorButton() {
        colorButtonFlag = false;
        image_red.setVisibility(View.GONE);
        image_green.setVisibility(View.GONE);
        image_blue.setVisibility(View.GONE);
    }

    private void saveImage(final String imageName) {
        if (AppUtility.isInternetConnected() || allowInOffline) {
            AppUtility.progressBarShow(this);
            try {
                SaveSettings saveSettings = new SaveSettings.Builder()
                        .setClearViewsEnabled(true)
                        .setTransparencyEnabled(true)
                        .build();

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if (file.exists()) {
                    photoEditor.saveAsFile(file.getAbsolutePath(), saveSettings, new PhotoEditor.OnSaveListener() {
                        @Override
                        public void onSuccess(@NonNull String imagePath) {
                            AppUtility.progressBarDissMiss();
                            Log.v("Image File Name ","imagePath:::::"+imagePath);
                            Log.v("Image File Name ","imageName:::::"+imageName);

                            Intent intent = new Intent();
                            intent.putExtra("path", imagePath);
                            intent.putExtra("name", imageName);
                            intent.putExtra("isImage", isImage);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d("TAG", "failed");
                            AppUtility.progressBarDissMiss();
                        }
                    });
                } else {
                    Log.d("TAG", "file does't exist");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                AppUtility.alertDialog(ActivityEditImageDialog.this, LanguageController.getInstance().getMobileMsgByKey(AppConstant.dialog_alert), LanguageController.getInstance().getMobileMsgByKey(AppConstant.err_check_network), LanguageController.getInstance().getMobileMsgByKey(AppConstant.ok), "", new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return null;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Saves the state upon rotating the screen/restarting the activity
    @Override
    public void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putInt(ASPECT_RATIO_X, mAspectRatioX);
        bundle.putInt(ASPECT_RATIO_Y, mAspectRatioY);
    }

    /**
     * after image croping
     **/
    @Override
    public void onGetCroppedImageComplete(CropImageView view, Bitmap bitmap, Exception error) {
        if (error == null) {
            imageBitmap = bitmap;
            getVisibleViews();
            //getBitmapToUri(bitmap);
            photo_editor_view.getSource().setImageBitmap(bitmap);

        } else {
            cropiExit();
        }
    }

    private void getVisibleViews() {
        cropImageView.setVisibility(View.GONE);
        cropImageViewLayout.setVisibility(View.GONE);
        image_crop.setVisibility(View.VISIBLE);
        image_rotate.setVisibility(View.VISIBLE);
        photo_editor_view.setVisibility(View.VISIBLE);
        constraintLayout_editImage.setVisibility(View.VISIBLE);
        image_paint.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSetImageUriComplete(CropImageView view, Uri uri, Exception error) {
        if (error == null) {
            //Toast.makeText(mCropImageView.this, "Image load successful", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(mCropImageView.this, "Image load failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Unable to load image", Toast.LENGTH_LONG).show();
        }
    }


}
