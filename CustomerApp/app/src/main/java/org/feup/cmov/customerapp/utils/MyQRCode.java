package org.feup.cmov.customerapp.utils;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

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

        string = Base64.encodeToString(string.getBytes(), Base64.DEFAULT | Base64.NO_WRAP);

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
}