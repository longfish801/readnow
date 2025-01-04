/*
 * DraftIOSpec.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io

import data.MasterData
import data.ReviewList
import data.RnowServer
import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * DraftIOのテスト。
 * @author io.github.longfish801
 */
// @spock.lang.Ignore
@Slf4j('LOG')
class DraftIOSpec extends Specification {
	// 試験用ファイルの格納フォルダ
	File rootDir = new File('src/test/resources/io/DraftIOSpec')
	
	def 'loadMaster'(){
		given:
		File dir = new File(rootDir, 'draft')

		when:
		DraftIO draftIO = new DraftIO(dir)
		MasterData master = draftIO.loadMaster()

		then:
		master.key == 'master'
	}
	
	def 'loadMaster - マスタファイルが無い場合はnullを返す'(){
		given:
		File dir = new File(rootDir, 'draft_nomaster')

		when:
		DraftIO draftIO = new DraftIO(dir)
		MasterData master = draftIO.loadMaster()

		then:
		master == null
	}
	
	def 'load'(){
		given:
		File dir = new File(rootDir, 'draft')

		when:
		DraftIO draftIO = new DraftIO(dir)
		RnowServer server = draftIO.load()

		then:
		server['reviews:2022'].name == '2022'
	}
	
	def 'load - 感想ファイルの読込に失敗しました'(){
		given:
		File dir = new File(rootDir, 'draft_nomaster')
		File file = new File(dir, 'reviews2022.tpac')

		when:
		DraftIO draftIO = new DraftIO(dir)
		draftIO.load()

		then:
		IOException exc = thrown(IOException)
		exc.message == "感想ファイルの読込に失敗しました。 path=${file.absolutePath}"
	}
	
	def 'saveMaster'(){
		given:
		File dir = new File(rootDir, 'draft_save')
		MasterData master = new MasterData(tag: 'master')

		when:
		DraftIO draftIO = new DraftIO(dir)
		draftIO.saveMaster(master)
		File resultFile = new File(dir, draftIO.masterFname)

		then:
		resultFile.text == master.toString()
	}
	
	def 'save'(){
		given:
		File dir = new File(rootDir, 'draft_save')
		String yyyy = '2022'
		ReviewList reviews = new ReviewList(tag: 'reviews', name: yyyy)
		RnowServer server = new RnowServer()
		server << reviews

		when:
		DraftIO draftIO = new DraftIO(dir)
		draftIO.save(server)
		File resultFile = new File(dir, String.format(draftIO.reviwsFormat, yyyy))

		then:
		resultFile.text == reviews.toString()
	}
}
