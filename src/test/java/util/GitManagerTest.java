package test.java.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import main.java.util.GitManager;

public class GitManagerTest {
	private GitManager gitManager;
	private String testRepoPath;

	@BeforeEach
	public void setUp() throws Exception {
		testRepoPath = "C:\\Users\\sure\\CTcode\\engine";
		gitManager = GitManager.initialize(testRepoPath);

		if (!Files.exists(Paths.get(testRepoPath))) {
			throw new IOException("테스트 레포가 존재하지 않습니다.");
		}

	}

	@Test
	public void getChangedFilesTest() {
		// given
		String productName = "2024.06";
		int patchVersion = 2;

		// when
		List<String> changedFiles = GitManager.getInstance().getChangedFiles(productName, patchVersion);
//				gitManager.getInstance().getChangedFiles

		// then
		assertNotNull(changedFiles, "null 될 수 없습니다.");
		assertFalse(changedFiles.isEmpty(), "비어있을 수 없습니다.");

		System.out.println("Changed files:");
		changedFiles.forEach(System.out::println);
	}
}
