package org.feup.cmov.customerapp.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

import static android.app.Activity.RESULT_OK;

/**
 * Contains utilities for creating and scanning QR Codes.
 */
public class MyQRCode {

    /**
     * Create a QR Code with given content.
     *
     * @param string Content of QR Code.
     * @param dimensions Dimensions of QR Code.
     *
     * @return The bitmap representation of the created QR Code.
     *
     * @throws WriterException
     */
    public static Bitmap create(String string, int dimensions) throws WriterException {

        // encode string
        BitMatrix bitMatrix = new QRCodeWriter().encode(string, BarcodeFormat.QR_CODE, dimensions, dimensions);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();

        // create bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }

        return bitmap;
    }


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
                return data.getStringExtra("SCAN_RESULT");
            }
        }

        return null;
    }
}