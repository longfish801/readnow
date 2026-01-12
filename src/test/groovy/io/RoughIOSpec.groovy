/*
 * RoughIOSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io

import data.ReviewList
import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * RoughIOのテスト。
 * @author io.github.longfish801
 */
// @spock.lang.Ignore
@Slf4j('LOG')
class RoughIOSpec extends Specification {
	// 試験用ファイルの格納フォルダ
	File rootDir = new File('src/test/resources/io/RoughIOSpec')
	
	def 'getLatestFile'(){
		given:
		File dir = new File(rootDir, 'normal')

		when:
		RoughIO roughIO = new RoughIO(dir)
		File latestFile = roughIO.latestFile

		then:
		latestFile.absolutePath == new File(dir, roughIO.latestFname).absolutePath
	}
	
	def 'load'(){
		given:
		File dir = new File(rootDir, 'normal')

		when:
		RoughIO roughIO = new RoughIO(dir)
		ReviewList reviews = roughIO.load()

		then:
		reviews.lowers.size() == 3
	}
	
	def 'getLatestHistory'(){
		given:
		File dir = new File(rootDir, 'normal')

		when:
		RoughIO roughIO = new RoughIO(dir)
		String history = roughIO.latestHistory

		then:
		history == '20240425'
	}
	
	def 'save'(){
		given:
		File dir = new File(rootDir, 'normal_save')
		ReviewList reviews = new ReviewList(tag: 'reviews', name: 'latest')

		when:
		RoughIO roughIO = new RoughIO(dir)
		roughIO.save(reviews)
		File resultFile = new File(dir, roughIO.latestFname)

		then:
		resultFile.text == reviews.toString()
	}
	
	def 'getLatestHistory - 準正常系 - 感想履歴ファイルがありません'(){
		given:
		File dir = new File(rootDir, 'seminormal')

		when:
		RoughIO roughIO = new RoughIO(dir)
		roughIO.latestHistory

		then:
		IOException exc = thrown(IOException)
		exc.message == "感想履歴ファイルがありません。 path=${dir.absolutePath}"
	}
}
