package com.blackducksoftware.integration.hub.rest.oauth;

public class OAuthConfiguration {
	private final String clientId;
	private final String authResponseUrl;
	private final String localBaseUrl;
	private final String authorizeUrl;
	private final String tokenGrantUrl;

	public OAuthConfiguration(final String clientId, final String authResponseUrl, final String localBaseUrl,
			final String authorizeUrl, final String tokenGrantUrl) {
		this.clientId = clientId;
		this.authResponseUrl = authResponseUrl;
		this.localBaseUrl = localBaseUrl;
		this.authorizeUrl = authorizeUrl;
		this.tokenGrantUrl = tokenGrantUrl;
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

	public String getAuthorizeUrl() {
		return authorizeUrl;
	}

	public String getTokenGrantUrl() {
		return tokenGrantUrl;
	}

	@Override
	public String toString() {
		return "OAuthConfiguration [clientId=" + clientId + ", authResponseUrl=" + authResponseUrl + ", localBaseUrl="
				+ localBaseUrl + ", authorizeUrl=" + authorizeUrl + ", tokenGrantUrl=" + tokenGrantUrl + "]";
	}
}
