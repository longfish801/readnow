/*
 * ISBNGetter.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package executor

import io.RoughIO
import executor.Config as conf
import groovy.util.logging.Slf4j
import java.awt.Desktop
import tool.ISBNGetTool

/**
 * E2Eテストを実行します。
 */
@Slf4j('LOG')
class ISBNGetter {
	/**
	 * メイン処理です。
	 */
	static void main(String[] args){
		try {
			LOG.info('BGN ALL:ISBNを取得します')
			new ISBNGetter().execute()
			LOG.info('END ALL:ISBNを取得します')
		} catch (exc){
			String message = 'ISBNの取得に失敗しました。'
			LOG.error(message, exc)
			throw new Exception(message)
		}
	}
	
	/**
	 * メイン処理です。
	 */
	void execute(){
		// 追加感想ファイルから感想リストを参照します
		RoughIO roughIO = new RoughIO(conf.rough.dir)
		def reviews = roughIO.load()
		// 検索キーワードのマップを作成します
		def tool = new ISBNGetTool(conf.amaisbn.dir)
		Map keywordMap = reviews.lowers.values().collectEntries {
			String keyword = it.keyword
			// 講談社タイガや文庫、ノベルス/ノベルズ以外はタイトルと“単行本”だけで検索します
			if (it.publisher != '講談社タイガ'
			 && ['文庫', 'ノベルス', 'ノベルズ'].every {keyword.indexOf(it) < 0}){
				keyword = it.title + ' 単行本'
			}
			// シングルクォートを含む場合はエスケープします
			if (keyword.indexOf(/'/) > 0){
				keyword = keyword.replaceAll(/\'/, /\\'/)
			}
			return [it.name, keyword]
		}
		// ISBN取得ツールを実行し、下書き感想にISBNを追記して保存します
		List bookinfos = tool.execute(keywordMap)
		bookinfos.each { Map info ->
			if (reviews.lowers["review:${info.name}"] != null){
				reviews.lowers["review:${info.name}"]['isbn'] = info.isbn
			}
		}
		roughIO.save(reviews)
		// 書影確認のためのHTMLファイルを出力し、ブラウザで開きます
		File coverFile = tool.createCoverHTML(bookinfos)
		Desktop.getDesktop().browse(coverFile.toURI())
	}
}
