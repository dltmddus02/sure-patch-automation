package main.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Module {
	private StringBuilder moduleName;
	private String outputType;
	private Set<String> affectedModules;
	private List<String> sourceFiles;
	private boolean isTopModule;

	public Module(StringBuilder moduleName, String outputType) {
		this.moduleName = moduleName;
		this.outputType = outputType;
		this.setAffectedModules(new HashSet<>());
		this.setSourceFiles(new ArrayList<>());
		this.isTopModule = true;
	}

	public String getModuleName() {
		return moduleName.toString();
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public Set<String> getAffectedModules() {
		return affectedModules;
	}

	public void setAffectedModules(Set<String> affectedModules) {
		this.affectedModules = affectedModules;
	}

	public List<String> getSourceFiles() {
		return sourceFiles;
	}

	public void setSourceFiles(ArrayList<String> sourceFiles) {
		this.sourceFiles = sourceFiles;
	}

	public void addAffectedModule(String library) {
		affectedModules.add(library);
	}

	public void addSourceFile(String sourceFile) {
		if (isNullOrEmpty(sourceFile)) {
			return;
		}
		sourceFiles.add(sourceFile);
	}

	private boolean isNullOrEmpty(String sourceFile) {
		if (sourceFile == null || sourceFile.isBlank())
			return true;
		return false;
	}

	public boolean getIsTopModule() {
		return isTopModule;
	}

	public void setIsTopModule() {
		isTopModule = false;
	}

	public Set<String> getAffectedModulesByModuleName(String moduleName) {
		if (this.moduleName.toString().equals(moduleName)) {
			return affectedModules;
		}
		return new HashSet<>();
	}

	public static void printAllModuleNamesAndReferences(List<Module> modules) {
		for (Module module : modules) {
			System.out.println("모듈이름 : " + module.getModuleName());
			System.out.println("참조받는 모듈 : " + module.getAffectedModules());
			System.out.println("모듈타입 : " + module.getOutputType());
			System.out.println("top모듈인가 : " + module.getIsTopModule());
			System.out.println("소스파일 : " + module.getSourceFiles());
			System.out.println("---------");
		}
	}

}
