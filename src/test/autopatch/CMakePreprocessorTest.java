package test.autopatch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import main.autopatch.CMakePreprocessor;
import main.autopatch.CMakePreprocessor.CMakeContents;
import main.autopatch.CMakePreprocessor.Preprocessor;

class CMakePreprocessorTest {

	private Preprocessor preprocessor;

	@BeforeEach
	void setUp() {
		preprocessor = new CMakePreprocessor().new Preprocessor();
	}

	@Test
	@DisplayName("루트디렉토리_전처리_테스트")
	void rootPreprocessorTest() throws IOException {
		String rootPath = "C:\\Users\\sure\\test\\engine";
		CMakeContents root = preprocessor.preprocess(rootPath);

		assertNotNull(root);
		assertEquals(rootPath, root.path);
		assertFalse(root.contents.isEmpty());
	}

	@Test
	@DisplayName("하위디렉토리_전처리_테스트")
	void subdirectoryPreprocessorTest() throws IOException {
		CMakeContents child1 = preprocessor.preprocess("C:\\Users\\sure\\test\\engine\\sub1");
		assertNotNull(child1);
		assertEquals("C:\\Users\\sure\\test\\engine\\sub1", child1.path);
		assertFalse(child1.contents.isEmpty());

		CMakeContents child2 = preprocessor.preprocess("C:\\Users\\sure\\test\\engine\\sub2");
		assertNotNull(child2);
		assertEquals("C:\\Users\\sure\\test\\engine\\sub2", child2.path);
	}

	@Test
	@DisplayName("매크로_치환_테스트")
	void macroReplacementTest() throws IOException {
		String rootPath = "C:\\Users\\sure\\test\\engine";
		CMakeContents root = preprocessor.preprocess(rootPath);

		String expectedMacroValue = "매크로";
		assertTrue(root.contents.stream().anyMatch(line -> line.contains(expectedMacroValue)));
	}

	@Test
	@DisplayName("하위디렉토리_매크로_치환_테스트")
	void subdirectoryMacroReplacementTest() throws IOException {
		CMakeContents root = preprocessor.preprocess("C:\\Users\\sure\\test\\engine");
		CMakeContents child3 = root.children.get(2);
		CMakeContents child3_2 = child3.children.get(1);
		CMakeContents child3_2_1 = child3_2.children.getFirst();

		String expectedMacroValue = "Pocosub3의 매크로mt";
		assertTrue(child3_2_1.contents.stream().anyMatch(line -> line.contains(expectedMacroValue)));
	}

	@Test
	@DisplayName("하위디렉토리_구조_테스트")
	void subdirectoryStructureTest() throws IOException {
		CMakeContents root = preprocessor.preprocess("C:\\Users\\sure\\test\\engine");
		//
		assertEquals(4, root.children.size());

		CMakeContents child3 = root.children.get(2);

		assertEquals("C:\\Users\\sure\\test\\engine\\sub3", child3.path);
		assertEquals(2, child3.children.size());
	}

}
