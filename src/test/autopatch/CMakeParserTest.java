package test.autopatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import main.autopatch.CMakeParser;
import main.autopatch.CMakePreprocessor;
import main.autopatch.CMakePreprocessor.CMakeContents;
import main.autopatch.CMakePreprocessor.Preprocessor;
import main.autopatch.Module;
import main.domain.Condition;
import main.service.ModuleProcessor;

class CMakeParserTest {
	private Preprocessor preprocessor;
	private CMakeParser cmakeParser;
	private CMakeParser.Parser parser;
	private Condition condition;
//	private Parser parser;
	private CMakeContents root;
	private ModuleProcessor moduleProcessor;

	@BeforeEach
	void setUp() throws Exception {
		preprocessor = new CMakePreprocessor().new Preprocessor();
		cmakeParser = new CMakeParser();
		parser = new CMakeParser().new Parser();
		condition = new Condition();
		moduleProcessor = new ModuleProcessor();

		String rootPath = "src\\test\\engine";
		root = preprocessor.preprocess(rootPath);
	}

	@Test
	@DisplayName("EXE인 경우 모듈 정보 추출 검증")
	void processAddExecutableTest() {
		List<Module> modules = new ArrayList<>();

		String line = "add_executable(Foundation ${SRC_PROJECT})";

		moduleProcessor.processAddExecutable(line, modules);

		String expectedModuleName = "Foundation";
		String actualModuleName = modules.get(0).getModuleName();

		String expectedModuleType = "EXE";
		String actualModuleType = modules.get(0).getOutputType();

		assertEquals(expectedModuleName, actualModuleName);
		assertEquals(expectedModuleType, actualModuleType);
	}

	@Test
	@DisplayName("shared library인 경우 모듈 정보 추출 검증")
	void processAddLibrarySHAREDTest() {
		List<Module> modules = new ArrayList<>();

		String line = "add_library(Foundation SHARED ${SRC_PROJECT} ${HEADER_FILES})";

		moduleProcessor.processAddLibrary(line, modules);

		String expectedModuleName = "Foundation";
		String actualModuleName = modules.get(0).getModuleName();

		String expectedModuleType = "SHARED";
		String actualModuleType = modules.get(0).getOutputType();

		assertEquals(expectedModuleName, actualModuleName);
		assertEquals(expectedModuleType, actualModuleType);
	}

	@Test
	@DisplayName("static library인 경우 모듈 정보 추출 검증")
	void processAddLibrarySTATICTest() {
		List<Module> modules = new ArrayList<>();

		String line = "add_library(${PROJECT_NAME} STATIC ${SRC_PROJECT} ${HEADER_FILES})";

		moduleProcessor.processAddLibrary(line, modules);

		assertTrue(modules.isEmpty(), "STATIC 라이브러리는 고려하지 않습니다.");
	}

	@Test
	@DisplayName("Condition if 정보 정상적으로 push되는지 검증")
	public void testStoreConditionIFTest() throws Exception {
		Method method = CMakeParser.Parser.class.getDeclaredMethod("storeConditionInfo", String.class, Condition.class);
		method.setAccessible(true);

		String ifLine = "if(CONDITION_A)";
		method.invoke(parser, ifLine, condition);

		assertEquals(1, condition.getData().size());
		assertEquals("CONDITION_A", condition.getData().peek());
	}

	@Test
	@DisplayName("Condition elseif 정보 정상적으로 push되는지 검증")
	public void testStoreConditionElseIfTest() throws Exception {
		Method method = CMakeParser.Parser.class.getDeclaredMethod("storeConditionInfo", String.class, Condition.class);
		method.setAccessible(true);

		String elseifLine = "elseif(CONDITION_A)";
		method.invoke(parser, elseifLine, condition);

		assertEquals(1, condition.getData().size());
		assertEquals("CONDITION_A", condition.getData().peek());
	}

	@Test
	@DisplayName("Condition endif/else 정상적으로 pop되는지 검증")
	public void testStoreConditionEndIfTest() throws Exception {
		condition.push("CONDITION_PRE");
		
		Method method = CMakeParser.Parser.class.getDeclaredMethod("storeConditionInfo", String.class, Condition.class);
		method.setAccessible(true);

		String elseLine = "else()";
		method.invoke(parser, elseLine, condition);
		
		assertEquals(1, condition.getData().size());

		String endifLine = "endif()";
		method.invoke(parser, endifLine, condition);

		assertEquals(0, condition.getData().size());
	}

	@Test
	@DisplayName("의존 모듈 정보 추출 검증")
	void processTargetLinkLibrariesTest() throws IOException {
	    String line = "target_link_libraries(testName PRIVATE -Wl,--start-group cs_common libUTIL_LIB64 PocoFoundationmt ucommon -Wl,--end-group ${CMAKE_DL_LIBS} )";

	    Module module = new Module(new StringBuilder("testName"), "EXE");
	    List<Module> modules = Arrays.asList(module);
	    
	    condition.push("UNIX");
 
	    moduleProcessor.processTargetLinkLibraries(line, condition, modules);
	    
	    Assertion.assertThat(module.addAffectedModule("testName")).contains("PocoFoundationmt");
	}

}
