package main.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import main.domain.Macro;
import main.domain.Macros;

public class MacroExtractor {
	private Macros macros;
	private MacroReplacer macroReplacer;

	public MacroExtractor(Macros macros) {
		this.macros = macros;
		macroReplacer = new MacroReplacer(macros);
	}

	public Macro findSetMacro(String line) {
		Pattern setPattern = Pattern.compile("set\\s*\\(\\s*(\\w+)\\s+(.+?)\\s*\\)", Pattern.CASE_INSENSITIVE);

		line = line.trim();
		Matcher matcher = setPattern.matcher(line);

		if (matcher.matches()) {
			Macro macro = new Macro();
			macro.setKey(matcher.group(1));

			String macroValue = matcher.group(2);
			if (macroValue.contains("\"")) {
				macroValue = macroValue.replaceAll("\"", "");
			}
			List<String> macroValues = Arrays.stream(macroValue.split("\\s+")).map(String::trim)
					.filter(s -> !s.isEmpty()).collect(Collectors.toList());

			macro.setValue(macroValues);
			return macro;
		}
		return null;

	}

	public Macro findFileMacro(String line) throws IOException {
		Pattern setPattern = Pattern.compile("file\\s*\\(\\s*(\\w+)\\s+(\\w+)\\s+(.+)\\s*\\)",
				Pattern.CASE_INSENSITIVE);

		line = line.trim();
		Matcher matcher = setPattern.matcher(line);

		if (matcher.matches()) {
			Macro macro = new Macro();

			if (!checkGlobOrGlobRecursion(matcher.group(1)))
				return null;

			macro.setKey(matcher.group(2));
			String macroValue = matcher.group(3);
			if (macroValue.contains("\"")) {
				macroValue = macroValue.replaceAll("\"", "");
			}

			List<String> macroValues = Arrays.asList(macroValue.split("\\s+"));

			macro.setValue(processWildcardPattern(macroValues));

			return macro;
		}
		return null;

	}

	private boolean checkGlobOrGlobRecursion(String s) {
		return s.equals("GLOB") || s.equals("GLOB_RECURSE");
	}

	private List<String> processWildcardPattern(List<String> macroValues) throws IOException {
		List<String> returnMacroValues = new ArrayList<>();

		for (String macrovalue : macroValues) {
			boolean flag = true;
			if (!macrovalue.contains("${") || !macrovalue.contains("}")) {
				flag = false;
			} else {
				macrovalue = macroReplacer.replaceMacro(macrovalue);
			}
			if (macrovalue.contains("*")) {
				int starIndex = macrovalue.indexOf("*");
				String extractedPath = "";

				if (starIndex != -1) {
					extractedPath = macrovalue.substring(0, starIndex);
				}

				String pattern = macrovalue.substring(starIndex);

				Path path;
				if (flag == true) {
					path = Paths.get(extractedPath);
				} else {
					String cmakeCurrentSourceDir = macros.find("CMAKE_CURRENT_SOURCE_DIR").get(0);
					path = Paths.get(cmakeCurrentSourceDir + "\\" + extractedPath);
				}

				DirectoryStream<Path> stream = Files.newDirectoryStream(path, pattern);
				for (Path entry : stream) {
					returnMacroValues.add(entry.toString());
				}
			} else {
				returnMacroValues.add(macrovalue);
			}
		}

		return returnMacroValues;
	}

	public Macro findPOCOMacro(String line) {
		Pattern setPattern = Pattern.compile("POCO\\w*\\s*\\(\\s*(\\w+)\\s+(.+?)\\s*\\)", Pattern.CASE_INSENSITIVE); // set(매크로명

		line = line.trim();
		Matcher matcher = setPattern.matcher(line);

		if (matcher.matches()) {
			Macro macro = new Macro();
			macro.setKey(matcher.group(1));

			String macroValue = matcher.group(2);
			if (macroValue.contains("\"")) {
				macroValue = macroValue.replaceAll("\"", "");
			}
			List<String> macroValues = Arrays.stream(macroValue.split("\\s+")).map(String::trim)
					.filter(s -> !s.isEmpty()).collect(Collectors.toList());

			macro.setValue(macroValues);
			return macro;
		}
		return null;

	}

	public Macro findProjectMacro(String line) {
		Pattern setPattern = Pattern.compile("project\\s*\\(\\s*(\\w+)\\s*.*\\)"); // project(<프로젝트 이름> ~~)

		line = line.trim();
		Matcher matcher = setPattern.matcher(line);

		if (matcher.matches()) {
			Macro macro = new Macro();
			macro.setKey("PROJECT_NAME");
			macro.setValue(Arrays.asList(matcher.group(1)));
			return macro;
		}
		return null;

	}
}
