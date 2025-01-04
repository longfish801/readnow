/*
 * DraftDataMergerSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package executor

import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * DraftDataMergerのテスト。
 * @author io.github.longfish801
 */
// @spock.lang.Ignore
@Slf4j('LOG')
class DraftDataMergerSpec extends Specification {
	// 試験用ファイルの格納フォルダ
	File rootDir = new File('src/test/resources/executor/DraftDataMergerSpec')
	
	def 'execute'(){
		given:
		File roughDir = new File(rootDir, 'rough')
		File draftDir = new File(rootDir, 'draft')
		File expectDir = new File(rootDir, 'expected')

		when:
		DraftDataMerger draftDataMerger = new DraftDataMerger()
		draftDataMerger.execute(roughDir, draftDir)
		String result_master = new File(draftDir, 'master.tpac').text.normalize()
		String expect_master = new File(expectDir, 'master.tpac').text.normalize()
		String result_reviews = new File(draftDir, 'reviews2022.tpac').text.normalize()
		String expect_reviews = new File(expectDir, 'reviews2022.tpac').text.normalize()

		then:
		result_master == expect_master
		result_reviews == expect_reviews
	}
}
