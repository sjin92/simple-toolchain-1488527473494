package fido.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

import fido.util.DatastoreMsg;

 
/**
 * Handles requests for the User service.
 * 단말/Admin 사용을 위해 제공한 컨트롤러
 */
@Controller
public class UserController {
		
	@Value("#{context['android.gcm.api.key']}")
    private String gcmKey;
	
	@Value("#{context['login.push.policy.id']}")
	private String loginPushPolicyId;
	
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    
    private static final String RESULT_CODE = "resultCode";
    private static final String LOGIN_ID = "loginId";
    private static final String DELIIMITER = "||";
    private static final String REG_ID = "regId";
    private static final String LOGIN_TOKEN = "loginToken";
    
    private static final String RESULT_CODE_SUCCESS = "0";
    private static final String SUCCESS = "success";
    private static final String READY = "ready";
    private static final String FAIL = "fail";
    private static final String DONE = "done";
    private static final String FAIL_NOT_REG = "fail_notReg";
    private static final String UNKNOWN = "unknown";

    private static final String TOKEN = "token";
    private static final String POLICY_ID = "policyId";
    
    @RequestMapping(value = "/user/reg/id", method = RequestMethod.POST)
    public @ResponseBody Map<String, String> updateUserRegId(
    		@RequestBody Map<String, String> requestMap) throws Exception{
    	
		if(requestMap == null){
			logger.error("@@ RequestBody is NULL");
			throw new Exception();
		}else if(requestMap.get(LOGIN_ID) == null || requestMap.get(LOGIN_ID).isEmpty()){
			logger.warn("@@ {} is NULL", LOGIN_ID);
			throw new Exception();
		}else if(requestMap.get(REG_ID) == null || requestMap.get(REG_ID).isEmpty()){
			logger.warn("@@ {} is NULL", REG_ID);
			throw new Exception();
		}

    	logger.debug("requestBody: {}", requestMap.toString());
		
    	// Save Reg ID in Memory(HashMap) 
		DatastoreMsg.registerSessionIdPool(requestMap.get(LOGIN_ID) + DELIIMITER + REG_ID, requestMap.get(REG_ID), 31536000); // 31536000 Seconds = 365 Days
		
    	requestMap.put(RESULT_CODE, RESULT_CODE_SUCCESS);
    	requestMap.put(LOGIN_ID, requestMap.get(LOGIN_ID));
    	requestMap.put(REG_ID, requestMap.get(REG_ID));
    	
    	return requestMap;
    }
    
    
    @RequestMapping(value = "/user/reg/id", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getUserRegId(
    		@RequestParam(LOGIN_ID) String loginId) throws Exception{
    	
    	Map<String, Object> responseMap = new HashMap<String, Object>();
    	
    	String regId = ""; 
    	if(DatastoreMsg.getSessionIdPool().get(loginId + DELIIMITER + REG_ID) != null){
    		regId = DatastoreMsg.getSessionIdPool().get(loginId + DELIIMITER + REG_ID);
    	}else{
    		logger.error("@@ REG ID No Data in DB");
    		throw new Exception();
    		//throw new ServiceException(ErrorConstants.ACNT_REG_ID_NULL.getErrorCode());
    	}
    	
		responseMap.put(RESULT_CODE, RESULT_CODE_SUCCESS);    	
		responseMap.put(LOGIN_ID, loginId);
		responseMap.put(REG_ID, DatastoreMsg.getSessionIdPool().get(loginId + DELIIMITER + REG_ID));
    	
    	return responseMap;
    }
    
    
    @RequestMapping(value = "/user/reg/id/list", method = RequestMethod.GET)
    public @ResponseBody Map<String, Object> getUserRegIdList(){
    	
    	Map<String, Object> responseMap = new HashMap<String, Object>();
    	
		responseMap.put(RESULT_CODE, RESULT_CODE_SUCCESS);
		ArrayList<Map<String, String>> arrayList = new ArrayList<Map<String, String>>();
		for(String key : DatastoreMsg.getSessionIdPoolList().keySet()){
			if(key.contains(DELIIMITER)){
				Map<String, String> map = new HashMap<String, String>();
				String[] splitKey = key.split("\\|\\|");
				map.put(LOGIN_ID, splitKey[0]);
				map.put(REG_ID, DatastoreMsg.getSessionIdPool().get(key));
				arrayList.add(map);
			}
		}
		responseMap.put("regIdList", arrayList);
    	
    	return responseMap;
    }  
    
    
	@RequestMapping(value="/user/login/push", method = RequestMethod.POST)
	public @ResponseBody Map<String, String> requestPush( 
			@RequestBody Map<String, String> requestMap) throws Exception {

		String loginToken = "token";
		if(requestMap == null){
			logger.error("@@ RequestBody is NULL");
			throw new Exception();
		}else if(requestMap.get(LOGIN_ID) == null || requestMap.get(LOGIN_ID).isEmpty()){
			logger.warn("@@ {} is NULL", LOGIN_ID);
			throw new Exception();
		}
		
		if(requestMap.get(LOGIN_TOKEN) != null && !requestMap.get(LOGIN_TOKEN).isEmpty()){
			loginToken = requestMap.get(LOGIN_TOKEN);
		}
		
		String loginId = requestMap.get(LOGIN_ID);
		logger.info("Login ID : {}", loginId);
		
		
		
		if(DatastoreMsg.getSessionIdPool().get(loginId + DELIIMITER + REG_ID) == null 
				|| DatastoreMsg.getSessionIdPool().get(loginId + DELIIMITER + REG_ID).isEmpty()){
			logger.error("@@ GCM Reg ID is NULL");
			throw new Exception();
			//throw new ServiceException(ErrorConstants.INVALID_PARAMETER.getErrorCode(), new Object[]{"Reg ID"});
		}
		
		String regId = DatastoreMsg.getSessionIdPool().get(loginId + DELIIMITER + REG_ID);
		logger.info("GCM Reg ID : {}", regId);
		
		Sender sender = new Sender(gcmKey);

		Message message = new Message.Builder()
					.addData(LOGIN_ID, requestMap.get(LOGIN_ID))
					.addData(TOKEN, loginToken)
					.addData(POLICY_ID, loginPushPolicyId)
					.build();
		
        Result result;
        
		try {
			result = sender.send(message, regId, 5);
		} catch (IOException e) {
			logger.error("GCM Push Transaction Error");
			throw new Exception();
		}
		
        logger.info("GCM Push Transaction Result: {}", result);
		
        DatastoreMsg.registerSessionIdPool(loginId + DELIIMITER + LOGIN_TOKEN, READY, 180); // 180 Seconds
        
        Map<String, String> responseMap = new HashMap<String, String>();
    	responseMap.put(RESULT_CODE, RESULT_CODE_SUCCESS);
    	responseMap.put(LOGIN_ID, requestMap.get(LOGIN_ID));
    	responseMap.put(LOGIN_TOKEN, READY);
    	responseMap.put(POLICY_ID, loginPushPolicyId);
    	responseMap.put(REG_ID, regId);
        
        return responseMap;
	}
	

	@RequestMapping(value="/user/login/token", method = RequestMethod.GET)
	public @ResponseBody Map<String, String> requestPush( 
			@RequestParam(LOGIN_ID) String loginId){

		Map<String, String> responseMap = new HashMap<String, String>();
		
		if(DatastoreMsg.getSessionIdPool().get(loginId + DELIIMITER + LOGIN_TOKEN) == null){
			logger.info("Login Token is NULL");
			responseMap.put(RESULT_CODE, UNKNOWN);
			return responseMap;
		}
		
		String loginToken = DatastoreMsg.getSessionIdPool().get(loginId + DELIIMITER + LOGIN_TOKEN);
		
		logger.info("Login Token: {}", loginToken);
		if(loginToken.equalsIgnoreCase(SUCCESS)){
			responseMap.put(RESULT_CODE, RESULT_CODE_SUCCESS);
			DatastoreMsg.updateSessionIdPool(LOGIN_ID + DELIIMITER + LOGIN_TOKEN, DONE);
		}else if(loginToken.equalsIgnoreCase(READY)){
			responseMap.put(RESULT_CODE, READY);
		}else if(loginToken.equalsIgnoreCase(DONE)){
			responseMap.put(RESULT_CODE, DONE);
		}else if(loginToken.equalsIgnoreCase(FAIL_NOT_REG)){
			responseMap.put(RESULT_CODE, FAIL_NOT_REG);
		}else{
			responseMap.put(RESULT_CODE, FAIL);
		}
    	
    	return responseMap;
	}
    
	
}