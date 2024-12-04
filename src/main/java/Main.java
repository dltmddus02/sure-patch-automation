package main.java;

import main.java.controller.CommandExecutor;

public class Main {
	public static void main(String[] args) {
		args = new String[] { "enginePath=C:\\Users\\sure\\CTcode\\build_engine_GIT_window",
				"src\\util\\UTIL_LIB\\cs_UTIL_hash.c", 
				"src\\ut\\COMMON\\Args\\Args.cpp", 
				"src\\util\\POCO_LIB\\Foundation\\zlib", 
				};

		try {
			new CommandExecutor(args).run();
		} catch (Exception e) {
			System.err.println("실패: ");
			e.printStackTrace();
		}
	}
}
