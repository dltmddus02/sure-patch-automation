package main.java.service;

import java.util.ArrayList;
import java.util.List;

public class ArgumentParser {
	private String enginePath;
	private List<String> changedSourceFiles;

	public ArgumentParser(String[] args) {
		this.changedSourceFiles = new ArrayList<>();
		parse(args);
	}

	/**
	 * enginePath, changedSourceFiles
	 * 
	 * @param args
	 */
	private void parse(String[] args) {
		for (String arg : args) {
			if (arg.startsWith("enginePath=")) {
				this.enginePath = arg.substring("enginePath=".length());
			} else {
				this.changedSourceFiles.add(arg);
			}
		}
	}

	public String getEnginePath() {
		return enginePath;
	}

	public List<String> getChangedSourceFiles() {
		return changedSourceFiles;
	}

}
