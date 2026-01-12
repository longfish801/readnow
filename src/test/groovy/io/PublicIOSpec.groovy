/*
 * PublicIOSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io

import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * PublicIOのテスト。
 * @author io.github.longfish801
 */
// @spock.lang.Ignore
@Slf4j('LOG')
class PublicIOSpec extends Specification {
	// 試験用ファイルの格納フォルダ
	File rootDir = new File('src/test/resources/io/PublicIOSpec')
	
	def 'saveMaster'(){
		given:
		File dir = new File(rootDir, 'normal_pretty')
		Map map = [
			'author' : [
				"id": "0001",
				"name": "山田太郎",
			]
		]

		when:
		PublicIO publicIO = new PublicIO(new File(dir, 'docs'), true)
		publicIO.saveMaster(map)
		File resultFile = new File(dir, 'docs/' + publicIO.masterFname)
		File expectedFile = new File(dir, 'expected/' + publicIO.masterFname)

		then:
		resultFile.text == expectedFile.text
	}
	
	def 'saveMaster - 整形しない'(){
		given:
		File dir = new File(rootDir, 'normal_notpretty')
		Map map = [
			'author' : [
				"id": "0001",
				"name": "山田太郎",
			]
		]

		when:
		PublicIO publicIO = new PublicIO(new File(dir, 'docs'), false)
		publicIO.saveMaster(map)
		File resultFile = new File(dir, 'docs/' + publicIO.masterFname)
		File expectedFile = new File(dir, 'expected/' + publicIO.masterFname)

		then:
		resultFile.text == expectedFile.text
	}
	
	def 'save'(){
		given:
		File dir = new File(rootDir, 'normal_pretty')
		String yyyy = '2022'
		Map map = [
			yyyy : [
				"id": "0001",
				"name": "山田太郎",
			]
		]

		when:
		PublicIO publicIO = new PublicIO(new File(dir, 'docs'), true)
		publicIO.save(map)
		String fname = String.format(publicIO.reviewFname, yyyy)
		File resultFile = new File(dir, 'docs/' + fname)
		File expectedFile = new File(dir, 'expected/' + fname)

		then:
		resultFile.text == expectedFile.text
	}
	
	def 'save - 整形しない'(){
		given:
		File dir = new File(rootDir, 'normal_notpretty')
		String yyyy = '2022'
		Map map = [
			"${yyyy}" : [
				"id": "0001",
				"name": "山田太郎",
			]
		]

		when:
		PublicIO publicIO = new PublicIO(new File(dir, 'docs'), false)
		publicIO.save(map)
		String fname = String.format(publicIO.reviewFname, yyyy)
		File resultFile = new File(dir, 'docs/' + fname)
		File expectedFile = new File(dir, 'expected/' + fname)

		then:
		resultFile.text == expectedFile.text
	}
}
