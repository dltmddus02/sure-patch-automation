package autoPatch;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import autoPatch.CMakePreproccesor.CMakeContents;

public class CMakeParser {
	
// 최상위 CMakeLists.txt 파일로부터 시작해 subdirectory 탐색
	public void parseCMakeFile(CMakeContents root, Utils utils, List<Module> modules) {		
		File cmakeFile = new File(root.path + "\\CMakeLists.txt");
		recurseProcess(root, utils, cmakeFile, modules);		
	}

	private void recurseProcess(CMakeContents cmakeContents, Utils utils, File cmakeFile, List<Module> modules) {
		
	    // 현재 CMakeContents에 대해 processCMakeFile 함수 실행
	    processCMakeFile(cmakeContents, utils, cmakeFile, modules);
	
	    // 하위 children 리스트가 있을 경우 재귀적으로 순회
	    for (CMakeContents child : cmakeContents.children) {
	    	String currentDirectory = child.path;
	    	File currentCMakeFile = new File(currentDirectory);
	    	recurseProcess(child, utils, currentCMakeFile, modules);
	    }
	}
	
	
	private void processCMakeFile(CMakeContents result, Utils utils, File cmakeFile, List<Module> modules) {
        StringBuilder moduleNameBuilder = new StringBuilder();
        
        for (String line : result.contents) {
//	        1. add_executable
        	if (isAddExecutableLine(line)) {
        		processAddExecutable(utils, line, moduleNameBuilder, cmakeFile, modules);
        	}
//	        2. add_library
            else if (isAddLibraryLine(line)) {
//            	System.out.println("AddLibrary : " + line);
            	processAddLibrary(utils, line, moduleNameBuilder, cmakeFile, modules);
            }                    
        }
	}	

	
	private boolean isAddExecutableLine(String line) {
	    return line.contains("add_executable");
	}
	
	private boolean isAddLibraryLine(String line) {
	    return line.contains("add_library");
	}
	
	private String deleteQuote(String moduleName) {
		return moduleName.substring(1, moduleName.length() - 1);
	}


	private void processAddExecutable(Utils utils, String line, StringBuilder moduleNameBuilder, File cmakeFile, List<Module> modules) {
        int startIndex = line.indexOf('(') + 1;
        int endIndex = line.indexOf(')');
        moduleNameBuilder.append(line.substring(startIndex, endIndex).trim());

        String[] moduleNameString = moduleNameBuilder.toString().split(" ");
        String finalModuleName = moduleNameString[0].trim();
        

//	    System.out.println("현재위치 : " + cmakeFile.getPath());
//	    System.out.println("모듈이름 : " + finalModuleName);
//	    System.out.println(" ");
	    
        String outputType = "EXE";
        utils.extractModuleInfo(new StringBuilder(finalModuleName), outputType, cmakeFile, modules);
        
        moduleNameBuilder.setLength(0);
	}
	
	private void processAddLibrary(Utils utils, String line, StringBuilder moduleNameBuilder, File cmakeFile, List<Module> modules) {
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
	    utils.extractModuleInfo(new StringBuilder(finalModuleName), outputType, cmakeFile, modules);
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
