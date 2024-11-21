package main.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Macros {
	private Stack<List<Macro>> data;

	public Macros(Stack<List<Macro>> data) {
		this.data = data;
	}

	public void push() {
		data.add(new ArrayList<>());
	}

	public void pop() {
		if (!data.isEmpty()) {
//	    	System.out.println("pop!");
			data.pop();
		}
	}

	public void showMacros() {
		System.out.println("showMacros() ");

		for (int i = data.size() - 1; i >= 0; i--) {
			List<Macro> currentList = data.get(i);

			for (Macro macro : currentList) {
				if (macro != null) {
					System.out.println("key : value = " + macro.getKey() + " : " + macro.getValue());
				}
			}
		}
	}

	public List<String> find(String key) {
		for (int i = data.size() - 1; i >= 0; i--) {
			List<Macro> currentList = data.get(i);

			for (Macro macro : currentList) {
				if (macro != null && macro.getKey().equals(key)) {
					return macro.getValue();
				}
			}
		}

		return null;
	}

	public void add(Macro macro) {
		if (macro == null) {
			return;
		}
		if (!data.isEmpty()) {
			List<Macro> currentList = data.peek();
			for (int i = 0; i < currentList.size(); i++) {
				if (currentList.get(i) != null && currentList.get(i).getKey().equals(macro.getKey())) {
					currentList.set(i, macro);
					return;
				}
			}
			currentList.add(macro);
			return;
		}
	}
}