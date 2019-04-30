package com.datastax.powertools.api;

/*
 *
 * @author Sebastián Estévez on 4/29/19.
 *
 */


import javax.json.bind.annotation.JsonbProperty;

public class Event {

    @JsonbProperty("EventID")
    private long eventId;
    @JsonbProperty("TEMPHEIGHT")
    private long tempHeight;
    @JsonbProperty("ALT")
    private long alt;
    @JsonbProperty("SPD")
    private long spd;
    @JsonbProperty("BAT")
    private long bat;
    @JsonbProperty("WIFI")
    private long wifi;
    @JsonbProperty("CAM")
    private long cam;
    @JsonbProperty("MODE")
    private long mode;

    public Event() {

    }


    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public long getTempHeight() {
        return tempHeight;
    }

    public void setTempHeight(long tempHeight) {
        this.tempHeight = tempHeight;
    }

    public long getAlt() {
        return alt;
    }

    public void setAlt(long alt) {
        this.alt = alt;
    }

    public long getSpd() {
        return spd;
    }

    public void setSpd(long spd) {
        this.spd = spd;
    }

    public long getBat() {
        return bat;
    }

    public void setBat(long bat) {
        this.bat = bat;
    }

    public long getWifi() {
        return wifi;
    }

    public void setWifi(long wifi) {
        this.wifi = wifi;
    }

    public long getCam() {
        return cam;
    }

    public void setCam(long cam) {
        this.cam = cam;
    }

    public long getMode() {
        return mode;
    }

    public void setMode(long mode) {
        this.mode = mode;
    }
}
