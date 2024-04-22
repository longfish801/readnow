/*
 * PublicData.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.json.JsonBuilder
import groovy.json.StringEscapeUtils
import groovy.util.logging.Slf4j
import io.github.longfish801.tpac.TpacDec
import io.github.longfish801.tpac.TpacHandle
import io.github.longfish801.tpac.TpacServer
import io.github.longfish801.yakumo.Yakumo

@Slf4j('LOG')
class PublicData {
	/** 公開原稿フォルダ */
	File pubDir
	/** 整形された状態で出力するか否か */
	boolean pretty
	/** マスタの公開原稿ファイル名 */
	String masterFname = 'master.json'
	/** レビューの公開原稿ファイル名 */
	String reviewFname = 'reviews%s.json'
	
	/**
	 * コンストラクタ。
	 * @param pubDir 公開原稿フォルダ
	 * @param pretty 整形された状態で出力するか否か
	 */
	PublicData(File pubDir, boolean pretty){
		this.pubDir = pubDir
		this.pretty = pretty
	}
	
	/**
	 * 公開原稿をjsonファイルとして保存します。
	 * @param draftsPub 公開原稿
	 */
	void save(Map draftsPub){
		draftsPub.each { String key, Map map ->
			File file = (key == 'master')?
				new File(pubDir, masterFname) : 
				new File(pubDir, String.format(reviewFname, key))
			LOG.info('公開原稿ファイルを出力しますpath={}', file.absolutePath)
			def builder = new JsonBuilder(map)
			String jsonStr = pretty ? builder.toPrettyString() : builder.toString()
			file.text = (pretty)? StringEscapeUtils.unescapeJavaScript(jsonStr) : jsonStr
		}
	}

	/**
	 * 原稿から公開原稿を生成します。
	 * 公開原稿はマップです。
	 * キーは、マスタならば "master"、感想ならば刊行年(yyyy)です。
	 * 値は JSONとして出力するファイル内容を Mapとして作成したものです。
	 * @param drafts 原稿
	 * @return 公開原稿
	 */
	Map generate(TpacServer drafts){
		LOG.info('公開マスタのベースを生成します')
		Map masterPub = generateMasterBase(drafts['master'])

		LOG.info('マスタから逆索引を生成します')
		Map masterRidx = generateReverseIndex(masterPub)
		
		Map draftsPub = [:]
		drafts.findAll(/reviews:\d{4}/).each { TpacDec reviewsYear ->
			LOG.info('感想データのJSONデータ（{}年）を生成します', reviewsYear.name)
			draftsPub[reviewsYear.name] = generateReviews(reviewsYear, masterRidx)
		}
		
		LOG.info('マスタに感想データへの索引を格納します')
		createReviewIndex(masterPub, draftsPub)
		draftsPub['master'] = masterPub
		
		return draftsPub
	}

	/**
	 * 公開マスタのベースを生成します。
	 * @param master マスタ
	 * @return 公開マスタ
	 */
	Map generateMasterBase(def master){
		Map masterPub = [:]
		masterPub['pubyears'] = master.refer('pubyears#')

		Map authors = [:]
		int idNum = 0
		master.solve('authors').lowers.values().each { def handle ->
			String id = String.format('%04d', ++idNum)
			authors[id] = [
				'id': id,
				'name': escapeHtml(handle.name),
				'hiraName': handle.hiraName,
				'reviews': [],
			]
		}
		masterPub['authors'] = authors

		Map tags = [:]
		idNum = 0
		master.solve('tags').lowers.values().each { def handle ->
			String id = String.format('%04d', ++idNum)
			tags[id] = [
				'id': id,
				'name': escapeHtml(handle.name),
				'reviews': [],
			]
		}
		masterPub['tags'] = tags

		Map bests = [:]
		idNum = 0
		master.solve('bests').lowers.values().each { def handle ->
			String id = String.format('%04d', ++idNum)
			bests[id] = [
				'id': id,
				'name': escapeHtml(handle.name),
				'reviews': [],
			]
		}
		masterPub['bests'] = bests
		return masterPub
	}

	/**
	 * 逆索引を生成します。
	 * 逆索引とはたとえば authorsなら、キーが著者名、値が著者IDのマップです。
	 * @param masterPub 公開マスタ
	 * @return 逆索引
	 */
	Map generateReverseIndex(Map masterPub){
		Map masterRidx = [:]
		masterRidx['authors'] = masterPub['authors'].values().collectEntries { Map author ->
			[author.name, author.id]
		}
		masterRidx['tags'] = masterPub['tags'].values().collectEntries { Map tag ->
			[tag.name, tag.id]
		}
		masterRidx['bests'] = masterPub['bests'].values().collectEntries { Map best ->
			[best.name, best.id]
		}
		return masterRidx
	}

	/**
	 * 指定した刊行年について公開感想を生成します。
	 * @param reviews 感想
	 * @param masterRidx 逆索引
	 * @return 指定した刊行年の公開感想
	 */
	Map generateReviews(TpacDec reviews, Map masterRidx){
		// HTML化します
		Map textMap = [:]
		reviews.findAll(/pubdate:.+/).each { TpacHandle pubdateHndl ->
			pubdateHndl.findAll(/review:.+/).each { TpacHandle review ->
				['title', 'abbre', 'body', 'secret', 'note'].each { String key ->
					String text
					if (key == 'abbre'){
						text = review.name.replaceAll(/_/, ' ')
						text = text.replaceAll(/\|/, ' / ')
					} else if (review.map.containsKey(key)){
						text = review.referAsString("#${key}")
					}
					if (text != null){
						def revPublicate = review as ReviewPublicate
						textMap[revPublicate.htmlKey(key)] = text
					}
					
				}
			}
		}
		Map htmlMap = htmlize(textMap)

		// 公開感想を生成します
		return reviews.lowers.values().collectEntries { def pubdates ->
			List reviewHandles = pubdates.lowers.values().withIndex()
			Map reviewMap = reviewHandles.collectEntries { def review, int idx ->
				def revPublicate = review as ReviewPublicate
				revPublicate.masterRidx = masterRidx
				revPublicate.htmlMap = htmlMap
				revPublicate.pubdateIdx = idx
				return generateReview(revPublicate)
			}
			return [ PublicData.convertPubdate(pubdates.name), reviewMap ]
		}
	}

	/**
	 * 公開感想を生成します。
	 * @param revPublicate 公開感想への変換のための感想
	 * @return リスト（第一要素がID、第二要素が公開感想）
	 */
	List generateReview(def revPublicate){
		Map reviewPub = [
			'id': revPublicate.idAsPub,
			'title': revPublicate.titleAsPub,
			'abbre': revPublicate.abbreAsPub,
			'publisher': escapeHtml(revPublicate.publisher),
			'pubdate': revPublicate.pubdateAsPub,
			'isbn': revPublicate.isbn,
			'keyword': escapeHtml(revPublicate.keyword),
			'body': revPublicate.bodyAsPub,
		]
		if (revPublicate.map.containsKey('authors')){
			reviewPub.authors = revPublicate.authorsAsPub
		}
		if (revPublicate.map.containsKey('creators')){
			reviewPub.creators = revPublicate.creatorsAsPub
		}
		if (revPublicate.map.containsKey('tags')){
			reviewPub.tags = revPublicate.tagsAsPub
		}
		if (revPublicate.map.containsKey('bests')){
			reviewPub.bests = revPublicate.bestsAsPub
		}
		if (revPublicate.map.containsKey('secret')){
			reviewPub.secret = revPublicate.secretAsPub
		}
		if (revPublicate.map.containsKey('note')){
			reviewPub.note = revPublicate.noteAsPub
		}
		return [ reviewPub.id, reviewPub ]
	}

	/**
	 * 感想を公開感想に変換するための特性です。
	 */
	trait ReviewPublicate {
		/** 逆索引 */
		Map masterRidx
		/** HTML化した文字列のマップ */
		Map htmlMap
		/** 同じ刊行年月での通番 */
		int pubdateIdx

		/**
		 * IDを返します。
		 * @return ID
		 */
		String getIdAsPub(){
			return pubdateAsPub + String.format('%02d', pubdateIdx + 1)
		}

		/**
		 * HTML化したタイトルを返します。
		 * @return HTML化したタイトル
		 */
		String getTitleAsPub(){
			return formatHTML('title', true)
		}

		/**
		 * HTML化した略称を返します。
		 * @return HTML化した略称
		 */
		String getAbbreAsPub(){
			return formatHTML('abbre', true)
		}

		/**
		 * HTML化した本文を返します。
		 * @return HTML化した本文
		 */
		String getBodyAsPub(){
			return formatHTML('body', false)
		}

		/**
		 * HTML化したネタバレを返します。
		 * @return HTML化したネタバレ
		 */
		String getSecretAsPub(){
			return formatHTML('secret', false)
		}

		/**
		 * HTML化した付記を返します。
		 * @return HTML化した付記
		 */
		String getNoteAsPub(){
			return formatHTML('note', false)
		}

		/**
		 * 刊行年月をyyyymm形式で返します。
		 * @return 刊行年月(yyyymm)
		 */
		String getPubdateAsPub(){
			return PublicData.convertPubdate(pubdate)
		}

		/**
		 * 著者をIDとフォーマットに置き換えたリストを返します。
		 * @return 著者をIDとフォーマットに置き換えたリスト
		 */
		List getAuthorsAsPub(){
			return publicateWithFormat(authors, masterRidx['authors'])
		}

		/**
		 * 作り手をIDとフォーマットに置き換えたリストを返します。
		 * @return 作り手をIDとフォーマットに置き換えたリスト
		 */
		List getCreatorsAsPub(){
			return publicateWithFormat(creators, masterRidx['authors'])
		}

		/**
		 * タグをIDに置き換えたリストを返します。
		 * @return タグをIDに置き換えたリスト
		 */
		List getTagsAsPub(){
			Map ridx = masterRidx['tags']
			return [tags].flatten().collect { String tag ->
				return ridx[tag]
			}
		}

		/**
		 * ベストをIDとフォーマットに置き換えたリストを返します。
		 * @return ベストをIDとフォーマットに置き換えたリスト
		 */
		List getBestsAsPub(){
			Map ridx = masterRidx['bests']
			return [bests].flatten().collect { String best ->
				List splited = best.split(' ')
				return ['id': ridx[splited[0]], 'value': splited[1]]
			}
		}

		/**
		 * HTML化した文字列のマップのためのキーを返します。
		 * @return HTML化した文字列のマップのためのキー
		 */
		String htmlKey(String key){
			return [pubdateAsPub, name, key].join('-')
		}

		/**
		 * HTML化した文字列を整形します。
		 * @param key キー
		 * @param removeParaTag 先頭と末尾の段落タグを削除するか否か
		 * @return 整形したHTML文字列
		 */
		String formatHTML(String key, boolean removeParaTag){
			String htmlStr = htmlMap[htmlKey(key)].toString().trim()
			if (removeParaTag){
				htmlStr = htmlStr.replaceFirst('^<p>', '')
				htmlStr = htmlStr.replaceFirst('</p>$', '')
			}
			return htmlStr
		}

		/**
		 * 指定された値をIDとフォーマットに置き換えたリストを返します。
		 * @param val 値
		 * @param ridx 逆索引
		 * @return 指定されたキーの値をIDとフォーマットに置き換えたリスト
		 */
		List publicateWithFormat(def val, Map ridx){
			return ((val instanceof List)? val : [val]).collect { String elem ->
				String id
				String format
				if (elem.indexOf('［') < 0){
					id = ridx[val]
					format = "%s"
				} else if (elem.indexOf('［') == 0){
					elem = elem.replaceFirst(/［([^］]+)］/, '$1\t')
					List splited = elem.split('\t')
					id = ridx[splited[1]]
					format = "［${splited[0]}］%s"
				} else {
					elem = elem.replaceFirst(/［([^］]+)］/, '\t$1')
					List splited = elem.split('\t')
					id = ridx[splited[0]]
					format = "%s［${splited[1]}］"
				}
				return ['id': id, 'format': format]
			}
		}
	}

	/**
	 * マスタに感想データへの索引を格納します。
	 * @param masterPub 公開マスタ
	 * @param draftsPub 公開感想
	 */
	void createReviewIndex(Map masterPub, Map draftsPub){
		draftsPub.values().each { Map pubdatesMap ->
			pubdatesMap.values().each { Map reviewsMap ->
				reviewsMap.each { String id, Map review ->
					review.authors.each { Map author ->
						masterPub.authors[author.id].reviews << id
					}
					review.creators.each { Map creator ->
						masterPub.authors[creator.id].reviews << id
					}
					review.tags.each { String tagID ->
						masterPub.tags[tagID].reviews << id
					}
					review.bests.each { Map best ->
						masterPub.bests[best.id].reviews << id
					}
				}
			}
		}
	}

	/**
	 * 指定されたマップの値をHTML化します。
	 * @param textMap マップ
	 * @return 値をHTML化したマップ
	 */
	Map htmlize(Map textMap){
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

	/**
	 * 刊行年月をyyyymm形式で返します。
	 * @param pubdate 刊行年月(yyyy年m月)
	 * @return 刊行年月(yyyymm)
	 */
	static String convertPubdate(String pubdate){
		List splited = pubdate.split(/[年月]/)
		String yyyy = splited[0]
		String mm = (splited[1].size() == 1)? '0' + splited[1] : splited[1]
		return "${yyyy}${mm}"
	}
}
