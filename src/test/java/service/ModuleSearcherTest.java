package test.java.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import main.java.model.CMakeContents;
import main.java.model.Module;
import main.java.service.CMakeParser;
import main.java.service.CMakePreprocessor;
import main.java.service.ModuleSearcher;
import main.java.service.CMakeParser.Parser;

class ModuleSearcherTest {
	CMakePreprocessor cmakePreprocessor = new CMakePreprocessor();

	CMakeParser cmakeParser = new CMakeParser();
	Parser parser = cmakeParser.new Parser();
	List<Module> modules = new ArrayList<>();

	@BeforeEach
	void setUp() throws Exception {
		String topDirectory = "C:\\Users\\sure\\CTcode\\build_engine_GIT_window";
		CMakeContents root = cmakePreprocessor.preprocess(topDirectory);
		parser.parseCMakeFile(root, modules);
	}

	@ParameterizedTest
	@ValueSource(strings = "src\\ut\\CoverageEvaluator\\ProjectCoverageMerge\\CoverageMerge.cpp") // ucem
	@DisplayName("TestExecutor 추출 테스트")
	public void getTestExecutorTest(String sourceFile) {
		// given
		List<String> sourceFiles = Arrays.asList(sourceFile);
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		String actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		assertTrue(actualResult.contains("TestExecutor"));
	}

	@Test
	@DisplayName("여러 소스파일에서 TestExecutor 추출 테스트")
	public void getTestExecutorsTest() {
		// given
		List<String> sourceFiles = Arrays.asList(
				"src\\ut\\TestEngine\\RunnableExecutorSm.cpp", // uecm 소스파일
				"src\\ut\\Repository\\RimUtil.cpp"); // urim 소스파일
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		String actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		assertTrue(actualResult.contains("TestExecutor"));
//		}
	}

	@ParameterizedTest
	@ValueSource(strings = "src\\ut\\TestrunBuilderDriver\\TestrunBuilder.cpp")
	@DisplayName("TestrunBuilder 추출 테스트")
	public void getTestrunBuilderTest(String sourceFile) {
		// given
		List<String> sourceFiles = Arrays.asList(sourceFile);
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		String actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		assertEquals(actualResult, "TestrunBuilder");
	}

	@Test
	@DisplayName("TestExecutor, TestrunBuilder 동시 추출 테스트")
	public void getTestExecutorAndTestrunBuilderTest() {
		// given
		List<String> sourceFiles = Arrays.asList(
				"src\\ut\\TestEngine\\RunnableExecutorSm.cpp",  // uecm 소스파일 - TestExecutor
				"src\\ut\\Repository\\RimUtil.cpp", 			// urim 소스파일 - TestExecutor, TestrunBuilder
				"src\\ut\\Builder\\LinkLog.cpp"); 				// ubuild 소스파일 - TestrunBuidler
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		String actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		assertTrue(actualResult.contains("TestExecutor") && actualResult.contains("TestrunBuilder"));
//		}
	}

	@ParameterizedTest
	@ValueSource(strings = "src\\ut\\TestCodeParser\\TestcodeSymbolUtil.h")
	@DisplayName("헤더파일에서 TestExecutor 추출 테스트")
	public void getTestExecutorFromHeaderTest(String sourceFile) {
		// given
		List<String> sourceFiles = Arrays.asList(sourceFile);
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		String actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		assertTrue(actualResult.contains("TestExecutor"));
	}

	@ParameterizedTest
	@ValueSource(strings = "src\\ut\\CLI\\UCli.cpp")
	@DisplayName("ucli모듈에서 UCLIDriver 추출 테스트")
	public void getUCLIDriverTest(String sourceFile) {
		// given
		List<String> sourceFiles = Arrays.asList(sourceFile);
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		String actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		assertTrue(actualResult.contains("UCLIDriver"));
	}

	@ParameterizedTest
	@ValueSource(strings = "src\\ut\\Repository\\Database.cpp")
	@DisplayName("CoverageRecalculator 추출 테스트")
	public void getCoverageRecalculatorTest(String sourceFile) {
		// given
		List<String> sourceFiles = Arrays.asList(sourceFile);
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		String actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		assertTrue(actualResult.contains("CoverageRecalculator"));
	}

	@ParameterizedTest
	@ValueSource(strings = "src\\PA\\src\\TOOLS\\ArmCCInfoExtractor\\arm_cc_info_extractor.cpp")
	@DisplayName("armcc_config 추출 테스트")
	public void getArmccConfigTest(String sourceFile) {
		// given
		List<String> sourceFiles = Arrays.asList(sourceFile);
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		String actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		Set<String> expectedResult = new HashSet<>();
		expectedResult.addAll(Arrays.asList("armcc_config"));
		assertEquals(actualResult, "armcc_config");
	}

	@ParameterizedTest
	@ValueSource(strings = "src\\util\\UTIL_LIB\\cs_UTIL_hash.c")
	@DisplayName("libUTIL_LIB64 추출 테스트")
	public void getlibUTILLIB64Test(String sourceFile) {
		// given
		List<String> sourceFiles = Arrays.asList(sourceFile);
		ModuleSearcher searcher = new ModuleSearcher(modules);

		// when
		String actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);

		// then
		String expectedResult = "TestRemoteUtil cop tce TestTinyRunner TestProjectImporter MessageCodeExtractor TestCOP";
		assertEquals(actualResult, expectedResult);
	}

}
