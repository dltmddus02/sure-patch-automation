package autopatch;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import autopatch.CMakePreproccesor.CMakeContents;
import util.CodeLineUtil;
import util.ValidationUtil;

public class CMakeParser {

	public class Condition {
		String value;
	}

	public class Conditions {
		Stack<String> data = new Stack<>();

		void push(String value) {
			data.add(value);
		}

		void pop() {
			if (!data.isEmpty()) {
				data.pop();
			}
		}
	}

	public class Parser {
		public void parseCMakeFile(CMakeContents root, List<Module> modules) {
			File cmakeFile = new File(root.path + "\\CMakeLists.txt");
			recurseProcess(root, cmakeFile, modules);
			for (Module m : modules) {
				if (m.affectedModules.isEmpty())
					continue;
				System.out.println("모듈이름: " + m.moduleName);

				for (String s : m.affectedModules) {
					System.out.println(s);
				}
				System.out.println("");
			}
		}

		private void recurseProcess(CMakeContents cmakeContents, File cmakeFile, List<Module> modules) {
			processCMakeFile(cmakeContents, cmakeFile, modules);

			for (CMakeContents child : cmakeContents.children) {
				String currentDirectory = child.path;
				File currentCMakeFile = new File(currentDirectory);
				recurseProcess(child, currentCMakeFile, modules);
			}
		}

		private void processCMakeFile(CMakeContents result, File cmakeFile, List<Module> modules) {
			Conditions conditions = new Conditions();

			for (String line : result.contents) {

				storeConditionInfo(line, conditions);

//				 1. 모듈 이름 저장
				if (CodeLineUtil.isAddExecutableLine(line)) {
					processAddExecutable(line, modules);
				} else if (CodeLineUtil.isAddLibraryLine(line)) {
					processAddLibrary(line, modules);
				}

//				2. 의존 모듈 저장
				if (CodeLineUtil.isTargetLinkLibrariesLine(line)) {
					processTargetLinkLibraries(line, conditions, modules);
				}

			}
		}

		private void storeConditionInfo(String line, Conditions conditions) {
			if (line.contains("endif()") || line.contains("ENDIF()")) {
				conditions.pop();
				return;
			} else if (line.contains("elseif(") || line.contains("ELSEIF(") || line.contains("else(") || line.contains("ELSE(")) {
				conditions.pop();
			} else if (line.contains("if(") || line.contains("IF(")) {

			} else
				return;

			int startIndex = line.indexOf('(');
			int endIndex = line.lastIndexOf(')');

			if (startIndex != -1 && endIndex != -1 && startIndex < endIndex) {
				String extracted = line.substring(startIndex + 1, endIndex);
				conditions.push(extracted);
			}
		}

		private String deleteQuote(String moduleName) {
			return moduleName.substring(1, moduleName.length() - 1);
		}

		private void processAddExecutable(String line, List<Module> modules) {
			String moduleName = ValidationUtil.getModuleName(line);
			String[] moduleNames = moduleName.split(" ");

			String currentModuleName = moduleNames[0].trim();

			String outputType = "EXE";

			Module module = new Module(new StringBuilder(currentModuleName), outputType);
			modules.add(module);
		}

		private void processAddLibrary(String line, List<Module> modules) {
			String moduleName = ValidationUtil.getModuleName(line);
			String[] moduleNames = moduleName.split(" ");

			String currentModuleName = moduleNames[0].trim();

			if (currentModuleName.startsWith("\"")) {
				currentModuleName = deleteQuote(currentModuleName);
			}

			String outputType = line.contains("STATIC") ? "STATIC" : line.contains("SHARED") ? "SHARED" : "";

			if (outputType.equals("SHARED")) {
				Module module = new Module(new StringBuilder(currentModuleName), outputType);
				modules.add(module);
			}
		}

		private void processTargetLinkLibraries(String line, Conditions conditions, List<Module> modules) {
			String moduleName = ValidationUtil.getModuleName(line);
			String[] affectedModuleNames = moduleName.split(" ");

			String currentModuleName = affectedModuleNames[0].trim();

			for (String condition : conditions.data) {
				if (ValidationUtil.isInvalidCondition(condition)) {
					return;
				}
				for (Module module : modules) {
					if (module.moduleName.toString().equals(currentModuleName)) {
						processAffectedModules(module, affectedModuleNames, condition);
						return;
					}
				}
			}
		}

		private void processAffectedModules(Module module, String[] affectedModuleNames, String condition) {
			for (int i = 1; i < affectedModuleNames.length; i++) {
				String affectedModuleName = affectedModuleNames[i].trim();
				if (ValidationUtil.isIgnorableModule(affectedModuleName, condition)) {
					continue;
				}
				module.addAffectedModule(affectedModuleName);
			}
		}

//		구현 전
//		모듈 순회하며 모듈 간의 참조+의존성 저장하는 함수
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

			// return mlist;
		}
	}
}