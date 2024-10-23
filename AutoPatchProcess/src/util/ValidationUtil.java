package util;

import java.io.BufferedReader;
import java.io.IOException;

public class ValidationUtil {
	public static String getModuleName(String line) {
		int startIndex = line.indexOf('(') + 1;
		int endIndex = line.indexOf(')');
		return line.substring(startIndex, endIndex).trim();
	}

	public static boolean isInvalidCondition(String condition) {
		return !condition.equals("WIN32") && !condition.equals("UNIX");
	}

	public static boolean isIgnorableModule(String moduleName, String condition) {
		return moduleName.isEmpty() || moduleName.equals("PRIVATE") || moduleName.equals("PUBLIC")
				|| (condition.equals("UNIX") && !moduleName.equals("ucommon"));
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
