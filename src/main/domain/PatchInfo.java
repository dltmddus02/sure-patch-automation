package main.domain;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class PatchInfo {
	private static final int PREV_CONTENTS_START_INDEX = 2;

	private File patchInfoPath;
	private String productVersion;

	private List<String> patchInfoContents;

	private int newPatchVersion;

	public PatchInfo(File rootPath, boolean overwrite) {
		this.patchInfoPath = getPatchInfoFile(rootPath);
		this.productVersion = rootPath.getName();

		load(overwrite);
	}

	private static final File getPatchInfoFile(File rootPath) { // 이 경로에 patch.info 생성 괜찮은지
		return Paths.get(rootPath.getAbsolutePath(), "patch", "common", "plugins", "patch.info").toFile();
	}

	private void load(boolean overwrite) {
		// patch.info 파일 내용 로드
		String patchInfo = StringUtils.EMPTY;
		try {
			if (!patchInfoPath.createNewFile()) {
				patchInfo = Files.readString(patchInfoPath.toPath(), StandardCharsets.UTF_8);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		List<String> patchInfoLines = patchInfo.lines().toList();

		// [patchInfoContents] 이전 patch.info 내용 로드
		this.patchInfoContents = extractPrevContents(patchInfoLines, overwrite);

		// [patchVersion] 가장 최신 패치 버전 로드해서 현재 패치 버전 만들기
		this.newPatchVersion = extractPatchVersion(patchInfoContents);
	}

	/**
	 * 덮어쓰는 경우와 새로 생성하는 경우의 이전 patch.info 내용을 다르게 만들어줌
	 */
	private List<String> extractPrevContents(List<String> patchInfoLines, boolean overwrite) {
		if (patchInfoLines.isEmpty() || patchInfoLines.get(0).isBlank()) {
			// 이전 patch.info가 비어있는 경우 빈 배열 리턴
			return new ArrayList<>();
		}

		if (overwrite) {
			// 덮어쓰는 경우에는 제품 정보를 포함한 첫 줄 + 이전 패치 내용을 제외하고 반환
			return patchInfoLines.subList(PREV_CONTENTS_START_INDEX, patchInfoLines.size()).stream()
					.dropWhile(str -> !str.isBlank()).toList();
		} else {
			// 새로 생성할 때에는 제품 정보를 포함한 첫 줄을 제외하고 반환
			return patchInfoLines.subList(1, patchInfoLines.size());
		}
	}

	private int extractPatchVersion(List<String> patchInfoContents) {
		String patchVersionStr = patchInfoContents.stream().filter(line -> line.startsWith("V")).findFirst()
				.orElse(StringUtils.EMPTY);

		int patchVersion = 1;

		if (!patchVersionStr.isEmpty()) {
			patchVersion = Integer.parseInt(patchVersionStr.substring(1).trim());
			patchVersion++;
		}

		return patchVersion;
	}

	public File getPatchInfoPath() {
		return patchInfoPath;
	}

	public String getProductVersion() {
		return productVersion;
	}

	public int getPatchVersion() {
		return newPatchVersion;
	}

	public String getDisplayPatchVersion() {
		return "V" + newPatchVersion;
	}

	public List<String> getPatchInfoContents() {
		return patchInfoContents;
	}
}
