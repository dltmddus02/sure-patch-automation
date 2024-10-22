package autoPatch;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import autoPatch.CMakePreproccesor.CMakeContents;

public class CMakeParser {
	
	class Condition {
		String value;
	}

	class Conditions {
		Stack<String> data = new Stack<>();

		void push(String value) {
			data.add(value);
		}
		
		void pop() {
			if (!data.isEmpty()) {
		    	System.out.println("pop!");
				data.pop();
			}
		}

		
//		void add(Condition condition) {
//			if (condition == null) {
//				System.out.println("조건식 비어있어서 추가할 수 없습니다.");
//				return;
//			}
//			if (!data.isEmpty()) {
//				List<Condition> currentList = data.peek();
//				for (int i = 0; i < currentList.size(); i++) {
//					if (currentList.get(i) != null && currentList.get(i).key.equals(condition.key)) {
//						currentList.set(i, condition);
//						return;
//					}
//				}
//				currentList.add(macro);
//				return;
//			}
//		}

	}
	
	class Parser {
//		Conditions conditions;

//		public Parser() {
//			this.conditions = new Conditions();
//			this.conditions.data = new Stack<>();
//		}
		public void parseCMakeFile(CMakeContents root, Utils utils, List<Module> modules) {		
			File cmakeFile = new File(root.path + "\\CMakeLists.txt");
			recurseProcess(root, utils, cmakeFile, modules);
	        for(Module m : modules) {
	        	if(m.affectedModules.isEmpty()) continue;
	    		System.out.println("모듈이름: " + m.moduleName);
	
	        	for(String s : m.affectedModules) {
	        		System.out.println(s);
	        	}
	    		System.out.println("");
	        }

	//		printCMakeContents(root, cmakeFile);
	//		printModuleInfo(modules);
		}
		
		private void printCMakeContents(CMakeContents cmakeContents, File cmakeFile) {
			
		    // 현재 CMakeContents에 대해 processCMakeFile 함수 실행
	//	    processCMakeFile(cmakeContents, utils, cmakeFile, modules);
		
		    // 하위 children 리스트가 있을 경우 재귀적으로 순회
		    for (CMakeContents child : cmakeContents.children) {
		    	String currentDirectory = child.path;
		    	File currentCMakeFile = new File(currentDirectory);
		    	System.out.println(child.path);
		    	printCMakeContents(child, currentCMakeFile);
		    }
		}
	
		private void printModuleInfo(List<Module> modules) {
			for (Module module : modules) {
				System.out.println(module.moduleName);
			}
		}
		private void recurseProcess(CMakeContents cmakeContents, Utils utils, File cmakeFile, List<Module> modules) {
			
		    // 현재 CMakeContents에 대해 processCMakeFile 함수 실행
		    processCMakeFile(cmakeContents, utils, cmakeFile, modules);
		
		    // 하위 children 리스트가 있을 경우 재귀적으로 순회
		    for (CMakeContents child : cmakeContents.children) {
		    	String currentDirectory = child.path;
		    	File currentCMakeFile = new File(currentDirectory);
	//	    	System.out.println(child.path);
		    	recurseProcess(child, utils, currentCMakeFile, modules);
		    }
		}
		
		
		private void processCMakeFile(CMakeContents result, Utils utils, File cmakeFile, List<Module> modules) {
	        StringBuilder moduleNameBuilder = new StringBuilder();
	        
	        Conditions conditions = new Conditions();
//	        conditions.push();
	        
	        for (String line : result.contents) {
	        	
	//        	if 스택 저장하기
        		storeConditionInfo(line, conditions);
        		
	//	        1. add_executable
	        	if (isAddExecutableLine(line)) {
	        		processAddExecutable(line, moduleNameBuilder, modules);
	        	}
	//	        2. add_library
	            else if (isAddLibraryLine(line)) {
	//            	System.out.println("AddLibrary : " + line);
	            	processAddLibrary(line, moduleNameBuilder, modules);
	            }
	        	
//	        	여기까지는 모듈이름, 모듈아웃풋 형태만 module에 저장되어 있음
//	        	순회해서 이름으로 찾아야되나
	        	
	        	if (isTargetLinkLibrariesLine(line)) {
	        		processTargetLinkLibraries(line, conditions, modules);
	        	}
	        	
	        }
		}	
	
		private void storeConditionInfo(String line, Conditions conditions) {
			if(line.contains("endif()")) {
				conditions.pop();
				return;
			}
			else if(line.contains("elseif(")) {
				conditions.pop();
			}
			else if(line.contains("if(") || line.contains("IF(")) {
				
			}
			else return;
			
	        int startIndex = line.indexOf('(');
	        int endIndex = line.lastIndexOf(')');
	        
	        if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
	            String extracted = line.substring(startIndex + 1, endIndex);
	            conditions.push(extracted);
	        }
		}
		

		private boolean isAddExecutableLine(String line) {
		    return line.contains("add_executable");
		}
		
		private boolean isAddLibraryLine(String line) {
		    return line.contains("add_library");
		}
		
		private boolean isTargetLinkLibrariesLine(String line) {
			return line.contains("target_link_libraries");
		}

		private String deleteQuote(String moduleName) {
			return moduleName.substring(1, moduleName.length() - 1);
		}
	
		private void processAddExecutable(String line, StringBuilder moduleNameBuilder, List<Module> modules) {
	        int startIndex = line.indexOf('(') + 1;
	        int endIndex = line.indexOf(')');
	        moduleNameBuilder.append(line.substring(startIndex, endIndex).trim());
	
	        String[] moduleNameString = moduleNameBuilder.toString().split(" ");
	        String finalModuleName = moduleNameString[0].trim();
	        
	//	    System.out.println("현재위치 : " + cmakeFile.getPath());
	//	    System.out.println("모듈이름 : " + finalModuleName);
	//	    System.out.println(" ");
	//	    if(finalModuleName.startsWith("\"")) {
	//    	finalModuleName = deleteQuote(finalModuleName);
	//    }
	
	        System.out.println(line);
	        System.out.println("모듈이름 : " + finalModuleName);
	        String outputType = "EXE";
	        System.out.println("add_exe 또 실행");

	        Module module = new Module(new StringBuilder(finalModuleName), outputType);
			modules.add(module);
	        
	        moduleNameBuilder.setLength(0);
		}
		
		private void processAddLibrary(String line, StringBuilder moduleNameBuilder, List<Module> modules) {
	        int startIndex = line.indexOf('(') + 1;
	        int endIndex = line.indexOf(')');
	        moduleNameBuilder.append(line.substring(startIndex, endIndex).trim());
	
	        String[] moduleNameString = moduleNameBuilder.toString().split(" ");
	        String finalModuleName = moduleNameString[0].trim();
	
	//	    System.out.println("현재위치 : " + cmakeFile.getPath());
	//	    System.out.println("모듈이름 : " + finalModuleName);
	//	    System.out.println(" ");
			
		    if(finalModuleName.startsWith("\"")) {
	    	finalModuleName = deleteQuote(finalModuleName);
	    }
	
			String outputType = line.contains("STATIC") ? "STATIC" : line.contains("SHARED") ? "SHARED" : "";
			if(outputType.equals("SHARED")) {
				Module module = new Module(new StringBuilder(finalModuleName), outputType);
				modules.add(module);
			}
		}
		
		
		private void processTargetLinkLibraries(String line, Conditions conditions, List<Module> modules) {
	        for (String item : conditions.data) {
	            if (!item.equals("WIN32") && !item.equals("UNIX")) { // 조건이 WIN32인 경우 아니면 리턴
	                return;
	            }
	            
	            int startIndex = line.indexOf('(') + 1;
	            int endIndex = line.indexOf(')');
	            
	            StringBuilder moduleNameBuilder = new StringBuilder();
	            moduleNameBuilder.append(line.substring(startIndex, endIndex).trim());
	            String[] affactedModuleNameString = moduleNameBuilder.toString().split(" ");
	            
	            String currentModuleName = affactedModuleNameString[0].trim();
	            
//	        	ucommon 모듈 있는지 확인 후 넣기
	        	if (item.equals("UNIX")) {
        			System.out.println("UNIX : ");
    	            for(Module m : modules) {
    	            	if(m.moduleName.toString().equals(currentModuleName)) {
    	    	            for (int i = 1; i < affactedModuleNameString.length; i++) {
    	    	            	String affectedModuleName = affactedModuleNameString[i].trim();
	    	            		if(affectedModuleName.equals("ucommon")) {
//	        	            		System.out.println("일치함");
		    	            		if(m.affectedModules.contains("ucommon")) continue;
	        	            		m.addAffectedModule("ucommon");
	    	            		}
    	    	            }
    	            	}
    	            }
	            	return;
	        	}
	            
	            
	            for(Module m : modules) {
	            	if(m.moduleName.toString().equals(currentModuleName)) {
	    	            for (int i = 1; i < affactedModuleNameString.length; i++) {
	    	            	String affectedModuleName = affactedModuleNameString[i].trim();
	    	            	if (!affectedModuleName.isEmpty()) {  // 공백을 제거한 후 비어있지 않으면 추가
	    	            		if(affectedModuleName.equals("PRIVATE") || affectedModuleName.equals("PUBLIC")) continue;
	    	            		if(m.affectedModules.contains(affectedModuleName)) continue;
	    	            		m.addAffectedModule(affectedModuleName);
//	    						System.out.println(affectedModuleName);
	    	            	}
	    	            }

	            	}
	            }
	        }
		}

	
		// 참조 모듈 추출
		public void extractLinkedModules(CMakeContents result, String line, Conditions conditions, Module module, File cmakeFile, List<Module> modules) {
		}
	  
		public void extractModuleInfo(CMakeContents result, String line, Conditions conditions, StringBuilder moduleName, String outputType, File cmakeFile, List<Module> modules) {
			
			Module module = new Module(moduleName, outputType);
			
//			extractLinkedModules(result, line, conditions, module, cmakeFile, modules);
	//			extractSourceFile(module, cmakeFile);
			
			modules.add(module);
		}
		
		
		
	//	모듈 순회하며 모듈 간의 참조+의존성 저장하는 함수
		public void findDependencies(List<String> input, Set<Module> output, List<Module> modules) {		
			for (String mf : input) {
				for (Module m : modules) {
					// m.affected~ 리스트 보면서 moduleFile와 같은지 보기
					// 같다면 uniqueAffectedModules에 m 추가
					if (m.affectedModules.contains(mf)) {
						output.add(m);
						findDependencies(Collections.singletonList(m.moduleName.toString()), output, modules);
					}
				}
			}
			// 예외상황 : A 는 a,b,c,의존 b는 q,w,e 의존, w는 t,y,u 의존하면
			// y 를 찾으면 w, b, A 나와야 한다
			// findDependencies({y}, mlist);
			// mlist.add(w)
			// findDependencies({w}, mlist)
			// mlist.add(b)
			// findDependencies({b}, mlist)
			// mlist.add(A)
			// findDependencies({A}, mlist)
			// 없으므로 mlist return
			
	//		return mlist; 
		}
	}
}