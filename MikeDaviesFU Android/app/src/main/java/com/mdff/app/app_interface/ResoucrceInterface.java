package com.mdff.app.app_interface;

import android.support.v4.app.FragmentTransaction;

import com.mdff.app.model.ResourceItems;

/**
 * Created by Swati.Gupta on 7/6/2018.
 */

public interface ResoucrceInterface {
    public void displayContent(String id,FragmentTransaction fragmentTransaction);
    public void displaySublist(ResourceItems resource, FragmentTransaction fragmentTransaction);
}
