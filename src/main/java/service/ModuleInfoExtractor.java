package main.java.service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import main.java.model.Condition;
import main.java.model.Module;
import main.java.util.ValidationUtil;

public class ModuleInfoExtractor {

	public void processAddExecutable(String line, List<Module> modules, String currentPath) {
		String outputType = "EXE";
		int startIdx = 1;

		Module module = initializeModule(modules, line, currentPath, outputType, startIdx);

		if (module != null && !isModuleExists(modules, module)) {
			modules.add(module);
		}
	}

	public void processAddLibrary(String line, List<Module> modules, String currentPath) {
		String outputType = line.contains("STATIC") ? "STATIC" : line.contains("SHARED") ? "SHARED" : "X";

		int startIdx = 2;
		Module module;

		module = initializeModule(modules, line, currentPath, outputType, startIdx);
		if (module != null && !isModuleExists(modules, module)) {
			modules.add(module);
		}
	}

	private Module initializeModule(List<Module> modules, String line, String currentPath, String outputType,
			int startIdx) {
		line = formatPath(line);

		String moduleLine = ValidationUtil.getModuleName(line);
		String[] moduleLines = Arrays.stream(moduleLine.split("\\s+")).filter(str -> !str.isBlank())
				.toArray(String[]::new);

		String moduleName = removeQuotes(moduleLines[0].trim());

		Module module = getModuleByModuleName(modules, moduleName);
		if (module == null) {
			module = new Module(new StringBuilder(moduleName), outputType);
		} else {
			module.setOutputType(outputType);
		}
		for (int i = startIdx; i < moduleLines.length; i++) {
			if (moduleLines[i].trim().equals(""))
				continue;
			String path = resolvePathForModuleLine(moduleLines[i], currentPath);
			addSourceFiles(module, path, currentPath);
		}

		if (line.contains("ALIAS")) {
			Module m = getModuleByModuleName(modules, removeQuotes(moduleLines[2]));
			m.addAffectedModule(removeQuotes(moduleLines[0]));
		}

		return module;
	}

	private String formatPath(String line) {
		return line.replaceAll("[/\\\\]", "\\\\");
	}

	private String resolvePathForModuleLine(String line, String currentPath) {
		String resolvedPath = resolvePath(currentPath, line.replace("\"", "").trim());
		return formatPath(resolvedPath.replace("\"", ""));
	}

	private String resolvePath(String currentAbsolutePath, String path) { // *.cpp
		Path resolvedPath;

		// 경로 확인 전에 만약에 와일드카드 있으면?
		if (path.contains("*")) {
			return path;
		}
		// 상대 경로인 경우
		if (!Paths.get(path).isAbsolute()) {
			resolvedPath = Paths.get(currentAbsolutePath, path).toAbsolutePath().normalize();
		}
		// 절대 경로인 경우
		else {
			resolvedPath = Paths.get(path).normalize();
		}
		return (resolvedPath).toString();
	}

	private void addSourceFiles(Module module, String path, String currentAbsolutePath) {
		List<String> validModuleLines = hasWildCardPath(path, currentAbsolutePath);

		validModuleLines.forEach(validLine -> module.addSourceFile(getRelativePath(validLine, "engine")));
	}

	private List<String> hasWildCardPath(String path, String currentPath) {
		if (!path.contains("*"))
			return Collections.singletonList(path);

		List<String> returnMacroValues = new ArrayList<>();
		String directoryPath = path.substring(0, path.indexOf("*"));

		Path searchPath;
		if (Files.exists(Paths.get(directoryPath))) {
			searchPath = Paths.get(directoryPath);
		} else {
			searchPath = Paths.get(currentPath + "\\" + directoryPath);
		}

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(searchPath, path.substring(path.indexOf("*")))) {
			for (Path entry : stream) {
				returnMacroValues.add(entry.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return returnMacroValues;
	}

	private String getRelativePath(String fullPath, String baseWord) {
		int baseIndex = fullPath.indexOf(baseWord);

		if (baseIndex == -1) {
			return fullPath;
		}
		return fullPath.substring(baseIndex + baseWord.length() + 1);
	}

	private String removeQuotes(String moduleName) {
		if (moduleName.startsWith("\"")) {
			return moduleName.substring(1, moduleName.length() - 1);
		} else {
			return moduleName;
		}
	}

	public void processTargetLinkLibraries(String line, Condition condition, List<Module> modules) {
		String moduleName = ValidationUtil.getModuleName(line);
		String[] affectedModuleNames = moduleName.split(" ");

		String currentModuleName = affectedModuleNames[0].trim();

		for (String c : condition.getData()) {
			if (ValidationUtil.isInvalidCondition(c)) {
				return;
			}
			for (Module module : modules) {
				if (module.getModuleName().equals(currentModuleName)) {
					processAffectedModules(module, affectedModuleNames, c, modules);
					return;
				}
			}
		}
	}

	private void processAffectedModules(Module module, String[] affectedModuleNames, String condition,
			List<Module> modules) {
		for (int i = 1; i < affectedModuleNames.length; i++) {
			String affectedModuleName = affectedModuleNames[i].trim();
			if (ValidationUtil.isIgnorableModule(affectedModuleName, condition)) {
				continue;
			}
			Module m = getModuleByModuleName(modules, affectedModuleName);
			if (m == null) {
				// 만약 이미 정의된 모듈이 아니라면 추가하기
				m = new Module(new StringBuilder(affectedModuleName), "");
				modules.add(m);
			}
			m.setIsTopModule(false);
			module.addAffectedModule(affectedModuleName);
		}
	}

	public static Module getModuleByModuleName(List<Module> modules, String moduleName) {
		for (Module module : modules) {
			if (module.getModuleName().equals(moduleName)) {
				return module;
			}
		}
		return null;
	}

	public static boolean isModuleExists(List<Module> modules, Module newModule) {
		return modules.stream().anyMatch(existingModule -> existingModule.getModuleName().toString()
				.equals(newModule.getModuleName().toString()));
	}
}