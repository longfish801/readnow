/*
 * ReviewList.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import io.github.longfish801.tpac.tea.TeaDec

/**
 * 感想リストです。
 * tpacファイル上のreviews宣言に相当します。
 */
@Slf4j('LOG')
class ReviewList implements TeaDec {
	// 感想や書誌情報の区切りとなるキーワード
	static final KEYWORD_DIV = '読了。'

	/**
	 * テキストから感想リストのインスタンスを生成します。
	 * テキスト上の並びとは逆順で感想リストに格納します。
	 * @param text テキスト
	 * @return 感想リスト
	 */
	static ReviewList createInstance(String text){
		// テキストを分割して感想に変換し、そのリストを作成します
		List body = []
		List reviews = []
		text.split("[\r\n]+").each { String line ->
			int idx = line.indexOf(KEYWORD_DIV)
			if (idx < 0){
				body << line
			} else {
				String biblio = line.substring(0, idx)
				body << line.substring(idx + KEYWORD_DIV.length())
				reviews << ReviewData.createInstance(biblio, body.reverse())
				body.clear()
			}
		}
		// 感想のリストを逆順に並び変えて感想リストに格納します
		ReviewList reviewList = new ReviewList(tag: 'reviews', name: 'latest')
		reviews.reverse().each { ReviewData review ->
			if (reviewList.solve('review:' + review.name)){
				throw new Exception("タイトルが重複しています。name=${review.name}")
			}
			reviewList << review
		}
		return reviewList
	}
}
