package com.example.admin.vkclub;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by admin on 7/26/2017.
 */

public class DbBitmapUtility {
    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public static String getString(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        byte[] b = stream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static byte[] getBytesFromString(String encoded){
        byte[] imageAsBytes = Base64.decode(encoded, Base64.DEFAULT);
        return imageAsBytes;
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
