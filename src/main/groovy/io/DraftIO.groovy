/*
 * DraftIO.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io

import groovy.util.logging.Slf4j
import data.MasterData
import data.ReviewList
import data.RnowServer

/**
 * 原稿の入出力です。
 */
@Slf4j('LOG')
class DraftIO {
	/** 原稿フォルダ */
	File dir
	/** マスタのtpacファイル名 */
	String masterFname = 'master.tpac'
	/** 感想のtpacファイル名 */
	String reviwsFname = 'reviews*.tpac'
	/** 感想のtpacファイル名のフォーマット */
	String reviwsFormat = 'reviews%s.tpac'

	/**
	 * コンストラクタ。
	 * @param dir 原稿フォルダ
	 */
	DraftIO(File dir){
		this.dir = dir
	}

	/**
	 * マスタファイルを読みこんでマスタを返します。
	 * マスタファイルが存在しない場合は nullを返します。
	 * @return マスタ
	 */
	MasterData loadMaster(){
		File file = new File(dir, masterFname)
		if (!file.exists()) return null
		RnowServer server = new RnowServer()
		server.soak(file)
		return server['master']
	}

	/**
	 * 感想ファイルをすべて読みこんで感想リストのマップを返します。
	 * 感想ファイルの宣言の名前(yyyy)をキー、
	 * 感想リストを値としたマップを返します。
	 * @return 感想リストのマップ
	 */
	RnowServer load(){
		def scanner = new AntBuilder().fileScanner {
			fileset(dir: dir.path) { include(name: reviwsFname) }
		}
		RnowServer server = new RnowServer()
		for (File file in scanner){
			try {
				server.soak(file)
			} catch (exc){
				throw new IOException("感想ファイルの読込に失敗しました。 path=${file.absolutePath}", exc)
			}
		}
		return server
	}

	/**
	 * マスタをマスタファイルとして保存します。
	 * @param マスタ
	 */
	void saveMaster(MasterData master){
		File file = new File(dir, masterFname)
		LOG.info('原稿のマスタファイルを保存します。path={}', file.absolutePath)
		file.withWriter { Writer writer -> 
			master.write(writer)
		}
	}

	/**
	 * 感想リストを感想ファイルとして保存します。
	 * @param server 感想リストを格納したRnowServer
	 */
	void save(RnowServer server){
		server.decs.values().each { ReviewList reviews ->
			String fname = String.format(reviwsFormat, reviews.name)
			File file = new File(dir, fname)
			LOG.info('原稿の感想ファイルを保存します。path={}', file.absolutePath)
			file.withWriter { Writer writer -> 
				reviews.write(writer)
			}
		}
	}
}
