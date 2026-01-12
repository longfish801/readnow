/*
 * E2ETestTool.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package tool

import groovy.util.logging.Slf4j
import groovy.text.SimpleTemplateEngine

/**
 * E2Eテストを実行します。
 */
@Slf4j('LOG')
class E2ETestTool extends CypressTool {
	/** ベースURL */
	String baseURL = 'http://localhost/'
	/** E2Eテスト用スクリプトファイル */
	String scriptFpath = 'cypress/e2e/spec.cy.js'

	/**
	 * コンストラクタ。
	 * ルートフォルダはcypressフォルダの直上フォルダを指定してください。
	 * @param rootDir ルートフォルダ
	 */
	E2ETestTool(File rootDir){
		super(rootDir)
	}

	/**
	 * E2Eテストを実行します。
	 */
	void execute(){
		// スクリプトを実行します
		List stdOuts = runScript(scriptFpath, baseURL)
		// 全テストが成功した旨のメッセージが出力されなければ例外を投げます
		if (stdOuts.any { it.indexOf('All specs passed!') >= 0 }) return
		throw new Exception('E2Eテストの実行が失敗しました。')
	}
}
