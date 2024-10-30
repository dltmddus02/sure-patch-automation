package main.domain;

import java.util.Stack;

public class Condition {
	private String value;
	private Stack<String> data = new Stack<>();

	public void push(String value) {
		data.add(value);
	}

	public void pop() {
		if (!data.isEmpty()) {
			data.pop();
		}
	}

	public String getValue() {
		return value;
	}

	public Stack<String> getData() {
		return data;
	}

}
