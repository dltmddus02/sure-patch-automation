//package test.java.service;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//
//import main.java.service.ModuleSearcher;
//
//class ModuleInfoExtractorTest {
//
//	@BeforeAll
//	static void setUpBeforeClass() throws Exception {
//	}
//
//	@AfterAll
//	static void tearDownAfterClass() throws Exception {
//	}
//
//	@BeforeEach
//	void setUp() throws Exception {
//	}
//
//	@AfterEach
//	void tearDown() throws Exception {
//	}
//
//	@ParameterizedTest
//	@ValueSource(strings = "")
//	@DisplayName("GLOB_RECURSE 옵션 사용하는 Data모듈의 소스파일 추출 테스트")
//	public void getTestExecutorTest(String sourceFile) {
//		// given
//		List<String> sourceFiles = Arrays.asList(sourceFile);
//		ModuleSearcher searcher = new ModuleSearcher(modules);
//
//		// when
//		String actualResult = searcher.getModuleNamesBySourceFiles(sourceFiles);
//
//		// then
//		assertTrue(actualResult.contains("TestExecutor"));
//	}
//
//}
