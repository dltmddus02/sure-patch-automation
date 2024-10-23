package autoPatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Module {
	StringBuilder moduleName;
	String outputType;
	List<String> sourceFiles;
	Set<String> affectedModules;

	public Module(StringBuilder moduleName, String outputType) {
		this.moduleName = moduleName;
		this.outputType = outputType;
		this.sourceFiles = new ArrayList<>();
		this.affectedModules = new HashSet<>();
	}

	public void addSourceFile(String sourceFile) {
		sourceFiles.add(sourceFile);
	}

	public void addAffectedModule(String library) {
		affectedModules.add(library);
	}

}
