package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.autopatch.ArgumentParser;
import main.autopatch.CMakeParser;
import main.autopatch.CMakeParser.Parser;
import main.autopatch.CMakePreprocessor;
import main.autopatch.CommandExecutor;
import main.domain.CMakeContents;
import main.domain.Module;
import main.domain.PatchInfo;
import main.service.ModuleSearcher;
import main.util.GitManager;

public class Main {
	public static void main(String[] args) {
		ArgumentParser argumentParser = new ArgumentParser(args);

		GitManager.initialize(argumentParser.getEnginePath());

		new CommandExecutor(
				argumentParser.getRootPath(), 
				argumentParser.getEnginePath(),
				argumentParser.getPatchUtilityPath(), 
				argumentParser.getDescription(), 
				argumentParser.isOverwrite())
				.run();

		CMakePreprocessor cmakePreprocessor = new CMakePreprocessor();

		CMakeParser cmakeParser = new CMakeParser();
		Parser parser = cmakeParser.new Parser();

//		패치 대상 파일 식별
//		PatchInfo patchInfo = new PatchInfo(new File(rootPath), isOverwrite); // 패치 생성 잘못 했을 때 덮어쓰기 할거면 isOverwrite == false
		List<File> changedFiles = GitManager.getInstance().getChangedFiles(patchInfo.getProductVersion(),
				patchInfo.getPatchVersion()); // 현재 패치 경로 이름, 현재 패치 버전
//		platform platform.3

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
	}
}
