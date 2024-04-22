/*
 * DraftDataMerger.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package executor

import data.AddData
import data.DraftData
import executor.Config as conf
import org.slf4j.LoggerFactory

def LOG = LoggerFactory.getLogger('DraftDataMerger')

// 追加感想フォルダ
File additionalDir = conf.additional.dir
// 原稿フォルダ
File draftDir = conf.draft.dir

try {
	LOG.info('BGN ALL:Merge latest reviews to draft data')
	// 追加感想を原稿に反映します
	def draftData = new DraftData(draftDir)
	def drafts = draftData.load()
	def newReviws = new AddData(additionalDir).load()
	newReviws.lowers.values().each { def newReview ->
		try {
			LOG.info('追加感想を反映します newReview={}', newReview.name)
			draftData.merge(newReview, drafts)
		} catch (exc){
			LOG.error('追加感想の反映に失敗しました。 review={}', newReview)
			throw exc
		}
	}
	draftData.save(drafts)
	LOG.info('END ALL:Merge latest reviews to draft data')
} catch (exc){
	String message = 'Failed to merge latest reviews to draft data, check the log for details.'
	LOG.error(message, exc)
	throw new Exception(message)
}
