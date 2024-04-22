/*
 * PublicDataSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.json.JsonBuilder
import groovy.json.StringEscapeUtils
import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * PublicDataのテスト。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class PublicDataSpec extends Specification {
	// 試験用ファイルの格納フォルダ
	File dir = new File('src/test/resources/data/PublicDataSpec')

	def 'generate'(){
		given:
		File draftDir = new File(dir, 'draft')
		def drafts = new DraftData(draftDir).load()
		File expectedDir = new File(dir, 'expected')
		Map draftsPub
		String jsonMaster
		String jsonReviews

		when:
		draftsPub = new PublicData(null, true).generate(drafts)
		jsonMaster = new JsonBuilder(draftsPub.master).toPrettyString()
		jsonMaster = StringEscapeUtils.unescapeJavaScript(jsonMaster).normalize()
		jsonReviews = new JsonBuilder(draftsPub['2022']).toPrettyString()
		jsonReviews = StringEscapeUtils.unescapeJavaScript(jsonReviews).normalize()

		then:
		jsonMaster == new File(expectedDir, 'master.json').text.normalize()
		jsonReviews == new File(expectedDir, 'reviews2022.json').text.normalize()
	}
}
