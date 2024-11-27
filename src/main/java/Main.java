package main.java;

import java.util.ArrayList;
import java.util.List;

import main.java.controller.CommandExecutor;
import main.java.model.CMakeContents;
import main.java.model.Module;
import main.java.service.ArgumentParser;
import main.java.service.CMakeParser;
import main.java.service.CMakePreprocessor;
import main.java.service.CMakeParser.Parser;
import main.java.util.GitManager;

public class Main {
	public static void main(String[] args) {
//		args = new String[] { "-enginePath=C:\\Users\\sure\\CTcode\\engine",
//				"-utilityPath=C:\\Users\\sure\\test\\utilityPath",
////				"-rootPath=\\\\10.10.10.10\\0.public\\[00_전사 공유 자료]\\[00_Revision_Packages]\\CT 2024\\2024.6", 
//				"-rootPath=C:\\Users\\sure\\test\\2024.06", "-description=하이", "-overwrite=" };

		ArgumentParser argumentParser = new ArgumentParser(args);
		GitManager.initialize(argumentParser.getEnginePath());

		try {
//			
//			List<Module> modules = preprocessModules("C:\\Users\\sure\\CTcode\\engine");
			List<Module> modules = preprocessModules(argumentParser.getEnginePath());
			new CommandExecutor(argumentParser.getRootPath(), argumentParser.getEnginePath(),
					argumentParser.getPatchUtilityPath(), argumentParser.getDescription(), argumentParser.isOverwrite(),
					modules).run();
			System.out.println("완뇨");
		} catch (Exception e) {
			System.err.println("실패: ");
			e.printStackTrace();
		}
	}

	private static List<Module> preprocessModules(String topDirectory) throws Exception {
		CMakePreprocessor cmakePreprocessor = new CMakePreprocessor();
		CMakeParser cmakeParser = new CMakeParser();
		Parser parser = cmakeParser.new Parser();

		List<Module> modules = new ArrayList<>();
		CMakeContents root = cmakePreprocessor.preprocess(topDirectory);

		parser.parseCMakeFile(root, modules);
//		System.out.println("모듈 정보 추출 완료.");
//		Module.printAllModuleNamesAndReferences(modules);

		return modules;
	}
}
