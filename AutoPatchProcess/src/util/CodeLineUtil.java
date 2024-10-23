package util;

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

}
