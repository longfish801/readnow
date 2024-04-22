/*
 * CypressExecutor.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package tool

import groovy.util.logging.Slf4j
import groovy.text.SimpleTemplateEngine

@Slf4j('LOG')
class CypressExecutor {
	/** 追加感想フォルダ */
	File dir
	/** AmazonのURL */
	String amazonURL = 'https://www.amazon.co.jp/'
	/** localhostのURL */
	String localURL = 'http://localhost/'
	/** ISBN取得用スクリプトファイル名 */
	String isbnScriptFname = 'cypress/e2e/isbn.cy.js'
	/** E2Eテスト用スクリプトファイル名 */
	String e2eScriptFname = 'cypress/e2e/spec.cy.js'
	/** 書影確認用HTMLファイル名 */
	String coverFname = 'readnow/cover.html'
	/** スクリプトテンプレートファイル名 */
	String scriptTemplateFname = 'readnow/template/isbn.cy.tmpl'
	/** 書影確認用HTMLテンプレートファイル名 */
	String coverTemplateFname = 'readnow/template/cover.html.tmpl'
	/** npxコマンドファイルのパス */
	String exe = /C:\Program Files\nodejs\npx.cmd/
	/** ツール実行コマンドのフォーマット */
	String format = '"%s" cypress run --browser chrome --spec %s --config baseUrl=%s'

	/**
	 * コンストラクタ。
	 * @param dir 追加感想フォルダ
	 */
	CypressExecutor(File dir){
		this.dir = dir
	}

	/**
	 * 追加感想からスクリプトファイルを生成します。
	 * @param keywordMap タイトルと検索キーワードのマップ
	 */
	void createScript(Map keywordMap){
		File templateFile = new File(dir, scriptTemplateFname)
		def template = new SimpleTemplateEngine().createTemplate(templateFile)
		File scriptFile = new File(dir, isbnScriptFname)
		scriptFile.text = template.make([reviews: keywordMap]).toString()
	}

	/**
	 * Cypress実行ツールを実行し、取得した書籍情報を返します。
	 * @return 書籍情報のリスト
	 */
	List getBookInfos(){
		// Cypressを実行してISBNを取得します
		String command = String.format(format, exe, isbnScriptFname, amazonURL)
		LOG.info('command={}', command)
		def process = command.execute(null, dir)
		def stdOut = new BufferedReader(new InputStreamReader(process.inputStream));
		def stdErr = new BufferedReader(new InputStreamReader(process.errorStream));
		// 標準出力をログに出力します
		List stdOuts = []
		String lineOut
		while ((lineOut = stdOut.readLine()) != null) stdOuts << lineOut
		if (stdOuts.size() > 0) LOG.info('STDOUT={}', stdOuts.join("\n"))
		// 標準エラー出力があればログに出力します
		List stdErrs = []
		String lineErr
		while ((lineErr = stdErr.readLine()) != null) stdErrs << lineErr
		if (stdErrs.size() > 0) LOG.info('STDERR={}', stdErrs.join("\n"))
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
	File createScript(List bookInfos){
		File templateFile = new File(dir, coverTemplateFname)
		def template = new SimpleTemplateEngine().createTemplate(templateFile)
		File coverFile = new File(dir, coverFname)
		coverFile.text = template.make([bookInfos: bookInfos]).toString()
		return coverFile
	}

	/**
	 * E2Eテストを実行します。
	 */
	void doE2ETest(){
		// Cypressを実行してISBNを取得します
		String command = String.format(format, exe, e2eScriptFname, localURL)
		LOG.info('command={}', command)
		def process = command.execute(null, dir)
		def stdOut = new BufferedReader(new InputStreamReader(process.inputStream));
		def stdErr = new BufferedReader(new InputStreamReader(process.errorStream));
		// 標準出力をログに出力します
		List stdOuts = []
		String lineOut
		while ((lineOut = stdOut.readLine()) != null) stdOuts << lineOut
		if (stdOuts.size() > 0) LOG.info('STDOUT={}', stdOuts.join("\n"))
		// 標準エラー出力があればログに出力します
		List stdErrs = []
		String lineErr
		while ((lineErr = stdErr.readLine()) != null) stdErrs << lineErr
		if (stdErrs.size() > 1) LOG.info('STDERR={}', stdErrs.join("\n"))
		// 全テストが成功した旨のメッセージが出力されなければ例外を投げます
		if (stdOuts.every { it.indexOf('All specs passed!') < 0 }){
			throw new Exception('Failed to E2E test.')
		}
	}
}
