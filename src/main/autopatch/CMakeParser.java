package main.autopatch;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import main.autopatch.CMakePreprocessor.CMakeContents;
import main.domain.Condition;
import main.service.ModuleProcessor;
import main.util.CodeLineUtil;
import main.util.ValidationUtil;

public class CMakeParser {

	public class Parser {
		ModuleProcessor moduleProcessor = new ModuleProcessor();

		public void parseCMakeFile(CMakeContents root, List<Module> modules) {
			File cmakeFile = new File(root.path + "\\CMakeLists.txt");
			recurseProcess(root, cmakeFile, modules);
			for (Module m : modules) {
				if (m.affectedModules.isEmpty())
					continue;
				System.out.println("모듈이름: " + m.getModuleName());

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
			Condition condition = new Condition();

			for (String line : result.contents) {

				storeConditionInfo(line, condition);

//				 1. 모듈 이름 저장
				if (CodeLineUtil.isAddExecutableLine(line)) {
					moduleProcessor.processAddExecutable(line, modules);
				} else if (CodeLineUtil.isAddLibraryLine(line)) {
					moduleProcessor.processAddLibrary(line, modules);
				}

//				2. 의존 모듈 저장
				if (CodeLineUtil.isTargetLinkLibrariesLine(line)) {
					moduleProcessor.processTargetLinkLibraries(line, condition, modules);
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


//		구현 전
//		모듈 순회하며 모듈 간의 참조+의존성 저장하는 함수
		public void findDependencies(List<String> input, Set<Module> output, List<Module> modules) {
			for (String mf : input) {
				for (Module m : modules) {
					// m.affected~ 리스트 보면서 moduleFile와 같은지 보기
					// 같다면 uniqueAffectedModules에 m 추가
					if (m.affectedModules.contains(mf)) {
						output.add(m);
						findDependencies(Collections.singletonList(m.getModuleName()), output, modules);
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