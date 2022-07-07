package jp.gbc.cmd.manager;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import jp.gbc.cmd.item.AppliedFileItem;
import jp.gbc.cmd.util.FileUtils;

public class AppliedFileManager {
	static final String LINEDATA_SEPARATER = ",";

	public AppliedFileManager() {
		super();
	}

	/**
	 * @param linedata
	 * @return
	 */
	public static AppliedFileItem createAppliedLineItem(String linedata) {
		String[] linedataArr = linedata.split(LINEDATA_SEPARATER);
		Path relativePath = Paths.get(linedataArr[0]);
		LocalDateTime lastModifiedTime = FileUtils.convert2LocalDateTime(linedataArr[1]);
		int fileSize = Integer.parseInt(linedataArr[2]);

		AppliedFileItem item = new AppliedFileItem(relativePath, lastModifiedTime, fileSize);
		return item;
	}

	/**
	 * AppliedLineItemの行データ表現を返す。
	 * @param item AppliedLineItem
	 * @return
	 */
	public static String toLineData(AppliedFileItem item) {
		StringBuffer buf = new StringBuffer(item.getRelativePath().toString())
				.append(LINEDATA_SEPARATER).append(FileUtils.toLocalDateTimeString(item.getLastModifiedTime()))
				.append(LINEDATA_SEPARATER).append(item.getFileSize());
		return buf.toString();

	}

}
