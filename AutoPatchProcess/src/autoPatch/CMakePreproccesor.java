package autoPatch;

import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class CMakePreproccesor {
	
	List<CMakeContents> allResults; // 모든 CMakeContents를 저장하는 리스트
	
	public CMakePreproccesor() {
		this.allResults = new ArrayList<>();
	}
	
    public List<CMakeContents> getAllResults() {
        return this.allResults;
    }
    
	class CMakeContents { // 매크로 치환한 txt 저장하는 클래스
		List<String> contents;
		List<CMakeContents> children;
		String path; // 현재 경로
		List<String> childrenPaths; // child들 경로
		
	    public CMakeContents() {
	        this.contents = new ArrayList<>();
	        this.children = new ArrayList<>();
//	        this.path = new String;
	        this.childrenPaths = new ArrayList<>();
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
		
		public void addChildPath(String path) {
			this.childrenPaths.add(path);
		}
		
		public void showChildPath() {
			if(this.childrenPaths.size() == 0) return;
			int index = 1;
			System.out.println("children들의 경로 from " + this.path);
			for (String path : this.childrenPaths) {
				System.out.println(index + " : " + path);
				index++;
			}
		}
	}
	
	class Macro {
		String key;
		String value;
	}
	
	class Macros {
		Stack < List<Macro> > data;
		
		void push() {
			data.add(new ArrayList <> ());
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
		    	for(int i = 0; i < currentList.size(); i++) {
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

	class Preproccesor {
		Macros macros;
		
	    public Preproccesor() {
	        this.macros = new Macros();
	        this.macros.data = new Stack<>();
	    }
	    
		int depth = 0;
		
		List<String> read(String cMakeListPath) {
//			+ "\\CMakeLists.txt"
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
		    	if (line.trim().startsWith("#")) continue;
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
		        if (numBrackets == 0 && currentStatement.length() > 0) {
		            statements.add(currentStatement.toString().trim());
		            currentStatement.setLength(0);
		        }
		    }

		    if (currentStatement.length() > 0) {
		        statements.add(currentStatement.toString().trim());
		    }

		    return statements;
		}


		
		Macro getMacro(String line) {
			Pattern setPattern = Pattern.compile("set\\s*\\(\\s*(\\w+)\\s+(.+?)\\s*\\)"); // set(매크로명 <파일리스트>)

			line = line.trim();
			Matcher matcher = setPattern.matcher(line);
			
			if(matcher.matches()) { 
				Macro macro = new Macro();
	            macro.key = matcher.group(1);
	            String macroValue = matcher.group(2);
	            
	            if(macroValue.startsWith("\"") && macroValue.endsWith("\"")) {
	            	macroValue = macroValue.substring(1, macroValue.length() - 1);
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
			
			if(matcher.matches()) { 
				Macro macro = new Macro();
	            macro.key = "PROJECT_NAME";
	            macro.value = matcher.group(1);
	            return macro;
			}
			return null;

		}
		
		Macros setDefaultMacros(Macros macros) {
		    Macro macro1 = new Macro();
		    macro1.key = "CMAKE_SOURCE_DIR";
		    macro1.value = "C:\\Users\\sure\\CTcode\\engine";
		    macros.add(macro1);

		    Macro macro2 = new Macro();
		    macro2.key = "CMAKE_BINARY_DIR";
		    macro2.value = "?";
		    macros.add(macro2);

		    Macro macro3 = new Macro();
		    macro3.key = "CMAKE_CXX_COMPILER";
		    macro3.value = "?";
		    macros.add(macro3);
			
			return macros;
		}
		
		boolean isSetMacro(String line) { // 매크로를 정의하는 줄이냐? set()이 있는가?
		    Pattern setPattern = Pattern.compile("set\\(.*\\)");
		    line = line.trim();
		    Matcher matcher = setPattern.matcher(line);
		    
		    return matcher.matches();
		}
		
		boolean isProjectStatememt(String line) {
		    Pattern setPattern = Pattern.compile("project\\(.*\\)");
		    line = line.trim();
		    Matcher matcher = setPattern.matcher(line);
		    
		    return matcher.matches();
		}
		
//		${CMAKE_CURRENT_SOURCE_DIR}: 현재 CMakeLists.txt 파일이 있는 디렉터리의 경로
//		${CMAKE_SOURCE_DIR}: 최상위 CMakeLists.txt 파일이 위치한 디렉터리의 절대 경로
//		${CMAKE_BINARY_DIR}: 빌드 디렉터리의 경로
//		${CMAKE_CXX_COMPILER}: C++ 컴파일러 경로
		
		boolean isAddSubDirectory(String line) {
			if (line.contains("add_subdirectory")) {
				return true;
			}
			return false;
		}
		
		String getCmakePath(String cMakeListPath, String statement) {
            String path = statement.substring(statement.indexOf('(') + 1, statement.indexOf(')')).trim();
            if (path == null) return null;
            if (!path.contains("\\")) { // 슬래시 방향 수정
            	path = path.replace('/', '\\');
            }
            if(path != null) {
            	path = cMakeListPath + "\\" + path;
            }
			return path;
		}
		
		List<String> replaceMacro(List<String> statements) {
			List<String> replacedStatements = new ArrayList<>();
			
			for (String line : statements) {
				line = line.trim();
				
	//			${...} 형태 매크로 치환하는 함수
		        if (!line.contains("${") || !line.contains("}")) {
//		            System.out.println("잘못된 매크로 형식: " + line);
		            replacedStatements.add(line); // 잘못된 경우 그대로 추가
		            continue;
		        }
				
				Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
				Matcher matcher = pattern.matcher(line);
				StringBuffer result = new StringBuffer();
				
		        while (matcher.find()) {
		            String macroName = matcher.group(1);
		            String macroValue = macros.find(macroName);
		            
		            if (macroValue == null) {
//		                System.out.println("매크로 못 찾겠다: " + matcher.group(0));
		                macroValue = matcher.group(0);
		            }
		            
		            macroValue = Matcher.quoteReplacement(macroValue);
		            matcher.appendReplacement(result, macroValue);
		        }
		 
		        matcher.appendTail(result); // 한 문장에 매크로 두 개일 수 있음

				
		        replacedStatements.add(result.toString());

			}
			return replacedStatements;
		}
		
		
		CMakeContents preprocess(String cMakeListPath) throws IOException {
			CMakeContents result = new CMakeContents();
			List<String> replacedStatements = new ArrayList<>();

			try {
				List<String> lines = read(cMakeListPath);
				List<String> statements = makeStatements(lines);
				macros.push();
				macros = setDefaultMacros(macros);
				result.setPath(cMakeListPath);
//				System.out.println("현재경로 : " + result.path);
				for ( String statement : statements ) {					

					if ( isSetMacro(statement) ) {
						Macro macro = getMacro(statement);
						macros.add(macro);
					}
					if ( isProjectStatememt(statement) ) { // project 매크로 가져옴
						Macro macro = getProjectMacro(statement);
//						System.out.println("macro : " + macro.key + " : " + macro.value);
						macros.add(macro);
					}
					if ( isAddSubDirectory(statement) ) {
//						System.out.println(statement);
						String path = getCmakePath(cMakeListPath, statement);
						result.addChildPath(path);
						CMakeContents subResult = preprocess(path);
						result.addChild(subResult);
					}
				}
				
//				macros.showMacros();
				replacedStatements = replaceMacro(statements);
//				System.out.println("replacedState : " + replacedStatements);
				macros.pop();

				result.setContent(replacedStatements);
//				result.showChildPath();

				allResults.add(result);
//				for(String s : replacedStatements) {
//					System.out.println(s);
//				}
			} catch(Exception e) {
				System.out.println("처리 중 오류 발생." + e.getMessage());
				e.printStackTrace();
			}
			return result;
		}
	}
}