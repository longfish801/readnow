/*
 * ReviewData.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import io.github.longfish801.tpac.tea.TeaHandle

/**
 * 感想です。
 * tpacファイル上のreviewハンドルに相当します。
 */
@Slf4j('LOG')
class ReviewData implements TeaHandle {
	/**
	 * 感想のインスタンスを生成します。
	 * @params biblio 書誌情報
	 * @params body 本文
	 * @return ReviewData
	 */
	static ReviewData createInstance(String biblio, List body){
		// 著者名、書名、その他の書誌情報を抽出します
		String rex = /^([^『]+)『(.+)』（(.+)）$/
		def matcher = (biblio =~ rex)
		if (matcher.size() == 0){
			throw new Exception ("書誌情報が不正です。biblio=${biblio}")
		}
		String fullAuthor = matcher[0][1]
		String fullTitle = matcher[0][2]
		List otherBibs = matcher[0][3].split('／')
		// 著者名を解析します
		List authors = (fullAuthor.find(/[ 、]/) != null)? fullAuthor.split(/[ 、]/) : [ fullAuthor ]
		// その他の書誌情報を解析します
		String pubdate = (otherBibs.size() >= 2)? otherBibs.removeLast() : ''
		if (pubdate.empty){
			throw new Exception ("刊行年月の記述がありません。biblio=${biblio}")
		}
		if (!(pubdate =~ /^\d{4}年\d{1,2}月$/)){
			throw new Exception ("刊行年月の書式が不正です。biblio=${biblio}")
		}
		String publisher = (otherBibs.size() > 0)? otherBibs.removeLast() : null
		List maybeCreators = (otherBibs.size() > 0)? otherBibs : []
		List creators = []
		// その他の作り手を解析します
		maybeCreators.each { String maybeCreator ->
			int spaceIdx = maybeCreator.indexOf(' ')
			if (maybeCreator.find(/［.+］/)){
				// 補足情報が〔〕で記述されている場合
				creators << maybeCreator
			} else if (spaceIdx > 0){
				// 補足情報が半角スペース区切りで記述されている場合
				String first = maybeCreator.substring(0, spaceIdx)
				String second = maybeCreator.substring(spaceIdx + 1)
				// 〔〕を用いた記法に置き換えます
				if (first.length() < second.length()){
					creators << "［${first}］${second}"
				} else {
					creators << "${first}［${second}］"
				}
			} else {
				// 補足情報がない場合
				creators << maybeCreator
			}
		}
		List keywords = []
		keywords.addAll(authors)
		keywords << fullTitle
		keywords << publisher

		// ハンドルに書誌情報を格納します
		String name = '★' + authors.join(' ') + '|' + fullTitle
		// ハンドル名に使用できない文字を置換します
		name = name.replaceAll(' ', '_')
		name = name.replaceAll('　', '__')
		name = name.replaceAll('#', '_sharp_')
		name = name.replaceAll('/', '_slash_')
		name = name.replaceAll(':', '_colon_')
		ReviewData review = new ReviewData(tag: 'review', name: name)
		review.title = fullTitle
		if (authors.size() == 1){
			review.authors = authors[0]
		} else if (authors.size() > 1){
			review.authors = authors
		}
		if (creators.size() == 1){
			review.creators = creators[0]
		} else if (creators.size() > 1){
			review.creators = creators
		}
		if (publisher != null) review.publisher = publisher
		review.pubdate = pubdate
		if (keywords.size() > 0) review.keyword = keywords.join(' ')
		review.body = body.collect { String line ->
			// 行頭や行末に"≫"があれば削除する
			line = line.replaceFirst(/^≫/, '')
			line = line.replaceFirst(/≫$/, '')
			// 行頭に全角スペースを入れる
			line = '　' + line
			return line
		}
		return review
	}
	
	/**
	 * 刊行年月の年を返します。
	 * @return 刊行年月の年
	 */
	String getPubYearAsStr(){
		return pubdate.substring(0, 4)
	}
	
	/**
	 * 刊行年月の月を整数型で返します。
	 * @return 刊行年月の月
	 * @throw pubdateの形式が不正です。
	 */
	int getPubMonth(){
		int month = 0
		try {
			month = Integer.parseInt(pubdate.replaceFirst(/\d{4}年(\d{1,2})月/, '$1'))
		} catch (NumberFormatException exc){
			throw new Exception("pubdateの形式が不正です。pubdate=${pubdate}", exc)
		}
		return month
	}
}
