/*
 * PublicReviewsSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import data.ReviewList
import data.RnowServer
import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * PublicReviewsのテスト。
 * @author io.github.longfish801
 */
// @spock.lang.Ignore
@Slf4j('LOG')
class PublicReviewsSpec extends Specification {
	def 'generate'(){
		given:
		ReviewList reviews = new RnowServer().soak('''\
			#! reviews
			#> review:山田太郎|大傑作
			#-title 大傑作
			#-authors 山田太郎
			#-creators
			山田次郎［訳］
			［編集］山田三郎
			#-publisher "分校"文庫
			#-pubdate 2022年1月
			#-tags
			密室トリック
			私的ベスト2022 1位
			#-keyword 山田太郎 大傑作 "分校"文庫
			#-isbn _9784167919757
			#-body
			　素晴らしい。
			#-note
			　と、思ったのだが。
			#-secret
			　本当はそれほどでもない。
			#> review:山田太郎|大傑作２　帰ってきたアイツ
			#-title 大傑作２　帰ってきたアイツ
			#-authors 山田太郎
			#-publisher "分校"文庫
			#-pubdate 2022年1月
			#-keyword 山田太郎 大傑作２　帰ってきたアイツ "分校"文庫
			#-isbn _9784167919757
			#-body
			　やっぱり素晴らしい。
			'''.stripIndent())['reviews']
		PublicReviews publicReviews = new PublicReviews([
			'authors': [
				'山田太郎': '0001',
				'山田次郎': '0002',
				'山田三郎': '0003',
			],
			'tags': [
				'密室トリック': '0001',
				'私的ベスト2022': '0002',
			],
		])

		when:
		Map pubReviews = publicReviews.generate(reviews)

		then:
		pubReviews == [
			'20220101': [
				id: '20220101',
				title: '大傑作',
				authors: [
					['id': '0001']
				],
				abbre: '山田太郎 / 大傑作',
				publisher: '&quot;分校&quot;文庫',
				pubdate: '202201',
				isbn: '9784167919757',
				keyword: '山田太郎 大傑作 &quot;分校&quot;文庫',
				body: "<p>　素晴らしい。</p>",
				creators: [
					['id': '0002', 'format': "%s［訳］"],
					['id': '0003', 'format': "［編集］%s"],
				],
				tags: [
					['id': '0001'],
					['id': '0002', 'value': "1位"],
				],
				note: '<p>　と、思ったのだが。</p>',
				secret: '<p>　本当はそれほどでもない。</p>',
			],
			'20220102': [
				id: '20220102',
				title: '大傑作２　帰ってきたアイツ',
				authors: [
					['id': '0001']
				],
				abbre: '山田太郎 / 大傑作２　帰ってきたアイツ',
				publisher: '&quot;分校&quot;文庫',
				pubdate: '202201',
				isbn: '9784167919757',
				keyword: '山田太郎 大傑作２　帰ってきたアイツ &quot;分校&quot;文庫',
				body: "<p>　やっぱり素晴らしい。</p>",
			],
		]
	}
}
