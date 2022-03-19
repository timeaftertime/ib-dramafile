package cn.milai.ib.drama.dramafile.interpreter;

import java.io.File;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.milai.common.http.Https;
import cn.milai.common.io.Files;
import cn.milai.common.io.InputStreams;
import cn.milai.ib.IBBeans;
import cn.milai.ib.conf.PathConf;
import cn.milai.ib.conf.RepoConf;
import cn.milai.ib.drama.dramafile.compiler.IBCompiler;
import cn.milai.ib.drama.dramafile.interpreter.statics.DramaMetadata;

/**
 * 剧本文件加载器
 * 2019.12.14
 * @author milai
 */
public class DramaFileLoader {

	private static final Logger LOG = LoggerFactory.getLogger(DramaFileLoader.class);

	private static final String DRAMA_PREFIX = "drama/";
	private static final String DRAMA_SUFFIX = ".drama";

	private static RepoConf repoConf = IBBeans.getBean(RepoConf.class);

	/**
	 * 加载指定的剧本片段
	 * @param dramaCode
	 * @return
	 * @throws DramaFileNotFoundException 若找不到剧本定义文件
	 */
	public static DramaMetadata loadDrama(String dramaCode) throws DramaFileNotFoundException {
		byte[] dramaBytes = null;
		try {
			dramaBytes = readDramaFile(dramaCode);
		} catch (Exception e) {
			LOG.error("获取剧本定义文件失败，dramaCode = {}, error = {}", dramaCode, ExceptionUtils.getStackTrace(e));
			throw new DramaFileNotFoundException(dramaCode, e);
		}
		return new DramaMetadata(dramaBytes);
	}

	private static byte[] readDramaFile(String clipCode) {
		byte[] dramaBytes = null;
		String path = dramaPath(clipCode);
		File file = new File(path);
		if (!file.exists()) {
			LOG.info("剧本文件 {} 不存在，尝试从远程服务器获取……", path);
			dramaBytes = Https.getFile(PathConf.dramaRepo(clipCode));
			Files.saveRethrow(path, dramaBytes);
		} else {
			dramaBytes = Files.toBytes(file);
		}
		return IBCompiler.compile(InputStreams.fromBytes(dramaBytes));
	}

	/**
	 * 获取剧本文件的本地绝对路径
	 * @param dramaCode
	 * @return
	 */
	public static String dramaPath(String dramaCode) {
		return repoConf.getLocalResourcePath() + DRAMA_PREFIX + dramaCode + DRAMA_SUFFIX;
	}

}
