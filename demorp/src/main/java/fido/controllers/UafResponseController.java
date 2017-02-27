package fido.controllers;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.net.HttpHeaders;
import com.samsung.sds.fido.uaf.server.sdk.http.HttpResponse;

import fido.api.ApiRequest;
import fido.api.Configurations;
import fido.service.UafResponseService;

@Controller
public class UafResponseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UafResponseController.class);

    @Inject
    UafResponseService uafResponseService;
    
    @Inject
    @Named("configurations")
    private Configurations configuration;
    
    @RequestMapping(value = "/{rpId}/uaf/response", method = RequestMethod.POST)
    @ResponseBody
    public String postUafResponse(@PathVariable String rpId, @RequestBody String requestBody, 
    		HttpServletRequest request, HttpServletResponse servletResponse) {

		// Retrieve API key used to access FIDO server API
		String apiKey = configuration.getApiKey(rpId);
		  
		// Invoke static factory method to get an instance of UafRequest
		String baseUri = configuration.getApiUri(rpId);
		
        String contentType = request.getContentType();
        String accept = request.getHeader(HttpHeaders.ACCEPT);
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        
		ApiRequest apiRequest = new ApiRequest(contentType, accept, userAgent, requestBody);
		HttpResponse response = uafResponseService.processUafResponse(apiRequest, baseUri, apiKey);
		
		return response.getBody();
    }
}