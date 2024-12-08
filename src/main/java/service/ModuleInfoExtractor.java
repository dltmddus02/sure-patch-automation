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
import java.util.stream.Stream;

import main.java.model.Condition;
import main.java.model.Module;
import main.java.util.ValidationUtil;

public class ModuleInfoExtractor {
	// add_executable()
	public void processAddExecutable(String line, List<Module> modules, String currentPath) {
		String outputType = "EXE";
		int startIdx = 1;

		Module module = initializeModule(modules, line, currentPath, outputType, startIdx);

		if (module != null && !isModuleExists(modules, module)) {
			modules.add(module);
		}
	}

	// add_library()
	public void processAddLibrary(String line, List<Module> modules, String currentPath) {
		String outputType = line.contains("STATIC") ? "STATIC" : line.contains("SHARED") ? "SHARED" : "X";

		int startIdx = 2;
		Module module;

		module = initializeModule(modules, line, currentPath, outputType, startIdx);
		if (module != null && !isModuleExists(modules, module)) {
			modules.add(module);
		}
	}

	// target_link_libraries()
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

	private String removeQuotes(String moduleName) {
		if (moduleName.startsWith("\"")) {
			return moduleName.substring(1, moduleName.length() - 1);
		} else {
			return moduleName;
		}
	}

	private String resolvePathForModuleLine(String line, String currentPath) {
		String resolvedPath = resolvePath(currentPath, line.replace("\"", "").trim());
		return formatPath(resolvedPath.replace("\"", ""));
	}

	private String resolvePath(String currentAbsolutePath, String path) { // *.cpp
		Path resolvedPath;

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

		/*
		 * build_engine_GIT_window 기준으로 경로 수정
		 * 
		 * 예)
		 * C:\\01.jenkins\\agent\\workspace\\build_engine_GIT_window\\src\\ut\\COMMON\\
		 * Args\\Args.cpp -> src\\ut\\COMMON\\Args\\Args.cpp
		 */

		for (String moduleLine : validModuleLines) {
		
			String formattedModuleLine = getRelativePath(moduleLine, "build_engine_GIT_window");
			if (isExistSourcePath(formattedModuleLine)) {
				module.addSourceFile(formattedModuleLine);
			}
		}
	}

	private List<String> hasWildCardPath(String path, String currentPath) {
		if (path.startsWith("GLOB_RECURSE-")) {
			return getRecursiveFilePaths(path, currentPath);
		}

		if (!path.contains("*")) {
			return Collections.singletonList(path);
		}

		return getMatchingFilePaths(path, currentPath);
	}

	private List<String> getRecursiveFilePaths(String path, String currentPath) {
		String directoryPath = currentPath + "\\" + path.substring("GLOB_RECURSE-".length(), path.indexOf("*"));
		List<String> returnMacroValues = new ArrayList<>();

		Path searchPath = getSearchPath(directoryPath, currentPath);

		try {
			String extension = path.substring(path.lastIndexOf("*") + 1);
			try (Stream<Path> paths = Files.walk(searchPath)) {
				paths.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(extension))
						.forEach(p -> returnMacroValues.add(p.toString()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return returnMacroValues;
	}

	private List<String> getMatchingFilePaths(String path, String currentPath) {
		List<String> returnMacroValues = new ArrayList<>();
		String directoryPath = path.substring(0, path.indexOf("*"));

		Path searchPath = getSearchPath(directoryPath, currentPath);

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(searchPath, path.substring(path.indexOf("*")))) {
			for (Path entry : stream) {
				returnMacroValues.add(entry.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return returnMacroValues;
	}

	private Path getSearchPath(String directoryPath, String currentPath) {
		if (Files.exists(Paths.get(directoryPath))) {
			return Paths.get(directoryPath);
		} else {
			return Paths.get(currentPath + "\\" + directoryPath);
		}
	}

	private String getRelativePath(String fullPath, String baseWord) {
		int baseIndex = fullPath.indexOf(baseWord);

		if (baseIndex == -1) {
			return fullPath;
		}
		return fullPath.substring(baseIndex + baseWord.length() + 1);
	}

	private boolean isExistSourcePath(String sourcePath) {
		// 디버깅용
		 return (Files.exists(Paths.get("C:\\Users\\sure\\CTcode\\build_engine_GIT_window\\" + sourcePath)));
		
		// 실제 engine 리포지토리 경로
//		return (Files.exists(Paths.get("C:\\01.jenkins\\agent\\workspace\\build_engine_GIT_window\\" + sourcePath)));
	}

	private static Module getModuleByModuleName(List<Module> modules, String moduleName) {
		for (Module module : modules) {
			if (module.getModuleName().equals(moduleName)) {
				return module;
			}
		}
		return null;
	}

	private static boolean isModuleExists(List<Module> modules, Module newModule) {
		return modules.stream().anyMatch(existingModule -> existingModule.getModuleName().toString()
				.equals(newModule.getModuleName().toString()));
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
				m = new Module(new StringBuilder(affectedModuleName), "");
				modules.add(m);
			}
			m.setIsTopModule(false);
			module.addAffectedModule(affectedModuleName);
		}
	}

}