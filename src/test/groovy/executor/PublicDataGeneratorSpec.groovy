/*
 * PublicDataGeneratorSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package executor

import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * PublicDataGeneratorのテスト。
 * @author io.github.longfish801
 */
// @spock.lang.Ignore
@Slf4j('LOG')
class PublicDataGeneratorSpec extends Specification {
	// 試験用ファイルの格納フォルダ
	File rootDir = new File('src/test/resources/executor/PublicDataGeneratorSpec')
	
	def 'execute'(){
		given:
		File draftDir = new File(rootDir, 'draft')
		File publicDir = new File(rootDir, 'docs')
		File expectDir = new File(rootDir, 'expected')

		when:
		PublicDataGenerator publicDataGenerator = new PublicDataGenerator()
		publicDataGenerator.execute(draftDir, publicDir, true)
		String result_master = new File(publicDir, 'master.json').text.normalize()
		String expect_master = new File(expectDir, 'master.json').text.normalize()
		String result_reviews = new File(publicDir, 'reviews2022.json').text.normalize()
		String expect_reviews = new File(expectDir, 'reviews2022.json').text.normalize()

		then:
		result_master == expect_master
		result_reviews == expect_reviews
	}
}
