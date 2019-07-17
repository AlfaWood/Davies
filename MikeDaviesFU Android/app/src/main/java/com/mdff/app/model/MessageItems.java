package com.mdff.app.model;

import java.io.Serializable;

/**
 * Created by Deepika.Mishra on 5/18/2018.
 */

public class MessageItems implements Serializable {


    private String to;
    private String id;
    private String thumbnail_url;
    private String title;
    private String attachment_url;
    private String description;
    private String created_at;
    private String from;
    private String type;
    private String textDescription;

    MessageItems messageItems;
    public int getMessage_read() {
        return message_read;
    }

    //  private String message_read;
    private int message_read;

    public void setMessage_read(int message_read) {
        this.message_read = message_read;
    }


    public  MessageItems(String From, String Description, String Title,int message_read,String id,String thumbnail_url,String attachment_url,String created_at,String type,String textDescription,String to) {

        this.from = From;
        this.description = Description;
        this.title = Title;
        this.message_read = message_read;
        this.id = id;
        this.to = to;
        this.thumbnail_url = thumbnail_url;
       this.attachment_url=attachment_url;
       this.created_at=created_at;
       this.type=type;
        this.textDescription = textDescription;


    }
    public  MessageItems(){

    }


    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getThumbnail_url() {
        return thumbnail_url;
    }

    public void setThumbnail_url(String thumbnail_url) {
        this.thumbnail_url = thumbnail_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAttachment_url() {
        return attachment_url;
    }

    public void setAttachment_url(String attachment_url) {
        this.attachment_url = attachment_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTextDescription() {
        return textDescription;
    }

    public void setTextDescription(String textDescription) {
        this.textDescription = textDescription;
    }
}
