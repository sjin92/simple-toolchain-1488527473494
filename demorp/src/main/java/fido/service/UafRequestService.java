package fido.service;

import com.samsung.sds.fido.uaf.server.sdk.http.HttpResponse;

import fido.api.ApiRequest;

public interface UafRequestService {
	/**
	 * Processes UafRequest for request part of each operation.
	 *
	 * @param request
	 *            Relying Party Implementation of HttpRequest
	 * @param baseUri
	 *            Base URI of FIDO Server deployment, e.g.
	 *            {@code "https://api.fido.com"}
	 * @throws Exception 
	 **/
	public HttpResponse processUafRequest(ApiRequest request, String baseUri, String apiKey) throws Exception;
}
