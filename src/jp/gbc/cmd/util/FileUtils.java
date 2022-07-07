package jp.gbc.cmd.util;

import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

/**
 * @author Nakamatsu
 *
 */
public class FileUtils {
	public static final DateTimeFormatter DATETIME_FORMAT_BASE = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

	/**
	 * FileTimeの値をLocalDateTimeにしてから変換した日時文字列(DATETIME_FORMAT_BASEフォーマット)を返す。
	 * @param filetime 日時文字列にするFileTimeの値
	 * @return 日時文字列(DATETIME_FORMAT_BASEフォーマット)
	 */
	public static String toLocalDateTimeString(FileTime filetime) {
		LocalDateTime localDatetime = convert2LocalDateTime(filetime);
		return DATETIME_FORMAT_BASE.format(localDatetime);
	}

	/**
	 * LocalDateTimeの値を変換した日時文字列(DATETIME_FORMAT_BASEフォーマット)を返す。
	 * @param localDatetime 日時文字列にするLocalDateTimeの値
	 * @return 日時文字列(DATETIME_FORMAT_BASEフォーマット)
	 */
	public static String toLocalDateTimeString(LocalDateTime localDatetime) {
		return DATETIME_FORMAT_BASE.format(localDatetime);
	}

	/**
	 * 日時文字列を変換したLocalDateTimeの値を返す。
	 * @param datetime LocalDateTimeに変換する日時文字列(DATETIME_FORMAT_BASEフォーマット)
	 * @return LocalDateTimeの値
	 */
	public static LocalDateTime convert2LocalDateTime(String datetime) {
		TemporalAccessor parsed = DATETIME_FORMAT_BASE.parse(datetime);
		LocalDateTime localDatetime = LocalDateTime.from(parsed);
		return localDatetime;
	}

	/**
	 * FileTimeの値を変換したLocalDateTimeの値を返す。
	 * @param filetime LocalDateTimeに変換するFileTimeの値
	 * @return LocalDateTimeの値
	 */
	public static LocalDateTime convert2LocalDateTime(FileTime filetime) {
		Instant instant = filetime.toInstant();
		LocalDateTime localDatetime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		localDatetime = convert2LocalDateTime(toLocalDateTimeString(localDatetime));
		return localDatetime;
	}
}
