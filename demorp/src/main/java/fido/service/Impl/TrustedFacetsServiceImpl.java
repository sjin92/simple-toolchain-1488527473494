package fido.service.Impl;

import java.net.ConnectException;

import org.springframework.stereotype.Service;

import com.samsung.sds.fido.uaf.server.sdk.http.HttpResponse;
import com.samsung.sds.fido.uaf.server.sdk.operation.TrustedFacet;
import com.samsung.sds.fido.uaf.server.sdk.operation.UafOperations;

import fido.api.ApiRequest;
import fido.service.TrustedFacetsService;

@Service
public class TrustedFacetsServiceImpl implements TrustedFacetsService {
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
	public HttpResponse getTrustedFacets(ApiRequest request, String baseUri,
			String apiKey) {

		// Invoke static factory method to get an instance of TrustedFacet
		TrustedFacet trustedFacetRequest = UafOperations.newTrustedFacet(
				request, baseUri, apiKey);
		if (null == trustedFacetRequest) {
			// Skip caused by invalid parameter
		}
		HttpResponse response = null;

		// Invoke execute() to make REST API call to FIDO server
		try {
			response = trustedFacetRequest.execute().getResponse();
		} catch (ConnectException e) {
			// Connection failure
		}
		return response;
		// TODO Return error response with 400 HTTP status code
		// (No more failover)
	}

}
