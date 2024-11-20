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

//		File outputDirPath = new File(new File(patchUtilityPath).getParentFile(),
//				OUTPUT + "/" + patchInfo.getDisplayPatchVersion());
		// 패치 대상 파일 식별
		List<String> changedFiles = GitManager.getInstance().getChangedFiles(patchInfo.getProductVersion(),
				patchInfo.getPatchVersion());

		
		
		GitManager.getInstance().updateTag(patchInfo.getProductVersion(), patchInfo.getPatchVersion());

        List<String> changedFilePaths = new ArrayList<>();
//        for (File file : changedFiles) {
//        	changedFilePaths.add(file.getPath());
//        }
        
        System.out.println(changedFiles);
        
		ModuleSearcher moduleSearcher = new ModuleSearcher(modules);

//		List<String> changedFiles = List.of("C:\\Users\\sure\\CTcode\\engine\\src\\util\\UTIL_LIB\\cs_UTIL_hash.c");
		Set<String> resultModules = moduleSearcher.getModuleNamesBySourceFiles(changedFilePaths);

	}
}