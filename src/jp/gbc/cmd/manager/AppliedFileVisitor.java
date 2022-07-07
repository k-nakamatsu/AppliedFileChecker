package jp.gbc.cmd.manager;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jp.gbc.cmd.item.AppliedFileItem;
import jp.gbc.cmd.main.Apps;

/**
 * @author Nakamatsu
 *
 */
public class AppliedFileVisitor implements FileVisitor<Path> {
	protected Path basePath;
	protected int indentSize;
	protected int fileCount;
	protected List<String> resultLineList = new ArrayList<String>();
	public static String STR_PREFIX_AFCDIR = "AFCDIR:";

	public AppliedFileVisitor(Path basePath) {
		super();
		this.basePath = basePath;
		setResult(String.format("%s%s", STR_PREFIX_AFCDIR, this.basePath));
	}

	public int getFileCount() {
		return fileCount;
	}

	public List<String> getResultLineList() {
		return resultLineList;
	}

	/**
	 * ディレクトリ内のエントリがビジットされる前に、そのディレクトリに対して呼び出されます。
	 * @param dir ディレクトリへの参照
	 * @param attrs ディレクトリの基本属性
	 * @return ビジットの結果
	 * @throws IOException 入出力エラーが発生した場合
	 * @see java.nio.file.FileVisitor#preVisitDirectory(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
	 */
	@Override
	public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		if(Apps.isDebug()) {
			print("preVisitDirectory : " + dir.getFileName());
			this.indentSize++;
		}
		return FileVisitResult.CONTINUE;
	}

	/**
	 * ディレクトリ内のファイルに対して呼び出されます。
	 * @param file ファイルへの参照
	 * @param attrs ファイルの基本属性
	 * @return ビジットの結果
	 * @throws IOException 入出力エラーが発生した場合
	 * @see java.nio.file.FileVisitor#visitFile(java.lang.Object, java.nio.file.attribute.BasicFileAttributes)
	 */
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		if(Apps.isDebug()) {
			print("visitFile : " + file.getFileName());
		}
		FileTime fileTime = Files.getLastModifiedTime(file);
		Instant instant = fileTime.toInstant();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		AppliedFileItem newItem = new AppliedFileItem(basePath.relativize(file), localDateTime, Files.size(file));
		setResult(AppliedFileManager.toLineData(newItem));
		fileCount++;
		return FileVisitResult.CONTINUE;
	}

	/**ディレクトリ内のエントリ、およびそのすべての子孫がビジットされたあとにそのディレクトリに対して呼び出されます。
	 * このメソッドは、ディレクトリの反復処理が早く完了しすぎた場合(visitFileメソッドがSKIP_SIBLINGSを返したり、
	 * ディレクトリに対する反復処理時に入出力エラーが発生したりすることにより)にも呼び出されます。
	 * @param dir ディレクトリへの参照
	 * @param exc エラーが発生せずにディレクトリの反復が完了した場合はnull、そうでない場合はディレクトリの反復が早く完了させた入出力例外
	 * @return ビジットの結果
	 * @throws IOException 入出力エラーが発生した場合
	 * @see java.nio.file.FileVisitor#postVisitDirectory(java.lang.Object, java.io.IOException)
	 */
	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		if(Apps.isDebug()) {
			this.indentSize--;
			print("postVisitDirectory : " + dir.getFileName());
		}
		return FileVisitResult.CONTINUE;
	}

	/**
	 * ビジットできなかったファイルに対して呼び出されます。
	 * このメソッドは、ファイルの属性を読み取れなかったり、ファイルが開けないディレクトリだったりなどの理由の場合に呼び出されます。
	 * @param file ファイルへの参照
	 * @param exc ファイルへのビジットを妨げた入出力例外
	 * @return ビジットの結果
	 * @throws IOException 入出力エラーが発生した場合
	 * @see java.nio.file.FileVisitor#visitFileFailed(java.lang.Object, java.io.IOException)
	 */
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		String error = String.format(" [exception=%s, message=%s]", exc.getClass(), exc.getMessage());
		print("visitFileFailed : " + file.getFileName() + error);
		return FileVisitResult.CONTINUE;
	}

	/**
	 * @param message
	 */
	protected void print(String message) {
		System.out.println(new StringBuilder().append(StringUtils.repeat(' ', indentSize)).append(message));
	}

	/**
	 * @param file
	 * @return
	 */
	protected Path getRelativePath(Path file) {
		Path relative = basePath.relativize(file);
		return relative;
	}

	protected void setResult(String line) {
		resultLineList.add(line);
		System.out.println(String.format("%s", line));
	}

}
