package jp.gbc.cmd.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathUtils {

	static Pattern PTN_FOLDER_PATH = Pattern.compile("^[^\\\\/]*(\\\\{1,2}[^\\\\/]*)+$"); // 正規表現(\x2)
	static Pattern PTN_DIRECTORY_PATH = Pattern.compile("^[^\\\\/]*(/[^\\\\/]*)+$"); // 正規表現(\x2)
	static Pattern PTN_PATH_SEPARATER_FOLDER = Pattern.compile("\\\\{1,2}"); // 正規表現(\x2)
	static Pattern PTN_PATH_SEPARATER_DIRECTORY = Pattern.compile("\\/"); // 正規表現(\x2)
	static String STR_PATH_SEPARATER_FOLDER = "\\";
	static String STR_PATH_SEPARATER_DIRECTORY = "/";

	/**
	 * @param path
	 * @return
	 */
	public static boolean isFolderPath(String path) {
		Matcher matcher = PTN_FOLDER_PATH.matcher(path);
		return matcher.matches();
	}

	/**
	 * @param path
	 * @return
	 */
	public static boolean isDirectoryPath(String path) {
		Matcher matcher = PTN_DIRECTORY_PATH.matcher(path);
		return matcher.matches();
	}

	/**
	 * @param path
	 * @return
	 */
	public static String convDirectoryPath(String path) {
		Matcher matcher = PTN_PATH_SEPARATER_FOLDER.matcher(path);
		if (matcher.find()) {
			return matcher.replaceAll(STR_PATH_SEPARATER_DIRECTORY);
		} else {
			return path;
		}
	}

	/**
	 * @param path
	 * @return
	 */
	public static String convFolderPath(String path) {
		Matcher matcher = PTN_PATH_SEPARATER_DIRECTORY.matcher(path);
		if (matcher.find()) {
			return matcher.replaceAll(STR_PATH_SEPARATER_FOLDER);
		} else {
			return path;
		}
	}

}
