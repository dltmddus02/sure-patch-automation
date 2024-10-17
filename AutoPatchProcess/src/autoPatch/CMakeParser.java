package autoPatch;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import autoPatch.CMakePreproccesor.CMakeContents;

public class CMakeParser {
	
// 최상위 CMakeLists.txt 파일로부터 시작해 subdirectory 탐색
	public void parseCMakeFile(CMakeContents root, Utils utils, String directoryPath, List<Module> modules) {		
//		C:\Users\sure\CTcode\engine		
//		Stack<String> directories = new Stack<>();
//		Set<String> visited = new HashSet<>();
//		
//		directories.push(directoryPath);
//		visited.add(directoryPath);
		
//	    public void parseCMakeFile(Utils utils, String directoryPath, List<Module> modules) {
//	        // 최상위 CMakeContents 객체를 생성해야 할 경우, 예시:
//	        CMakeContents root = loadCMakeFile(directoryPath); // loadCMakeFile은 CMakeContents를 생성하는 가상의 함수
//
//	        // root부터 모든 CMakeContents 객체를 순회하며 processCMakeFile을 실행
//	        traverseAndProcess(root, utils, modules);
//	    }
//
//	    // 재귀적으로 CMakeContents 순회하며 processCMakeFile 실행하는 메서드
//	    private void traverseAndProcess(CMakeContents cmakeContents, Utils utils, List<Module> modules) {
//	        // 현재 CMakeContents에 대해 processCMakeFile 함수 실행
//	        processCMakeFile(cmakeContents, utils, modules);
//
//	        // 하위 children 리스트가 있을 경우 재귀적으로 순회
//	        for (CMakeContents child : cmakeContents.children) {
//	            traverseAndProcess(child, utils, modules);
//	        }
//	    }
		File cmakeFile = new File(root.path + "\\CMakeLists.txt");
		recurseProcess(root, utils, cmakeFile, modules);
//		for (CMakeContents result : allResults) {
//			String currentDirectory = result.path;
//			File cmakeFile = new File(currentDirectory + "\\CMakeLists.txt");
//			
//			if (!cmakeFile.exists()) {
//	            System.out.println("파일 존재x : " + currentDirectory + "\\CMakeLists.txt");
//	            continue;
//	        }
//			
//			processCMakeFile(result, utils, cmakeFile, currentDirectory, directories, visited, modules);
//
//		}
		
	}

	private void recurseProcess(CMakeContents cmakeContents, Utils utils, File cmakeFile, List<Module> modules) {
		
	    // 현재 CMakeContents에 대해 processCMakeFile 함수 실행
	    processCMakeFile(cmakeContents, utils, cmakeFile, modules);
	
	    // 하위 children 리스트가 있을 경우 재귀적으로 순회
	    for (CMakeContents child : cmakeContents.children) {
	    	recurseProcess(child, utils, cmakeFile, modules);
	    }
	}
	
	
	private void processCMakeFile(CMakeContents result, Utils utils, File cmakeFile, List<Module> modules) {
        StringBuilder moduleNameBuilder = new StringBuilder();
        
        for (String line : result.contents) {
//    	    System.out.println(line);                	    
//	        1. add_executable
        	if (isAddExecutableLine(line)) {
        		processAddExecutable(utils, line, moduleNameBuilder, cmakeFile, modules);
//            	System.out.println(line);                	    
        	}
//	            2. add_library
            else if (line.contains("add_library")) {
            	processAddLibrary(utils, line, moduleNameBuilder, cmakeFile, modules);
            }                    
        }
	}	

	
	private boolean isAddExecutableLine(String line) {
	    return line.contains("add_executable");
	}
	
	private void processAddExecutable(Utils utils, String line, StringBuilder moduleNameBuilder, File cmakeFile, List<Module> modules) {
//    	System.out.println("add_Exe: " + line);
    	
        int startIndex = line.indexOf('(') + 1;
        int endIndex = line.indexOf(')');
        moduleNameBuilder.append(line.substring(startIndex, endIndex).trim());

        String[] moduleNameString = moduleNameBuilder.toString().split(" ");
        String finalModuleName = moduleNameString[0].trim();

	    System.out.println(cmakeFile.getPath() + ": 현재 위치");
//	    System.out.println(line + ": 현재 위치");S
	    System.out.println("모듈이름 : " + finalModuleName);
        
        String outputType = "EXE";
        utils.extractModuleInfo(new StringBuilder(finalModuleName), outputType, cmakeFile, modules);
        
        moduleNameBuilder.setLength(0);
	}
	
	private void processAddLibrary(Utils utils, String line, StringBuilder moduleNameBuilder, File cmakeFile, List<Module> modules) {
	    String outputType = line.contains("STATIC") ? "STATIC" : line.contains("SHARED") ? "SHARED" : "";
	    utils.extractModuleInfo(moduleNameBuilder, outputType, cmakeFile, modules);
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
