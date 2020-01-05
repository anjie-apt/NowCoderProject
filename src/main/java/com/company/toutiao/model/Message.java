package com.company.toutiao.model;

import java.util.Date;

public class Message {
    private int id;
    private int fromId;
    private int toId;
    private String content;
    private int conversationId;
    private Date createDate;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getFromId()
    {
        return fromId;
    }

    public void setFromId(int fromId)
    {
        this.fromId = fromId;
    }

    public int getToId()
    {
        return toId;
    }

    public void setToId(int toId)
    {
        this.toId = toId;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

    public int getConversationId()
    {
        return conversationId;
    }

    public void setConversationId(int conversationId)
    {
        this.conversationId = conversationId;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }
}
