package main.autopatch;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;

import main.domain.PatchInfo;
import main.util.GitManager;
import main.util.PatchInfoReviser;

public class CommandExecutor {
	private static final String OUTPUT = "output";

	private String rootPath;
	private String enginePath;
	private String patchUtilityPath;
	private String description;
	private boolean isOverwrite;

	public CommandExecutor(String rootPath, String enginePath, String patchUtilityPath, String description,
			boolean isOverwrite) {
		this.rootPath = rootPath;
		this.enginePath = enginePath;
		this.patchUtilityPath = patchUtilityPath;
		this.description = description;
		this.isOverwrite = isOverwrite;
	}

	public void run() {
		PatchInfo patchInfo = new PatchInfo(new File(rootPath), isOverwrite);
		try {
			// patch.info 수정
			new PatchInfoReviser(patchInfo, description).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

		File outputDirPath = new File(new File(patchUtilityPath).getParentFile(),
				OUTPUT + "/" + patchInfo.getDisplayPatchVersion());
		// 패치 대상 파일 식별
		List<File> changedFiles = GitManager.getInstance().getChangedFiles(patchInfo.getProductVersion(),
				patchInfo.getPatchVersion());
	}
}