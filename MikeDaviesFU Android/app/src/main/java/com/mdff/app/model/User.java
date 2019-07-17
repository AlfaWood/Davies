package com.mdff.app.model;

import java.io.Serializable;

/**
 * Created by Swati.Gupta on 4/5/2018.
 */

public class User implements Serializable{
    private String id;
    private String fname;
    private String lname;
    private String uname;
    private String email;
    private String password;
    private String paymentNonce;

    public void setPaymentNonce(String paymentNonce)
    {
        this.paymentNonce=paymentNonce;
    }

    public String getPaymentNonce()
    {
        return this.paymentNonce;
    }
    public void setFname(String fname)
    {
        this.fname=fname;
    }

    public String getFname()
    {
        return this.fname;
    }

    public void setLname(String lname)
    {
        this.lname=lname;
    }

    public String getLname()
    {
        return this.lname;
    }
    public void setUname(String uname)
    {
        this.uname=uname;
    }

    public String getUname()
    {
        return this.uname;
    }
    public void setEmail(String email)
    {
        this.email=email;
    }

    public String getEmail()
    {
        return this.email;
    }

    public void setPassword(String password)
    {
        this.password=password;
    }

    public String getPassword()
    {
        return this.password;
    }

    public void setId(String id)
    {
        this.id=id;
    }

    public String getId()
    {
        return this.id;
    }

}
