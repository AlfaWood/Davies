package com.mdff.app.model;

/**
 * Created by Deepika.Mishra on 6/1/2018.
 */

public class Profile {
    private String id;

    private String first_name;

    private String username;

    private String email;

    private String last_name;

    private String profile_pic;
    private String exp_year;

    private String exp_month;
    private String last4;
    private String created_at;
    public String getLast4 ()
    {
        return last4;
    }

    public void setLast4 (String last4)
    {
        this.last4 = last4;
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

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getFirst_name ()
    {
        return first_name;
    }

    public void setFirst_name (String first_name)
    {
        this.first_name = first_name;
    }

    public String getUsername ()
    {
        return username;
    }

    public void setUsername (String username)
    {
        this.username = username;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    public String getLast_name ()
    {
        return last_name;
    }

    public void setLast_name (String last_name)
    {
        this.last_name = last_name;
    }

    public String getProfile_pic ()
    {
        return profile_pic;
    }

    public void setProfile_pic (String profile_pic)
    {
        this.profile_pic = profile_pic;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
