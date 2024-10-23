package util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeLineUtil {
	public static boolean isAddExecutableLine(String line) {
		return line.contains("add_executable");
	}

	public static boolean isAddLibraryLine(String line) {
		return line.contains("add_library");
	}

	public static boolean isTargetLinkLibrariesLine(String line) {
		return line.contains("target_link_libraries");
	}

	public static boolean isSetMacro(String line) {
		Pattern setPattern = Pattern.compile("set\\(.*\\)", Pattern.CASE_INSENSITIVE);
		line = line.trim();
		Matcher matcher = setPattern.matcher(line);

		return matcher.matches();
	}

	public static boolean isProjectStatememt(String line) {
		Pattern setPattern = Pattern.compile("project\\(.*\\)");
		line = line.trim();

		Matcher matcher = setPattern.matcher(line);

		return matcher.matches();
	}

}
