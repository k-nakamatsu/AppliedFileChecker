package jp.gbc.cmd.manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import jp.gbc.cmd.main.Apps;
import jp.gbc.cmd.util.PathUtils;

public class IgnoreManager {

	protected List<PathMatcher> pathmatcherList = new ArrayList<PathMatcher>();
	protected List<String> patternList = new ArrayList<String>();

	public IgnoreManager() {
		super();
	}

	public void load(String file) throws IOException {
		if (StringUtils.isEmpty(file)) {
			return;
		}

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(file), StandardCharsets.UTF_8)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (!StringUtils.startsWith(line, "#")) {
					line = PathUtils.convDirectoryPath(line);
//					line = StringEscapeUtils.escapeJava(line);
					if (Apps.isDebug()) {
						System.out.println(String.format("IgnoreManager.load():%s", line));
					}
					PathMatcher pm = FileSystems.getDefault().getPathMatcher("glob:"+line);
					pathmatcherList.add(pm);
					patternList.add(line);
				}
			}
		}
	}

	public boolean isIgnore(Path path, BasicFileAttributes attrs) {
		if (pathmatcherList.isEmpty()) {
			return false;
		}

		for (PathMatcher pm : pathmatcherList) {
			if (pm.matches(path)) {
				if (Apps.isDebug()) {
					String patternStr = patternList.get(pathmatcherList.indexOf(pm));
					System.out.println(String.format("IgnoreManager.isIgnore():%s / %s / %s", path.toAbsolutePath(), patternStr, pm.matches(path)));
				}
				return true;
			}
		}

		return false;
	}

}
