package test.autopatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import main.autopatch.Module;
import main.autopatch.CMakeParser;
import main.autopatch.CMakeParser.Parser;
import main.autopatch.CMakePreprocessor;
import main.autopatch.CMakePreprocessor.CMakeContents;
import main.autopatch.CMakePreprocessor.Preprocessor;
import main.service.ModuleProcessor;

class CMakeParserTest {
	private Preprocessor preprocessor;
	private CMakeParser cmakeParser;
	private Parser parser;
	private CMakeContents root;
	private ModuleProcessor moduleProcessor;

	@BeforeEach
	void setUp() throws Exception {
		preprocessor = new CMakePreprocessor().new Preprocessor();
		cmakeParser = new CMakeParser();
		parser = cmakeParser.new Parser();
		moduleProcessor = new ModuleProcessor();

		String rootPath = "C:\\Users\\sure\\test\\engine";
		root = preprocessor.preprocess(rootPath);
	}

	@Test
	@DisplayName("output 타입이 EXE인 경우")
	void processAddExecutableTest() {
		List<Module> modules = new ArrayList<>();

		String line = "add_executable(${PROJECT_NAME} ${SRC_PROJECT})";

		moduleProcessor.processAddExecutable(line, modules);

		String expectedModuleType = "EXE";
		String actualModuleType = modules.get(0).getOutputType();

		assertEquals(expectedModuleType, actualModuleType);
	}

	@Test
	@DisplayName("output 타입이 shared library인 경우")
	void processAddLibrarySHAREDTest() {
		List<Module> modules = new ArrayList<>();

		String line = "add_library(${PROJECT_NAME} SHARED ${SRC_PROJECT} ${HEADER_FILES})";

		moduleProcessor.processAddLibrary(line, modules);

		String expectedModuleType = "SHARED";
		String actualModuleType = modules.get(0).getOutputType();

		assertEquals(expectedModuleType, actualModuleType);
	}

	@Test
	@DisplayName("output 타입이 static library인 경우")
	void processAddLibrarySTATICTest() {
		List<Module> modules = new ArrayList<>();

		String line = "add_library(${PROJECT_NAME} STATIC ${SRC_PROJECT} ${HEADER_FILES})";

		moduleProcessor.processAddLibrary(line, modules);
		
		assertTrue(modules.isEmpty(), "STATIC 라이브러리는 고려하지 않습니다.");
	}

}
