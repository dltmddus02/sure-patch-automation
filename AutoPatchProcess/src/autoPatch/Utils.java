package autoPatch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import autoPatch.CMakePreproccesor.CMakeContents;

public class Utils {
	public String getNextLine(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		if (line != null) {
		    return line.trim();
		} 
		else {
		    return null;
		}
	}
	
	public String getModuleNameType(String moduleName) {
	    switch (moduleName) {
	        case "${PROJECT_NAME}":
	            return "PROJECT_NAME";
	        case "${SAMPLE_NAME}":
	            return "SAMPLE_NAME";
	        case "${EXE_NAME}":
	            return "EXE_NAME";
	        case "${TESTUNIT}":
	            return "TESTUNIT";
	        default:
	            return null;
	    }
	}
	
    public String extractModuleNameFromLine(String line, String currentDirectory) {
        String moduleName = extractModuleName(line);
//  	만약 set() / project() 에도 ${LIBNAME} 매크로가 있다면? 
//      디렉의 상위 폴더에 있는 ${LIBNAME} 저장
        if (moduleName.contains("${LIBNAME}")) {
            moduleName = extractLibName(moduleName, currentDirectory);
        }

        return moduleName;
    }
	
//	set() / project() 에 위치한 프로젝트 이름 추출
	public String extractModuleName(String line) {
		int startIndex = line.indexOf('(') + 1;
		int endIndex = line.indexOf(')');
		String moduleName = line.substring(startIndex, endIndex).trim();
		
		String[] moduleNameString = moduleName.toString().split(" ");
		moduleName = moduleNameString[1].trim();
		
        if (moduleName.startsWith("\"") && moduleName.endsWith("\"")) { // 따옴표 제거
            moduleName = moduleName.substring(1, moduleName.length() - 1);
        }
        return moduleName;
	}


	public String extractLibName(String moduleName, String currentDirectory) {
        int lastIndex = currentDirectory.lastIndexOf('\\');
        String parentDirectory = currentDirectory.substring(0, lastIndex);
        File cmakeFile = new File(parentDirectory + "\\CMakeLists.txt");
	    
        try (BufferedReader reader = new BufferedReader(new FileReader(cmakeFile))) {
	        String parentLine;
	        while ((parentLine = reader.readLine()) != null) {
        		if (parentLine.contains("set(LIBNAME")) {
        			moduleName = extractModuleName(parentLine);
                    break;
        		}
	        }
	    } catch (FileNotFoundException e) {
	        System.out.println("CMake 파일을 찾을 수 없다 : " + cmakeFile.getPath());
	    } catch (IOException e) {
	        System.out.println("CMake 파일을 읽는 중 오류 발생 : " + e.getMessage());
	    }
        return moduleName;
	}
  
//  소스 파일 추출
//  private void extractSourceFile(Module module, String cmakeFile) {
//	  List<String> sourceFiles = new ArrayList<>();
//	  if (module.outputType == 'EXE') {
//			// 소스 파일 추출하는 코드 작성
//			// add_executable 함수 마지막 인자
//			for () {
//				module.addSourceFile(~);
//			}
//	  }
//		else if (module.outputType == 'SHARED') {
//			// 소스 파일 추출하는 코드 작성
//			// add_library 함수 마지막 인자
//			for () {
//				module.addSourceFile(~);
//			}
//		}
//		else if (module.outputType == 'STATIC') {
//			// 소스 파일 추출하는 코드 작성
//			// add_library 함수 마지막 인자
//			for () {
//				module.addSourceFile(~);
//			}
//		}
//  }
	


}
