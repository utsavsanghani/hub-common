package com.blackducksoftware.integration.hub.swagger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.swagger.client.ApiResponse;

public class MetaService {
	/**
	 * Returns the first found ID extracted for an ApiResponse's headers
	 * @param response is the response returned by the API call
	 * @return A string containing the first found ID
	 */
	@Deprecated
	public String extractID(ApiResponse<Void> response) {
		String location = response.getHeaders().get("Location").get(0);
		return extractIDs(location).get(0);
	}

	/**
	 * Returns the first found ID extracted for an ApiResponse's headers
	 * @param response is the response returned by the API call
	 * @return A string containing the first found ID
	 */
	public String extractID(ApiResponse<Void> response, int index) {
		String location = response.getHeaders().get("Location").get(index);
		return extractIDs(location).get(index); // TODO: SWAGGER: return null if the list is empty
	}

	/**
	 * Returns the first found ID extracted for an ApiResponse's headers
	 * @param url is string of the url that contains ID's for the hub
	 * @return A string containing the ID requested
	 */
	public String extractID(String url, int index) {
		return extractIDs(url).get(index); // TODO: SWAGGER: return null if the list is empty
	}

	/**
	 * Extracts ID's from an ApiResponse's headers
	 * @param response is the response returned by the API call
	 * @return A list of string where each string is a found ID
	 */
	public List<String> extractIDs(ApiResponse<Void> response) {
		String location = response.getHeaders().get("Location").get(0);
		return extractIDs(location);
	}

	/**
	 * Uses regular expressions to better parse url's for ID's
	 * @param url is string of the url that contains ID's for the hub
	 * @return A list of strings where each string is a found ID
	 */
	public List<String> extractIDs(String url) {
		List<String> found = new ArrayList<String>();
		Pattern pattern = Pattern.compile("(([a-z & 0-9]*)-\\w+){4}");
		Matcher matcher = pattern.matcher(url);
		for (; matcher.find();) {
			found.add(matcher.group());
		}
		return found;
	}
}
