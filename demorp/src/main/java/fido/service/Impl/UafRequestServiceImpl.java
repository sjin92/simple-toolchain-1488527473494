package fido.service.Impl;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.io.BaseEncoding;
import com.samsung.sds.fido.uaf.message.transport.context.RpContext;
import com.samsung.sds.fido.uaf.server.sdk.http.HttpResponse;
import com.samsung.sds.fido.uaf.server.sdk.operation.OperationType;
import com.samsung.sds.fido.uaf.server.sdk.operation.UafOperations;
import com.samsung.sds.fido.uaf.server.sdk.operation.UafRequest;
import com.samsung.sds.fido.uaf.server.sdk.operation.UafRequestResult;

import fido.api.ApiConstants;
import fido.api.ApiRequest;
import fido.api.Configurations;
import fido.controllers.UafRequestController;
import fido.service.UafRequestService;
import fido.util.DatastoreMsg;
import fido.util.HashMapCache;
import fido.util.Utils;

@Service
public class UafRequestServiceImpl implements UafRequestService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UafRequestServiceImpl.class);
	
	private static final String FONT_ARIAL_BLACK = "Arial Black";
	
	@Inject
	@Named("configurations")
	private Configurations configuration;
	
	private String userIdPrefix = "Sample_";
	
	/**
	 * Processes UafRequest for request part of each operation.
	 *
	 * @param request
	 *            Relying Party Implementation of HttpRequest
	 * @param baseUri
	 *            Base URI of FIDO Server deployment, e.g.
	 *            {@code "https://api.fido.com"}
	 **/
	@Override
	public HttpResponse processUafRequest(ApiRequest request, String baseUri, String apiKey) throws Exception {
		HttpResponse response = null;
		UafRequestResult result = null;
		
		// Invoke static factory method to get an instance of UafRequest
		UafRequest uafRequest = UafOperations.newUafRequest(request, baseUri, apiKey);
		if (null == uafRequest) {
			// Skip caused by invalid request parameter
			//continue;
		}

		// RpContext is a sample RP context class with static fromJson()
		// method that accepts a JSON string and returns an instance of 
		// RpContext.
		
		RpContext context = null;
        String contextJson = uafRequest.getContext();
        if (null != contextJson) {
            try {
                context = RpContext.fromJson(contextJson);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                LOGGER.error("action=postUafRequest, RpContext.fromJson exception");
            }
        } else {
            context = RpContext.newBuilder().build();
        }

        String userName="";
        String userId="";
        String deviceId="";
        if(context.getUserName() != null){
        	// Retrieve the user name passed by RP app 
    		userName = context.getUserName();
    		
    		// Optionally generate a new user id that maps to the given 
    		// userName, whose mapping should be persisted in RP server. 
    		// User id is persisted in FIDO server database, but user 
    		// name is not.
    		userId = generateUserId(userName);
    		if (null != userName) {
    			// TODO Return error response with 400 HTTP status code
    			// (Unrecognized user)
    		}
        }
        if(context.getDeviceId() != null){
        	deviceId = context.getDeviceId();
        }
        
		// Generate a session id            
		String sessionId = UUID.randomUUID().toString();

		// Processing and the cached context if you need """"
		// i.e. "static Map<String, RpContext> contextCache" is declared 
		// for caching
		//contextCache.put(sessionId, context);

		
		//optional - multi policy
		//step 1. read policyType value in rpContext
        //Map<String, String> rpContext = new HashMap<String, String>();
		//rpContext = Utils.convertJsonToMap(contextJson);
		//String policyType ="";
		String policyId ="";
		
        if(context.getPolicyId() != null){
				policyId = context.getPolicyId();
            	uafRequest.setPolicyId(policyId);
            	LOGGER.debug(" policyId = {}", policyId);
        }else{
        	//step 2-2. if policyType(key) is empty, set policyId null.
			//          FIDO server will get default policyId.
        	uafRequest.setPolicyId(null);
        }
		
        OperationType op = uafRequest.getOperationType();
		LOGGER.debug("action = uafRequest.getOperationType(); = {}", op);
		LOGGER.debug("userId = {}, userName = {}", userId, userName);
        
		switch (op) {
		case REGISTRATION:
			if(context.getDeviceId() != null){
				uafRequest.setServerData(sessionId).setUserId(userId).setDeviceId(deviceId)
				.setUserName(userName);
			}else{
				uafRequest.setServerData(sessionId).setUserId(userId).setUserName(userName);
			}
			break;

		case AUTHENTICATION:
			if(context.getUserName() != null){
				if(context.getDeviceId() != null){
					uafRequest.setServerData(sessionId).setUserId(userId).setDeviceId(deviceId);
				}else{
					uafRequest.setServerData(sessionId).setUserId(userId);
				}
			}else{
				uafRequest.setServerData(sessionId);
			}
			
			// Set transaction data for transaction confirmation
			if (isTransactionConfirmation(context)) {
				// TODO : Need to change for your transaction data types
			 	byte[] image = textToimage(context.getTransactionData(), ApiConstants.IMAGE_WIDTH,
	                    ApiConstants.IMAGE_HEIGHT);
	            uafRequest.addTransaction(ApiConstants.CONTENT_TYPE_IMAGE,
	                    BaseEncoding.base64Url().encode(image));
	            uafRequest.addTransaction(ApiConstants.CONTENT_TYPE_TEXT,
	                    BaseEncoding.base64Url().encode(context.getTransactionData().getBytes()));
			}
			break;
			
		case DEREGISTRATION:
			if(context.getDeviceId() != null){
				uafRequest.setServerData(sessionId).setUserId(userId).setDeviceId(deviceId);
			}else{
				uafRequest.setServerData(sessionId).setUserId(userId);
			}
			break;


		default:
			break;
			// TODO Return error response with 400 HTTP status code
			// (Invalid Operation)
		}
		

		// Invoke execute() to make REST API call to FIDO server
		try {
			result = uafRequest.execute();
		} catch (ConnectException e) {
			// Connection failure
			//continue;
		}

		response = result.getResponse();

		if (result.isSuccess()) {
			result.setLifetimeMillis(configuration.getLifetimeMillis());
			// TODO Return HTTP response with 200 HTTP status code and 
			// content type retrieved from response.getContentType().
		}else{
			// Fail Case - Web login token status handling
			switch(op) {
				case AUTHENTICATION:
					
					if(DatastoreMsg.getSessionIdPool().get(context.getUserName() + "||" + "loginToken") != null){
						if(DatastoreMsg.getSessionIdPool().get(context.getUserName() + "||" + "loginToken").equalsIgnoreCase("ready")){
							LOGGER.warn("NO FIDO Registration {}", context.getUserName());
						}
					}
					
					if (null != context.getToken()) {
		                HashMapCache cache = DatastoreMsg.getSessionIdPool();
		                String value = cache.get(context.getToken().getValue());
		                String[] tmp = value.split("\\|");
		                if (context.getUserName().equals(tmp[0]) && "CREATE".equals(tmp[1])) {
		                    String token = context.getToken().getValue();
		                    DatastoreMsg.updateSessionIdPool(token, context.getUserName() + "|FAIL_REQUEST");
		                }
		            }    
					break;
				default:
					break;
			}
		}

		return response;
	}

	/**
	 * Generate userId.
	 *
	 * @param request
	 *        userName
	 **/
	public String generateUserId(String userName) {
		return userIdPrefix + userName;
	}
	
	public boolean isTransactionConfirmation(RpContext context) {
		String transactionData = context.getTransactionData();
		if(transactionData == null || transactionData.isEmpty()) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public static byte[] textToimage(String transactionData, int width, int height){
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(Color.lightGray);
        graphics.fillRect(0, 0, width, height);
        graphics.setColor(Color.BLACK);
        graphics.setFont(new Font(FONT_ARIAL_BLACK, Font.PLAIN, 14));
        drawString(graphics, transactionData, 10, 25);
        graphics.dispose();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytedImage = null;
        try {
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            bytedImage = byteArrayOutputStream.toByteArray();
            LOGGER.debug("encoded image of byte array = ", bytedImage);
        }catch (IOException ioe){
            LOGGER.debug("ImageIo exception :{} ", ioe.getMessage());
            throw new RuntimeException("textToimage() Convert text to image");
        }

        LOGGER.trace("Image Created");
        return bytedImage;
    }
	
	private static void drawString(Graphics graphics, String text, int x, int y) {
        String[] texts = text.split("\n");
        if(texts.length > 0){
            for (String texInNewLine : texts) {
                LOGGER.trace("string draws in line : {}", texInNewLine);
                y += graphics.getFontMetrics().getHeight();
                graphics.drawString(texInNewLine, x, y);
            }
        }else{
            LOGGER.trace("string draws in line : {}", text);
            graphics.drawString(text, x, y);
        }
    }
}