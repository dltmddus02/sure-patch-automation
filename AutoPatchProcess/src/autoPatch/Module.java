package autoPatch;

import java.util.ArrayList;
import java.util.List;

public class Module {
	StringBuilder moduleName; // 모듈 배포 이름
	String outputType; // 출력 파일 종류 (STATIC, SHARED, exe)
	List<String> sourceFiles; // 소스 파일 리스트
	List<String> affectedModules;

	public Module(StringBuilder moduleName, String outputType) {
		this.moduleName = moduleName;
		this.outputType = outputType;
		this.sourceFiles = new ArrayList<>(); // default는 빈 배열
		this.affectedModules = new ArrayList<>(); // default는 빈 배열
	}

	public void addSourceFile(String sourceFile) {
		sourceFiles.add(sourceFile);
	}

	public void addAffectedModule(String library) {
		affectedModules.add(library);
	}

}
