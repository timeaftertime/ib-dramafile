package cn.milai.ib.drama.dramafile.interpreter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import cn.milai.ib.drama.Drama;
import cn.milai.ib.drama.DramaResolver;
import cn.milai.ib.drama.dramafile.interpreter.runtime.Clip;
import cn.milai.ib.drama.dramafile.interpreter.runtime.DramaSpace;
import cn.milai.ib.drama.dramafile.interpreter.statics.DramaMetadata;

/**
 * 解析剧本定义文件的剧本解析器
 * @author milai
 * @date 2020.03.05
 */
@Order(2)
@Component
public class DramaFileDramaResolver implements DramaResolver {

	@Override
	public Drama resolve(String dramaCode) {
		DramaMetadata drama = null;
		try {
			drama = DramaFileLoader.loadDrama(dramaCode);
		} catch (DramaFileNotFoundException e) {
			return null;
		}
		Clip clip = new Clip(drama);
		DramaSpace dramaSpace = new DramaSpace(clip);
		return new DramaFileDrama(dramaSpace);
	}

}
