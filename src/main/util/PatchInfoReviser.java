package main.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;

import main.domain.PatchInfo;

/**
 * patch.info 파일을 입력한 description을 반영해서 수정해주는 메소드
 */
public class PatchInfoReviser implements Command {

	private static final String PATCH_INFO_PREFIX = "CT_";
	private static final String YYYY_MM_DD = "yyyy-MM-dd";

	private PatchInfo patchInfo;
	private String description;

	public PatchInfoReviser(PatchInfo patchInfo, String description) {
		this.patchInfo = patchInfo;
		this.description = description;
	}

	@Override
	public void execute() throws IOException {

		if (description.isBlank()) {
			// description 없음 => patch.info 그대로 사용
			return;
		}

		// patch.info 첫 줄 (e.g., CT_2024.6)
		String patchInfoStartLine = String.join(StringUtils.EMPTY, PATCH_INFO_PREFIX, patchInfo.getProductVersion(),
				StringUtils.LF);

		// 새로운 patch.info에 대한 내용 생성
		String newPatchInfo = String.join(StringUtils.LF,
				LocalDateTime.now().format(DateTimeFormatter.ofPattern(YYYY_MM_DD)), patchInfo.getDisplayPatchVersion(),
				description);

		// 만든 첫 줄 + 새로운 patch.info 내용 + 이전 patch.info 내용
		StringBuilder newPatchInfoContents = new StringBuilder(patchInfoStartLine).append(StringUtils.LF)
				.append(newPatchInfo).append(StringUtils.LF);

		for (String content : patchInfo.getPatchInfoContents()) {
			newPatchInfoContents.append(content);
			newPatchInfoContents.append(StringUtils.LF);
		}

		Files.writeString(patchInfo.getPatchInfoPath().toPath(), newPatchInfoContents.toString(),
				StandardCharsets.UTF_8);
	}
}
