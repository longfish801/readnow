/*
 * PublicReviewSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import data.RnowServer
import groovy.util.logging.Slf4j
import spock.lang.Shared
import spock.lang.Specification

/**
 * PublicReviewのテスト。
 * @author io.github.longfish801
 */
// @spock.lang.Ignore
@Slf4j('LOG')
class PublicReviewSpec extends Specification {
	@Shared ReviewData review

	def setup(){
		review = new RnowServer().soak('''\
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
			　お見事。
			#-note
			　と、思ったのだが。
			#-secret
			　本当はそれほどでもない。
			'''.stripIndent())['reviews'].solve('review:山田太郎|大傑作')
	}

	def 'generate'(){
		given:
		PublicReview pubRev = new PublicReview(review, 1)
		Map masterRidx = [
			'authors': [
				'山田太郎': '0001',
				'山田次郎': '0002',
				'山田三郎': '0003',
			],
			'tags': [
				'密室トリック': '0001',
				'私的ベスト2022': '0002',
			]
		]
		Map htmlMap = [
			(pubRev.htmlKey('title')) : '<p>大傑作</p>',
			(pubRev.htmlKey('abbre')) : '<p>山田太郎 / 大傑作</p>',
			(pubRev.htmlKey('body')) : "<p>　素晴らしい。</br>\n　お見事。</p>",
			(pubRev.htmlKey('note')) : '<p>　と、思ったのだが。</p>',
			(pubRev.htmlKey('secret')) : '<p>　本当はそれほどでもない。</p>',
		]

		when:
		pubRev.generate(masterRidx, htmlMap)

		then:
		pubRev.id == '20220101'
		pubRev.title == '大傑作'
		pubRev.abbre == '山田太郎 / 大傑作'
		pubRev.publisher == '&quot;分校&quot;文庫'
		pubRev.pubdate == '202201'
		pubRev.isbn == '9784167919757'
		pubRev.keyword == '山田太郎 大傑作 &quot;分校&quot;文庫'
		pubRev.body == "<p>　素晴らしい。</br>\n　お見事。</p>"
		pubRev.authors == [
			['id': '0001']
		]
		pubRev.creators == [
			['id': '0002', 'format': "%s［訳］"],
			['id': '0003', 'format': "［編集］%s"],
		]
		pubRev.tags == [
			['id': '0001'],
			['id': '0002', 'value': "1位"],
		]
		pubRev.note == '<p>　と、思ったのだが。</p>'
		pubRev.secret == '<p>　本当はそれほどでもない。</p>'
	}

	def 'htmlKey'(){
		given:
		PublicReview pubRev = new PublicReview(review, 1)
		
		when:
		String key = pubRev.htmlKey('some')

		then:
		key == '202201-山田太郎|大傑作-some'
	}

	def 'getIdAsPub'(){
		given:
		PublicReview pubRev = new PublicReview(review, 1)
		
		when:
		String id = pubRev.getIdAsPub()

		then:
		id == '20220101'
	}

	def 'getPubdateAsPub'(){
		given:
		PublicReview pubRev = new PublicReview(review, 1)

		when:
		String pubdate = pubRev.getPubdateAsPub()

		then:
		pubdate == '202201'
	}
}
