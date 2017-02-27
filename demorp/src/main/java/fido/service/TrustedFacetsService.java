package fido.service;

import com.samsung.sds.fido.uaf.server.sdk.http.HttpResponse;

import fido.api.ApiRequest;

public interface TrustedFacetsService {
	/**
	 * Processes UafResponse for response part of each operation.
	 *
	 * @param request
	 *            Relying Party Implementation of HttpRequest
	 * @param baseUri
	 *            Base URI of FIDO Server deployment, e.g.
	 *            {@code "https://api.fido.com"}
	 **/
	public HttpResponse getTrustedFacets(ApiRequest request, String baseUri, String apiKey);
}
