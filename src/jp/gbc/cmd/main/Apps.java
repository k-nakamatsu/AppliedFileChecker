package jp.gbc.cmd.main;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * Appsクラス
 * @author Nakamatsu
 *
 */
public class Apps extends Properties {

	/** XXX */
	public static final String PARAM_TEST = "test";
	/** XXX */
	public static final String PARAM_MAKE = "make";

	/** XXX */
	public static final String PARAM_OUTPUT = "output";
	/** XXX */
	public static final String PARAM_IGNORE = "ignore";

	/** XXX */
	public static final String PARAM_BASEPATH = "basepath";

	/** XXX */
	public static final String PARAM_FORCED = "forced";
	/** XXX */
	public static final String PARAM_DEBUG = "debug";
	/** XXX */
	public static final String PARAM_HELP = "help";
	/** XXX */
	public static final String PARAM_VERSION = "version";

	/** XXX */
	private static boolean _make = false;
	/** XXX */
	private static String[] _makeArgs = new String[0];
	/** XXX */
	private static String _ignore = StringUtils.EMPTY;
	/** XXX */
	private static String _output = StringUtils.EMPTY;
	/** XXX */
	private static boolean _test = false;
	/** XXX */
	private static String[] _testArgs = new String[0];
	/** XXX */
	private static String[] _basepaths = new String[0];
	/** XXX */
	private static boolean _forced = false;
	/** XXX */
	private static boolean _debug = false;
	/** XXX */
	private static List<String> _header = null;

	/**
	 * コンストラクタ
	 */
	private Apps() {
		super();
	}

	/**
	 * XXX
	 * @return
	 */
	public static Apps getInstance() {
		return AppsInstanceHolder.INSTANCE;
	}

	/** XXX */
	public static class AppsInstanceHolder {
		private static final Apps INSTANCE = new Apps();
	}

	/**
	 * XXX
	 * @return
	 */
	public static boolean isMake() {
		return _make;
	}

	/**
	 * XXX
	 * @param _print
	 */
	public static void setMake(boolean _make) {
		Apps._make = _make;
	}

	public static String[] getMakeArgs() {
		return _makeArgs;
	}

	public static void setMakeArgs(String[] _makeArgs) {
		Apps._makeArgs = _makeArgs;
	}

	public static String getIgnore() {
		return _ignore;
	}

	public static void setIgnore(String _ignore) {
		Apps._ignore = _ignore;
	}

	public static String getOutput() {
		return _output;
	}

	public static void setOutput(String _output) {
		Apps._output = _output;
	}

	/**
	 * XXX
	 * @return
	 */
	public static boolean isTest() {
		return _test;
	}

	/**
	 * XXX
	 * @param _set
	 */
	public static void setTest(boolean _test) {
		Apps._test = _test;
	}

	public static String[] getTestArgs() {
		return _testArgs;
	}

	public static void setTestArgs(String[] _testArgs) {
		Apps._testArgs = _testArgs;
	}

	public static String[] getBasepaths() {
		return _basepaths;
	}

	public static void setBasepaths(String[] _basepaths) {
		Apps._basepaths = _basepaths;
	}

	/**
	 * XXX
	 * @return
	 */
	public static boolean isForced() {
		return _forced;
	}

	/**
	 * XXX
	 * @param _forced
	 */
	public static void setForced(boolean _forced) {
		Apps._forced = _forced;
	}

	/**
	 * XXX
	 * @return
	 */
	public static boolean isDebug() {
		return _debug;
	}

	/**
	 * XXX
	 * @param _debug
	 */
	public static void setDebug(boolean _debug) {
		Apps._debug = _debug;
	}

	/**
	 * XXX
	 * @return
	 */
	public static List<String> getHeader() {
		return _header;
	}

	/**
	 * XXX
	 * @param header
	 */
	public static void setHeader(List<String> header) {
		Apps._header = header;
	}

	/**
	 * XXX
	 * @throws IOException
	 */
	public static void initialize() throws IOException {

	}
}
