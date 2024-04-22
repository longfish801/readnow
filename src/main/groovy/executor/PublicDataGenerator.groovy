/*
 * PublicDataGenerator.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package executor

import data.DraftData
import data.PublicData
import executor.Config as conf
import org.slf4j.LoggerFactory

def LOG = LoggerFactory.getLogger('PublicDataGenerator')

// 原稿フォルダ
File draftDir = conf.draft.dir
// 公開原稿ファイルの出力フォルダ
File pubDir = conf.docs.dir

try {
	LOG.info('BGN ALL:Generate public draft data')
	def drafts = new DraftData(draftDir).load()
	def pubData = new PublicData(pubDir, false)
	def pubDrafts = pubData.generate(drafts)
	pubData.save(pubDrafts)
	LOG.info('END ALL:Generate public draft data')
} catch (exc){
	String message = 'Failed to generate public draft data, check the log for details.'
	LOG.error(message, exc)
	throw new Exception(message)
}
