package com.blackducksoftware.integration.hub.rest.oauth;

public class OAuthConfiguration {
	private final String clientId;
	private final String localBaseUrl;
	private final String authCodeResponseUrl;
	private final String authorizeUrl;
	private final String tokenRequestUrl;
	private final String hubAuthAckUrl;

	public OAuthConfiguration(final String clientId, final String localBaseUrl, final String authCodeResponseUrl,
			final String authorizeUrl, final String tokenRequestUrl, final String hubAuthAckUrl) {
		this.clientId = clientId;
		this.localBaseUrl = localBaseUrl;
		this.authCodeResponseUrl = authCodeResponseUrl;
		this.authorizeUrl = authorizeUrl;
		this.tokenRequestUrl = tokenRequestUrl;
		this.hubAuthAckUrl = hubAuthAckUrl;
	}

	public String getClientId() {
		return clientId;
	}

	public String getLocalBaseUrl() {
		return localBaseUrl;
	}

	public String getAuthCodeResponseUrl() {
		return authCodeResponseUrl;
	}

	public String getAuthorizeUrl() {
		return authorizeUrl;
	}

	public String getTokenRequestUrl() {
		return tokenRequestUrl;
	}

	public String getHubAuthAckUrl() {
		return hubAuthAckUrl;
	}

	@Override
	public String toString() {
		return "OAuthConfiguration [clientId=" + clientId + ", localBaseUrl=" + localBaseUrl + ", authCodeResponseUrl="
				+ authCodeResponseUrl + ", authorizeUrl=" + authorizeUrl + ", tokenRequestUrl=" + tokenRequestUrl
				+ ", hubAuthAckUrl=" + hubAuthAckUrl + "]";
	}
}
