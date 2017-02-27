package com.sds.sample.util;

import java.util.*;

import android.util.*;

import com.google.gson.*;
import com.samsung.sds.fido.uaf.client.sdk.*;
import com.sds.sample.util.UafRequestDataItem.*;

public class UafRequestParsing {

    private static final String TAG = UafRequestParsing.class.getSimpleName();

    private UafRequestDataItem uafProtocolMessage;
    private PolicyAaidforTest policyAaid;
    private long lifetimeMillis;
    private String op;
    private int statusCode;
    
    
    public UafRequestParsing(ReturnUafRequest returnUafRequest) {

    	uafProtocolMessage = new UafRequestDataItem();
        policyAaid = new PolicyAaidforTest();
        uafProtocolMessage.setUpv(new UpvforTest());

        if (returnUafRequest.getLifetimeMillis() != null)
        	lifetimeMillis = returnUafRequest.getLifetimeMillis();
        op = returnUafRequest.getOperation();
        statusCode = returnUafRequest.getStatusCode();
	
	    Gson gson = new Gson();
	    String json = gson.toJson(returnUafRequest);
	
	    JsonObject headerObject = null;
	    JsonObject policyObject = null;
	    JsonObject upvObject = null;
	    JsonArray authenticatorsObject = null;
	    JsonObject authenticators = null;
	
	    try {
	
	        JsonObject jsobject = gson.fromJson(json, JsonObject.class);
	        JsonObject jsonReturnUafRequest = jsobject.getAsJsonObject("returnUafRequest");
	        
	        if(jsonReturnUafRequest == null) {
	        	jsonReturnUafRequest = jsobject.getAsJsonObject("a");
	        }
	
	        String jsonuafRequest = jsonReturnUafRequest.get("uafRequest").getAsString();
	        JsonObject jsonObjectuafRequest = gson.fromJson(jsonuafRequest, JsonObject.class);
	
	        String uafProtocolMessageString = jsonObjectuafRequest.get("uafProtocolMessage").getAsString();
	        JsonArray uafProtocolMessageJsonArray = gson.fromJson(uafProtocolMessageString, JsonArray.class);
	
	        //  for (JsonElement element : uafProtocolMessageJsonArray) {
	        //       JsonObject object = element.getAsJsonObject();
	
	        JsonObject object = uafProtocolMessageJsonArray.get(0).getAsJsonObject();
	        if (object.get("challenge") != null)
	        	uafProtocolMessage.setChallenge(object.get("challenge").getAsString());
	        if (object.get("username") != null)
	        	uafProtocolMessage.setUsername(object.get("username").getAsString());
	
	        headerObject = object.get("header").getAsJsonObject();
	        if (object.get("policy") != null)
	        	policyObject = object.get("policy").getAsJsonObject();
	        
	        if (object.get("authenticators") != null)
	        	authenticatorsObject = object.get("authenticators").getAsJsonArray();
	
	        //    }
	        uafProtocolMessage.setOpforUafProtocolMessage(headerObject.get("op").getAsString());
	        uafProtocolMessage.setAppid(headerObject.get("appID").getAsString());
	        uafProtocolMessage.setServerdata(headerObject.get("serverData").getAsString());

	        if (authenticatorsObject != null)
	        	authenticators = authenticatorsObject.get(0).getAsJsonObject();
	        if (authenticators != null)
	        	uafProtocolMessage.setAuthenticatorAaid(authenticators.get("aaid").getAsString());
	        if (authenticators != null)
	        	uafProtocolMessage.setAuthenticatorKeyID(authenticators.get("keyID").getAsString());
	
	        upvObject = headerObject.get("upv").getAsJsonObject();
	        uafProtocolMessage.getUpv().setMajor(upvObject.get("major").getAsString());
	        uafProtocolMessage.getUpv().setMinor(upvObject.get("minor").getAsString());
	
	        //JsonArray acceptedJsonArray = policyObject.getAsJsonArray("accepted");
	        //JsonArray disallowedJsonArray = policyObject.getAsJsonArray("disallowed");
	
	    } catch (JsonSyntaxException e) {
	        Log.v(TAG, "JsonSyntax error : " + e.getMessage());
	
	    } catch (JsonParseException e) {
	        Log.v(TAG, "JsonParse error : " + e.getMessage());
	    }
	   }
    
	public class UpvforTest {
		
	    String major = null;
	    String minor = null;
	
	    public String getMajor() {
	        return major;
	    }
	
	    public void setMajor(String major) {
	        this.major = major;
	    }
	
	    public String getMinor() {
	        return minor;
	    }
	
	    public void setMinor(String minor) {
	        this.minor = minor;
	    }
	}
    
	public class PolicyAaidforTest {
		
	    private ArrayList<String> accepted;
	    private ArrayList<String> disallowed;
	
	    PolicyAaidforTest() {
	
	        accepted = new ArrayList<String>();
	        disallowed = new ArrayList<String>();
	    }
	}
	
	public UafRequestDataItem getUafProtocolMessage() {
	    return uafProtocolMessage;
	}
	
	public PolicyAaidforTest getPolicyAaid() {
	    return policyAaid;
	}
	
	public String getOp() {
	    return op;
	}
	
	public int getStatusCode() {
	    return statusCode;
	}	
    
}

