package main.java.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import main.java.model.CMakeContents;
import main.java.model.Macro;
import main.java.model.Macros;
import main.java.util.CodeLineUtil;

public class CMakePreprocessor {
	private Stack<List<Macro>> data;
	private Macros macros;
	private MacroExtractor macroExtractor;
	private MacroReplacer macroReplacer;

	private boolean isTopLevel = true;

	public CMakePreprocessor() {
		data = new Stack<>();
		macros = new Macros(data);
		macroExtractor = new MacroExtractor(macros);
		macroReplacer = new MacroReplacer(macros);
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
			result.setContents(macroReplacer.replaceMacros(replacedStatements));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			macros.pop();
		}
		return result;
	}

	private Macro setCMakeSourceDir(String cMakeListPath) {
		Macro macro = new Macro();
		macro.setKey("CMAKE_SOURCE_DIR");
		macro.setValue(Arrays.asList(cMakeListPath));
		return macro;
	}

	private Macro setCMakeCurrentSourceDir(String cMakeListPath) {
		Macro macro = new Macro();
		macro.setKey("CMAKE_CURRENT_SOURCE_DIR");
		macro.setValue(Arrays.asList(cMakeListPath));
		return macro;
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

	private List<String> makeStatements(List<String> lines) {
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

	private List<String> processStatements(CMakeContents result, String cMakeListPath, List<String> statements)
			throws IOException {
		List<String> resultReplaceMacro = new ArrayList<>();

		for (String statement : statements) {
			resultReplaceMacro.add(macroReplacer.replaceMacro(statement));

			if (CodeLineUtil.isSetStatement(statement)) {
				macros.add(macroExtractor.findSetMacro(statement));
			}
			if (CodeLineUtil.isFileStatement(statement)) {
				macros.add(macroExtractor.findFileMacro(statement));
			}
			if (CodeLineUtil.isPOCOStatement(statement)) {
				macros.add(macroExtractor.findPOCOMacro(statement));
			}
			if (CodeLineUtil.isProjectStatememt(statement)) {
				macros.add(macroExtractor.findProjectMacro(statement));
			}
			if (CodeLineUtil.isAddSubDirectory(statement)) {
				String path = getCmakePath(cMakeListPath, statement);
				CMakeContents subResult = preprocess(path);
				result.addChild(subResult);
			}
		}

		return resultReplaceMacro;
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

}