package com.blackducksoftware.integration.hub.rest.oauth;

public class TokenLinks {
	private final String authorizeUrl;
	private final String tokenUrl;

	public TokenLinks(final String authorizeUrl, final String tokenUrl) {
		this.authorizeUrl = authorizeUrl;
		this.tokenUrl = tokenUrl;
	}

	public String getAuthorizeUrl() {
		return authorizeUrl;
	}

	public String getTokenUrl() {
		return tokenUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((authorizeUrl == null) ? 0 : authorizeUrl.hashCode());
		result = prime * result + ((tokenUrl == null) ? 0 : tokenUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TokenLinks other = (TokenLinks) obj;
		if (authorizeUrl == null) {
			if (other.authorizeUrl != null) {
				return false;
			}
		} else if (!authorizeUrl.equals(other.authorizeUrl)) {
			return false;
		}
		if (tokenUrl == null) {
			if (other.tokenUrl != null) {
				return false;
			}
		} else if (!tokenUrl.equals(other.tokenUrl)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "TokenLinks [authorizeUrl=" + authorizeUrl + ", tokenUrl=" + tokenUrl + "]";
	}
}
