/**
 * 
 */
package com.cts.aws.poc.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Azharkhan
 *
 */
public abstract class GeographyUtil {

	private static final List<String> APAC_CURRENCIES = new ArrayList<>();
	
	private static final List<String> EUR_CURRENCIES = new ArrayList<>();
	
	static {
		
		APAC_CURRENCIES.add("INR");
		APAC_CURRENCIES.add("SGP");
		
		EUR_CURRENCIES.add("GBP");
		EUR_CURRENCIES.add("SEK");
		EUR_CURRENCIES.add("DKK");
		EUR_CURRENCIES.add("NOK");
	}
	
	public static String getRegionFromCurrency(String currency) {
		
		if ("USD".equals(currency))
			return "US";
		else if (APAC_CURRENCIES.contains(currency))
			return "APAC";
		else if (EUR_CURRENCIES.contains(currency))
			return "EUR";
		else
			return "UNKNOWN";
	}
}