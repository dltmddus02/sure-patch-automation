package autoPatch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import autoPatch.CMakeParser.Parser;
import autoPatch.CMakePreproccesor.CMakeContents;
import autoPatch.CMakePreproccesor.Preproccesor;

public class Main {
	public static void main(String[] args) {

		CMakeParser cmakeParser = new CMakeParser();
		Parser parser = cmakeParser.new Parser();

		CMakePreproccesor cmakePreprocessor = new CMakePreproccesor();
		Preproccesor preprocessor = cmakePreprocessor.new Preproccesor();

		List<Module> modules = new ArrayList<>(); // 결과 모듈 정보들 저장할 리스트
		String topDirectory = "C:\\Users\\sure\\CTcode\\engine";

		try {
			CMakeContents root = preprocessor.preprocess(topDirectory);
			parser.parseCMakeFile(root, modules);
			System.out.println("성공적으로 완료.");
		} catch (Exception e) {
			System.err.println("실패: " + e.getMessage());
		}

		List<String> input = new ArrayList<>();
//		input.add("참조를 알고싶은 모듈 리스트");
//		중복 방지 set
		Set<Module> output = new HashSet<>();
		parser.findDependencies(input, output, modules);
	}
}
