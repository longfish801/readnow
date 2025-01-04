/*
 * ReviewDataSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * ReviewDataのテスト。
 * @author io.github.longfish801
 */
// @spock.lang.Ignore
@Slf4j('LOG')
class ReviewDataSpec extends Specification {
	def 'createInstance - シンプルな書誌情報'(){
		given:
		ReviewData review
		String biblio = '山田太郎『大傑作』（出帆社／2013年4月）'
		List body = ['素晴らしい。≫', '≫見事だ。≫', '≫最高！']

		when:
		review = ReviewData.createInstance(biblio, body)

		then:
		review.name == '★山田太郎|大傑作'
		review.title == '大傑作'
		review.authors == '山田太郎'
		review.publisher == '出帆社'
		review.pubdate == '2013年4月'
		review.keyword == '山田太郎 大傑作 出帆社'
		review.body.size() == 3
		review.body[0] == '　素晴らしい。'
		review.body[1] == '　見事だ。'
		review.body[2] == '　最高！'
	}

	def 'createInstance - 著者が複数'(){
		given:
		ReviewData review
		String biblio = '山田一人 山田二人、山田三人『大傑作』（出帆社／2013年4月）'

		when:
		review = ReviewData.createInstance(biblio, [])

		then:
		review.name == '★山田一人_山田二人_山田三人|大傑作'
		review.title == '大傑作'
		review.authors == ['山田一人', '山田二人', '山田三人']
		review.keyword == '山田一人 山田二人 山田三人 大傑作 出帆社'
	}

	def 'createInstance - その他の作り手、補足情報を［］で記述'(){
		given:
		ReviewData review
		String biblio = '山田太郎『大傑作』（薬師次郎［訳］／出帆社／2013年4月）'

		when:
		review = ReviewData.createInstance(biblio, [])

		then:
		review.name == '★山田太郎|大傑作'
		review.title == '大傑作'
		review.authors == '山田太郎'
		review.creators == '薬師次郎［訳］'
		review.keyword == '山田太郎 大傑作 出帆社'
	}

	def 'createInstance - その他の作り手が複数、補足情報を半角スペース区切りで記述'(){
		given:
		ReviewData review
		String biblio = '山田太郎『大傑作』（編集研究会 編／［訳］薬師次郎／出帆社／2013年4月）'

		when:
		review = ReviewData.createInstance(biblio, [])

		then:
		review.name == '★山田太郎|大傑作'
		review.title == '大傑作'
		review.authors == '山田太郎'
		review.creators == ['編集研究会［編］', '［訳］薬師次郎']
		review.keyword == '山田太郎 大傑作 出帆社'
	}

	def 'createInstance - その他の作り手が複数、補足情報無し'(){
		given:
		ReviewData review
		String biblio = '編集研究会〔編〕『大傑作』（山田一人／山田二人／山田三人／出帆社／2013年4月）'

		when:
		review = ReviewData.createInstance(biblio, [])

		then:
		review.name == '★編集研究会〔編〕|大傑作'
		review.title == '大傑作'
		review.authors == '編集研究会〔編〕'
		review.creators == ['山田一人', '山田二人', '山田三人']
		review.keyword == '編集研究会〔編〕 大傑作 出帆社'
	}

	def 'createInstance - ハンドル名に使用できない文字がある'(){
		given:
		ReviewData review
		String biblio = '山田/太郎『the 大傑作　#拡散:希望』（出帆社／2013年4月）'

		when:
		review = ReviewData.createInstance(biblio, [])

		then:
		review.name == '★山田_slash_太郎|the_大傑作___sharp_拡散_colon_希望'
		review.title == 'the 大傑作　#拡散:希望'
		review.authors == '山田/太郎'
		review.keyword == '山田/太郎 the 大傑作　#拡散:希望 出帆社'
	}

	def 'createInstance - 準正常系 - 書誌情報が不正です'(){
		given:
		ReviewData review
		String biblio = '『大傑作』（出帆社／2013年4月）'
		Exception exc

		when:
		review = ReviewData.createInstance(biblio, [])

		then:
		exc = thrown(Exception)
		exc.message == "書誌情報が不正です。biblio=${biblio}"
	}

	def 'createInstance - 準正常系 - 刊行年月の記述がありません'(){
		given:
		ReviewData review
		String biblio = '山田太郎『大傑作』（出帆社）'
		Exception exc

		when:
		review = ReviewData.createInstance(biblio, [])

		then:
		exc = thrown(Exception)
		exc.message == "刊行年月の記述がありません。biblio=${biblio}"
	}

	def 'createInstance - 準正常系 - 刊行年月の書式が不正です'(){
		given:
		ReviewData review
		String biblio = '山田太郎『大傑作』（出帆社／2013年四月）'
		Exception exc

		when:
		review = ReviewData.createInstance(biblio, [])

		then:
		exc = thrown(Exception)
		exc.message == "刊行年月の書式が不正です。biblio=${biblio}"
	}

	def 'getPubYear, getPubMonth'(){
		given:
		ReviewData review
		String biblio = '山田太郎『大傑作』（出帆社／2013年4月）'

		when:
		review = ReviewData.createInstance(biblio, [])

		then:
		review.pubYearAsStr == '2013'
		review.pubMonth == 4
	}
}
