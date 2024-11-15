package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.autopatch.CMakeParser;
import main.autopatch.CMakeParser.Parser;
import main.autopatch.CMakePreprocessor;
import main.domain.CMakeContents;
import main.domain.Module;
import main.service.ModuleSearcher;

public class Main {
	public static void main(String[] args) {

		CMakePreprocessor cmakePreprocessor = new CMakePreprocessor();

		CMakeParser cmakeParser = new CMakeParser();
		Parser parser = cmakeParser.new Parser();

		List<Module> modules = new ArrayList<>(); // 결과 모듈 정보들 저장할 리스트
		String topDirectory = "C:\\Users\\sure\\CTcode\\engine";
//		String topDirectory = "C:\\Users\\sure\\sure-project\\sure-patch-automation\\AutoPatchProcess\\src\\test\\engine";

		try {

			CMakeContents root = cmakePreprocessor.preprocess(topDirectory);
			parser.parseCMakeFile(root, modules);
			System.out.println("성공적으로 완료.");
			Module.printAllModuleNamesAndReferences(modules);

		} catch (Exception e) {
			System.err.println("실패: " + e.getMessage());
		}

// 		테스트 폴더로 테스트
//		ModuleSearcher moduleSearcher = new ModuleSearcher(modules);
//
//		List<String> changedFiles = List
//				.of("C:\\Users\\sure\\CTcode\\engine\\src\\util\\POCO_LIB\\Foundation\\src\\trees.c");
//
//		Map<String, List<String>> affectedModules = moduleSearcher.getModuleNamesBySourceFiles(changedFiles);
//
//		System.out.println("\n\n\n\nAffected Modules:");
//		affectedModules.forEach((sourceFile, moduleNames) -> {
//			System.out.println("Source File: " + sourceFile + " -> Modules: " + moduleNames);
//		});

	}
}
