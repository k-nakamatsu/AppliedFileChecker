package jp.gbc.cmd.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Option.Builder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import jp.gbc.cmd.item.AppliedFileItem;
import jp.gbc.cmd.manager.AppliedFileManager;
import jp.gbc.cmd.manager.AppliedFileVisitor;
import jp.gbc.cmd.util.FileUtils;
import jp.gbc.cmd.util.PathUtils;

/** \mainpage
 *
 * SetupResourceXml.java<br>
 *
 */

/**
 * AppliedFileCheckerクラス
 * @author Nakamatsu
 *
 */
public class AppliedFileChecker {
	/** XXX */
	private Log log = LogFactory.getLog(AppliedFileChecker.class);

	/** XXX */
	private Options optionsAll = new Options();
	/** XXX */
	private List<String> orderOptions = Arrays.asList("M", "o", "T", "b", "f", "d", "h", "v");

	/**
	 * コンストラクタ
	 */
	public AppliedFileChecker() {
	}

	/**
	 * start処理(メイン)
	 * @param args
	 */
	private void start(String[] args) {
		if (log.isDebugEnabled()) log.debug("## Start");
		try {

			Builder optionMake = Option.builder("M").longOpt(Apps.PARAM_MAKE).type(String.class)
					.hasArgs().argName("directory").desc("Make configuration management file of directory.");
			Builder optionTest = Option.builder("T").longOpt(Apps.PARAM_TEST).type(String.class)
					.hasArgs().argName("conf.mana.file").desc("Test the configuration management file.");

			Builder optionOutput = Option.builder("o").longOpt(Apps.PARAM_OUTPUT).type(String.class)
			.hasArg().argName("make output").desc("Make output.");
			Builder optionBasepath = Option.builder("b").longOpt(Apps.PARAM_BASEPATH).type(String.class)
					.hasArgs().argName("test basepaths").desc("Test basepaths.");

			Builder optionForced = Option.builder("f").longOpt(Apps.PARAM_FORCED).type(Boolean.class)
					.hasArg(false).desc("Turn on forced.");
			Builder optionDebug = Option.builder("d").longOpt(Apps.PARAM_DEBUG).type(Boolean.class)
					.hasArg(false).desc("Turn on debug.");
			Builder optionHelp = Option.builder("h").longOpt(Apps.PARAM_HELP).type(Boolean.class)
					.hasArg(false).desc("Print this message and exit.");
			Builder optionVersion = Option.builder("v").longOpt(Apps.PARAM_VERSION).type(Boolean.class)
					.hasArg(false).desc("Print the version information and exit.");

			// All
			optionsAll.addOption(optionMake.build());
			optionsAll.addOption(optionTest.build());

			optionsAll.addOption(optionOutput.build());
			optionsAll.addOption(optionBasepath.build());

			optionsAll.addOption(optionForced.build());
			optionsAll.addOption(optionDebug.build());
			optionsAll.addOption(optionHelp.build());
			optionsAll.addOption(optionVersion.build());

			CommandLineParser parser = new DefaultParser();

			try {
				// parse options
				CommandLine cmd = parser.parse(optionsAll, args);

				// 強制 オプション
				Apps.setForced(cmd.hasOption(Apps.PARAM_FORCED));

				// デバッグ オプション
				Apps.setDebug(cmd.hasOption(Apps.PARAM_DEBUG));

				if (cmd.hasOption(Apps.PARAM_HELP)) {
					printHelp();
					return;
				}

				if (cmd.hasOption(Apps.PARAM_VERSION)) {
					printVersion();
					return;
				}

				// 必須 オプション処理
				// 登録 or 検証 オプション ※トグル設定
				if (!cmd.hasOption(Apps.PARAM_TEST) && !cmd.hasOption(Apps.PARAM_MAKE)) {
					throw new ParseException("Missing required option: M(Make value) or T(Test value)");
				}

				if (cmd.hasOption(Apps.PARAM_TEST)) {
					String[] optValues = cmd.getOptionValues(Apps.PARAM_TEST);
					for(String file : optValues) {
						if (Files.notExists(Paths.get(PathUtils.convDirectoryPath(file)))) {
							throw new ParseException(String.format("File(=%s) does not exist.", file));
						} else if (Files.isDirectory(Paths.get(PathUtils.convDirectoryPath(file)))) {
							throw new ParseException(String.format("File(=%s) is not a file.", file));
						}
					}

					Apps.setTestArgs(optValues);
					Apps.setTest(true);

					if (cmd.hasOption(Apps.PARAM_BASEPATH)) {
						optValues = cmd.getOptionValues(Apps.PARAM_BASEPATH);
						Apps.setBasepaths(optValues);
					}

				} else { // cmd.hasOption(Apps.PARAM_MAKE)
					String[] optValues = cmd.getOptionValues(Apps.PARAM_MAKE);
					for(String dir : optValues) {
						if (Files.notExists(Paths.get(PathUtils.convDirectoryPath(dir)))) {
							throw new ParseException(String.format("Directory(=%s) does not exist.", dir));
						} else if (!Files.isDirectory(Paths.get(PathUtils.convDirectoryPath(dir)))) {
							throw new ParseException(String.format("Directory(=%s) is not a directory.", dir));
						}
					}

					Apps.setMakeArgs(optValues);
					Apps.setMake(true);

					if (cmd.hasOption(Apps.PARAM_OUTPUT)) {
						String optValue = cmd.getOptionValue(Apps.PARAM_OUTPUT);
						Apps.setOutput(optValue);
					}
				}

				// 任意 オプション処理
				if (Apps.isDebug()) {
					log.info("APPS設定を初期化しました");
				}
				Apps.initialize();

				if (Apps.isDebug()) {
					StringBuffer buf = new StringBuffer();
					buf
					.append(String.format("Apps.forced=[%s]\n", Apps.isForced()))
					.append(String.format("Apps.debug=[%s]\n", Apps.isDebug()))
					.append(String.format("Apps.make=[%s]\n", Apps.isMake()))
					.append(String.format("Apps.output=[%s]\n", Apps.getOutput().toString()))
					.append(String.format("Apps.test=[%s]\n", Apps.isTest()))
					.append(String.format("Apps.basepath=[%s]\n", Apps.getBasepaths().toString()))
					.append(String.format("user.dir=[%s]\n", System.getProperty("user.dir")))
					.append(String.format("Apps=[%s]", Apps.getInstance().toString()));
					log.info(buf.toString());
				}

			} catch (ParseException e) {
				log.error( "Parsing failed.  Reason: " + ExceptionUtils.getStackTrace(e) );
				printHelp();
				System.exit(1);
			} catch (Exception e) {
				log.error( "Perform failed.  Reason: " + ExceptionUtils.getStackTrace(e) );
				System.exit(1);
			}

			try {
				if (Apps.isMake()) {
					performMake();
				} else { // if (Apps.isTest())
					performTest();
				}
			} catch (Exception e) {
				log.error(ExceptionUtils.getStackTrace(e));
				System.exit(1);
			}

		} finally {
			if (log.isDebugEnabled()) log.debug("## End");
		}
	}

	/**
	 * performMake処理
	 * @throws Exception
	 */
	private void performMake() throws Exception {

		String[] makeArgs = Apps.getMakeArgs();

		Path output = null;
		if (!StringUtils.isEmpty(Apps.getOutput())) {
			String outputFile = Apps.getOutput();
			output = Paths.get(System.getProperty("user.dir"), outputFile);
			if (Apps.isForced()) {
				Files.deleteIfExists(output);
			} else if (Files.exists(output)) {
				throw new FileAlreadyExistsException(outputFile);
			}
		}

		int dirCount = 0;
		for (String dir : makeArgs) {

			if (PathUtils.isFolderPath(dir)) {
				dir = PathUtils.convDirectoryPath(dir);
			}
			Path basePath = Paths.get(dir);
			AppliedFileVisitor afv = new AppliedFileVisitor(basePath);
			try {
				Files.walkFileTree(basePath, afv);
			} catch (IOException e) {
				e.printStackTrace();
			}

			List<String> resultLineList = afv.getResultLineList();

			if (output != null) {
				StandardOpenOption openOption = StandardOpenOption.CREATE_NEW;
				if (dirCount > 0) {
					openOption = StandardOpenOption.APPEND;
				}

				try (BufferedWriter writer = Files.newBufferedWriter(output, StandardCharsets.UTF_8, openOption)) {
					for (String str : resultLineList) {
						// nullを渡すと、nullという文字列として書き込むようので注意
						writer.append(str);
						writer.newLine();
					}
				} catch (IOException e) {
					ExceptionUtils.printRootCauseStackTrace(e);
				}
			}
			dirCount++;
		}

	}

	private void performTest() throws Exception {
		String[] makeArgs = Apps.getTestArgs();
		String[] optBasepaths = Apps.getBasepaths();

		for (String file : makeArgs) {
			try (BufferedReader reader = Files.newBufferedReader(Paths.get(file), StandardCharsets.UTF_8)) {
				String line;
				Path basePath = null;
				int lineno = 0;
				int fileCount = 0;
				int matchCount = 0;
				int basepathCount = 0;
				while ((line = reader.readLine()) != null) {
					lineno++;
					if (Apps.isDebug() && (lineno % 200) == 0) {
						System.out.println(String.format("lineno:%05d", lineno));
					}
					if (StringUtils.startsWith(line, AppliedFileVisitor.STR_PREFIX_AFCDIR)) {
//						System.out.println(line);
						if (optBasepaths.length > 0 && optBasepaths[basepathCount].trim().length() > 0) {
							basePath = Paths.get(optBasepaths[basepathCount]);
						} else {
							basePath = Paths.get(line.substring(AppliedFileVisitor.STR_PREFIX_AFCDIR.length()));
						}
						basepathCount++;
					} else {
						fileCount++;
						AppliedFileItem appliedItem = AppliedFileManager.createAppliedLineItem(line);
						Path appliedPath = basePath.resolve(appliedItem.getRelativePath());
						if (Files.notExists(appliedPath)) {
							System.out.println(String.format("Mismatch [L.%6d] File not found.(%s)", lineno, appliedPath));
						}
						else {
							FileTime targetFileTime = Files.getLastModifiedTime(appliedPath);
							LocalDateTime targetLocalDatetime = FileUtils.convert2LocalDateTime(targetFileTime);
							long targetFileSize = Files.size(appliedPath);

							if (appliedItem.getLastModifiedTime().isEqual(targetLocalDatetime)
									&& appliedItem.getFileSize() == targetFileSize) {
								matchCount++;
								continue;
							}

							if (!appliedItem.getLastModifiedTime().isEqual(targetLocalDatetime)) {
								if (appliedItem.getLastModifiedTime().isBefore(targetLocalDatetime)) {
									System.out.println(String.format("Mismatch [L.%6d] Last modified datetime is newer.(%s)", lineno, appliedPath));
								} else {
									System.out.println(String.format("Mismatch [L.%6d] Last modified datetime is old.(%s)", lineno, appliedPath));
								}
							}

							if (appliedItem.getFileSize() != targetFileSize) {
								if (appliedItem.getFileSize() > targetFileSize) {
									System.out.println(String.format("Mismatch [L.%6d] File size is small.(%s)", lineno, appliedPath));
								} else {
									System.out.println(String.format("Mismatch [L.%6d] File size is large.(%s)", lineno, appliedPath));
								}
							}
						}
					}
				}
				BigDecimal bdMatchCount = new BigDecimal(matchCount);
				BigDecimal bdPercentage = bdMatchCount.divide(new BigDecimal(fileCount), 2, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(100)).setScale(1, BigDecimal.ROUND_HALF_UP);

				System.out.println(String.format("Number of lines:%d, Applied files:%d, Match:%d(%s%%)", lineno, fileCount, matchCount, bdPercentage.toString()));
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}

	}

	/**
	 * printHelp処理
	 */
	private void printHelp() {
		HelpFormatter help = new HelpFormatter();
		help.setOptionComparator(new Comparator<Option>() {
			@Override
			public int compare(Option o1, Option o2) {
				String s1 = ((Option) o1).getOpt();
				String s2 = ((Option) o2).getOpt();
				return orderOptions.indexOf(s1) - orderOptions.indexOf(s2);
			}
		});

		// print usage.
		help.printHelp("java -jar AppliedFileChecker.jar [options] <species>\n Options:",
				optionsAll);
	}

	/**
	 * printVersion処理
	 */
	private void printVersion() {
		log.info(String.format("AppliedFileChecker.jar version \"%s\"\n", "1.0.0"));
	}

	/**
	 * readFile処理
	 * @param datfile
	 * @return
	 * @throws IOException
	 */
	public List<CSVRecord> readFile(File datfile) throws IOException {
		try (FileInputStream fis = new FileInputStream(datfile);
				BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));) {
			CSVParser parser = CSVFormat.DEFAULT
					.parse(br);

			List<CSVRecord> records = parser.getRecords();

			return records;
		}
	}

	/**
	 * メインメソッド
	 * @param args
	 */
	public static void main(String[] args) {
		new AppliedFileChecker().start(args);
		System.exit(0);
	}

}
