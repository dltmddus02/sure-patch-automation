package main.service;

import java.util.List;

import main.autopatch.Module;
import main.domain.Condition;
import main.util.ValidationUtil;

public class ModuleProcessor {
	public void processAddExecutable(String line, List<Module> modules) {
		String moduleName = ValidationUtil.getModuleName(line);
		String[] moduleNames = moduleName.split(" ");

		String currentModuleName = moduleNames[0].trim();

		String outputType = "EXE";

		Module module = new Module(new StringBuilder(currentModuleName), outputType);
		modules.add(module);
	}

	public void processAddLibrary(String line, List<Module> modules) {
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

	private String deleteQuote(String moduleName) {
		return moduleName.substring(1, moduleName.length() - 1);
	}

}