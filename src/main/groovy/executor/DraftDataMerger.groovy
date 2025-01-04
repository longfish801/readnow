/*
 * DraftDataMerger.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package executor

import data.MasterData
import data.ReviewData
import data.ReviewList
import data.RnowServer
import executor.Config as conf
import groovy.util.logging.Slf4j
import io.RoughIO
import io.DraftIO
import io.github.longfish801.tpac.TpacDec

/**
 * 追加感想ファイルを原稿に反映します。
 */
@Slf4j('LOG')
class DraftDataMerger {
	/**
	 * メイン処理です。
	 */
	static void main(String[] args){
		try {
			LOG.info('BGN ALL:追加感想ファイルを原稿に反映します')
			DraftDataMerger self = new DraftDataMerger()
			self.execute(conf.rough.dir, conf.draft.dir)
			LOG.info('END ALL:追加感想ファイルを原稿に反映します')
		} catch (exc){
			String message = '追加感想ファイルを原稿に反映することに失敗しました'
			LOG.error(message, exc)
			throw new Exception(message)
		}
	}
	
	/**
	 * メイン処理です。
	 * @param roughDir 下書きフォルダ
	 * @param draftDir 原稿フォルダ
	 */
	void execute(File roughDir, File draftDir){
		// 原稿をファイルから読みこみます
		DraftIO draftIO = new DraftIO(draftDir)
		MasterData master = draftIO.loadMaster() ?: new MasterData(tag: 'master')
		RnowServer reviewsServer = draftIO.load()
		// 追加感想をファイルから読みこみます
		ReviewList newReviws = new RoughIO(roughDir).load()
		// 追加感想を原稿に反映します
		newReviws.lowers.values().each { ReviewData newReview ->
			try {
				LOG.info('追加感想を反映します newReview={}', newReview.name)
				// 感想リストに追加感想を反映します
				String yyyy = newReview.pubYearAsStr
				if (reviewsServer['reviews:' + yyyy] == null){
					reviewsServer << new ReviewList(tag: 'reviews', name: yyyy)
				}
				reviewsServer['reviews:' + yyyy] << newReview
				// マスタに追加感想を反映します
				master.merge(newReview)
			} catch (exc){
				LOG.error('追加感想の反映に失敗しました。 newReview={}', newReview.name)
				throw exc
			}
		}
		// 刊行年月順に感想をソートします
		reviewsServer.decs = reviewsServer.decs.values().collectEntries { def reviews ->
			reviews.lowers = reviews.lowers.values().sort { reviewA, reviewB ->
				reviewA.pubMonth <=> reviewA.pubMonth
			}.collectEntries { [ it.key, it ] }
			return [ reviews.key, reviews ]
		}
		// 原稿をファイルに保存します
		draftIO.saveMaster(master)
		draftIO.save(reviewsServer)
	}
}
