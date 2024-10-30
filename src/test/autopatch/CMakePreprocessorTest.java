package test.autopatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import main.autopatch.CMakePreprocessor;
import main.domain.CMakeContents;

class CMakePreprocessorTest {

	private CMakePreprocessor cmakePreprocessor;

	@BeforeEach
	void setUp() {
		cmakePreprocessor = new CMakePreprocessor();
	}

	@Test
	@DisplayName("루트디렉토리_전처리_테스트")
	void rootPreprocessorTest() throws IOException {
		String rootPath = "src\\test\\engine";

		Path fullPath = Paths.get("").toAbsolutePath().resolve(rootPath);

		CMakeContents root = cmakePreprocessor.preprocess(fullPath.toString());

		assertNotNull(root);
		assertEquals(fullPath.toString(), root.getPath());
		assertFalse(root.getContents().isEmpty());

	}

	@Test
	@DisplayName("하위디렉토리_전처리_테스트")
	void subdirectoryPreprocessorTest() throws IOException {
		CMakeContents child1 = cmakePreprocessor.preprocess("src\\test\\engine\\sub1");
		assertNotNull(child1);
		assertEquals("src\\test\\engine\\sub1", child1.getPath());
		assertFalse(child1.getContents().isEmpty());

		CMakeContents child2 = cmakePreprocessor.preprocess("src\\test\\engine\\sub2");
		assertNotNull(child2);
		assertEquals("src\\test\\engine\\sub2", child2.getPath());
	}

	@Test
	@DisplayName("매크로_치환_테스트")
	void macroReplacementTest() throws IOException {
		String rootPath = "src\\test\\engine";
		CMakeContents root = cmakePreprocessor.preprocess(rootPath);

		String expectedMacroValue = "매크로";
		assertTrue(root.getContents().stream().anyMatch(line -> line.contains(expectedMacroValue)));
	}

	@Test
	@DisplayName("하위디렉토리_매크로_치환_테스트")
	void subdirectoryMacroReplacementTest() throws IOException {
		CMakeContents root = cmakePreprocessor.preprocess("src\\test\\engine");
		CMakeContents child3 = root.getChildren().get(2);
		CMakeContents child3_2 = child3.getChildren().get(1);
		CMakeContents child3_2_1 = child3_2.getChildren().getFirst();

		String expectedMacroValue = "Pocosub3의 매크로mt";
		assertTrue(child3_2_1.getContents().stream().anyMatch(line -> line.contains(expectedMacroValue)));
	}

	@Test
	@DisplayName("하위디렉토리_구조_테스트")
	void subdirectoryStructureTest() throws IOException {
		CMakeContents root = cmakePreprocessor.preprocess("src\\test\\engine");

		assertEquals(4, root.getChildren().size());

		CMakeContents child3 = root.getChildren().get(2);

		assertEquals("src\\test\\engine\\sub3", child3.getPath());
		assertEquals(2, child3.getChildren().size());
	}

}
