/*
 * AddData.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import io.github.longfish801.tpac.TpacServer
import io.github.longfish801.tpac.TpacDec
import io.github.longfish801.tpac.TpacHandle

@Slf4j('LOG')
class AddData {
	/** 追加感想フォルダ */
	File dir
	/** 追加感想最新ファイル名 */
	String latestFname = 'latest.tpac'

	/**
	 * コンストラクタ。
	 * @param dir 追加感想フォルダ
	 */
	AddData(File dir){
		this.dir = dir
	}

	/**
	 * 追加感想最新ファイルを返します。
	 * @return 追加感想最新ファイル
	 */
	File getLatestFile(){
		return new File(dir, latestFname)
	}

	/**
	 * 追加感想最新ファイルから最新の追加感想を返します。
	 * @return 最新の追加感想
	 */
	TpacDec load(){
		return new TpacServer().soak(latestFile).getAt('reviews:latest')
	}

	/**
	 * 最新の追加感想を追加感想最新ファイルに保存します。
	 * @param dec 最新の追加感想
	 */
	void save(TpacDec dec){
		latestFile.withWriter { Writer writer -> dec.write(writer) }
	}

	/**
	 * 最新の追加感想履歴ファイルの内容を取得します。
	 * ファイル名順で末尾かつ拡張子が txtのファイルを、
	 * 最新の追加感想履歴ファイルとみなします。
	 * @throws IOException 追加感想履歴ファイルがありません。
	 */
	String getLatestHistory(){
		File[] txtFiles = dir.listFiles({ File file ->
			return file.name.endsWith('.txt')
		} as FileFilter)
		if (txtFiles.length == 0){
			throw new IOException("追加感想履歴ファイルがありません。 path=${dir.absolutePath}")
		}
		return txtFiles[-1].text
	}

	/**
	 * 追加感想のテキストをtpac形式に変換します。
	 * @param text 追加感想のテキスト
	 * @return tpac形式の追加感想
	 */
	TpacDec convert(String text){
		// 追加感想のテキストを逆順に並べ替えて感想毎に分割します
		List reviews = []
		Review review
		text.split("[\r\n]+").reverse().each { String line ->
			int idx = line.indexOf('読了。')
			if (idx < 0){
				review.body << line
			} else {
				review = new Review(biblio: line.substring(0, idx))
				review.body << line.substring(idx + '読了。'.length())
				reviews << review
			}
		}

		// tpac形式に変換して返します
		TpacDec dec = new TpacDec(tag: 'reviews', name: 'latest')
		reviews.each { dec << it.handle }
		return dec
	}

	/**
	 * 感想データ。
	 */
	class Review {
		/** 書誌情報 */
		String biblio
		/** 本文 */
		List body = []

		/**
		 * ハンドルを返します。
		 * @return ハンドル
		 */
		TpacHandle getHandle(){
			// 著者名、書名、その他の書誌情報を抽出します
			String rex = /^([^『]+)『([^』]+)』（([^）]+)）$/
			def matcher = (biblio =~ rex)
			if (matcher.size() == 0){
				throw new Exception ("書誌情報が不正です。biblio=${biblio}")
			}
			String fullAuthor = matcher[0][1]
			String fullTitle = matcher[0][2]
			List otherBibs = matcher[0][3].split('／')
			// 書名を解析します
			int titleDivIdx = fullTitle.indexOf('　')
			String title = (titleDivIdx >= 0)? fullTitle.substring(0, titleDivIdx): fullTitle
			title = title.replaceAll(' ', '_')
			// 著者名を解析します
			List authors = (fullAuthor.find(/[ 、]/) != null)? fullAuthor.split(/[ 、]/) : [ fullAuthor ]
			// 略称を作成します
			String abbre = '★' + authors.join('_') + '|' + title
			// その他の書誌情報を解析します
			String pubdate = (otherBibs.size() > 0)? otherBibs.removeLast() : null
			String publisher = (otherBibs.size() > 0)? otherBibs.removeLast() : null
			List maybeCreators = (otherBibs.size() > 0)? otherBibs : []
			List creators = []
			maybeCreators.each { String maybeCreator ->
				int spaceIdx = maybeCreator.indexOf(' ')
				if (maybeCreator.find(/［.+］/)){
					creators << maybeCreator
				} else if (spaceIdx > 0){
					String first = maybeCreator.substring(0, spaceIdx)
					String second = maybeCreator.substring(spaceIdx + 1)
					if (first.length() < second.length()){
						creators << "［${first}］${second}"
					} else {
						creators << "${first}［${second}］"
					}
				} else {
					creators << maybeCreator
				}
			}
			List keywords = []
			keywords.addAll(authors)
			keywords << fullTitle
			keywords << publisher

			// ハンドルに書誌情報を格納します
			TpacHandle handle = new TpacHandle(tag: 'review', name: abbre)
			handle.title = fullTitle
			if (authors.size() == 1){
				handle.authors = authors[0]
			} else if (authors.size() > 1){
				handle.authors = authors
			}
			if (creators.size() == 1){
				handle.creators = creators[0]
			} else if (creators.size() > 1){
				handle.creators = creators
			}
			if (publisher != null) handle.publisher = publisher
			if (pubdate != null) handle.pubdate = pubdate
			if (keywords.size() > 0) handle.keyword = keywords.join(' ')
			handle.body = body.collect { String line ->
				// 行頭や行末に"≫"があれば削除する
				line = line.replaceFirst(/^≫/, '')
				line = line.replaceFirst(/≫$/, '')
				// 行頭に全角スペースを入れる
				line = '　' + line
				return line
			}
			return handle
		}
	}
}
