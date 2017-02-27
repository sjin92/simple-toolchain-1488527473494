package fido.service.Impl;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.samsung.sds.fido.uaf.message.transport.context.RpContext;
import com.samsung.sds.fido.uaf.server.sdk.http.HttpResponse;
import com.samsung.sds.fido.uaf.server.sdk.operation.OperationType;
import com.samsung.sds.fido.uaf.server.sdk.operation.TokenType;
import com.samsung.sds.fido.uaf.server.sdk.operation.UafOperations;
import com.samsung.sds.fido.uaf.server.sdk.operation.UafResponse;
import com.samsung.sds.fido.uaf.server.sdk.operation.UafResponseResult;

import fido.api.ApiRequest;
import fido.api.Configurations;
import fido.controllers.UafResponseController;
import fido.service.UafResponseService;
import fido.util.DatastoreMsg;
import fido.util.Utils;

@Service
public class UafResponseServiceImpl implements UafResponseService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UafResponseServiceImpl.class);
	
	@Inject
	@Named("configurations")
	private Configurations configuration;
	
	/**
	 * Processes UafResponse for response part of each operation.
	 *
	 * @param request
	 *            Relying Party Implementation of HttpRequest
	 * @param baseUri
	 *            Base URI of FIDO Server deployment, e.g.
	 *            {@code "https://api.fido.com"}
	 **/
	@Override
	public HttpResponse processUafResponse(ApiRequest request, String baseUri,
			String apiKey) {

		HttpResponse response = null;
		UafResponseResult result = null;
		// Invoke static factory method to get an instance of UafResponse
		UafResponse uafResponse = UafOperations.newUafResponse(request,
				baseUri, apiKey);
		if (null == uafResponse) {
			// Skip caused by invalid request parameter
			//continue;
		}

		// RpContext is a sample RP context class with static fromJson()
		// method that accepts a JSON string and returns an instance of
		// RpContext.
		RpContext context = null;
        String contextJson = uafResponse.getContext();
        if (null != contextJson) {
            try {
                context = RpContext.fromJson(contextJson);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                LOGGER.error("action=postUafResponse, RpContext.fromJson exception");

            }
        } else {
            context = RpContext.newBuilder().build();
        }
		
		//RpContext context = RpContext.fromJson(uafResponse.getContext());

		// Generate a session id
		String sessionId = UUID.randomUUID().toString();

		// Processing and the cached context if you need """"
		// i.e. "static Map<String, RpContext> contextCache" is declared
		// for caching
		//contextCache.put(sessionId, context);

		
		//optional - multi policy
		//step 1. read policyType value in rpContext
        String policyId ="";
        
        if(context.getPolicyId() != null){
                policyId = context.getPolicyId();
                uafResponse.setPolicyId(policyId);
                LOGGER.debug(" policyId = {}", policyId);
        }else{
            //step 2-2. if policyType(key) is empty, set policyId null.
            //          FIDO server will get default policyId.
            uafResponse.setPolicyId(null);
        }
		
		OperationType op = uafResponse.getOperationType();
		
		switch (op) {
		case REGISTRATION:
			// Invoke execute() to make REST API call to FIDO server
			try {
				result = uafResponse.execute();
			} catch (ConnectException e) {
				// Connection failure
				//continue;
			}

			response = result.getResponse();

			if (result.isSuccess()) {
				// Retrive the session id generated during UAF Response
				// processing and the cached context
				sessionId = result.getServerData();
				//RpContext cachedContext = getContextFromCache(sessionId);

				// Persist tuples of userId, deviceId, and registrationIds
				String userId = result.getUserId();
				String deviceId = context.getDeviceId();  //cachedContext.getDeviceId();
				List<String> regIds = result.getRegistrationIds();

				// TODO Return HTTP response with 200 HTTP status code and
				// content type retrieved from response.getContentType().
				
				// New Push API Area - Web login token status handling ////////
				if(DatastoreMsg.getSessionIdPool().get(context.getUserName() + "||" + "loginToken") != null){
					if(DatastoreMsg.getSessionIdPool().get(context.getUserName() + "||" + "loginToken").equalsIgnoreCase("ready")){
						DatastoreMsg.updateSessionIdPool(context.getUserName() + "||" + "loginToken", "success");
					}
				}
		    	/////////////////////////////
			}

			return response;

		case AUTHENTICATION:
			// Invoke execute() to make REST API call to FIDO server
			try {
				result = uafResponse.execute();
			} catch (ConnectException e) {
				// Connection failure
				//continue;
			}

			response = result.getResponse();

			if (result.isSuccess()) {
				// Generate a session token
				String token = "<Token Value>";
				result.addToken(TokenType.OAUTH2, token);

				// TODO Return HTTP response with 200 HTTP status code and
				// content type retrieved from response.getContentType().
				
				// New Push API Area - Web login token status handling ////////
				if(DatastoreMsg.getSessionIdPool().get(context.getUserName() + "||" + "loginToken") != null){
					if(DatastoreMsg.getSessionIdPool().get(context.getUserName() + "||" + "loginToken").equalsIgnoreCase("ready")){
						DatastoreMsg.updateSessionIdPool(context.getUserName() + "||" + "loginToken", "success");
					}
				}
		    	/////////////////////////////
			}

			return response;

		default:
			// TODO Return error response with 400 HTTP status code
			// (Invalid Operation)
		}
		return response;

		// TODO Return error response with 400 HTTP status code
		// (No more failover)
	}

}
