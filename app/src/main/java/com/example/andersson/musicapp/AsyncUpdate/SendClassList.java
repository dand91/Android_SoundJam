package com.example.andersson.musicapp.AsyncUpdate;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;
import java.util.List;


@Root(name="sendclasslist")
public class SendClassList
{
    @ElementList(required=true,inline=true)
    private List<SendClass> sendclass = new ArrayList<SendClass>();
 
    public List<SendClass> getSendClassList() {
        return sendclass;
    }
 
    public void setSendClassList(List<SendClass> sendclass) {
        this.sendclass = sendclass;
    }
}