package test.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import main.autopatch.CMakeParser;
import main.autopatch.CMakeParser.Parser;
import main.autopatch.CMakePreprocessor;
import main.domain.CMakeContents;
import main.domain.Module;
import main.service.ModuleSearcher;

class ModuleSearcherTest {
	CMakePreprocessor cmakePreprocessor = new CMakePreprocessor();

	CMakeParser cmakeParser = new CMakeParser();
	Parser parser = cmakeParser.new Parser();
	List<Module> modules = new ArrayList<>();

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		String topDirectory = "C:\\Users\\sure\\CTcode\\engine";
		CMakeContents root = cmakePreprocessor.preprocess(topDirectory);
		parser.parseCMakeFile(root, modules);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@ParameterizedTest
	@ValueSource(strings = "C:\\Users\\sure\\CTcode\\engine\\src\\ut\\TestrunExecutor\\TestrunExecutor.cpp")
	@DisplayName("TestExecutor 추출 테스트")
	public void getTestExecutorTest(String sourceFile) {
		// given
		List<String> sourceFiles = Arrays.asList(sourceFile);
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		Map<String, List<String>> actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		Map<String, List<String>> expectedResult = new HashMap<>();
		expectedResult.put("C:\\Users\\sure\\CTcode\\engine\\src\\ut\\TestrunExecutor\\TestrunExecutor.cpp",
				Arrays.asList("TestExecutor"));
		assertEquals(actualResult, expectedResult);
	}

	@ParameterizedTest
	@ValueSource(strings = "C:\\Users\\sure\\CTcode\\engine\\src\\ut\\TestrunBuilderDriver\\TestrunBuilder.cpp")
	@DisplayName("TestrunBuilder 추출 테스트")
	public void getTestrunBuilderTest(String sourceFile) {
		// given
		List<String> sourceFiles = Arrays.asList(sourceFile);
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		Map<String, List<String>> actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		Map<String, List<String>> expectedResult = new HashMap<>();
		expectedResult.put("C:\\Users\\sure\\CTcode\\engine\\src\\ut\\TestrunBuilderDriver\\TestrunBuilder.cpp",
				Arrays.asList("TestrunBuilder"));
		assertEquals(actualResult, expectedResult);
	}

	@ParameterizedTest
	@ValueSource(strings = "C:\\Users\\sure\\CTcode\\engine\\src\\ut\\CLI\\UCli.cpp")
	@DisplayName("ucli모듈에서 UCLIDriver 추출 테스트")
	public void getUCLIDriverTest(String sourceFile) {
		// given
		List<String> sourceFiles = Arrays.asList(sourceFile);
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		Map<String, List<String>> actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		Map<String, List<String>> expectedResult = new HashMap<>();
		expectedResult.put("C:\\Users\\sure\\CTcode\\engine\\src\\ut\\CLI\\UCli.cpp", Arrays.asList("UCLIDriver"));
		assertTrue(actualResult.getOrDefault(sourceFile, Collections.emptyList()).contains("UCLIDriver"));
	}

	@ParameterizedTest
	@ValueSource(strings = "C:\\Users\\sure\\CTcode\\engine\\src\\ut\\Repository\\Database.cpp")
	@DisplayName("CoverageRecalculator 추출 테스트")
	public void getCoverageRecalculatorTest(String sourceFile) {
		// given
		List<String> sourceFiles = Arrays.asList(sourceFile);
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		Map<String, List<String>> actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		Map<String, List<String>> expectedResult = new HashMap<>();
		expectedResult.put("C:\\Users\\sure\\CTcode\\engine\\src\\ut\\Repository\\Database.cpp",
				Arrays.asList("CoverageRecalculator"));
		assertTrue(actualResult.getOrDefault(sourceFile, Collections.emptyList()).contains("CoverageRecalculator"));
	}

	@ParameterizedTest
	@ValueSource(strings = "C:\\Users\\sure\\CTcode\\engine\\src\\PA\\src\\TOOLS\\ArmCCInfoExtractor\\arm_cc_info_extractor.cpp")
	@DisplayName("armcc_config 추출 테스트")
	public void getArmccConfigTest(String sourceFile) {
		// given
		List<String> sourceFiles = Arrays.asList(sourceFile);
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		Map<String, List<String>> actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		Map<String, List<String>> expectedResult = new HashMap<>();
		expectedResult.put(
				"C:\\Users\\sure\\CTcode\\engine\\src\\PA\\src\\TOOLS\\ArmCCInfoExtractor\\arm_cc_info_extractor.cpp",
				Arrays.asList("armcc_config"));
		assertEquals(actualResult, expectedResult);
	}

	@ParameterizedTest
	@ValueSource(strings = "C:\\Users\\sure\\CTcode\\engine\\src\\util\\UTIL_LIB\\cs_UTIL_hash.c")
	@DisplayName("libUTIL_LIB64 추출 테스트")
	public void getlibUTILLIB64Test(String sourceFile) {
		// given
		List<String> sourceFiles = Arrays.asList(sourceFile);
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		Map<String, List<String>> actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		Map<String, List<String>> expectedResult = new HashMap<>();
		expectedResult.put("C:\\Users\\sure\\CTcode\\engine\\src\\util\\UTIL_LIB\\cs_UTIL_hash.c",
				Arrays.asList("TestRemoteUtil", "cop", "tce", "TestTinyRunner", "TestProjectImporter",
						"MessageCodeExtractor", "TestCOP"));
		assertEquals(actualResult, expectedResult);
	}

}