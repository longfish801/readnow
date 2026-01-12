/*
 * E2ETestExecutor.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package executor

import io.DraftIO
import io.PublicIO
import executor.Config as conf
import groovy.util.logging.Slf4j
import tool.E2ETestTool

/**
 * E2Eテストを実行します。
 */
@Slf4j('LOG')
class E2ETestExecutor {
	/**
	 * メイン処理です。
	 */
	static void main(String[] args){
		try {
			LOG.info('BGN ALL:E2Eテストを実行します')
			new E2ETestExecutor().execute()
			LOG.info('END ALL:E2Eテストを実行します')
		} catch (exc){
			String message = 'E2Eテストの実行に失敗しました。'
			LOG.error(message, exc)
			throw new Exception(message)
		}
	}
	
	/**
	 * メイン処理です。
	 */
	void execute(){
		// E2Eテスト用の原稿を公開原稿に変換します
		File draftDir = new File(conf.amaisbn.dir, 'readnow/draft')
		PublicDataGenerator generator = new PublicDataGenerator()
		generator.execute(draftDir, conf.docs.dir, conf.docs.pretty)
		// ISBN取得ツールを用いてE2Eテストを実行します
		def tool = new E2ETestTool(conf.amaisbn.dir)
		tool.execute()
	}
}
