/*
 * HTMLTool.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package tool

import io.github.longfish801.yakumo.Yakumo
import groovy.util.logging.Slf4j

/**
 * HTML関連の変換処理をします。
 */
@Slf4j('LOG')
class HTMLTool {
	/**
	 * 指定されたマップの値をHTML化します。
	 * @param textMap マップ
	 * @return 値をHTML化したマップ
	 */
	static Map htmlize(Map textMap){
		Map htmlMap = textMap.collectEntries { [it.key, new StringWriter()] }
		Yakumo yakumo = new Yakumo()
		yakumo.load {
			material 'fyakumo', 'thtml'
			material new File('src/yakumo/material')
		}
		yakumo.script {
			targets {
				textMap.each { String key, String text ->
					target key, text
				}
			}
			results {
				textMap.each { String key, String text ->
					result key, htmlMap[key]
				}
			}
		}
		yakumo.convert()
		return htmlMap
	}
	
	/**
	 * HTML文法上の特殊文字を実体参照に置換します。
	 * @param text 対象文字列
	 * @return HTML文法上の特殊文字を実体参照に置換した文字列
	 */
	static String escapeHtml(String text){
		text = text.replaceAll(/\&/, '&amp;')
		text = text.replaceAll(/\</, '&lt;')
		text = text.replaceAll(/\>/, '&gt;')
		text = text.replaceAll(/\"/, '&quot;')
		return text
	}
}
