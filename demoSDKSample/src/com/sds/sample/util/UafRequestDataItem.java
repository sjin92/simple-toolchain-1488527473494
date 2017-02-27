package com.sds.sample.util;

import java.util.*;

import com.sds.sample.util.UafRequestParsing.UpvforTest;

public class UafRequestDataItem {

    private UpvforTest upv = null;
    private String opforUafProtocolMessage = null;
    private String appid = null;
    private String serverdata = null;
    private String challenge = null;
    private String username = null;
    private String aaid = null;
    private String keyID = null;

    public void setUpv(UpvforTest upv) {
        this.upv = upv;
    }

    public void setOpforUafProtocolMessage(String op) {
        this.opforUafProtocolMessage = op;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setServerdata(String serverdata) {
        this.serverdata = serverdata;
    }

    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    public void setUsername(String username) {
        this.username = username;
    }    

    public void setAuthenticatorAaid(String aaid) {
    	this.aaid = aaid;
    }

    public void setAuthenticatorKeyID(String keyID) {
    	this.keyID = keyID;
    }

    public UpvforTest getUpv() {
        return upv;
    }

    public String getOpforUafProtocolMessage() {
        return opforUafProtocolMessage;
    }

    public String getAppid() {
        return appid;
    }

    public String getServerdata() {
        return serverdata;
    }

    public String getChallenge() {
        return challenge;
    }

    public String getUsername() {
        return username;
    }

    public String getAuthenticatorAaid() {
        return aaid;
    }

    public String getAuthenticatorKeyID() {
        return keyID;
    }





	
}
