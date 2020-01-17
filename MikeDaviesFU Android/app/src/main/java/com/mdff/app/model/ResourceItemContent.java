package com.mdff.app.model;

import java.io.Serializable;

/**
 * Created by Swati.Gupta on 4/30/2018.
 */

public class ResourceItemContent implements Serializable{

    private String id;
    private String topic_id;
    private String topic;
    private String thumbnail_url;

    private String title;

    private String video_url;

    private String location;

    private String description;

    private String created_at;

    private String image;
    private String type;

    public String getAudio_url() {
        return audio_url;
    }

    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
    }

    private String audio_url;


    public ResourceItemContent() {

    }

    public ResourceItemContent(String topic, String id, String sub_topic_id,String thumbnail_url,String title,String video_url,String location,String description,String created_at,String image,String type) {
        this.id=id;
        this.topic = topic;
        this.topic_id = sub_topic_id;
        this.thumbnail_url=thumbnail_url;
        this. title=title;
        this. video_url=video_url;
        this. location=location;
        this. description=description;
        this. created_at=created_at;
        this. image=image;
        this. type=type;
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

    public String getThumbnail_url ()
    {
        return thumbnail_url;
    }

    public void setThumbnail_url (String thumbnail_url)
    {
        this.thumbnail_url = thumbnail_url;
    }

    public String getTitle ()
    {
        return title;
    }

    public void setTitle (String title)
    {
        this.title = title;
    }

    public String getVideo_url ()
    {
        return video_url;
    }

    public void setVideo_url (String video_url)
    {
        this.video_url = video_url;
    }

    public String getLocation ()
    {
        return location;
    }

    public void setLocation (String location)
    {
        this.location = location;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getCreated_at ()
    {
        return created_at;
    }

    public void setCreated_at (String created_at)
    {
        this.created_at = created_at;
    }

    public String getImage ()
    {
        return image;
    }

    public void setImage (String image)
    {
        this.image = image;
    }

    public String getTopic_id ()
    {
        return topic_id;
    }

    public void setTopic_id (String topic_id)
    {
        this.topic_id = topic_id;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

}
