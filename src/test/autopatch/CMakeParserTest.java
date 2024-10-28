package test.autopatch;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import main.autopatch.CMakeParser;
import main.autopatch.CMakeParser.Parser;
import main.autopatch.CMakePreprocessor;
import main.autopatch.CMakePreprocessor.CMakeContents;
import main.autopatch.CMakePreprocessor.Preprocessor;
import main.util.ValidationUtil;

class CMakeParserTest {
	private Preprocessor preprocessor;
	private CMakeParser cmakeParser;
	private Parser parser;
	private CMakeContents root;

	@BeforeEach
	void setUp() throws Exception {
		preprocessor = new CMakePreprocessor().new Preprocessor();
		cmakeParser = new CMakeParser();
		parser = cmakeParser.new Parser();

		String rootPath = "C:\\Users\\sure\\test\\engine";
		root = preprocessor.preprocess(rootPath);
	}

	@Test
	@DisplayName("output 타입이 EXE인 경우")
	void processAddExecutableTest() {
		List<Module> modules = new ArrayList<>();
		
		String line = "add_executable(${PROJECT_NAME} ${SRC_PROJECT})";

//		processAddExecutable(line, modules);

		String path = "C:\\Users\\sure\\test\\engine\\sub1";

	}

}
