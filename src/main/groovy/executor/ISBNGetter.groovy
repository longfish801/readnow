/*
 * ISBNGetter.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package executor

import data.AddData
import executor.Config as conf
import java.awt.Desktop
import org.slf4j.LoggerFactory
import tool.CypressExecutor

def LOG = LoggerFactory.getLogger('ISBNGetter')

// 追加感想フォルダ
File additionalDir = conf.additional.dir
// Cypress実行ツールのフォルダ
File amaisbnDir = conf.amaisbn.dir

try {
	LOG.info('BGN ALL:Get ISBN from Amazon')
	// 追加感想最新ファイルを参照します
	AddData addData = new AddData(additionalDir)
	def newReviws = addData.load()
	// Cypress実行ツールを用いて追加感想にISBNを追記します
	def tool = new CypressExecutor(amaisbnDir)
	Map keywordMap = newReviws.lowers.values().collectEntries {
		String keyword = it.keyword
		if (keyword.indexOf(/'/) > 0){
			keyword = keyword.replaceAll(/\'/, /\\'/)
		}
		if (it.publisher != '講談社タイガ'
		 && ['文庫', 'ノベルス', 'ノベルズ'].every {keyword.indexOf(it) < 0}){
			keyword = it.title + ' 単行本'
		}
		return [it.name, keyword]
	}
	tool.createScript(keywordMap)
	List bookinfos = tool.getBookInfos()
	bookinfos.each { Map info ->
		if (newReviws.lowers["review:${info.name}"] != null){
			newReviws.lowers["review:${info.name}"]['isbn'] = info.isbn
		}
	}
	addData.save(newReviws)
	// 書影確認のためのHTMLファイルを出力し、ブラウザで開きます
	File coverFile = tool.createScript(bookinfos)
	Desktop.getDesktop().browse(coverFile.toURI())
	LOG.info('END ALL:Get ISBN from Amazon')
} catch (exc){
	String message = 'Failed to get ISBN from Amazon, check the log for details.'
	LOG.error(message, exc)
	throw new Exception(message)
}
