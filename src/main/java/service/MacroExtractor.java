package main.java.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import main.java.model.Macro;
import main.java.model.Macros;

public class MacroExtractor {
	private MacroReplacer macroReplacer;
	private Macros macros;

	public MacroExtractor(Macros macros) {
		macroReplacer = new MacroReplacer(macros);
		this.macros = macros;
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

			macroValues = resolveMacro(macroValues, matcher.group(1));
			macro.setValue(macroValues);

			return macro;
		}
		return null;

	}

	private boolean checkGlobOrGlobRecursion(String s) {
		return s.equals("GLOB") || s.equals("GLOB_RECURSE");
	}

	private List<String> resolveMacro(List<String> macroValues, String type) throws IOException {
		List<String> returnMacroValues = new ArrayList<>();

		for (String macrovalue : macroValues) {
			if (macrovalue.contains("${") || !macrovalue.contains("}")) {
				macrovalue = macroReplacer.replaceMacro(macrovalue);
			}

			if ("GLOB_RECURSE".equalsIgnoreCase(type)) {
				macrovalue = "GLOB_RECURSE-" + macrovalue;
			}
			returnMacroValues.add(macrovalue);
		}
		return returnMacroValues;

	}

	public Macro findPOCOMacro(String line, Macros macros) throws IOException {
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

			List<String> existedMacroValues = findMacroValueFromKey(matcher.group(1), macros);
			macroValues.addAll(existedMacroValues);

			macroValues = resolveMacro(macroValues, "no");
			macro.setValue(macroValues);
			return macro;
		}
		return null;

	}

	private List<String> findMacroValueFromKey(String key, Macros macros) {
		List<String> values = new ArrayList<>();
		List<Macro> currentList = macros.getData().get(macros.getData().size() - 1);
		for (Macro macro : currentList) {
			if (macro != null && macro.getKey().equals(key)) {
				values = macro.getValue();
				return values;
			}
		}
		return values;
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
