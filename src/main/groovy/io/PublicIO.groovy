/*
 * PublicIO.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io

import groovy.json.JsonBuilder
import groovy.json.StringEscapeUtils
import groovy.util.logging.Slf4j

/**
 * 公開原稿の入出力です。
 */
@Slf4j('LOG')
class PublicIO {
	/** 公開原稿フォルダ */
	File dir
	/** 整形された状態で出力するか否か */
	boolean pretty
	/** マスタの公開原稿ファイル名 */
	String masterFname = 'master.json'
	/** レビューの公開原稿ファイル名 */
	String reviewFname = 'reviews%s.json'
	
	/**
	 * コンストラクタ。
	 * @param dir 公開原稿フォルダ
	 * @param pretty 整形された状態で出力するか否か
	 */
	PublicIO(File dir, boolean pretty){
		this.dir = dir
		this.pretty = pretty
	}
	
	/**
	 * 公開マスタをjsonファイルとして保存します。
	 * @param publicMaster 公開マスタ
	 */
	void saveMaster(Map publicMaster){
		File file = new File(dir, masterFname)
		saveAsJsonFile(publicMaster, file)
	}
	
	/**
	 * 公開感想をjsonファイルとして保存します。
	 * @param publicReviewsMap 公開感想
	 */
	void save(Map publicReviewsMap){
		publicReviewsMap.each { String key, Map map ->
			File file = new File(dir, String.format(reviewFname, key))
			saveAsJsonFile(map, file)
		}
	}
	
	/**
	 * マップ形式のデータをJSON形式でファイルに保存します。
	 * クラス変数prettyの値に応じて整形あるいは整形せずに出力します。
	 * @param map マップ形式のデータ
	 * @param file 保存先のファイル
	 */
	private void saveAsJsonFile(Map map, File file){
		LOG.info('公開原稿を保存します。path={}', file.absolutePath)
		def builder = new JsonBuilder(map)
		String jsonStr = (pretty)? builder.toPrettyString() : builder.toString()
		file.text = (pretty)? StringEscapeUtils.unescapeJavaScript(jsonStr) : jsonStr
	}
}
