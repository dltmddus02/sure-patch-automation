package main.java.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.java.model.Module;

public class ModuleSearcher {
	private List<Module> modules;
	private Map<Path, Set<String>> sourceFileToModules;
	private Map<String, Module> moduleMap;
	private Module topModule;

	public ModuleSearcher(List<Module> modules) {
		this.modules = modules;
		this.sourceFileToModules = new HashMap<>();
		this.moduleMap = new HashMap<>();

		for (Module module : modules) {
			moduleMap.put(module.getModuleName(), module);
		}

		createSourceFilesMap();
	}

	public void createSourceFilesMap() {
		for (Module module : modules) {
			Set<String> visitedModules = new HashSet<>();

			topModule = module;
			addSourceFilesRecursively(module, visitedModules);
		}
	}

	private void addSourceFilesRecursively(Module module, Set<String> visitedModules) {
		if (visitedModules.contains(module.getModuleName()))
			return;
		visitedModules.add(module.getModuleName());

		for (String sourceFile : module.getSourceFiles()) {
			Path sourcePath = Paths.get(sourceFile);

			if (topModule.getIsTopModule()) {
				sourceFileToModules.computeIfAbsent(sourcePath, k -> new HashSet<>()).add(topModule.getModuleName());
			}
		}

		for (String affectedModuleName : module.getAffectedModules()) {
			Module affectedModule = moduleMap.get(affectedModuleName);
			if (affectedModule != null) {
				addSourceFilesRecursively(affectedModule, visitedModules);
			}
		}
	}

	public Set<String> getModuleNamesBySourceFiles(List<String> sourceFiles) { // C:\Users\sure\CTcode-test\engine\src/PA/build/unix/CFE/Makefile
		Map<String, List<String>> result = new HashMap<>();
		for (String sourceFile : sourceFiles) { // sourceFile안에 mo
			Set<String> moduleNames = getModuleNamesBySourceFile(sourceFile);
			result.put(sourceFile, new ArrayList<>(moduleNames));
		}
		return getResultModules(result);
	}

	public Set<String> getModuleNamesBySourceFile(String sourceFile) {
		Path sourcePath = Paths.get(sourceFile);

		Set<String> result = new HashSet<>();

		for (Map.Entry<Path, Set<String>> entry : sourceFileToModules.entrySet()) {
			Path keyPath = entry.getKey();

			if (sourcePath.toString().contains(keyPath.toString())) {
				result.addAll(entry.getValue());
			}
		}
		if (result.isEmpty()) {
			return Collections.emptySet();
		}
		return result;
	}

	public Set<String> getResultModules(Map<String, List<String>> modules) {
		Set<String> result = new HashSet<>();
		for (List<String> list : modules.values()) {
			for (String s : list) {
				result.add(s);
			}
		}
		return result;
	}
}