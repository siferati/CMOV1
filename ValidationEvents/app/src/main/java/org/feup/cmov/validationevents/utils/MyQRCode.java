package org.feup.cmov.validationevents.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import static android.app.Activity.RESULT_OK;

/**
 * Contains utilities for creating and scanning QR Codes.
 */
public class MyQRCode {

    /**
     * Start an intent to scan a QR Code.
     *
     * @param activity The activity that called this method.
     */
    public static void scan(Activity activity) {

        try {
            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            activity.startActivityForResult(intent, 0);
        }
        catch (ActivityNotFoundException e) {
            AlertDialog.Builder downloadDialog = new AlertDialog.Builder(activity);
            downloadDialog.setTitle("No Scanner Found");
            downloadDialog.setMessage("Download a scanner?");
            downloadDialog.setPositiveButton("Yes", (dialogInterface, i) -> {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            });
            downloadDialog.setNegativeButton("No", null);
            downloadDialog.show();
        }
    }


    /**
     * Handles the result returned by the scan activity.
     * Should be called inside the onActivityResult() method
     * of the activity that called the scan() method.
     *
     * @param requestCode Same as onActivityResult.
     * @param resultCode Same as onActivityResult.
     * @param data Same as onActivityResult.
     *
     * @return The string read from the QR Code, or null if none could be read.
     */
    public static String onScanResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String decoded = new String(Base64.decode(data.getStringExtra("SCAN_RESULT"), Base64.DEFAULT));
                return decoded;
            }
        }

        return null;
    }
}