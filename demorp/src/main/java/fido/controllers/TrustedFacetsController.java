package fido.controllers;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.net.HttpHeaders;
import com.samsung.sds.fido.uaf.server.sdk.http.HttpResponse;

import fido.api.ApiRequest;
import fido.api.Configurations;
import fido.service.TrustedFacetsService;

@Controller
public class TrustedFacetsController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(TrustedFacetsController.class);

	@Inject
    TrustedFacetsService trustedFacetsService;
	
	@Inject
    @Named("configurations")
    private Configurations configuration;
	
	@RequestMapping(value = "/{rpId}/uaf/trustedfacets", method = RequestMethod.GET)
	@ResponseBody
	public String getTrustedFacets(@PathVariable String rpId,
			HttpServletRequest request, HttpServletResponse servletResponse) {

		LOGGER.trace("action=getTrustedFacets, rp=\"{}\"", rpId);

		// Retrieve API key used to access FIDO server API
		String apiKey = configuration.getApiKey(rpId);
		  
		// Invoke static factory method to get an instance of UafRequest
		String baseUri = configuration.getApiUri(rpId);
		
		String contentType = request.getContentType();
        String accept = request.getHeader(HttpHeaders.ACCEPT);
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        
		ApiRequest apiRequest = new ApiRequest(contentType, accept, userAgent, "");
		HttpResponse response = trustedFacetsService.getTrustedFacets(apiRequest, baseUri, apiKey);
		return response.getBody();
	}
}
