package com.blackducksoftware.integration.hub.rest.oauth;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.restlet.engine.util.Base64;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class AuthenticationState {

	private static final String REFERING_PATH_KEY = "refering_path";

	private static final String RETURN_URL_KEY = "return_url";

	private String referingPath;

	private String returnUrl;

	public AuthenticationState(final String urlState) {
		final Map<String, String> decoded = decodeMap(urlState);

		referingPath = null;
		returnUrl = null;

		if (decoded != null) {
			if (decoded.containsKey(REFERING_PATH_KEY)) {
				referingPath = decoded.get(REFERING_PATH_KEY);
			}

			if (decoded.containsKey(RETURN_URL_KEY)) {
				returnUrl = decoded.get(RETURN_URL_KEY);
			}
		}
	}

	public AuthenticationState() {
		referingPath = null;
		returnUrl = null;
	}

	public String getReferingPath() {
		return referingPath;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReferingPath(final String referingPath) {
		this.referingPath = referingPath;
	}

	public void setReturnUrl(final String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String encode() {
		String result = null;

		if (StringUtils.isNotBlank(referingPath) || StringUtils.isNotBlank(returnUrl)) {
			final Map<String, String> stateMap = Maps.newHashMap();

			if (StringUtils.isNotBlank(referingPath)) {
				stateMap.put(REFERING_PATH_KEY, referingPath);
			}

			if (StringUtils.isNotBlank(returnUrl)) {
				stateMap.put(RETURN_URL_KEY, returnUrl);
			}

			result = encodeMap(stateMap);
		}

		return result;
	}

	private String encodeMap(final Map<String, String> a) {
		String result = null;

		if (a != null) {
			final Collection<String> allValues = Lists.newArrayList();

			for (final Entry<String, String> entry : a.entrySet()) {
				allValues.add(entry.getKey() + "=" + entry.getValue());
			}

			result = StringUtils.join(allValues, ",");
			result = new String(Base64.encode(result.getBytes(StandardCharsets.UTF_8), false));
		}

		return result;
	}

	private Map<String, String> decodeMap(final String b) {
		Map<String, String> result = null;

		if (b != null) {
			result = Maps.newHashMap();

			final String encodedMap = new String(Base64.decode(b));

			final Collection<String> allValues = Lists.newArrayList(encodedMap.split(","));

			for (final String value : allValues) {
				final String[] pair = value.split("=");

				if (pair.length == 2) {
					result.put(pair[0], pair[1]);
				}
			}
		}

		return result;
	}

	@Override
	public int hashCode() {
		return Objects.hash(referingPath, returnUrl);
	}

	@Override
	public boolean equals(final Object obj) {
		boolean result = false;

		if (obj instanceof AuthenticationState) {
			final AuthenticationState compare = (AuthenticationState) obj;

			result = Objects.equals(compare.getReferingPath(), getReferingPath())
					&& Objects.equals(compare.getReturnUrl(), getReturnUrl());
		}

		return result;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass()).omitNullValues().add("referingPath", getReferingPath())
				.add("returnUrl", getReturnUrl()).toString();
	}
}
