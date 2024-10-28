<<<<<<<< HEAD:src/main/Main.java
package main;

========
>>>>>>>> 7f544c9421adf040b07ba7d98e3753ddb8004a2e:AutoPatchProcess/src/main/java/Main.java
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

<<<<<<<< HEAD:src/main/Main.java
import main.autopatch.Module;
import main.autopatch.CMakeParser;
import main.autopatch.CMakeParser.Parser;
import main.autopatch.CMakePreprocessor;
import main.autopatch.CMakePreprocessor.CMakeContents;
import main.autopatch.CMakePreprocessor.Preprocessor;
========
import autopatch.Module;
import autopatch.CMakeParser;
import autopatch.CMakeParser.Parser;
import autopatch.CMakePreproccesor;
import autopatch.CMakePreproccesor.CMakeContents;
import autopatch.CMakePreproccesor.Preproccesor;
>>>>>>>> 7f544c9421adf040b07ba7d98e3753ddb8004a2e:AutoPatchProcess/src/main/java/Main.java

public class Main {
	public static void main(String[] args) {

		CMakePreprocessor cmakePreprocessor = new CMakePreprocessor();
		Preprocessor preprocessor = cmakePreprocessor.new Preprocessor();

		CMakeParser cmakeParser = new CMakeParser();
		Parser parser = cmakeParser.new Parser();

		List<Module> modules = new ArrayList<>(); // 결과 모듈 정보들 저장할 리스트
		String topDirectory = "C:\\Users\\sure\\CTcode\\engine";

		try {

			CMakeContents root = preprocessor.preprocess(topDirectory);
			parser.parseCMakeFile(root, modules);
			System.out.println("성공적으로 완료.");
//			System.out.println(System.getenv("CMAKE_SOURCE_DIR"));
			} catch (Exception e) {
			System.err.println("실패: " + e.getMessage());
		}

		List<String> input = new ArrayList<>();
//		중복 방지 set
		Set<Module> output = new HashSet<>();
		parser.findDependencies(input, output, modules);
	}
}
