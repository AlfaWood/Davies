package com.mdff.app.model;

import java.io.Serializable;

/**
 * Created by Swati.Gupta on 6/7/2018.
 */

public class SubscriptionPlan implements Serializable {
    private String product;

    private String amount;

    private String id;

    private String billing_scheme;

    private String statement_descriptor;

    private String interval;

    private String created;

    private String name;

    private String livemode;

    private String object;

    private String currency;

    public String getProduct ()
    {
        return product;
    }

    public void setProduct (String product)
    {
        this.product = product;
    }

    public String getAmount ()
    {
        return amount;
    }

    public void setAmount (String amount)
    {
        this.amount = amount;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getBilling_scheme ()
    {
        return billing_scheme;
    }

    public void setBilling_scheme (String billing_scheme)
    {
        this.billing_scheme = billing_scheme;
    }

    public String getStatement_descriptor ()
    {
        return statement_descriptor;
    }

    public void setStatement_descriptor (String statement_descriptor)
    {
        this.statement_descriptor = statement_descriptor;
    }

    public String getInterval ()
    {
        return interval;
    }

    public void setInterval (String interval)
    {
        this.interval = interval;
    }

    public String getCreated ()
    {
        return created;
    }

    public void setCreated (String created)
    {
        this.created = created;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getLivemode ()
    {
        return livemode;
    }

    public void setLivemode (String livemode)
    {
        this.livemode = livemode;
    }

    public String getObject ()
    {
        return object;
    }

    public void setObject (String object)
    {
        this.object = object;
    }

    public String getCurrency ()
    {
        return currency;
    }

    public void setCurrency (String currency)
    {
        this.currency = currency;
    }



}
