package main.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GitManager {
	private String enginePath;

	private static GitManager instance;

	private GitManager(String enginePath) {
		this.enginePath = enginePath;
	}

	public static GitManager initialize(String enginePath) {
		instance = new GitManager(enginePath);
		return instance;
	}

	public static GitManager getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Instance not yet initialized. Call initialize first.");
		}
		return instance;
	}

	public String runGitCommand(String... command) {
		StringBuilder output = new StringBuilder();

		try {
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.directory(new File(enginePath));
			pb.redirectErrorStream(true);
			Process process = pb.start();

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					output.append(line).append("\n");
				}
			}

			int exitCode = process.waitFor();
			if (exitCode != 0) {
				throw new IOException("Command exited with code " + exitCode + ": " + String.join(" ", command));
			}

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			System.err.println("Failed to run git command: " + String.join(" ", command));
		}

		return output.toString().trim();
	}

	/**
	 * HEAD 커밋의 해시 값을 반환한다.
	 * 
	 * @return HEAD commit hash
	 */
	public String findHeadCommitHash() {
		String result = runGitCommand("git", "rev-parse", "HEAD");
		System.out.println("HEAD commit hash: " + result);
		return result;
	}

	/**
	 * HEAD와 prevCommitHash 사이 변경된 파일들을 반환한다.
	 * 
	 * @param prevTagName
	 * @return changed files
	 */
	public List<String> getChangedFiles(String productName, int patchVersion) {
		List<String> changedFiles = new ArrayList<>();

		String prevTag = productName + "." + (patchVersion - 1);

		String prevTagOrCommit = existsTag(prevTag) ? prevTag : findMergeBaseCommitHash();
//		prevTag가 업슬 수 있음

		String output = runGitCommand("git", "diff", "--name-status", prevTagOrCommit, "HEAD");
		String[] lines = output.split("\n");

		for (String line : lines) {
			String[] parts = line.split("\t");
			if (parts.length >= 2) {
				String status = parts[0];
				String filePath = parts[1];
				if (shouldAddToPatch(status, filePath)) {
					changedFiles.add(enginePath + filePath);
				}
			}
		}

//		System.out.println(
//				"Changed files: " + changedFiles.stream().map(File::getPath).collect(Collectors.joining("\n")));

		return changedFiles;
	}

	/**
	 * HEAD(release) 브랜치와 master 브랜치의 merge base commit hash를 반환한다. 이번 패치에 대한 태그가 없는
	 * 경우, HEAD와 master 브랜치의 merge base commit hash를 반환한다.
	 *
	 * @return merge base commit hash
	 */
	public String findMergeBaseCommitHash() {
		String result = runGitCommand("git", "merge-base", "HEAD", "master");
		System.out.println("Merge base commit hash: " + result);
		return result;
	}

	private boolean shouldAddToPatch(String status, String filePath) {
//		List<String> filePattern = List.of("bundles/", "features/"); // tests/, releng/ 변경사항 및 기타 파일은 패치에 반영하지 않음
		List<String> validStatuses = List.of("A", "M", "R", "C"); // ADD, MODIFY, RENAME, COPY
//		filePattern.stream()
//		.anyMatch(path -> filePath.startsWith(path) 
		return validStatuses.contains(status) && Paths.get(filePath).getNameCount() > 2;
	}

	/**
	 * 새 패치에 대한 태그를 생성하고, 이전 패치보다 더 이전 태그가 남아있다면 삭제한다.
	 * 
	 * @param patchVersion
	 */
	public void updateTag(String productName, int patchVersion) {
		String newTagName = productName + "." + patchVersion;
		System.out.println("Creating tag: " + newTagName);

		runGitCommand("git", "tag", "-f", newTagName);
		if (hasRemote()) {
			runGitCommand("git", "push", "origin", "-f", newTagName);
		}

		String deleteTag = productName + "." + (patchVersion - 2);
		System.out.println("Deleting tag: " + deleteTag);
		if (existsTag(deleteTag)) {
			runGitCommand("git", "tag", "-d", deleteTag);

			if (hasRemote()) {
				runGitCommand("git", "push", "origin", ":refs/tags/" + deleteTag);
			}
		}
	}

	private boolean existsTag(String tagName) {
		if (hasRemote()) {
			String remoteOutput = runGitCommand("git", "ls-remote", "--tags", "origin");
			return Arrays.stream(remoteOutput.split("\n")).anyMatch(line -> line.contains("refs/tags/" + tagName));
		} else {
			String output = runGitCommand("git", "tag");
			return Arrays.stream(output.split("\n")).anyMatch(tag -> tag.equals(tagName));
		}
	}

	private boolean hasRemote() {
		return !runGitCommand("git", "remote").isEmpty();
	}

}
