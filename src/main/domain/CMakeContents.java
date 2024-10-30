package main.domain;

import java.util.ArrayList;
import java.util.List;

public class CMakeContents {
	private List<String> contents;
	private List<CMakeContents> children;
	private String path;

	public CMakeContents() {
		this.setContents(new ArrayList<>());
		this.setChildren(new ArrayList<>());
	}

	public String getPath() {
		return path;
	}

	public void setPath(String cMakeListPath) {
		this.path = cMakeListPath;
	}

	public List<String> getContents() {
		return contents;
	}

	public void setContents(List<String> contents) {
		this.contents = contents;
	}

	public List<CMakeContents> getChildren() {
		return children;
	}

	public void setChildren(List<CMakeContents> children) {
		this.children = children;
	}

	public void addChild(CMakeContents child) {
		children.add(child);
	}

}
