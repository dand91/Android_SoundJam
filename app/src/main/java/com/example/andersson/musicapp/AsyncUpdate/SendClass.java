package com.example.andersson.musicapp.AsyncUpdate;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "sendclass")
public class SendClass {

    @Element
    private String groupName;
    @Element
    private String instrumentName;
    @Element
    private String data;
    @Element
    private String volume;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }


}