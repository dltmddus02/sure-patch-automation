package main.java.service;

import java.util.Map;
import java.util.Map.Entry;

public class ArgumentParser {
	private static final String ENGINE_PATH_OPTION = "-enginePath";
	private static final String CHANGED_SOURCE_FILES = "-changedSourceFiles";

	private static final String EQUAL = "=";

	private String enginePath;
	private String changedSourceFiles;

	public ArgumentParser(String[] args) {
		parse(args);
	}

	/**
	 * enginePath, changedSourceFiles
	 * 
	 * @param args
	 */
	private void parse(String[] args) {
		for (String arg : args) {

			Entry<String, String> argEntry = getArgEntry(arg);

			switch (argEntry.getKey()) {
			case ENGINE_PATH_OPTION -> this.enginePath = argEntry.getValue();
			case CHANGED_SOURCE_FILES -> this.changedSourceFiles = argEntry.getValue();
			default -> throw new IllegalArgumentException("Unexpected value: " + getArgEntry(arg));
			}

		}
	}

	private Entry<String, String> getArgEntry(String line) {
		String[] keyValue = line.replace("\"", "").split(EQUAL, 2);
		return Map.entry(keyValue[0], keyValue[1]);
	}

	public String getEnginePath() {
		return enginePath;
	}

	public String getChangedSourceFiles() {
		return changedSourceFiles;
	}

}
