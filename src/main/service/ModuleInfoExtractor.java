package main.service;

import java.util.List;

import main.domain.Condition;
import main.domain.Module;
import main.util.ValidationUtil;

public class ModuleInfoExtractor {
	public void processAddExecutable(String line, List<Module> modules) {
		String moduleLine = ValidationUtil.getModuleName(line);
		String[] moduleLines = moduleLine.split(" ");

		String currentModuleName = moduleLines[0].trim();

		String outputType = "EXE";

		System.out.println(line);

		Module module = new Module(new StringBuilder(currentModuleName), outputType);

		for (int i = 1; i < moduleLines.length; i++) {
			module.addSourceFile(moduleLines[i].trim());
		}

		modules.add(module);
	}

	public void processAddLibrary(String line, List<Module> modules) {
		String moduleLine = ValidationUtil.getModuleName(line);
		String[] moduleLines = moduleLine.split(" ");

		String currentModuleName = moduleLines[0].trim();

		if (currentModuleName.startsWith("\"")) {
			currentModuleName = deleteQuote(currentModuleName);
		}

		String outputType = line.contains("STATIC") ? "STATIC" : line.contains("SHARED") ? "SHARED" : "";

		if (outputType.equals("SHARED")) {
			Module module = new Module(new StringBuilder(currentModuleName), outputType);
			
			for (int i = 1; i < moduleLines.length; i++) {
				module.addSourceFile(moduleLines[i].trim());
			}

			modules.add(module);
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
					processAffectedModules(module, affectedModuleNames, c);
					return;
				}
			}
		}
	}

	public void processAffectedModules(Module module, String[] affectedModuleNames, String condition) {
		for (int i = 1; i < affectedModuleNames.length; i++) {
			String affectedModuleName = affectedModuleNames[i].trim();
			if (ValidationUtil.isIgnorableModule(affectedModuleName, condition)) {
				continue;
			}
			module.addAffectedModule(affectedModuleName);
		}
	}

//	public void processSourceFiles(String line, List<Module> modules) {
//
//	}

	private String deleteQuote(String moduleName) {
		return moduleName.substring(1, moduleName.length() - 1);
	}

}