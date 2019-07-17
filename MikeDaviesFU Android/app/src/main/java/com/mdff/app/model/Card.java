package com.mdff.app.model;

import java.io.Serializable;

/**
 * Created by Swati.Gupta on 6/12/2018.
 */

public class Card implements Serializable{

    private String id;

    private String fingerprint;

    private String exp_year;

    private String exp_month;

    private String alpha_id;

    private String created_at;

    private String brand;

    private String user_id;

    private String last4;

    private String customer;

    private String card_id;

    private String country;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getFingerprint ()
    {
        return fingerprint;
    }

    public void setFingerprint (String fingerprint)
    {
        this.fingerprint = fingerprint;
    }

    public String getExp_year ()
    {
        return exp_year;
    }

    public void setExp_year (String exp_year)
    {
        this.exp_year = exp_year;
    }

    public String getExp_month ()
    {
        return exp_month;
    }

    public void setExp_month (String exp_month)
    {
        this.exp_month = exp_month;
    }

    public String getAlpha_id ()
    {
        return alpha_id;
    }

    public void setAlpha_id (String alpha_id)
    {
        this.alpha_id = alpha_id;
    }

    public String getCreated_at ()
    {
        return created_at;
    }

    public void setCreated_at (String created_at)
    {
        this.created_at = created_at;
    }

    public String getBrand ()
    {
        return brand;
    }

    public void setBrand (String brand)
    {
        this.brand = brand;
    }

    public String getUser_id ()
    {
        return user_id;
    }

    public void setUser_id (String user_id)
    {
        this.user_id = user_id;
    }

    public String getLast4 ()
    {
        return last4;
    }

    public void setLast4 (String last4)
    {
        this.last4 = last4;
    }

    public String getCustomer ()
    {
        return customer;
    }

    public void setCustomer (String customer)
    {
        this.customer = customer;
    }

    public String getCard_id ()
    {
        return card_id;
    }

    public void setCard_id (String card_id)
    {
        this.card_id = card_id;
    }

    public String getCountry ()
    {
        return country;
    }

    public void setCountry (String country)
    {
        this.country = country;
    }


}
