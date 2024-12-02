package main.java.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import main.java.model.Module;
import main.java.service.ModuleSearcher;

public class CommandExecutor {
	private String changedSourceFiles;
	private List<Module> modules;

	public CommandExecutor(String changedSourceFiles, List<Module> modules) {
		this.changedSourceFiles = changedSourceFiles;
		this.modules = modules;
	}

	public void run() {
		// changedSourceFiles 쉼표(,) 기준으로 분리 후 List로 저장
		List<String> changedFiles = parseChangedSourceFiles(changedSourceFiles);

		System.out.println("changedFiles: " + changedFiles);

		// 변경된 소스파일로 모듈 찾기
		Set<String> resultModules = extractModulesByChangedFiles(changedFiles);

		System.out.println("resultModules: " + resultModules);
	}

	private List<String> parseChangedSourceFiles(String changedSourceFiles) {
		return Arrays.stream(changedSourceFiles.split(",")).map(String::trim).toList();
	}

	private Set<String> extractModulesByChangedFiles(List<String> changedFiles) {
		ModuleSearcher moduleSearcher = new ModuleSearcher(modules);
		return moduleSearcher.getModuleNamesBySourceFiles(changedFiles);
	}

}