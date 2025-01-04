/*
 * WikiTool.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package tool

import groovy.util.logging.Slf4j
import tool.Config as conf

/**
 * Wikipediaから情報を取得します。
 */
@Slf4j('LOG')
class WikiTool {
	/** WikipediaのURL */
	static String wikiURL = conf.wiki.url

	/**
	 * 指定された名前の読み仮名と思われる文字列を Wikipediaから参照します。
	 * 読み仮名を取得できなかった場合は“★”を返します。
	 * @param name 著者名
	 * @return 著者名の読み仮名と思われる文字列
	 */
	static String referHiraName(String name){
		// Wikipediaから名前に該当するページがあればページ内容を取得します
		String content = getContent(name)
		if (content == null) return '★'

		// Wikipediaの記述内容から正規表現で読みを探します
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

	/**
	 * 指定された名前に相当するWikipediaの記述内容を取得します。
	 * WikipediaのURLが空文字、あるいは接続エラー時はnullを返します。
	 * @param name 著者名
	 * @return Wikipediaの記述内容
	 */
	private static String getContent(String name){
		if (wikiURL.empty) return null
		String content
		try {
			String url = wikiURL + URLEncoder.encode(name, 'UTF-8')
			LOG.info("Wikipediaにアクセスします。 ${url}")
			content = url.toURL().openConnection().with { conn ->
				// 読取タイムアウトは１秒とします
				readTimeout = 1000
				if (responseCode != 200) {
					throw new Exception("HTTPS要求に失敗しました。responseCode=${responseCode}")
				}
				conn.content.withReader { reader -> reader.text }
			}
		} catch(exc) {
			LOG.debug('Wikipediaに未登録の名前です。name={}', name, exc)
		}
		return content
	}
}
