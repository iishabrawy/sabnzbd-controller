package com.gmail.at.faint545.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;

public class HttpResponseParser {
	public static String parseResponse(HttpResponse response) {
		String line = "";
		StringBuilder result = new StringBuilder();		

		try {
			// Wrap a BufferedReader around the InputStream
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			// Read response until the end
			while ((line = rd.readLine()) != null) { 
				result.append(line); 
			}

			// Return full string
			return result.toString();
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}