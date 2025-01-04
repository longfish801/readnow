/*
 * PublicMaster.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import io.github.longfish801.tpac.tea.TeaDec
import tool.HTMLTool

/**
 * 公開マスタです。
 */
@Slf4j('LOG')
class PublicMaster extends LinkedHashMap {
	/**
	 * コンストラクタ。
	 * @param masterData マスタ
	 */
	PublicMaster(TeaDec masterData){
		generateBase(masterData)
	}

	/**
	 * 公開マスタのベースを生成します。
	 * 公開マスタのベースとは、マスタの内容をMapに置き換えたものです。
	 * マスタ内での順番を踏まえてidを生成します。
	 * この時点では感想への索引は未作成です。
	 * @param masterData マスタ
	 * @return 公開マスタのベース
	 */
	void generateBase(TeaDec masterData){
		this['pubyears'] = masterData.refer('pubyears#')

		Map authors = [:]
		int authorID = 0
		masterData.solve('authors').lowers.values().each { def handle ->
			String id = String.format('%04d', ++authorID)
			authors[id] = [
				'id': id,
				'name': HTMLTool.escapeHtml(handle.name),
				'hiraName': handle.hiraName,
			]
		}
		this['authors'] = authors

		Map tags = [:]
		Map tagMap = [:]
		int tagID = 0
		masterData.solve('tags').lowers.values().each { def handle ->
			String id = String.format('%04d', ++tagID)
			tags[id] = [
				'id': id,
				'name': HTMLTool.escapeHtml(handle.name),
			]
			tagMap[handle.name] = id
		}
		this['tags'] = tags

		int categoryID = 0
		this['categories'] = masterData.solve('categories').lowers.values().collect { def handle ->
			return [
				'id': String.format('%04d', ++categoryID),
				'name': HTMLTool.escapeHtml(handle.name),
				'tags': handle.dflt.collect { tagMap[it] },
			]
		}
	}

	/**
	 * 逆索引を生成します。
	 * 逆索引とはたとえば authorsなら、キーが著者名、値が著者IDのマップです。
	 * @return 逆索引
	 */
	Map generateReverseIndex(){
		Map masterRidx = [:]
		masterRidx['authors'] = this['authors'].values().collectEntries { Map author ->
			[author.name, author.id]
		}
		masterRidx['tags'] = this['tags'].values().collectEntries { Map tag ->
			[tag.name, tag.id]
		}
		return masterRidx
	}

	/**
	 * マスタに感想データへの索引を格納します。
	 * @param publicReviews 公開感想のリスト
	 */
	void createReviewIndex(Map publicReviews){
		if (!this.containsKey('pubdates')){
			this['pubdates'] = [:]
		}
		publicReviews.each { String id, Map review ->
			String yyyy = review.pubdate.substring(0, 4)
			if (!this.pubdates.containsKey(yyyy)){
				this.pubdates[yyyy] = [:]
			}
			if (!this.pubdates[yyyy].containsKey(review.pubdate)){
				this.pubdates[yyyy][review.pubdate] = []
			}
			this.pubdates[yyyy][review.pubdate] << id
			review.authors.each { Map author ->
				if (!this.authors[author.id].containsKey('reviews')){
					this.authors[author.id].reviews = []
				}
				this.authors[author.id].reviews << id
			}
			review.creators.each { Map creator ->
				if (!this.authors[creator.id].containsKey('reviews')){
					this.authors[creator.id].reviews = []
				}
				this.authors[creator.id].reviews << id
			}
			review.tags.each { Map tag ->
				if (!this.tags.containsKey(tag.id)){
					LOG.info('★tag={}', tag)
					LOG.info('★tags={}', this.tags)
				}
				if (!this.tags[tag.id].containsKey('reviews')){
					this.tags[tag.id].reviews = []
				}
				this.tags[tag.id].reviews << id
			}
		}
	}
}
