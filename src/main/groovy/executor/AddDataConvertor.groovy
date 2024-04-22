/*
 * AddDataConvertor.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package executor

import data.AddData
import executor.Config as conf
import org.slf4j.LoggerFactory

def LOG = LoggerFactory.getLogger('AddDataGenerator')

// 追加感想フォルダ
File additionalDir = conf.additional.dir

try {
	LOG.info('BGN ALL:Convert additional data')
	// 最新の追加感想履歴ファイルを追加感想最新ファイルに変換します
	def addData = new AddData(additionalDir)
	String text = addData.latestHistory
	def newReviews = addData.convert(text)
	addData.save(newReviews)
	LOG.info('END ALL:Convert additional data')
} catch (exc){
	String message = 'Failed to convert additional data, check the log for details.'
	LOG.error(message, exc)
	throw new Exception(message)
}
