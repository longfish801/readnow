/*
 * E2ETestExecutor.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package executor

import data.DraftData
import data.PublicData
import executor.Config as conf
import org.slf4j.LoggerFactory
import tool.CypressExecutor

def LOG = LoggerFactory.getLogger('ISBNGetter')

// E2Eテスト用の原稿フォルダ
File draftDir = new File(conf.amaisbn.dir, 'readnow/draft')
// 公開原稿ファイルの出力フォルダ
File pubDir = conf.docs.dir
// Cypress実行ツールのフォルダ
File amaisbnDir = conf.amaisbn.dir

try {
	LOG.info('BGN ALL:Execute E2E Test')
	// E2Eテスト用の原稿を公開原稿に変換します
	def drafts = new DraftData(draftDir).load()
	def pubData = new PublicData(pubDir, false)
	def pubDrafts = pubData.generate(drafts)
	pubData.save(pubDrafts)
	// Cypress実行ツールを用いてE2Eテストを実行します
	def tool = new CypressExecutor(amaisbnDir)
	tool.doE2ETest()
	LOG.info('END ALL:Execute E2E Test')
} catch (exc){
	String message = 'Failed to execute E2E Test, check the log for details.'
	LOG.error(message, exc)
	throw new Exception(message)
}
