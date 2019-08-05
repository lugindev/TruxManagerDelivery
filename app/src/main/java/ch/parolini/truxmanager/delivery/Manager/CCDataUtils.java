package ch.parolini.truxmanager.delivery.Manager;

import android.content.Context;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.IOException;
import java.net.URI;

/**
 * Created by salam on 24.06.2019.
 */

class CCDataUtils {

        public static int getRotationFromFileUri(Context context, URI contentUri) {
            String filepath = contentUri.getPath();
            ExifInterface exifData = null;
            try {
                exifData = new ExifInterface(filepath);
                int orientation = exifData.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                return exifToDegrees(orientation);
            } catch (IOException e) {
            } catch (IllegalArgumentException e) {
            } catch (Exception e) {
            }
            return 0;
        }

        private static int exifToDegrees(int exifOrientation) {
            if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return 90;
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
                return 180;
            } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
                return 270;
            }
            return 0;
        }

    }

