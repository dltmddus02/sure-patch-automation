package test.java.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import main.java.model.Condition;
import main.java.model.Module;
import main.java.service.CMakeParser;
import main.java.service.ModuleInfoExtractor;

class CMakeParserTest {
	private CMakeParser.Parser parser;
	private Condition condition;
	private ModuleInfoExtractor moduleProcessor;

	@BeforeEach
	void setUp() throws Exception {
		parser = new CMakeParser().new Parser();
		condition = new Condition();
		moduleProcessor = new ModuleInfoExtractor();
	}

	@Test
	@DisplayName("EXE인 경우 모듈 정보 추출 검증")
	void processAddExecutableTest() {
		List<Module> modules = new ArrayList<>();

		String line = "add_executable(Foundation ${SRC_PROJECT})";
		String currentPath = "";

		moduleProcessor.processAddExecutable(line, modules, currentPath);

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
		String currentPath = "";

		moduleProcessor.processAddLibrary(line, modules, currentPath);

		String expectedModuleName = "Foundation";
		String actualModuleName = modules.get(0).getModuleName();

		String expectedModuleType = "SHARED";
		String actualModuleType = modules.get(0).getOutputType();

		assertEquals(expectedModuleName, actualModuleName);
		assertEquals(expectedModuleType, actualModuleType);
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

		List<Module> modules = new ArrayList<>();
		modules.add(module);

		condition.push("UNIX");

		moduleProcessor.processTargetLinkLibraries(line, condition, modules);

		assertTrue(module.getAffectedModules().contains("PocoFoundationmt"));
	}

}
