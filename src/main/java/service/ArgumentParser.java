package main.java.service;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

public class ArgumentParser {
	private static final String ENGINE_PATH_OPTION = "-enginePath";
	private static final String UTILITY_OPTION = "-utilityPath";
	private static final String ROOT_OPTION = "-rootPath";
	private static final String DESCRIPTION_OPTION = "-description";
	private static final String OVERWRITE_OPTION = "-overwrite";

	private static final String EQUAL = "=";

	private String enginePath;

	private String patchUtilityPath;
	private String rootPath;
	private String description;

	private boolean overwrite;

	public ArgumentParser(String[] args) {
		parse(args);
	}

	/**
	 * enginePath, patchUtilityPath, rootPath, description, overwrite
	 * 
	 * @param args
	 */
	private void parse(String[] args) {
		for (String arg : args) {

			Entry<String, String> argEntry = getArgEntry(arg);

			switch (argEntry.getKey()) {
			case ENGINE_PATH_OPTION -> this.enginePath = argEntry.getValue();
			case UTILITY_OPTION -> this.patchUtilityPath = argEntry.getValue();
			case ROOT_OPTION -> this.rootPath = argEntry.getValue();
			case DESCRIPTION_OPTION -> this.description = argEntry.getValue().replace("```", StringUtils.LF);
			case OVERWRITE_OPTION -> this.overwrite = Boolean.valueOf(argEntry.getValue());
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

	public String getPatchUtilityPath() {
		return patchUtilityPath;
	}

	public String getRootPath() {
		return rootPath;
	}

	public String getDescription() {
		return description;
	}

	public boolean isOverwrite() {
		return overwrite;
	}
}
