/*
 * MasterData.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.tea.TeaDec
import io.github.longfish801.tpac.tea.TeaHandle
import tool.WikiTool

/**
 * マスタの宣言です。
 * tpacファイル上のmasterハンドルに相当します。
 */
@Slf4j('LOG')
class MasterData implements TeaDec {
	/**
	 * 追加感想をマスタに反映します。
	 * @param newReview 追加感想
	 */
	void merge(ReviewData newReview){
		// 刊行年のリストに不足があれば追記します
		if (solve('pubyears') == null){
			this << new TpacHandle(tag: 'pubyears')
			solve('pubyears').dflt = []
		}
		if (!solve('pubyears').dflt.contains(newReview.pubYearAsStr)){
			solve('pubyears').dflt << newReview.pubYearAsStr
			solve('pubyears').dflt.sort()
		}

		// 存在しない著者名があればハンドルを追加します
		if (solve('authors') == null){
			this << new TpacHandle(tag: 'authors')
		}
		List authors = []
		authors << newReview.authors
		if (newReview.creators != null) authors << newReview.creators
		authors.flatten().collect {
			// 補足情報を削除します
			it.replaceAll(/［([^］]+)］/, '')
		}.each { String author ->
			if (solve('authors').solve("author:${author}") == null){
				def authorHandle = new TpacHandle(tag: 'author', name: author)
				authorHandle.hiraName = WikiTool.referHiraName(author)
				solve('authors') << authorHandle
			}
		}

		// 存在しないタグがあればハンドルを追加します
		// あわせてカテゴリにも未整理として追加します
		if (newReview.tags != null){
			if (solve('tags') == null){
				this << new TpacHandle(tag: 'tags')
			}
			if (solve('categories') == null){
				this << new TpacHandle(tag: 'categories')
			}
			[ newReview.tags ].flatten().each { String tag ->
				// タグ値を削除します
				tag = tag.replaceAll(/\ .+$/, '')
				if (solve('tags').solve("tag:${tag}") == null){
					solve('tags') << new TpacHandle(tag: 'tag', name: tag)
					if (solve('categories/category:★未整理') == null){
						solve('categories') << new TpacHandle(tag: 'category', name: '★未整理')
						solve('categories/category:★未整理').dflt = []
					}
					solve('categories/category:★未整理').dflt << tag
				}
			}
		}
	}
}
