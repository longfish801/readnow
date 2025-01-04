/*
 * CypressTool.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package tool

import groovy.util.logging.Slf4j
import groovy.text.SimpleTemplateEngine

/**
 * Cypressを利用します。
 */
@Slf4j('LOG')
class CypressTool {
	/** ルートフォルダ */
	File rootDir
	/** npxコマンドファイルのパス */
	String exe = /C:\Program Files\nodejs\npx.cmd/
	/** ツール実行コマンドのフォーマット */
	String format = '"%s" cypress run --browser chrome --spec %s --config baseUrl=%s'

	/**
	 * コンストラクタ。
	 * ルートフォルダはcypressフォルダの直上フォルダを指定してください。
	 * @param rootDir ルートフォルダ
	 */
	CypressTool(File rootDir){
		this.rootDir = rootDir
	}

	/**
	 * テンプレートファイルに埋込変数を適用し、スクリプトファイルを生成します。
	 * @param scriptFpath スクリプトファイルのパス（ルートフォルダからの相対パス）
	 * @param temlateFpath テンプレートファイルのパス（ルートフォルダからの相対パス）
	 * @param keyworepMaprdMap テンプレートに適用する埋込変数
	 */
	void createScript(String scriptFpath, String temlateFpath, Map repMap){
		File templateFile = new File(rootDir, temlateFpath)
		def template = new SimpleTemplateEngine().createTemplate(templateFile)
		File scriptFile = new File(rootDir, scriptFpath)
		scriptFile.text = template.make(repMap).toString()
	}

	/**
	 * スクリプトを実行し、標準出力を返します。
	 * 実行コマンド、標準出力をINFOログに出力します。
	 * 標準エラー出力があればINFOログに出力します。
	 * @param scriptFpath スクリプトファイルのパス（ルートフォルダからの相対パス）
	 * @param baseURL ベースURL（Cypress実行コマンドのbaseUrlオプションに渡す値）
	 * @return 標準出力（行単位でリストにしたもの）
	 */
	List runScript(String scriptFpath, String baseURL){
		// Cypressでスクリプトを実行するためのコマンドを生成します
		String command = String.format(format, exe, scriptFpath, baseURL)
		LOG.info('command={}', command)
		// コマンドを実行します
		def process = command.execute(null, rootDir)
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
		return stdOuts
	}
}
