/*
 * RoughConvertor.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package executor

import data.ReviewList
import executor.Config as conf
import groovy.util.logging.Slf4j
import io.RoughIO

/**
 * 最新の感想履歴ファイルを追加感想ファイルへ変換します。
 */
@Slf4j('LOG')
class RoughConvertor {
	/**
	 * メイン処理です。
	 */
	static void main(String[] args){
		try {
			LOG.info('BGN:追加感想ファイルへの変換')
			RoughConvertor self = new RoughConvertor()
			self.execute(conf.rough.dir)
			LOG.info('END:追加感想ファイルへの変換')
		} catch (exc){
			String message = '追加感想ファイルへの変換に失敗しました。'
			LOG.error(message, exc)
			throw new Exception(message)
		}
	}
	
	/**
	 * メイン処理です。
	 * @param roughDir 下書きフォルダ
	 */
	void execute(File roughDir){
		// 最新の感想履歴ファイルの内容を取得します
		RoughIO roughIO = new RoughIO(roughDir)
		String text = roughIO.latestHistory
		// 感想リストに変換して追加感想ファイルとして出力します
		ReviewList reviews = ReviewList.createInstance(text)
		roughIO.save(reviews)
	}
}
