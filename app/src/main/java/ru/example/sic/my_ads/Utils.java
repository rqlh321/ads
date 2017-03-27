package ru.example.sic.my_ads;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class Utils {
    final static float MAX_IMAGE_SIZE = 800;

    public static Bitmap scaleDown(Bitmap realImage, boolean filter) {
        float ratio = Math.min(
                MAX_IMAGE_SIZE / realImage.getWidth(),
                MAX_IMAGE_SIZE / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        return Bitmap.createScaledBitmap(realImage, width,
                height, filter);
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

}
