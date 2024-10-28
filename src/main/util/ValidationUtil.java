package main.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtil {
	public static String getModuleName(String line) {
		int startIndex = line.indexOf('(') + 1;
		int endIndex = line.indexOf(')');
		return line.substring(startIndex, endIndex).trim();
	}

	public static boolean isInvalidCondition(String condition) {
		return !condition.equals("WIN32") && !condition.equals("UNIX");
	}

	public static boolean isIgnorableModule(String moduleName, String condition) {
		Pattern pattern = Pattern.compile(".*[\\W]+.*");
		Matcher matcher = pattern.matcher(moduleName);

		return moduleName.isEmpty() || moduleName.equals("PRIVATE") || moduleName.equals("PUBLIC")
				|| matcher.matches();
	}

}
