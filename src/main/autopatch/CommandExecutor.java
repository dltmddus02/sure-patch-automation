package main.autopatch;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import main.domain.PatchInfo;
import main.service.ModuleSearcher;
import main.util.GitManager;
import main.util.PatchInfoReviser;
import main.domain.Module;

public class CommandExecutor {
	private static final String OUTPUT = "output";

	private String rootPath;
	private String enginePath;
	private String patchUtilityPath;
	private String description;
	private boolean isOverwrite;
	private List<Module> modules;

	public CommandExecutor(String rootPath, String enginePath, String patchUtilityPath, String description,
			boolean isOverwrite, List<Module> modules) {
		this.rootPath = rootPath;
		this.enginePath = enginePath;
		this.patchUtilityPath = patchUtilityPath;
		this.description = description;
		this.isOverwrite = isOverwrite;
		this.modules = modules;
	}

	public void run() {
		PatchInfo patchInfo = new PatchInfo(new File(rootPath), isOverwrite);
		try {
			// patch.info 수정
			new PatchInfoReviser(patchInfo, description).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 패치 대상 파일 식별
		List<String> changedFiles = GitManager.getInstance().getChangedFiles(patchInfo.getProductVersion(),
				patchInfo.getPatchVersion());

		
		
		GitManager.getInstance().updateTag(patchInfo.getProductVersion(), patchInfo.getPatchVersion());

//        List<String> changedFilePaths = new ArrayList<>();
        
        System.out.println(changedFiles);
        
		ModuleSearcher moduleSearcher = new ModuleSearcher(modules);

		// 여기서 changedFiles 변경되어야 함
		Set<String> resultModules = moduleSearcher.getModuleNamesBySourceFiles(changedFiles);

	}
}