package com.mdff.app.model;

/**
 * Created by Swati.Gupta on 6/29/2018.
 */

public class Comment {
    private String id;
    private String userName;
    private String content;
    private String feed_id;
    private String feed_unique_id;
    private String alpha_commented;



    public String getAlpha_commented()
    {
        return alpha_commented;
    }

    public void setAlpha_commented(String alpha_commented)
    {
        this.alpha_commented = alpha_commented;
    }

    public String getFeed_unique_id ()
    {
        return feed_unique_id;
    }

    public void setFeed_unique_id (String feed_unique_id)
    {
        this.feed_unique_id = feed_unique_id;
    }


    public String getFeed_id ()
    {
        return feed_id;
    }

    public void setFeed_id (String feed_id)
    {
        this.feed_id = feed_id;
    }

    public String getUserName ()
    {
        return userName;
    }

    public void setUserName (String userName)
    {
        this.userName = userName;
    }


    public String getContent ()
    {
        return content;
    }

    public void setContent (String content)
    {
        this.content = content;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }




}
