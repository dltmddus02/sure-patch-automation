package main.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeLineUtil {
	public static boolean isAddExecutableLine(String line) {
		return (line.contains("add_executable") || line.contains("ADD_EXECUTABLE"));
	}
	
	public static boolean isAddLibraryLine(String line) {
		return (line.contains("add_library") || line.contains("ADD_LIBRARY"));
	}
	
	public static boolean isTargetLinkLibrariesLine(String line) {
		return (line.contains("target_link_libraries") || line.contains("TARGET_LINK_LIBRARIES"));
	}

	public static boolean isSetMacro(String line) {
		Pattern setPattern = Pattern.compile("set\\(.*\\)", Pattern.CASE_INSENSITIVE);
		line = line.trim();
		Matcher matcher = setPattern.matcher(line);
//		System.out.println(line);
		return matcher.matches();
	}

	public static boolean isProjectStatememt(String line) {
		Pattern setPattern = Pattern.compile("project\\(.*\\)", Pattern.CASE_INSENSITIVE);
		line = line.trim();

		Matcher matcher = setPattern.matcher(line);

		return matcher.matches();
	}

}
