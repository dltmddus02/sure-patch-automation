package main.controller;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import main.model.Module;
import main.model.PatchInfo;
import main.service.ModuleSearcher;
import main.util.GitManager;
import main.util.PatchInfoReviser;

public class CommandExecutor {
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
		try {
			// patch.info 생성 및 수정
			PatchInfo patchInfo = revisePatchInfo();

			// 변경된 소스파일 검색
			List<String> changedFiles = findChangedFiles(patchInfo);

			System.out.println("changedFiles: " + changedFiles);

			// 변경된 소스파일로 모듈 찾기
			Set<String> resultModules = extractModulesByChangedFiles(changedFiles);
//			Set<String> resultModules = extractModulesByChangedFiles(
//					Arrays.asList("C:\\Users\\sure\\CTcode\\engine\\src\\ut\\TestEngine\\RunnableExecutorSm.cpp"));

			System.out.println("resultModules: " + resultModules);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private PatchInfo revisePatchInfo() throws IOException {
		PatchInfo patchInfo = new PatchInfo(new File(rootPath), isOverwrite);
		new PatchInfoReviser(patchInfo, description).execute();
		return patchInfo;
	}

	private List<String> findChangedFiles(PatchInfo patchInfo) {
		return GitManager.getInstance().getChangedFiles(patchInfo.getProductVersion(), patchInfo.getPatchVersion());
	}

	private Set<String> extractModulesByChangedFiles(List<String> changedFiles) {
		ModuleSearcher moduleSearcher = new ModuleSearcher(modules);
		return moduleSearcher.getModuleNamesBySourceFiles(changedFiles, rootPath);
	}

}