package main.autopatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import main.domain.CMakeContents;
import main.domain.Macro;
import main.domain.Macros;
import main.util.CodeLineUtil;

public class CMakePreprocessor {
	private Stack<List<Macro>> data;
	private Macros macros;

	private boolean isTopLevel = true;

	public CMakePreprocessor() {
		data = new Stack<>();
		macros = new Macros(data);
	}

	private List<String> read(String cMakeListPath) {
		List<String> lineList = new ArrayList<>();
		File cMakeList = new File(cMakeListPath + "\\CMakeLists.txt");
		try (BufferedReader reader = new BufferedReader(new FileReader(cMakeList))) {
			String line;
			while ((line = reader.readLine()) != null) {
				lineList.add(line);
			}
		} catch (IOException e) {
			System.out.println("파일 읽는 중 오류가 발생했습니다. : " + e.getMessage());
		}

		return lineList;
	}

	List<String> makeStatements(List<String> lines) {
		List<String> statements = new ArrayList<>();
		StringBuilder currentStatement = new StringBuilder();
		int numBrackets = 0;

		for (String line : lines) {
			if (line.trim().startsWith("#"))
				continue;
			currentStatement.append(line).append(" ");

			char[] arrChar = line.toCharArray();
			for (char ch : arrChar) {
				if (ch == '(') {
					numBrackets++;
				} else if (ch == ')') {
					numBrackets--;
				}
			}

			if (numBrackets == 0 && !currentStatement.isEmpty()) {
				statements.add(currentStatement.toString().trim());
				currentStatement.setLength(0);
			}
		}

		if (!currentStatement.isEmpty()) {
			statements.add(currentStatement.toString().trim());
		}

		return statements;
	}

//		- [x] ${CMAKE_SOURCE_DIR}
//		- [ ] ${CMAKE_MODULE_PATH}
//		- [ ] ${CMAKE_CURRENT_LIST_DIR}
//		- [x] ${CMAKE_CURRENT_SOURCE_DIR}

	private boolean isAddSubDirectory(String line) {
		if (line.contains("add_subdirectory")) {
			return true;
		}
		return false;
	}

	private String getCmakePath(String cMakeListPath, String statement) {
		String path = statement.substring(statement.indexOf('(') + 1, statement.indexOf(')')).trim();
		if (path == null)
			return null;
		if (!path.contains("\\")) {
			path = path.replace('/', '\\');
		}
		if (path != null) {
			path = cMakeListPath + "\\" + path;
		}
		return path;
	}

	private String replaceMacro(String statement) {
		return processMacro(statement.trim());
	}

	private List<String> replaceMacros(List<String> statements) {
		return statements.stream().map(statement -> processMacro(statement)).collect(Collectors.toList());
	}

	private String processMacro(String line) {
		if (!line.contains("${") || !line.contains("}")) {
			return line;
		}

		Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
		Matcher matcher = pattern.matcher(line);
		StringBuffer result = new StringBuffer();

		while (matcher.find()) {
			String macroName = matcher.group(1);
			List<String> macroValues = macros.find(macroName);

			if (macroValues == null) {
				macroValues = new ArrayList<>();
				macroValues.add(matcher.group(0));
			}

			String macroValueStr = String.join(" ", macroValues);
			matcher.appendReplacement(result, Matcher.quoteReplacement(macroValueStr));

//			macroValue = Matcher.quoteReplacement(macroValue);
//			matcher.appendReplacement(result, macroValue);
		}
		matcher.appendTail(result);

		return result.toString();
	}

	public CMakeContents preprocess(String cMakeListPath) throws IOException {
		CMakeContents result = new CMakeContents();
		macros.push();

		if (isTopLevel) {
			macros.add(setCMakeSourceDir(cMakeListPath));
			isTopLevel = false;
		}

		macros.add(setCMakeCurrentSourceDir(cMakeListPath));
		result.setPath(cMakeListPath);

		try {
			List<String> lines = read(cMakeListPath);
			List<String> statements = makeStatements(lines);
			List<String> replacedStatements = processStatements(result, cMakeListPath, statements);
			result.setContents(replaceMacros(replacedStatements));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			macros.pop();
		}

//		macros.showMacros();

		return result;
	}

	private Macro findSetMacro(String line) {
		Pattern setPattern = Pattern.compile("set\\s*\\(\\s*(\\w+)\\s+(.+?)\\s*\\)", Pattern.CASE_INSENSITIVE); // set(매크로명
																												// <파일리스트>)

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

//			macroValues.forEach(System.out::println);
			macro.setValue(macroValues);
			return macro;
		}
		return null;

	}

	private Macro findFileMacro(String line) throws IOException {
		Pattern setPattern = Pattern.compile("file\\s*\\(\\s*(\\w+)\\s+(\\w+)\\s+(.+)\\s*\\)",
				Pattern.CASE_INSENSITIVE);
//											  file <공백> (  <옵션> <매크로 이름> <암거나>  <암거나> ... ) 형태
//											  file ( GLOB_RECURSE HDRS_G "include/*.h" )

		line = line.trim();
		Matcher matcher = setPattern.matcher(line);

		if (matcher.matches()) {
			Macro macro = new Macro();
			
			if(!checkGlobOrGlobRecursion(matcher.group(1))) return null;
			
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
				macrovalue = replaceMacro(macrovalue);
			}
			if (macrovalue.contains("*")) {
				int starIndex = macrovalue.indexOf("*");
				String extractedPath = "";
				
				if (starIndex != -1) {
					extractedPath = macrovalue.substring(0, starIndex);
//					System.out.println("Extracted Path: " + extractedPath);
				}

				String pattern = macrovalue.substring(starIndex);

				
				Path path;
				if (flag == true) {
					path = Paths.get(extractedPath);
				}
				else {
					String cmakeCurrentSourceDir = macros.find("CMAKE_CURRENT_SOURCE_DIR").get(0);
					path = Paths.get(cmakeCurrentSourceDir + "\\" + extractedPath);
				}
				
				DirectoryStream<Path> stream = Files.newDirectoryStream(path, pattern);
				for (Path entry : stream) {
					returnMacroValues.add(entry.toString());
				}
			}
			else {
				returnMacroValues.add(macrovalue);
			}
		}

		return returnMacroValues;
	}

	public static String extractExtensionAfterWildcard(String path) {
		int starIndex = path.indexOf("*");
		if (starIndex != -1) {
			return path.substring(starIndex + 1);
		}
		return "";
	}

	private Macro findProjectMacro(String line) {
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

	private Macro setCMakeCurrentSourceDir(String cMakeListPath) {
		Macro macro = new Macro();
		macro.setKey("CMAKE_CURRENT_SOURCE_DIR");
		macro.setValue(Arrays.asList(cMakeListPath));
		return macro;
	}

	private Macro setCMakeSourceDir(String cMakeListPath) {
		Macro macro = new Macro();
		macro.setKey("CMAKE_SOURCE_DIR");
		macro.setValue(Arrays.asList(cMakeListPath));
		return macro;
	}

	private Macro setCMakeModulePath(String cMakeListPath) {
		Macro macro = new Macro();
		macro.setKey("CMAKE_MODULE_PATH");
		macro.setValue(Arrays.asList(cMakeListPath));
		return macro;
	}

	private Macro setCMakeCurrentListDir(String cMakeListPath) {
		Macro macro = new Macro();
		macro.setKey("CMAKE_CURRENT_LIST_DIR");
		macro.setValue(Arrays.asList(cMakeListPath));
		return macro;
	}

	private List<String> processStatements(CMakeContents result, String cMakeListPath, List<String> statements)
			throws IOException {
		List<String> resultReplaceMacro = new ArrayList<>();

		for (String statement : statements) {
			resultReplaceMacro.add(replaceMacro(statement));

			if (CodeLineUtil.isSetStatement(statement)) {
				macros.add(findSetMacro(statement));
//					System.out.println(statement);
			}
			if (CodeLineUtil.isFileStatement(statement)) {
				macros.add(findFileMacro(statement));
//					System.out.println(statement);
			}
			if (CodeLineUtil.isProjectStatememt(statement)) {
				macros.add(findProjectMacro(statement));
//					System.out.println(statement);
			}
			if (isAddSubDirectory(statement)) {
				String path = getCmakePath(cMakeListPath, statement);
				CMakeContents subResult = preprocess(path);
				result.addChild(subResult);
			}
		}

		return resultReplaceMacro;
	}

}