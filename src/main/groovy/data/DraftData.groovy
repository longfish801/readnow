/*
 * DraftData.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import io.github.longfish801.tpac.TpacDec
import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.TpacServer

@Slf4j('LOG')
class DraftData {
	/** 原稿フォルダ */
	File dir
	/** マスタのtpacファイル名 */
	String masterFname = 'master.tpac'
	/** 感想のtpacファイル名 */
	String reviwsFname = 'reviews*.tpac'
	/** 感想のtpacファイル名のフォーマット */
	String reviwsFormat = 'reviews%s.tpac'
	/** WikipediaのURL */
	String wikiURL = 'https://ja.wikipedia.org/wiki/'

	/**
	 * コンストラクタ。
	 * @param dir 原稿フォルダ
	 */
	DraftData(File dir){
		this.dir = dir
	}

	/**
	 * 原稿を読みこんだ TpacServerを返します。
	 * @return 原稿を読みこんだ TpacServer
	 */
	TpacServer load(){
		// 読み込む対象のファイルパスの一覧を作成します
		String masterPath = "${dir.path}/${masterFname}"
		List paths = [masterPath]
		def scanner = new AntBuilder().fileScanner {
			fileset(dir: dir.path) { include(name: reviwsFname) }
		}
		for (File file in scanner){
			paths << file.absolutePath
		}

		// 原稿ファイルを読み込みます
		TpacServer server = new TpacServer()
		paths.each { String path ->
			try {
				File file = new File(path)
				if (file.isFile()) server.soak(file)
			} catch (exc){
				LOG.error('原稿ファイルの読込に失敗しました。 path={}', path)
				throw exc
			}
		}
		return server
	}

	/**
	 * TpacServerの内容を原稿ファイルとして保存します。
	 * @param TpacServer
	 */
	void save(TpacServer server){
		server.decs.values().each { def dec ->
			String fname = (dec.tag == 'master')?
				masterFname : 
				String.format(reviwsFormat, dec.name)
			new File(dir, fname).withWriter { Writer writer -> 
				dec.write(writer)
			}
		}
	}

	/**
	 * 追加感想を原稿に一括反映します。
	 * @param newReview 追加感想
	 * @param server 原稿
	 */
	void merge(TpacHandle newReview, TpacServer server){
		// 感想に追加感想を反映します
		String pubYear = newReview.pubdate.substring(0, 4)
		if (server["reviews:${pubYear}"] == null){
			server << new TpacDec(tag: 'reviews', name: pubYear)
		}
		mergeToReviews(newReview, server["reviews:${pubYear}"])
		// マスタに追加感想を反映します
		if (server['master'] == null){
			def masterDec = new TpacDec(tag: 'master')
			masterDec << new TpacHandle(tag: 'pubyears')
			masterDec << new TpacHandle(tag: 'authors')
			masterDec << new TpacHandle(tag: 'tags')
			masterDec << new TpacHandle(tag: 'bests')
			server << masterDec
		}
		mergeToMaster(newReview, server['master'])
	}

	/**
	 * 追加感想を刊行年の感想一覧に反映します。
	 * @param newReview 追加感想のハンドル
	 * @param reviewsDec 刊行年の感想一覧
	 */
	void mergeToReviews(TpacHandle newReview, TpacDec reviewsDec){
		String key = "pubdate:${newReview.pubdate}"
		if (reviewsDec.lowers[key] == null){
			reviewsDec << new TpacHandle(tag: 'pubdate', name: newReview.pubdate)
		}
		reviewsDec.lowers[key] << newReview
	}

	/**
	 * 追加感想をマスタに反映します。
	 * @param newReview 追加感想のハンドル
	 * @param master マスタ
	 */
	void mergeToMaster(TpacHandle newReview, TpacDec master){
		// 刊行年のリストに不足があれば追記します
		String pubYear = newReview.pubdate.substring(0, 4)
		def pubyearsHndl = master.solve('pubyears')
		if (pubyearsHndl.dflt == null) pubyearsHndl.dflt = []
		if (!pubyearsHndl.dflt.contains(pubYear)){
			pubyearsHndl.dflt << pubYear
			pubyearsHndl.dflt.sort()
		}

		// 存在しない著者名があればハンドルを追加します
		def authorsHandle = master.solve('authors')
		List authors = []
		authors << newReview.authors
		authors << newReview.creators
		authors.flatten().findAll { it != null && it.length() > 0 }.collect {
			it.replaceAll(/［([^］]+)］/, '')
		}.each { String author ->
			if (authorsHandle.solve("author:${author}") == null){
				def authorHandle = new TpacHandle(tag: 'author', name: author)
				authorHandle.hiraName = getHiraName(author)
				authorsHandle << authorHandle
			}
		}

		// 存在しないタグがあればハンドルを追加します
		def tagsHandle = master.solve('tags')
		[ newReview.tags ].flatten().findAll {
			it != null && it.length() > 0
		}.each { String tag ->
			if (tagsHandle.solve("tag:${tag}") == null){
				tagsHandle << new TpacHandle(tag: 'tag', name: tag)
			}
		}

		// 存在しないベストがあればハンドルを追加します
		def bestsHandle = master.solve('bests')
		[ newReview.bests ].flatten().findAll {
			it != null && it.length() > 0
		}.collect { String best ->
			def matcher = (best =~ /^([^ ]+) .+$/)
			if (matcher.size() == 0){
				throw new Exception("不正な書式のベストです。best=${best}")
			}
			return matcher[0][1]
		}.each { String best ->
			if (bestsHandle.solve("best:${best}") == null){
				bestsHandle << new TpacHandle(tag: 'best', name: best)
			}
		}
	}
	
	/**
	 * 指定された名前の読み仮名と思われる文字列を Wikipediaから参照します。
	 * 読み仮名を取得できなかった場合は“★”を返します。
	 * @param name 著者名
	 * @return 著者名の読み仮名と思われる文字列
	 */
	String getHiraName(String name){
		String hiraName = '★'
		String url = wikiURL + URLEncoder.encode(name, 'UTF-8');
		String content 
		try {
			content = url.toURL().openConnection().with { conn ->
				// 読取タイムアウトは１秒とします
				readTimeout = 1000
				if (responseCode != 200) {
					throw new Exception("HTTPS要求に失敗しました。responseCode=${responseCode}")
				}
				conn.content.withReader { reader -> reader.text }
			}
		} catch(exc) {
			LOG.debug('Wikipediaに未登録の名前です。name={}', name)
			return hiraName
		}
		// Wikipediaの記述から読みを探します
		Closure matched
		matched = { List rexs, String target ->
			if (rexs.size() == 0) return '★'
			String rex = rexs.pop()
			def matcher = (target =~ rex)
			if (matcher.size() == 0) return matched(rexs, target)
			return matcher[0][1]
		}
		return matched([
			/\<rt\>([あ-ん ]+)\<\/rt\>/,
			/\<b\>[ / + name + /]+\<\/b\>\<\/span\>（([あ-ん]+ [あ-ん]+)/,
			/\<b\>[ / + name + /]+\<\/b\>（([あ-ん]+ [あ-ん]+)/,
		], content)
	}
}
