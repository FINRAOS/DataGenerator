package org.finra.datagenerator.exec;

import com.google.common.base.Strings;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

public class SystemParameter
{
	/**
	 * Trims the string and expands right number suffixes, if the right suffix is expandable, otherwise returns the
	 * original without expansion or trimming.<br/>
	 * <ul>
	 * <li>k: kilo, adds 3 zeroes
	 * <li>m or M: mega, adds 6 zeroes
	 * <li>g or G: gega, adds 9 zeroes
	 * <li>t or T: tera, adds 12 zeroes
	 * </ul>
	 * 
	 * @param param
	 * @return a String containing the expanded form.
	 */
	static String expandSuffixes(String param)
	{
		param = param.trim();
		if (param.endsWith("m") | param.endsWith("M"))
			return param.substring(0, param.length() - 1) + Strings.repeat("0", 6);
		if (param.endsWith("g") | param.endsWith("G"))
			return param.substring(0, param.length() - 1) + Strings.repeat("0", 9);
		if (param.endsWith("t") | param.endsWith("T"))
			return param.substring(0, param.length() - 1) + Strings.repeat("0", 12);
		return param;
	}

	public static int getInt(String paramName, int defaultValue)
	{
		String param = System.getProperty(paramName);
		if (param == null)
			return defaultValue;
		param = expandSuffixes(param);
		if (Ints.tryParse(param) != null)
			return Integer.parseInt(param);
		return defaultValue;
	}

	public static long getLong(String paramName, long defaultValue)
	{
		String param = System.getProperty(paramName);
		if (param == null)
			return defaultValue;
		param = expandSuffixes(param);
		if (Longs.tryParse(param) != null)
			return Long.parseLong(param);
		return defaultValue;
	}

	public static long getLong(String paramName, String defaultValue)
	{
		String param = System.getProperty(paramName);
		if (param == null)
			param = defaultValue;
		param = expandSuffixes(param);
		if (Longs.tryParse(param) != null)
			return Long.parseLong(param);
		else
		{
			throw new RuntimeException("The parameter " + paramName + " value " + param + " is not parseable digital");
		}
	}

	public static String getString(String paramName, String defaultValue)
	{
		String param = System.getProperty(paramName);
		if (param == null)
			return defaultValue;
		else
			return param;
	}
}