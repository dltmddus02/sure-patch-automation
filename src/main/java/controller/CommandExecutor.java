package main.java.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import main.java.model.CMakeContents;
import main.java.model.Module;
import main.java.service.ArgumentParser;
import main.java.service.CMakeParser;
import main.java.service.CMakePreprocessor;
import main.java.service.ModuleSearcher;

public class CommandExecutor {
	private String[] args;

	public CommandExecutor(String[] args) {
		this.args = args;
	}

	public void run() {
		try {
			// 파라미터에서 경로와 변경된 소스 파일 가져오기
			ArgumentParser argumentParser = new ArgumentParser(args);
			String enginePath = argumentParser.getEnginePath();
			List<String> changedSourceFiles = argumentParser.getChangedSourceFiles();

			// 모듈 전처리
			List<Module> modules = preprocessModules(enginePath);

			// 변경된 파일들로부터 변경될 모듈들 가져오기
//			List<String> changedFiles = parseChangedSourceFiles(changedSourceFiles);
			Set<String> resultModules = extractModulesByChangedFiles(changedSourceFiles, modules);

			System.out.println("결과 모듈: " + resultModules);
		} catch (Exception e) {
			System.err.println("실패: ");
			e.printStackTrace();
		}
	}

	private List<Module> preprocessModules(String topDirectory) throws Exception {
		CMakePreprocessor cmakePreprocessor = new CMakePreprocessor();
		CMakeParser cmakeParser = new CMakeParser();
		CMakeParser.Parser parser = cmakeParser.new Parser();

		List<Module> modules = new ArrayList<>();
		CMakeContents root = cmakePreprocessor.preprocess(topDirectory);
		parser.parseCMakeFile(root, modules);

		Module.printAllModuleNamesAndReferences(modules);

		return modules;
	}

//	private List<String> parseChangedSourceFiles(String changedSourceFiles) {
//		return Arrays.stream(changedSourceFiles.split(",")).map(String::trim).toList();
//	}

	private Set<String> extractModulesByChangedFiles(List<String> changedFiles, List<Module> modules) {
		ModuleSearcher moduleSearcher = new ModuleSearcher(modules);
		return moduleSearcher.getModuleNamesBySourceFiles(changedFiles);
	}

}
