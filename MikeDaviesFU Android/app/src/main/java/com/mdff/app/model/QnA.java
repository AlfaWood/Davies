package com.mdff.app.model;

import java.io.Serializable;

/**
 * Created by Swati.Gupta on 5/4/2018.
 */

public class QnA  implements Serializable {

    private String id;

    private String name;

    private String answer;

    private String question;

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getAnswer ()
    {
        return answer;
    }

    public void setAnswer (String answer)
    {
        this.answer = answer;
    }

    public String getQuestion ()
    {
        return question;
    }

    public void setQuestion (String question)
    {
        this.question = question;
    }


}
