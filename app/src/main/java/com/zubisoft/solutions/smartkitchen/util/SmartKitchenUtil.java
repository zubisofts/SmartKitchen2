package com.zubisoft.solutions.smartkitchen.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.yalantis.ucrop.UCrop;
import com.zubisoft.solutions.smartkitchen.BaseActivity;
import com.zubisoft.solutions.smartkitchen.BuildConfig;
import com.zubisoft.solutions.smartkitchen.R;

import org.aviran.cookiebar2.CookieBar;
import org.aviran.cookiebar2.OnActionClickListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SmartKitchenUtil {

    public static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 1002;
    public static final int REQUEST_IMAGE_CAPTURE = 1001;
    public static final int REQUEST_CAMERA_ACCESS_PERMISSION = 1004;
    public static int requestMode = BuildConfig.VERSION_CODE;
    public static Uri mCurrentPhotoPath;

    public static void pickFromGallery(BaseActivity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            context.requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    context.getString(R.string.permission_read_storage_rationale),
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
                    .setType("image/*")
                    .addCategory(Intent.CATEGORY_OPENABLE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String[] mimeTypes = {"image/jpeg", "image/png"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }

            context.startActivityForResult(Intent.createChooser(intent, "Select Image"), requestMode);
        }
    }

    public static void takeCameraPicture(BaseActivity context) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            photoFile = createImageFile(context);
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        "com.zubisoft.solutions.smartkitchen.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                context.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public static File createImageFile(Context context) {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */

            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = Uri.fromFile(image);

        return image;
    }

    public static void startCrop(Uri selectedUri, Activity context) {

        String destinationFileName = new SimpleDateFormat("dd_MM_yyyy_hhmmss").format(new Date()) + ".jpg";

        UCrop uCrop = UCrop.of(selectedUri, Uri.fromFile(new File(context.getCacheDir(), destinationFileName)));

        uCrop.withAspectRatio(1, 1);
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(60);
        uCrop.withOptions(options).start(context);

    }

    public static boolean deleteFile(File file) {
        if (file.exists()) {
            return file.delete();
        }

        return false;
    }

    public static void showToast(final Activity context, final String text, int duration, ToastType type, int gravity) {


        int color = 0xFFFF;
        int textColor;

        if (type == ToastType.SUCCESS) {
            color = context.getResources().getColor(R.color.success);
        } else if (type == ToastType.ERROR) {
            color = context.getResources().getColor(R.color.error);
        } else if (type == ToastType.INFO) {
            color = context.getResources().getColor(R.color.info);
        } else if (type == ToastType.WARNING) {
            color = context.getResources().getColor(R.color.warning);
        }


        final int finalColor = color;

        CookieBar.build(context)
                .setCustomView(R.layout.toast_layout)
                .setCustomViewInitializer(new CookieBar.CustomViewInitializer() {
                    @Override
                    public void initView(View view) {
                        View bg = view.findViewById(R.id.toast_bg);
                        bg.setBackgroundColor(finalColor);
                        TextView txtTextview = view.findViewById(R.id.text);
                        txtTextview.setText(text);
                    }
                })
                .setAction("Close", new OnActionClickListener() {
                    @Override
                    public void onClick() {
                        CookieBar.dismiss(context);
                    }
                })
                .setCookiePosition(gravity)
                .setDuration(duration)
                .setEnableAutoDismiss(true) // Cookie will stay on display until manually dismissed
                .show();
    }

    public static boolean isNetworkAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = manager.getActiveNetworkInfo();
        if (network != null) {
            if (network.isConnected()) {
                return true;
            } else {
                return false;
            }
        }

        return false;

    }

    public static enum ToastType {
        ERROR,
        SUCCESS,
        INFO,
        WARNING
    }

}
