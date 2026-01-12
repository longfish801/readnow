/*
 * ISBNGetTool.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package tool

import groovy.util.logging.Slf4j
import groovy.text.SimpleTemplateEngine

/**
 * ISBN取得ツールを実行します。
 */
@Slf4j('LOG')
class ISBNGetTool extends CypressTool {
	/** ベースURL */
	String baseURL = 'https://www.amazon.co.jp/'
	/** ISBN取得用スクリプトファイル */
	String scriptFpath = 'cypress/e2e/isbn.cy.js'
	/** ISBN取得用スクリプトテンプレートファイル名 */
	String temlateFpath = 'readnow/template/isbn.cy.tmpl'
	/** 書影確認用HTMLテンプレートファイル名 */
	String coverTemplateFpath = 'readnow/template/cover.html.tmpl'
	/** 書影確認用HTMLファイル */
	String coverFpath = 'readnow/cover.html'

	/**
	 * コンストラクタ。
	 * ルートフォルダはcypressフォルダの直上フォルダを指定してください。
	 * @param rootDir ルートフォルダ
	 */
	ISBNGetTool(File rootDir){
		super(rootDir)
	}

	/**
	 * Amazonで検索した本についてISBNを取得します。
	 * @param keywordMap タイトルと検索キーワードのマップ
	 */
	List execute(Map keywordMap){
		// スクリプトファイルを生成します
		createScript(scriptFpath, temlateFpath, [reviews: keywordMap])
		// ISBN取得ツールを実行します
		// スクリプトを実行します
		List stdOuts = runScript(scriptFpath, baseURL)
		// CypressのログからISBNなど情報を抽出します
		List bookinfos = []
		stdOuts.each { String line ->
			if (line.indexOf('★') == 0){
				List splited = line.split("\t")
				bookinfos << [
					name: splited[0],
					isbn: splited[1],
					path: splited[2],
				]
			}
		}
		return bookinfos
	}

	/**
	 * 書籍情報から書影確認用HTMLファイルを生成します。
	 * @param bookInfos 書籍情報のリスト
	 * @return 書影確認用HTMLファイル
	 */
	File createCoverHTML(List bookInfos){
		File templateFile = new File(rootDir, coverTemplateFpath)
		def template = new SimpleTemplateEngine().createTemplate(templateFile)
		File coverFile = new File(rootDir, coverFpath)
		coverFile.text = template.make([bookInfos: bookInfos]).toString()
		return coverFile
	}
}
