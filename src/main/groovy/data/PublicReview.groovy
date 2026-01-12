/*
 * PublicReviews.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import tool.HTMLTool

/**
 * 公開感想です。
 */
@Slf4j('LOG')
class PublicReview extends LinkedHashMap {
	/** 感想 */
	ReviewData review
	/** 同じ刊行年月での通番 */
	int pubdateIdx

	/**
	 * コンストラクタ。
	 * @param review 感想
	 * @param pubdateIdx 同じ刊行年月での通番
	 */
	PublicReview(ReviewData review, int pubdateIdx){
		this.review = review
		this.pubdateIdx = pubdateIdx
	}

	/**
	 * 感想を返します。
	 * @return 感想
	 */
	ReviewData getReview(){
		return this.review
	}

	/**
	 * 公開感想を生成します。
	 * @param masterRidx 逆索引
	 * @param htmlMap HTML化した文字列のマップ
	 * @return 公開感想
	 */
	Map generate(Map masterRidx, Map htmlMap){
		// 著者などをid, formatのマップとして返すためのクロージャです
		Closure parseFormat = { def val, Map ridx ->
			return ((val instanceof List)? val : [val]).collect { String elem ->
				if (elem.indexOf('［') < 0){
					return ['id': ridx[elem]]
				}
				String id
				String format
				if (elem.indexOf('［') == 0){
					elem = elem.replaceFirst(/［([^］]+)］/, '$1\t')
					List splited = elem.split('\t')
					id = ridx[splited[1]]
					format = "［${splited[0]}］%s"
				} else {
					elem = elem.replaceFirst(/［([^］]+)］/, '\t$1')
					List splited = elem.split('\t')
					id = ridx[splited[0]]
					format = "%s［${splited[1]}］"
				}
				return ['id': id, 'format': format]
			}
		}
		// HTML化した文字列を整形するためのクロージャです
		Closure formatHTML = { String key, boolean removeParaTag ->
			String htmlStr = htmlMap[htmlKey(key)].toString().trim()
			if (removeParaTag){
				htmlStr = htmlStr.replaceFirst('^<p>', '')
				htmlStr = htmlStr.replaceFirst('</p>$', '')
			}
			return htmlStr
		}
		// 公開感想を生成します
		this.id = this.getIdAsPub()
		this.title = formatHTML.call('title', true)
		this.authors = parseFormat.call(review.authors, masterRidx['authors'])
		this.abbre = formatHTML.call('abbre', true)
		this.publisher = HTMLTool.escapeHtml(review.publisher)
		this.pubdate = this.getPubdateAsPub()
		this.isbn = review.isbn
		this.keyword = HTMLTool.escapeHtml(review.keyword)
		this.body = formatHTML.call('body', false)
		if (review.map.containsKey('creators')){
			this.creators = parseFormat.call(review.creators, masterRidx['authors'])
		}
		if (review.map.containsKey('tags')){
			Map ridx = masterRidx['tags']
			this.tags =  [review.tags].flatten().collect { String tag ->
				if (tag.indexOf(' ') > 0){
					List splited = tag.split(' ')
					return ['id': ridx[splited[0]], 'value': splited[1]]
				}
				return ['id': ridx[tag]]
			}
		}
		if (review.map.containsKey('note')){
			this.note = formatHTML.call('note', false)
		}
		if (review.map.containsKey('secret')){
			this.secret = formatHTML.call('secret', false)
		}
		return this
	}

	/**
	 * HTML化した文字列のマップのためのキーを返します。
	 * @param key キー
	 * @return HTML化した文字列のマップのためのキー
	 */
	String htmlKey(String key){
		return [getPubdateAsPub(), review.name, key].join('-')
	}

	/**
	 * IDを返します。
	 * @return ID
	 */
	String getIdAsPub(){
		return getPubdateAsPub() + String.format('%02d', pubdateIdx)
	}

	/**
	 * 刊行年月をyyyymm形式で返します。
	 * @return 刊行年月(yyyymm)
	 */
	String getPubdateAsPub(){
		List splited = review.pubdate.split(/[年月]/)
		String yyyy = splited[0]
		String mm = (splited[1].size() == 1)? '0' + splited[1] : splited[1]
		return "${yyyy}${mm}"
	}
}
