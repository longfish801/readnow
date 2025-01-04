/*
 * PublicDataGenerator.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package executor

import data.MasterData
import data.PublicMaster
import data.PublicReviews
import data.RnowServer
import executor.Config as conf
import groovy.util.logging.Slf4j
import io.DraftIO
import io.PublicIO

/**
 * 原稿ファイルから公開原稿ファイルを生成します。
 */
@Slf4j('LOG')
class PublicDataGenerator {
	/**
	 * メイン処理です。
	 */
	static void main(String[] args){
		try {
			LOG.info('BGN ALL:原稿ファイルから公開原稿ファイルを生成します')
			PublicDataGenerator self = new PublicDataGenerator()
			self.execute(conf.draft.dir, conf.docs.dir, conf.docs.pretty)
			LOG.info('END ALL:原稿ファイルから公開原稿ファイルを生成します')
		} catch (exc){
			String message = '原稿ファイルから公開原稿ファイルを生成することに失敗しました'
			LOG.error(message, exc)
			throw new Exception(message)
		}
	}
	
	/**
	 * メイン処理です。
	 * @param draftDir 原稿フォルダ
	 * @param publicDir 公開原稿フォルダ
	 * @param pretty 公開原稿を整形するか否か
	 */
	void execute(File draftDir, File publicDir, boolean pretty){
		// 原稿をファイルから読みこみます
		DraftIO draftIO = new DraftIO(draftDir)
		MasterData draftMaster = draftIO.loadMaster()
		RnowServer reviewsServer = draftIO.load()

		// 原稿から公開原稿を生成します
		LOG.info('公開マスタのベースを生成します')
		PublicMaster publicMaster = new PublicMaster(draftMaster)
		LOG.info('公開マスタから逆索引を生成します')
		Map masterRidx = publicMaster.generateReverseIndex()
		// 公開感想を生成します
		Map publicReviewsMap = reviewsServer.decs.keySet().collectEntries { def key ->
			String yyyy = key.substring(key.length() - 4)
			LOG.info('公開感想（{}年）を生成します', yyyy)
			PublicReviews publicReviews = new PublicReviews(masterRidx)
			publicReviews.generate(reviewsServer[key])
			return [ yyyy, publicReviews ]
		}
		LOG.info('公開マスタに感想データへの索引を格納します')
		publicReviewsMap.values().each { Map publicReviews ->
			publicMaster.createReviewIndex(publicReviews)
		}

		// 公開原稿を保存します
		def publicIO = new PublicIO(publicDir, pretty)
		publicIO.save(publicReviewsMap)
		publicIO.saveMaster(publicMaster)
	}
}
