<<<<<<<< HEAD:src/main/autopatch/Module.java
package main.autopatch;
========
package autopatch;
>>>>>>>> 7f544c9421adf040b07ba7d98e3753ddb8004a2e:AutoPatchProcess/src/main/java/autopatch/Module.java

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Module {
	private StringBuilder moduleName;
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

	public String getModuleName() {
		return moduleName.toString();
	}
}
