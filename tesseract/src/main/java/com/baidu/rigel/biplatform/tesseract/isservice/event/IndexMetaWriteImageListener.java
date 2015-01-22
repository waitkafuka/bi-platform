package com.baidu.rigel.biplatform.tesseract.isservice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import com.baidu.rigel.biplatform.tesseract.util.isservice.LogInfoConstants;

/**
 * IndexMetaWriteImageListener 处理索引元数据更新事件
 * @author lijin
 *
 */
public class IndexMetaWriteImageListener implements
		ApplicationListener<IndexMetaWriteImageEvent> {
	/**
     * LOGGER
     */
    private static final Logger LOGGER = LoggerFactory
        .getLogger(IndexMetaWriteImageListener.class);

	@Override
	public void onApplicationEvent(IndexMetaWriteImageEvent event) {
		LOGGER.info(String.format(
				LogInfoConstants.INFO_PATTERN_ON_LISTENER_BEGIN,
				"IndexMetaWriteImageListener.onApplicationEvent", event));
		if (event == null || event.getIdxMeta() == null) {
			LOGGER.info(String.format(
					LogInfoConstants.INFO_PATTERN_FUNCTION_EXCEPTION,
					"IndexMetaWriteImageListener.onApplicationEvent", event));
			throw new IllegalArgumentException();
		}

		LOGGER.info(String.format(
				LogInfoConstants.INFO_PATTERN_ON_LISTENER_END,
				"IndexMetaWriteImageListener.onApplicationEvent", event));

	}

}
