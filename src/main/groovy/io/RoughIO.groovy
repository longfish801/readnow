/*
 * RoughIO.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package io

import data.ReviewList
import data.RnowServer
import groovy.util.logging.Slf4j
import io.github.longfish801.tpac.tea.TeaDec

/**
 * 下書き感想の入出力です。
 */
@Slf4j('LOG')
class RoughIO {
	/** 下書きフォルダ */
	File dir
	/** 追加感想ファイル名 */
	String latestFname = 'latest.tpac'

	/**
	 * コンストラクタ。
	 * @param dir 下書きフォルダ
	 */
	RoughIO(File dir){
		this.dir = dir
	}

	/**
	 * 追加感想ファイルを返します。
	 * @return 追加感想ファイル
	 */
	File getLatestFile(){
		return new File(dir, latestFname)
	}

	/**
	 * 追加感想ファイルから最新の下書き感想を返します。
	 * @return 最新の下書き感想
	 */
	ReviewList load(){
		return new RnowServer().soak(latestFile).getAt('reviews:latest')
	}

	/**
	 * 最新の下書き感想を追加感想ファイルに保存します。
	 * @param dec 最新の下書き感想
	 */
	void save(ReviewList dec){
		LOG.info('追加感想ファイルに保存します。path={}', latestFile.absolutePath)
		latestFile.withWriter { Writer writer -> dec.write(writer) }
	}

	/**
	 * 最新の感想履歴ファイルの内容を取得します。
	 * ファイル名の昇順で最後、かつ拡張子が txtのファイルを、
	 * 最新の感想履歴ファイルとみなします。
	 * @throws IOException 感想履歴ファイルがありません。
	 */
	String getLatestHistory(){
		File[] txtFiles = dir.listFiles({ File file ->
			return file.name.endsWith('.txt')
		} as FileFilter)
		if (txtFiles.length == 0){
			throw new IOException("感想履歴ファイルがありません。 path=${dir.absolutePath}")
		}
		return txtFiles[-1].text
	}
}
