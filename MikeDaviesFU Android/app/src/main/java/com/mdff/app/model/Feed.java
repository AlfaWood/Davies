package com.mdff.app.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Swati.Gupta on 4/13/2018.
 */

public class Feed implements Serializable{


    private String title;
    private String dateAndTime;
    private String socialPlatformName;
    private String storyData;
    private String numberOfLikes;
    private String numberOfComments;
    private String alphaCommented;
    private String like;
    private String feed_id;
    private String feed_unique_id;
    private String comment1;
    private String comment2;
    private String comment1_user;
    private String comment2_user;
    private String isLike;
    private String storyData_with_html;

    private String alpha_comment;
    public String getAlpha_comment()
    {
        return this.alpha_comment;
    }

    public void setAlpha_comment(String alpha_comment)
    {
        this.alpha_comment=alpha_comment;
    }
    public String getStoryData_with_html()
    {
        return this.storyData_with_html;
    }

    public void setStoryData_with_html(String storyData_with_html)
    {
        this.storyData_with_html=storyData_with_html;
    }

    public String getIsLike()
    {
        return this.isLike;
    }

    public void setIsLike(String isLike)
    {
        this.isLike=isLike;
    }

    public void setComment1_user(String comment1_user)
    {
        this.comment1_user=comment1_user;
    }

    public String getComment1_user()
    {
        return this.comment1_user;
    }
    public void setComment2_user(String comment2_user)
    {
        this.comment2_user=comment2_user;
    }

    public String getComment2_user()
    {
        return this.comment2_user;
    }

    public void setComment2(String comment2)
    {
        this.comment2=comment2;
    }

    public String getComment2()
    {
        return this.comment2;
    }

    public void setComment1(String comment1)
    {
        this.comment1=comment1;
    }

    public String getComment1()
    {
        return this.comment1;
    }

    public void setFeed_unique_id(String feed_unique_id)
    {
        this.feed_unique_id=feed_unique_id;
    }

    public String getFeed_unique_id()
    {
        return this.feed_unique_id;
    }

    public void setFeed_id(String feed_id)
    {
        this.feed_id=feed_id;
    }

    public String getFeed_id()
    {
        return this.feed_id;
    }

    public void setLike(String like)
    {
        this.like=like;
    }

    public String getLike()
    {
        return this.like;
    }

    public void setAlphaCommented(String alphaCommented)
    {
        this.alphaCommented=alphaCommented;
    }

    public String getAlphaCommented()
    {
        return this.alphaCommented;
    }

    private ArrayList<Asset> asset;

    public void setTitle(String title)
    {
        this.title=title;
    }

    public String getTitle()
    {
        return this.title;
    }

    public void setDateAndTime(String dateAndTime)
    {
        this.dateAndTime=dateAndTime;
    }

    public String getDateAndTime()
    {
        return this.dateAndTime;
    }

    public void setSocialPlatformName(String socialPlatformName)
    {
        this.socialPlatformName=socialPlatformName;
    }

    public String getSocialPlatformName()
    {
        return this.socialPlatformName;
    }

    public void setStoryData(String storyData)
    {
        this.storyData=storyData;
    }

    public String getStoryData()
    {
        return this.storyData;
    }

    public void setNumberOfLikes(String numberOfLikes)
    {
        this.numberOfLikes=numberOfLikes;
    }

    public String getNumberOfLikes()
    {
        return this.numberOfLikes;
    }

    public void setNumberOfComments(String numberOfComments)
    {
        this.numberOfComments=numberOfComments;
    }

    public String getNumberOfComments()
    {
        return this.numberOfComments;
    }

    public void setAsset(ArrayList<Asset> asset)
    {
        this.asset=asset;
    }

    public ArrayList<Asset>  getAsset()
    {
        return this.asset;
    }


}
