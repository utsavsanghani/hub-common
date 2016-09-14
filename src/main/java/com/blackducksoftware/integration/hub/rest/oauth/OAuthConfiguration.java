package com.blackducksoftware.integration.hub.rest.oauth;

public class OAuthConfiguration {
	private final String clientId;
	private final String authResponseUrl;
	private final String localBaseUrl;
	private final TokenLinks links;

	public OAuthConfiguration(final String clientId, final String authResponseUrl, final String localBaseUrl,
			final TokenLinks links) {
		this.clientId = clientId;
		this.authResponseUrl = authResponseUrl;
		this.localBaseUrl = localBaseUrl;
		this.links = links;
	}

	public String getClientId() {
		return clientId;
	}

	public String getAuthResponseUrl() {
		return authResponseUrl;
	}

	public String getLocalBaseUrl() {
		return localBaseUrl;
	}

	public TokenLinks getLinks() {
		return links;
	}

	@Override
	public String toString() {
		return "OAuthConfiguration [clientId=" + clientId + ", authResponseUrl=" + authResponseUrl + ", localBaseUrl="
				+ localBaseUrl + ", links=" + links + "]";
	}
}
