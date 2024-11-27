package main.java.util;

import java.io.IOException;
import java.net.URISyntaxException;

public interface Command {
	public void execute() throws IOException, URISyntaxException, InterruptedException;
}