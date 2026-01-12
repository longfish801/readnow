/*
 * PublicReviews.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import tool.HTMLTool

/**
 * 公開感想のリストです。
 */
@Slf4j('LOG')
class PublicReviews extends LinkedHashMap {
	/** 逆索引 */
	Map masterRidx

	/**
	 * コンストラクタ。
	 * @param masterRidx 逆索引
	 */
	PublicReviews(Map masterRidx){
		this.masterRidx = masterRidx
	}

	/**
	 * 感想リストから公開感想のリストを生成します。
	 * @param reviews 感想リスト
	 * @return 公開感想のリスト
	 */
	Map generate(ReviewList reviews){
		// 公開感想のインスタンスを生成します
		int pubdateIdx
		String prePubdate = ''
		List publicReviewList = reviews.lowers.values().collect { ReviewData review ->
			// 同じ刊行年月での通番を算出します
			pubdateIdx =  (review.pubdate != prePubdate)? 1 : pubdateIdx + 1
			prePubdate = review.pubdate
			PublicReview publicReview = new PublicReview(review, pubdateIdx)
			return publicReview
		}

		// HTML化します
		Map textMap = [:]
		publicReviewList.each { PublicReview publicReview ->
			ReviewData review = publicReview.getReview()
			['title', 'abbre', 'body', 'secret', 'note'].each { String key ->
				String text
				if (key == 'abbre'){
					text = review.name.replaceAll('_sharp_', '#')
					text = review.name.replaceAll('_slash_', '/')
					text = review.name.replaceAll('_colon_', ':')
					text = review.name.replaceAll(/__/, '　')
					text = review.name.replaceAll(/_/, ' ')
					text = text.replaceAll(/\|/, ' / ')
				} else if (review.map.containsKey(key)){
					text = review.referAsString("#${key}")
				}
				if (text != null){
					textMap[publicReview.htmlKey(key)] = text
				}
			}
		}
		Map htmlMap = HTMLTool.htmlize(textMap)

		// 公開感想を生成します
		Map masterRidx = this.masterRidx
		publicReviewList.each { PublicReview publicReview ->
			this[publicReview.getIdAsPub()] = publicReview.generate(masterRidx, htmlMap)
		}
		return this
	}
}
