/*
 * RoughConvertorSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package executor

import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * RoughConvertorのテスト。
 * @author io.github.longfish801
 */
// @spock.lang.Ignore
@Slf4j('LOG')
class RoughConvertorSpec extends Specification {
	// 試験用ファイルの格納フォルダ
	File rootDir = new File('src/test/resources/executor/RoughConvertorSpec')
	
	def 'execute'(){
		given:
		File dir = new File(rootDir, 'rough')

		when:
		RoughConvertor roughConvertor = new RoughConvertor()
		roughConvertor.execute(dir)

		then:
		new File(dir, 'latest.tpac').text == new File(dir, 'expected.tpac').text
	}
}
