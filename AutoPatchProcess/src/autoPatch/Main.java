package autoPatch;
import autoPatch.Module;
import autoPatch.CMakeParser;
import autoPatch.Utils;
import autoPatch.CMakePreproccesor;
import autoPatch.CMakePreproccesor.CMakeContents;
import autoPatch.CMakePreproccesor.Preproccesor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


public class Main {
    public static void main(String[] args) {
		CMakeParser parser = new CMakeParser();
	    CMakePreproccesor preprocessor = new CMakePreproccesor();
	    Preproccesor processor = preprocessor.new Preproccesor();
		Utils utils = new Utils();
		List<Module> modules = new ArrayList<>(); // 결과 모듈 정보들 저장할 리스트
	
		String topDirectory = "C:\\Users\\sure\\CTcode\\engine";
	
	    try {
	    	CMakeContents root = processor.preprocess(topDirectory);
	    	parser.parseCMakeFile(root, utils, modules);
	        System.out.println("성공적으로 완료.");
	    } catch (Exception e) {
	        System.err.println("실패: " + e.getMessage());
	    }
	    
		List<String> input= new ArrayList<>();
//		input.add("참조를 알고싶은 모듈 리스트");
//		중복 방지 set
		Set<Module> output = new HashSet<>();
		parser.findDependencies(input, output, modules);
    }
}
