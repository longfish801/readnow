/*
 * ReviewListSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * ReviewListのテスト。
 * @author io.github.longfish801
 */
// @spock.lang.Ignore
@Slf4j('LOG')
class ReviewListSpec extends Specification {
	def 'createInstance - ひとつのみ'(){
		given:
		String text = '''\
			≫最高！
			≫見事だ。≫
			山田太郎『大傑作』（出帆社／2013年4月）読了。素晴らしい。≫
			'''.stripIndent()

		when:
		ReviewList reviewList = ReviewList.createInstance(text)
		ReviewData review = reviewList.solve('review:★山田太郎|大傑作')

		then:
		reviewList.name == 'latest'
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

	def 'createInstance - 複数'(){
		given:
		String text = '''\
			≫さようなら！
			≫いままでありがとう。≫
			≫なんて悲しい。≫
			山田三郎『遺作』（出帆社／2013年12月）読了。これが最後だなんて。≫
			≫しかし最低。
			≫見事だ。≫
			山田次郎『大傑作』（武運文庫／1987年1月）読了。素晴らしい。≫
			≫見事だ。
			山田太郎『処女作』（鶴瓶ノベルス／2013年4月）読了。読んだ。≫
			'''.stripIndent()

		when:
		ReviewList reviewList = ReviewList.createInstance(text)
		String keys = reviewList.lowers.keySet().join('-')
		ReviewData review1 = reviewList.solve('review:★山田太郎|処女作')
		ReviewData review2 = reviewList.solve('review:★山田次郎|大傑作')
		ReviewData review3 = reviewList.solve('review:★山田三郎|遺作')

		then:
		reviewList.name == 'latest'
		keys == 'review:★山田太郎|処女作-review:★山田次郎|大傑作-review:★山田三郎|遺作'
		review1.title == '処女作'
		review1.authors == '山田太郎'
		review1.publisher == '鶴瓶ノベルス'
		review1.pubdate == '2013年4月'
		review1.body.size() == 2
		review1.body[0] == '　読んだ。'
		review1.body[1] == '　見事だ。'
		review2.title == '大傑作'
		review2.authors == '山田次郎'
		review2.publisher == '武運文庫'
		review2.pubdate == '1987年1月'
		review2.body.size() == 3
		review2.body[0] == '　素晴らしい。'
		review2.body[1] == '　見事だ。'
		review2.body[2] == '　しかし最低。'
		review3.title == '遺作'
		review3.authors == '山田三郎'
		review3.publisher == '出帆社'
		review3.pubdate == '2013年12月'
		review3.body.size() == 4
		review3.body[0] == '　これが最後だなんて。'
		review3.body[1] == '　なんて悲しい。'
		review3.body[2] == '　いままでありがとう。'
		review3.body[3] == '　さようなら！'
	}

	def 'createInstance - 準正常系 - タイトルが重複しています'(){
		given:
		String text = '''\
			≫しかし最低。
			≫見事だ。≫
			山田太郎『処女作』（武運文庫／1987年1月）読了。素晴らしい。≫
			≫見事だ。
			山田太郎『処女作』（鶴瓶ノベルス／2013年4月）読了。読んだ。≫
			'''.stripIndent()
		Exception exc

		when:
		ReviewList.createInstance(text)

		then:
		exc = thrown(Exception)
		exc.message == "タイトルが重複しています。name=★山田太郎|処女作"
	}
}
