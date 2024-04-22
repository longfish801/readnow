/*
 * DraftDataSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import io.github.longfish801.tpac.TpacServer
import spock.lang.Specification

/**
 * DraftDataのテスト。
 * @author io.github.longfish801
 */
@Slf4j('LOG')
class DraftDataSpec extends Specification {
	// 試験用ファイルの格納フォルダ
	File dir = new File('src/test/resources/data/DraftDataSpec')

	def 'merge'(){
		given:
		File additionalDir = new File(dir, 'additional')
		File draftDir = new File(dir, 'draft')
		File expectedDir = new File(dir, 'expected')
		def newReviws = new AddData(additionalDir).load()
		def draftData = new DraftData(draftDir)
		def drafts

		when:
		drafts = draftData.load()
		newReviws.lowers.values().each { def newReview ->
			draftData.merge(newReview, drafts)
		}

		then:
		drafts['master'].toString().normalize() == new File(expectedDir, 'master.tpac').text.normalize()
		drafts['reviews:2022'].toString().normalize() == new File(expectedDir, 'reviews2022.tpac').text.normalize()
	}
}
