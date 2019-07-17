package com.mdff.app.model;
import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by Swati.Gupta on 4/20/2018.
 */

public class BmpImage implements Serializable {

    private Bitmap bmp;
    public void setBmp(Bitmap bmp)
    {
        this.bmp=bmp;
    }

    public Bitmap getBmp()
    {
        return this.bmp;
    }
}
