package main.autopatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import main.util.CodeLineUtil;

public class CMakePreprocessor {

	public class CMakeContents {
		public List<String> contents;
		public List<CMakeContents> children;
		public String path;

		public CMakeContents() {
			this.contents = new ArrayList<>();
			this.children = new ArrayList<>();
		}

		public void setContent(List<String> content) {
			this.contents = content;
		}

		public void addChild(CMakeContents child) {
			children.add(child);
		}

		public void setPath(String cMakeListPath) {
			this.path = cMakeListPath;
		}

	}

	class Macro {
		String key;
		String value;
	}

	class Macros {
		Stack<List<Macro>> data;

		void push() {
			data.add(new ArrayList<>());
		}

		void pop() {
			if (!data.isEmpty()) {
//		    	System.out.println("pop!");
				data.pop();
			}
		}

		void showMacros() {
			System.out.println("showMacros() ");

			for (int i = data.size() - 1; i >= 0; i--) {
				List<Macro> currentList = data.get(i);

				for (Macro macro : currentList) {
					if (macro != null) {
						System.out.println("key : value = " + macro.key + " : " + macro.value);
					}
				}
			}
		}

		String find(String key) {
			for (int i = data.size() - 1; i >= 0; i--) {
				List<Macro> currentList = data.get(i);

				for (Macro macro : currentList) {
					if (macro != null && macro.key.equals(key)) {
						return macro.value;
					}
				}
			}

			return null;
		}

		void add(Macro macro) {
			if (macro == null) {
				System.out.println("macro 비어있어서 추가할 수 없습니다.");
				return;
			}
			if (!data.isEmpty()) {
				List<Macro> currentList = data.peek();
				for (int i = 0; i < currentList.size(); i++) {
					if (currentList.get(i) != null && currentList.get(i).key.equals(macro.key)) {
						currentList.set(i, macro);
						return;
					}
				}
				currentList.add(macro);
				return;
			}
		}
	}

	public class Preprocessor {
		Macros macros;
		boolean isTopLevel = true;

		public Preprocessor() {
			this.macros = new Macros();
			this.macros.data = new Stack<>();
		}

		List<String> read(String cMakeListPath) {
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

				// 현재 line에서 괄호의 열림과 닫힘을 카운트
				char[] arrChar = line.toCharArray();
				for (char ch : arrChar) {
					if (ch == '(') {
						numBrackets++;
					} else if (ch == ')') {
						numBrackets--;
					}
				}

				// 괄호가 모두 닫힌 경우
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

		Macro getMacro(String line) {
			Pattern setPattern = Pattern.compile("set\\s*\\(\\s*(\\w+)\\s+(.+?)\\s*\\)", Pattern.CASE_INSENSITIVE); // set(매크로명
																													// <파일리스트>)

			line = line.trim();
			Matcher matcher = setPattern.matcher(line);

			if (matcher.matches()) {
				Macro macro = new Macro();
				macro.key = matcher.group(1);
				String macroValue = matcher.group(2);
				if (macroValue.contains("\"")) {
					macroValue = macroValue.replaceAll("\"", "");
				}

				macro.value = macroValue;
				return macro;
			}
//			System.out.println(line);
//			System.out.println("set() 예외");
			return null;

		}

		Macro getProjectMacro(String line) {
			Pattern setPattern = Pattern.compile("project\\s*\\(\\s*(\\w+)\\s*.*\\)"); // project(<프로젝트 이름> ~~)

			line = line.trim();
			Matcher matcher = setPattern.matcher(line);

			if (matcher.matches()) {
				Macro macro = new Macro();
				macro.key = "PROJECT_NAME";
				macro.value = matcher.group(1);
				return macro;
			}
			return null;

		}

//		- [x] ${CMAKE_SOURCE_DIR}
//		- [ ] ${CMAKE_MODULE_PATH}
//		- [ ] ${CMAKE_CURRENT_LIST_DIR}
//		- [x] ${CMAKE_CURRENT_SOURCE_DIR}

		Macro setCMakeSourceDir(String cMakeListPath) {
			Macro macro = new Macro();
			macro.key = "CMAKE_SOURCE_DIR";
			macro.value = cMakeListPath;
			return macro;
		}

		Macro setCMakeModulePath(String cMakeListPath) {
			Macro macro = new Macro();
			macro.key = "CMAKE_MODULE_PATH";
			macro.value = cMakeListPath;
			return macro;
		}

		Macro setCMakeCurrentListDir(String cMakeListPath) {
			Macro macro = new Macro();
			macro.key = "CMAKE_CURRENT_LIST_DIR";
			macro.value = cMakeListPath;
			return macro;
		}

		Macro setCMakeCurrentSourceDir(String cMakeListPath) {
			Macro macro = new Macro();
			macro.key = "CMAKE_CURRENT_SOURCE_DIR";
			macro.value = cMakeListPath;
			return macro;
		}

		boolean isAddSubDirectory(String line) {
			if (line.contains("add_subdirectory")) {
				return true;
			}
			return false;
		}

		String getCmakePath(String cMakeListPath, String statement) {
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

//		
		String replaceMacro(String statement) {
			return processMacro(statement.trim());
		}

		List<String> replaceMacros(List<String> statements) {
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
				String macroValue = macros.find(macroName);

				if (macroValue == null) {
					macroValue = matcher.group(0);
				}
				macroValue = Matcher.quoteReplacement(macroValue);
				matcher.appendReplacement(result, macroValue);
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
				result.setContent(replaceMacros(replacedStatements));

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				macros.pop();
			}

//			macros.showMacros();

			return result;
		}

		private List<String> processStatements(CMakeContents result, String cMakeListPath, List<String> statements)
				throws IOException {
			List<String> resultReplaceMacro = new ArrayList<>();

			for (String statement : statements) {
//				if (statement.contains("CMAKE_CURRENT_LIST_DIR")) {
//					System.out.println(statement);
//
//				}

				resultReplaceMacro.add(replaceMacro(statement));

				if (CodeLineUtil.isSetMacro(statement)) {
					macros.add(getMacro(statement));
//					System.out.println(statement);
				}
				if (CodeLineUtil.isProjectStatememt(statement)) {
					macros.add(getProjectMacro(statement));
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
}