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

	public Module(StringBuilder moduleName, String outputType) {
		this.moduleName = moduleName;
		this.outputType = outputType;
		this.sourceFiles = new ArrayList<>();
		this.setAffectedModules(new HashSet<>());
	}

	public String getModuleName() {
		return moduleName.toString();
	}

	public String getOutputType() {
		return outputType;
	}

	public Set<String> getAffectedModules() {
		return affectedModules;
	}

	public void setAffectedModules(Set<String> affectedModules) {
		this.affectedModules = affectedModules;
	}

	public void addSourceFile(String sourceFile) {
		sourceFiles.add(sourceFile);
	}

	public void addAffectedModule(String library) {
		getAffectedModules().add(library);
	}

}
