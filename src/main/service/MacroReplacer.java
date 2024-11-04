package main.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import main.domain.Macros;

public class MacroReplacer {
	private Macros macros;

	public MacroReplacer(Macros macros) {
		this.macros = macros;
	}

	public String replaceMacro(String statement) {
		return processMacro(statement.trim());
	}

	public List<String> replaceMacros(List<String> statements) {
		return statements.stream().map(statement -> processMacro(statement)).collect(Collectors.toList());
	}

	private String processMacro(String line) {
		if (!line.contains("${") || !line.contains("}")) {
			return line;
		}

		Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
		Matcher matcher = pattern.matcher(line);
		StringBuffer result = new StringBuffer();

		while (matcher.find()) {
			String macroName = matcher.group(1);
			List<String> macroValues = macros.find(macroName);

			if (macroValues == null) {
				macroValues = new ArrayList<>();
				macroValues.add(matcher.group(0));
			}

			String macroValueStr = String.join(" ", macroValues);
			matcher.appendReplacement(result, Matcher.quoteReplacement(macroValueStr));
		}
		matcher.appendTail(result);

		return result.toString();
	}

}
