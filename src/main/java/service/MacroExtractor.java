package main.java.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import main.java.model.Macro;
import main.java.model.Macros;

public class MacroExtractor {
	private MacroReplacer macroReplacer;

	public MacroExtractor(Macros macros) {
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

//			checkValidValue(macroValues);
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

			macroValues = resolveWildcardPattern(macroValues, matcher.group(1));
			macro.setValue(macroValues);

			return macro;
		}
		return null;

	}

	private boolean checkGlobOrGlobRecursion(String s) {
		return s.equals("GLOB") || s.equals("GLOB_RECURSE");
	}

	private List<String> resolveWildcardPattern(List<String> macroValues, String type) throws IOException {
//		if ("GLOB".equalsIgnoreCase(type)) {
//			return resolveNonRecursiveFiles(macroValues);
//		}
//		if ("GLOB_RECURSE".equalsIgnoreCase(type)) {
//			List<String> globSources = resolveNonRecursiveFiles(macroValues);
//			return resolveRecursiveFiles(globSources);
//		} 
//		return null;
		List<String> returnMacroValues = new ArrayList<>();

		for (String macrovalue : macroValues) {
			if (macrovalue.contains("${") || !macrovalue.contains("}")) {
				macrovalue = macroReplacer.replaceMacro(macrovalue);
			}
			returnMacroValues.add(macrovalue);
		}
		return returnMacroValues;

	}

//	private List<String> resolveRecursiveFiles(List<String> macroValues) throws IOException {
//		List<String> returnMacroValues = new ArrayList<>();
//
//		for (String macroValue : macroValues) {
//			// 첫 번째로, 경로에 있는 모든 파일을 재귀적으로 탐색해야 하므로 Path 객체로 변환
//			Path path = Paths.get(macroValue);
//
//			// 경로가 디렉터리일 경우, 하위 디렉터리를 재귀적으로 탐색
//			if (Files.isDirectory(path)) {
//				try (Stream<Path> paths = Files.walk(path)) {
//					// 모든 파일 경로를 list로 변환하여 returnMacroValues에 추가
//					returnMacroValues.addAll(
//							paths.filter(Files::isRegularFile).map(Path::toString).collect(Collectors.toList()));
//				} catch (IOException e) {
//					// 오류가 발생하면 예외 처리
//					throw new IOException("Error reading directory: " + macroValue, e);
//				}
//			} else {
//				// 만약 경로가 파일이면 그냥 그 경로를 반환
//				returnMacroValues.add(macroValue);
//			}
//
//		}
//
//		return returnMacroValues;
//	}
//
//	private List<String> resolveNonRecursiveFiles(List<String> macroValues) {
//		List<String> returnMacroValues = new ArrayList<>();
//
//		for (String macrovalue : macroValues) {
//			if (macrovalue.contains("${") || !macrovalue.contains("}")) {
//				macrovalue = macroReplacer.replaceMacro(macrovalue);
//			}
//			returnMacroValues.add(macrovalue);
//		}
//		return returnMacroValues;
//	}

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
