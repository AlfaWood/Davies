package com.mdff.app.model;

import java.io.Serializable;

/**
 * Created by Swati.Gupta on 5/1/2018.
 */

public class ResourceItems implements Serializable {
    private String topic;
    private String id;
    private String description;
    private String sub_topic_id;
    private ResourceItemContent resourceItemContent;

    public String getSub_topic_id ()
    {
        return sub_topic_id;
    }

    public void setSub_topic_id (String sub_topic_id)
    {
        this.sub_topic_id = sub_topic_id;
    }

    public ResourceItems() {

    }
    public ResourceItems(String topic,String id,String sub_topic_id) {
        this.id=id;
        this.topic = topic;
        this.sub_topic_id = sub_topic_id;
    }


    public ResourceItemContent getResourceItemContent()
    {
        return resourceItemContent;
    }

    public void setResourceItemContent(ResourceItemContent resourceItemContent)
    {
        this.resourceItemContent = resourceItemContent;
    }

    public String getTopic ()
    {
        return topic;
    }

    public void setTopic (String topic)
    {
        this.topic = topic;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

}
