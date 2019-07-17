package com.mdff.app.utility;



import android.content.Context;
import android.graphics.Typeface;
import android.widget.Toast;

import java.lang.reflect.Field;

public class TypefaceUtil {


    public static void overrideFont(Context context, String defaultFontNameToOverride, String customFontFileNameInAssets) {
        try {
            final Typeface customFontTypeface = Typeface.createFromAsset(context.getAssets(), customFontFileNameInAssets);

            final Field defaultFontTypefaceField = Typeface.class.getDeclaredField(defaultFontNameToOverride);
            defaultFontTypefaceField.setAccessible(true);
            defaultFontTypefaceField.set(null, customFontTypeface);
        }
        catch (Exception e) {


            Toast.makeText(context, e+ "Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride, Toast.LENGTH_SHORT).show();
            //Log.i("Can not set custom font " + customFontFileNameInAssets + " instead of " + defaultFontNameToOverride);
        }
    }
}
