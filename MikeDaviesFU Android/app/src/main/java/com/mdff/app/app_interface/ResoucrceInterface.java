package com.mdff.app.app_interface;

import androidx.fragment.app.FragmentTransaction;

import com.mdff.app.model.ResourceItems;

/**
 * Created by Swati.Gupta on 7/6/2018.
 */

public interface ResoucrceInterface {
    public void displayContent(String id,FragmentTransaction fragmentTransaction);
    public void displaySublist(ResourceItems resource, FragmentTransaction fragmentTransaction);
}
