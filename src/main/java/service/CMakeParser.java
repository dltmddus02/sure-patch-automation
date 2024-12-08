package main.java.service;

import java.io.File;
import java.util.List;

import main.java.model.CMakeContents;
import main.java.model.Condition;
import main.java.model.Module;
import main.java.util.CodeLineUtil;

public class CMakeParser {

	public class Parser {
		ModuleInfoExtractor moduleProcessor = new ModuleInfoExtractor();

		public void parseCMakeFile(CMakeContents root, List<Module> modules) {
			File cmakeFile = new File(root.getPath() + "\\CMakeLists.txt");
			recurseProcess(root, cmakeFile, modules);
		}

		private void recurseProcess(CMakeContents cmakeContents, File cmakeFile, List<Module> modules) {
			processCMakeFile(cmakeContents, cmakeFile, modules);

 			for (CMakeContents child : cmakeContents.getChildren()) {
				String currentDirectory = child.getPath();
				File currentCMakeFile = new File(currentDirectory);
				recurseProcess(child, currentCMakeFile, modules);
			}
		}

		private void processCMakeFile(CMakeContents result, File cmakeFile, List<Module> modules) {
			Condition condition = new Condition();

			for (String statement : result.getContents()) {

				storeConditionInfo(statement, condition);

				String currentPath = result.getPath();
				
//				 1. 모듈 이름, 참조 소스파일 저장
				if (CodeLineUtil.isAddExecutableLine(statement)) {
					moduleProcessor.processAddExecutable(statement, modules, currentPath);
				} else if (CodeLineUtil.isAddLibraryLine(statement)) {
					moduleProcessor.processAddLibrary(statement, modules, currentPath);
				}

//				2. 의존 모듈 저장
				if (CodeLineUtil.isTargetLinkLibrariesLine(statement)) {
					moduleProcessor.processTargetLinkLibraries(statement, condition, modules);
				}

			}
		}

		private void storeConditionInfo(String line, Condition condition) {
			if (line.contains("endif()") || line.contains("ENDIF()")) {
				condition.pop();
				return;
			} else if (line.contains("elseif(") || line.contains("ELSEIF(") || line.contains("else(")
					|| line.contains("ELSE(")) {
				condition.pop();
			} else if (line.contains("if(") || line.contains("IF(")) {

			} else
				return;

			int startIndex = line.indexOf('(');
			int endIndex = line.lastIndexOf(')');

			if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
				String extracted = line.substring(startIndex + 1, endIndex);
				condition.push(extracted);
			}
		}
	}
}