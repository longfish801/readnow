/*
 * AddDataSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * AddDataのテスト。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class AddDataSpec extends Specification {
	// 試験用ファイルの格納フォルダ
	File dir = new File('src/test/resources/data/AddDataSpec')

	def 'convert'(){
		given:
		File targetFile = new File(dir, 'target.txt')
		File expFile = new File(dir, 'expected.tpac')
		def newReviews

		when:
		newReviews = new AddData(dir).convert(targetFile.text)

		then:
		newReviews.toString().normalize() == expFile.text.normalize()
	}
}
