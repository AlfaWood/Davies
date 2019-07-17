package com.mdff.app.model;

import java.io.Serializable;

/**
 * Created by Swati.Gupta on 4/13/2018.
 */

public class Asset implements Serializable {

    private String type;
    private String url;
    private String thumbnail_url;

    public void setType(String type)
    {
        this.type=type;
    }

    public String getType()
    {
        return this.type;
    }

    public void setUrl(String url)
    {
        this.url=url;
    }

    public String getUrl()
    {
        return this.url;
    }

    public void setThumbnail_url(String thumbnail_url)
    {
        this.thumbnail_url=thumbnail_url;
    }

    public String getThumbnail_url()
    {
        return this.thumbnail_url;
    }


}
