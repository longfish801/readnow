/*
 * MasterDataSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import io.github.longfish801.tpac.TpacDec
import io.github.longfish801.tpac.tea.TeaDec
import spock.lang.Specification

/**
 * MasterDataのテスト。
 * @author io.github.longfish801
 */
// @spock.lang.Ignore
@Slf4j('LOG')
class MasterDataSpec extends Specification {
	def 'merge'(){
		given:
		ReviewData newReview = new ReviewData(tag: 'review')
		newReview.pubdate = '2022年1月'
		newReview.authors = '山田太郎'
		newReview.creators = ['山田次郎［訳］', '山田三郎']
		newReview.tags = ['サスペンス', 'マイベスト2022 1位']
		String expected = '''\
			#! master
			#>
			
			#> pubyears
			2022
			#>
			
			#> authors
			#>
			
			#>> author:山田太郎
			#-hiraName ★
			#>
			
			#>> author:山田次郎
			#-hiraName ★
			#>
			
			#>> author:山田三郎
			#-hiraName ★
			#>
			
			#> tags
			#>
			
			#>> tag:サスペンス
			#>
			
			#>> tag:マイベスト2022
			#>
			
			#> categories
			#>
			
			#>> category:★未整理
			サスペンス
			マイベスト2022
			#>
			
			#!

			'''.stripIndent()

		when:
		MasterData masterData = new MasterData(tag: 'master')
		masterData.merge(newReview)

		then:
		masterData.toString().normalize() == expected
	}
}
