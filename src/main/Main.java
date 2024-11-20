package main;

import java.util.ArrayList;
import java.util.List;

import main.autopatch.ArgumentParser;
import main.autopatch.CMakeParser;
import main.autopatch.CMakeParser.Parser;
import main.autopatch.CMakePreprocessor;
import main.autopatch.CommandExecutor;
import main.domain.CMakeContents;
import main.domain.Module;
import main.util.GitManager;

public class Main {
	public static void main(String[] args) {
		CMakePreprocessor cmakePreprocessor = new CMakePreprocessor();

		CMakeParser cmakeParser = new CMakeParser();
		Parser parser = cmakeParser.new Parser();

		List<Module> modules = new ArrayList<>(); // 결과 모듈 정보들 저장할 리스트
		String topDirectory = "C:\\Users\\sure\\CTcode\\engine";

		try {

			CMakeContents root = cmakePreprocessor.preprocess(topDirectory);
			parser.parseCMakeFile(root, modules);
			System.out.println("성공적으로 완료.");
			Module.printAllModuleNamesAndReferences(modules);

		} catch (Exception e) {
			System.err.println("실패: " + e.getMessage());
		}

		////////////////////// 여기까지 모듈 정보 추출 완료

		args = new String[] {
				"-enginePath=C:\\Users\\sure\\sure-project\\engine-patchmodulefinder",
				"-utilityPath=C:\\Users\\sure\\test\\utilityPath",
//				"-rootPath=\\\\10.10.10.10\\0.public\\[00_전사 공유 자료]\\[00_Revision_Packages]\\CT 2024\\2024.6", 
				"-rootPath=C:\\Users\\sure\\test\\2024.06",
				"-description=하이",
				"-overwrite="
				};

		ArgumentParser argumentParser = new ArgumentParser(args);

		GitManager.initialize(argumentParser.getEnginePath());

		new CommandExecutor(argumentParser.getRootPath(), argumentParser.getEnginePath(),
				argumentParser.getPatchUtilityPath(), argumentParser.getDescription(), argumentParser.isOverwrite(),
				modules).run();

	}
}
