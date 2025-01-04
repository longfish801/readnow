/*
 * PublicMasterSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import data.MasterData
import data.RnowServer
import spock.lang.Shared
import spock.lang.Specification

/**
 * PublicMasterのテスト。
 * @author io.github.longfish801
 */
// @spock.lang.Ignore
@Slf4j('LOG')
class PublicMasterSpec extends Specification {
	@Shared MasterData masterData

	def setup(){
		masterData = new RnowServer().soak('''\
			#! master
			#> pubyears
			2022
			#> authors
			#>> author:山田太郎
			#-hiraName やまだ たろう
			#>> author:山田次郎
			#-hiraName やまだ じろう
			#> tags
			#>> tag:ベスト2022
			#> categories
			#>> category:ベスト
			ベスト2022
			'''.stripIndent())['master']
	}

	def 'generaterBase'(){
		when:
		PublicMaster publicMaster = new PublicMaster(masterData)

		then:
		publicMaster == [
			'pubyears': ['2022'],
			'authors': [
				'0001': [
					'id': '0001',
					'name': '山田太郎',
					'hiraName': 'やまだ たろう',
				],
				'0002': [
					'id': '0002',
					'name': '山田次郎',
					'hiraName': 'やまだ じろう',
				],
			],
			'tags': [
				'0001': [
					'id': '0001',
					'name': 'ベスト2022',
				],
			],
			'categories': [
				[
					'id': '0001',
					'name': 'ベスト',
					'tags': [ '0001' ]
				],
			],
		]
	}

	def 'generateReverseIndex'(){
		when:
		PublicMaster publicMaster = new PublicMaster(masterData)
		Map masterRidx = publicMaster.generateReverseIndex()

		then:
		masterRidx == [
			'authors': [
				'山田太郎': '0001',
				'山田次郎': '0002',
			],
			'tags': [
				'ベスト2022': '0001',
			]
		]
	}

	def 'createReviewIndex'(){
		given:
		Map publicReviews = [
			'20220101': [
				'pubdate': '202201',
				'authors': [
					[
						'id': '0001'
					]
				],
				'creators': [
					[
						'id': '0002'
					]
				],
				'tags': [
					[
						'id': '0001'
					]
				],
			]
		]

		when:
		PublicMaster publicMaster = new PublicMaster(masterData)
		publicMaster.createReviewIndex(publicReviews)

		then:
		publicMaster['pubdates']['2022']['202201'] == ['20220101']
		publicMaster['authors']['0001']['reviews'] == ['20220101']
		publicMaster['authors']['0002']['reviews'] == ['20220101']
		publicMaster['tags']['0001']['reviews'] == ['20220101']
	}
}
