/*
 * RnowServer.groovy
 *
 * Copyright (C) io.github.longfish801 All Rights Reserved.
 */
package data

import groovy.util.logging.Slf4j
import io.github.longfish801.tpac.tea.TeaDec
import io.github.longfish801.tpac.tea.TeaHandle
import io.github.longfish801.tpac.tea.TeaMaker
import io.github.longfish801.tpac.tea.TeaServer

/**
 * master文書とreviews文書を保持します。
 */
@Slf4j('LOG')
class RnowServer implements TeaServer {
	/**
	 * 宣言のタグに対応する生成器を返します。<br/>
	 * masterタグに対し {@link MasterMaker}のインスタンスを生成して返します。<br/>
	 * clmapタグに対し {@link ReviewsMaker}のインスタンスを生成して返します。<br/>
	 * それ以外はオーバーライド元のメソッドの戻り値を返します。
	 * @param tag 宣言のタグ
	 * @return TeaMaker
	 */
	@Override
	TeaMaker newMaker(String tag){
		if (tag == 'master') return new MasterMaker(server: this)
		if (tag == 'reviews') return new ReviewsMaker(server: this)
		return TeaServer.super.newMaker(tag)
	}

	/**
	 * master記法の文字列の解析にともない、各要素を生成します。
	 */
	class MasterMaker implements TeaMaker {
		/** RnowServer */
		RnowServer server
		
		/**
		 * 宣言を生成します。<br/>
		 * {@link Rnow}インスタンスを生成して返します。
		 * @param tag タグ
		 * @param name 名前
		 * @return 宣言
		 */
		@Override
		TeaDec newTeaDec(String tag, String name){
			return new MasterData()
		}
	}

	/**
	 * reviews記法の文字列の解析にともない、各要素を生成します。
	 */
	class ReviewsMaker implements TeaMaker {
		/** RnowServer */
		RnowServer server
		
		/**
		 * 宣言を生成します。<br/>
		 * {@link Rnow}インスタンスを生成して返します。
		 * @param tag タグ
		 * @param name 名前
		 * @return 宣言
		 */
		@Override
		TeaDec newTeaDec(String tag, String name){
			return new ReviewList()
		}
		
		/**
		 * ハンドルを生成します。
		 * @param tag タグ
		 * @param name 名前
		 * @param upper 上位ハンドル
		 * @return ハンドル
		 */
		@Override
		TeaHandle newTeaHandle(String tag, String name, TeaHandle upper){
			if (tag == 'review'){
				return new ReviewData()
			}
			return TeaMaker.super.newTeaHandle(tag, name, upper)
		}
	}
}
