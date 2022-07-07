package jp.gbc.cmd.item;

import java.nio.file.Path;
import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class AppliedFileItem {

	private Path relativePath;
	private LocalDateTime lastModifiedTime;
	private long fileSize;

	public AppliedFileItem(Path relativePath, LocalDateTime lastModifiedTime, long fileSize) {
		super();
		this.relativePath = relativePath;
		this.lastModifiedTime = lastModifiedTime;
		this.fileSize = fileSize;
	}

	/**
	 * 相対パスを返す。
	 * @return
	 */
	public Path getRelativePath() {
		return relativePath;
	}

	/**
	 * 相対パスをセットする。
	 * @param relativePath
	 */
	public void setRelativePath(Path relativePath) {
		this.relativePath = relativePath;
	}

	/**
	 * 最終更新日時(LocalDateTime)を取得する。
	 * @return
	 */
	public LocalDateTime getLastModifiedTime() {
		return lastModifiedTime;
	}

	/**
	 * 最終更新日時(LocalDateTime)を設定する。
	 * @param lastModifiedTime
	 */
	public void setLastModifiedTime(LocalDateTime lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	/**
	 * ファイルサイズを取得する。
	 * @return
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * ファイルサイズを設定する。
	 * @param fileSize
	 */
	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * AppliedLineItemの文字列表現を返す。
	 * @return このAppliedLineItemの文字列表現。
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}


}
