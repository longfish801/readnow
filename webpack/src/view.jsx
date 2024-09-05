
/**
 * ビューの共通処理。
 */
import * as React from 'react';
import { useLoaderData, NavLink } from 'react-router-dom';
import { Helmet, HelmetProvider } from 'react-helmet-async';
import reactStringReplace from 'react-string-replace';
import parse from 'html-react-parser';
import { useScript } from './controller';

/**
 * マスタを格納するクラスです。
 */
export class Master {
	/**
	 * コンストラクタです。
	 * @param {Object} masterData マスタデータ
	 */
	constructor(masterData) {
		this.pubyears = masterData.pubyears;
		this.authors = masterData.authors;
		this.tags = masterData.tags;
		this.bests = masterData.bests;
	}

	/**
	 * 著者名を返します。
	 * @return {string} 著者名
	 */
	getAuthorName(authorID) {
		const author = this.authors[authorID];
		return author.name;
	}

	/**
	 * 著者名の後ろに読みをカッコつきで付与して返します。
	 * 読みが著者名と同じときは、著者名だけを返します。
	 * @return {string} 著者名をルビ付きで表示するためのHTML
	 */
	getAuthorHira(authorID) {
		const author = this.authors[authorID];
		if (author.hiraName === author.name) return author.name;
		return (<>{author.name}（{author.hiraName}）</>);
	}

	/**
	 * 著者名をルビ付きで表示するためのHTMLを返します。
	 * 読みが著者名と同じときは、著者名だけを返します。
	 * @return {string} 著者名をルビ付きで表示するためのHTML
	 */
	getAuthorRuby(authorID) {
		const author = this.authors[authorID];
		if (author.hiraName === author.name) return author.name;
		return (
			<ruby>{author.name}<rp>(</rp><rt>{author.hiraName}</rt><rp>)</rp></ruby>
		);
	}

	/**
	 * 著者名へのリンクを返します。
	 * @return {string} 著者名へのリンク
	 */
	getAuthorLink(authorID) {
		return (
			<NavLink key={authorID} to={`/authors/${authorID}`}>{this.getAuthorName(authorID)}</NavLink>
		);
	}

	/**
	 * タグへのリンクを返します。
	 * @return {string} タグへのリンク
	 */
	getTagLink(tagID) {
		const tag = this.tags[tagID];
		return (
			<NavLink to={`/tags/${tagID}`}>{tag.name}</NavLink>
		);
	}

	/**
	 * ベストへのリンクを返します。
	 * @return {string} ベストへのリンク
	 */
	getBestLink(bestID) {
		const best = this.bests[bestID];
		return (
			<NavLink to={`/bests/${bestID}`}>{best.name}</NavLink>
		);
	}
}

/**
 * 感想データを格納するクラスです。
 */
export class Review {
	/**
	 * コンストラクタです。
	 * インスタンスの生成には buildメソッドのほうを使用してください。
	 * @param {Object} review 感想データ
	 * @param {Master} master Master
	 */
	constructor(review, master) {
		this.id = review.id;
		this._title = review.title;
		this._abbre = review.abbre;
		this._authors = ('authors' in review) ? review.authors : [];
		this._creators = ('creators' in review) ? review.creators : [];
		this.publisher = review.publisher;
		this._pubdate = review.pubdate;
		this.isbn = review.isbn;
		this.keyword = review.keyword;
		this._tags = ('tags' in review) ? review.tags : [];
		this._bests = ('bests' in review) ? review.bests : [];
		this._body = review.body;
		this._secret = review.secret;
		this._note = review.note;
		this.master = master;
	}

	/**
	 * ページ内目次用のリンクを返します。
	 * @return {string} ページ内目次用のリンク
	 */
	get tocLink() {
		return (
			<a href={'#' + this.id}>{parse(this._abbre)}</a>
		);
	}

	/**
	 * タイトルを返します。
	 * @return {string} タイトル
	 */
	get title() {
		return (parse(this._title));
	}

	/**
	 * リンク付きのタイトルを返します。
	 * @return {string} リンク付きのタイトル
	 */
	get titleLink() {
		return (
			<NavLink to={`/reviews/${this.id}`}>{this.title}</NavLink>
		);
	}

	/**
	 * HTMLタグを除いたタイトルを返します。
	 * @return {string} HTMLタグを除いたタイトル
	 */
	get titleWithoutTag() {
		return (
			this._title.replace(/\<([^\>]+)\>/g, '')
		);
	}

	/**
	 * 著者とその他の作り手のリンクを表示します。
	 * @return {string} 著者とその他の作り手のリンク
	 */
	get authors() {
		let authorList = [];
		for (const author of this._authors) {
			authorList.push({ id: author.id, format: author.format });
		}
		for (const creator of this._creators) {
			authorList.push({ id: creator.id, format: creator.format });
		}
		return (
			<>
				{authorList.map((author) => {
					return reactStringReplace(author.format, "%s", (match, cnt) => (
						<span key={cnt}>{this.master.getAuthorLink(author.id)}</span>
					))
				}).reduce((pre, cur) => {
					return [...pre, cur, '、']
				}, []).slice(0, -1)}
			</>
		);
	}

	/**
	 * 刊行年月を返します。
	 * @return {string} 刊行年月
	 */
	get pubdate() {
		return (
			<>
				{convertPubdateFormat(this._pubdate, true)}刊行
			</>
		);
	}

	/**
	 * 書影リンクを返します。
	 * @return {string} 書影リンク
	 */
	get ndlthum() {
		return (
			<>
				<div className="ndlthum" data-isbn13={this.isbn} data-keyword={this.keyword}></div>
			</>
		);
	}

	/**
	 * タグを返します。
	 * @return {string} タグ
	 */
	get tags() {
		if (this._tags.length === 0) return null
		return (
			<>
				{this._tags.map((tagID) => (
					<span key={tagID} className="tag">
						{this.master.getTagLink(tagID)}
					</span>
				))}
			</>
		);
	}

	/**
	 * ベストを返します。
	 * @return {string} ベスト
	 */
	get bests() {
		if (this._bests.length === 0) return null
		return (
			<>
				{this._bests.map((best) => (
					<span key={best.id} className="best">
						{this.master.getBestLink(best.id)} {best.value}
					</span>
				))}
			</>
		);
	}

	/**
	 * ベスト値を返します。
	 * @param bestID {string} ベストID
	 * @return {string} ベスト値
	 */
	bestValue(bestID) {
		return this._bests.find(best => best.id === bestID).value;
	}

	/**
	 * 本文を返します。
	 * @return {string} 本文
	 */
	get body() {
		return (parse(this._body));
	}

	/**
	 * ネタバレを返します。
	 * @return {string} ネタバレ
	 */
	get secret() {
		if (this._secret === undefined) return null;
		const targetID = 'secret_' + this.id;
		return (
			<aside className="alert alert-danger" role="alert">
				<header className="alert-heading text-center">
					<i className="bi bi-exclamation-triangle-fill"></i> 作品の内容に触れています <button type="button" className="revert-unseen btn btn-primary" data-target={targetID}>ネタバレを表示</button>
				</header>
				<div className="unseen" id={targetID}>{parse(this._secret)}</div>
			</aside>
		);
	}

	/**
	 * 付記を返します。
	 * @return {string} 付記
	 */
	get note() {
		if (this._note === undefined) return null;
		return (
			<aside className="alert alert-success" role="alert">
				<header className="alert-heading text-center"><i className="bi bi-info-circle-fill"></i>補足</header>
				{parse(this._note)}
			</aside>
		);
	}

	/**
	 * 感想を表示します。
	 * @return {string} 感想
	 */
	get content() {
		return (
			<div className="review" key={this.id}>
				{this.biblio}
				{this.ndlthum}
				{this.body}
				{this.note}
				{this.secret}
			</div>
		);
	}

	/**
	 * 書誌情報を表示します。
	 * @return {string} 書誌情報
	 */
	get biblio() {
		let bibs = [];
		if (this._authors.length > 0 || this._creators.length > 0) {
			bibs.push(this.authors);
		}
		bibs.push(this.publisher);
		bibs.push(this.pubdate);
		if (this._tags.length > 0) bibs.push(this.tags);
		if (this._bests.length > 0) bibs.push(this.bests);
		return (
			<div className="biblio">
				{bibs.reduce((pre, cur) => {
					return [...pre, cur, ' ']
				}, []).slice(0, -1).map((elem, idx) => {
					return <span key={idx}>{elem}</span>
				})}
			</div>
		)
	}
}

/**
 * 複数の感想を画面に表示します。
 */
export function Reviews() {
	useScript();
	const { view } = useLoaderData();
	return (
		<HelmetProvider>
			<Helmet>
				<title>読了なう - {view.headTitle}</title>
			</Helmet>
			<h1 id="header">{view.pageTitle}</h1>
			{view.toc}
			{view.content}
		</HelmetProvider>
	);
}

/**
 * 複数の感想を表示するためのビューです。
 */
export class ReviewsView {
	/**
	 * コンストラクタです。
	 * @param {Array} reviews Reviewのリスト
	 */
	constructor(reviews) {
		this.reviews = reviews;
	}

	/**
	 * ヘッダ用のタイトルを返します。
	 * @return {string} ヘッダ用のタイトル
	 */
	get headTitle() {
		throw new Error('本メソッドはオーバーライドしてください。');
	}

	/**
	 * タイトルを返します。
	 * @return {string} タイトル
	 */
	get pageTitle() {
		throw new Error('本メソッドはオーバーライドしてください。');
	}

	/**
	 * ページ内目次を表示します。
	 * @return {string} ページ内目次
	 */
	get toc() {
		if (this.reviews.length <= 1) return (<></>);
		return (
			<ul className="toc">
				{this.reviews.map((review) =>
					<li key={review.id}>{review.tocLink}</li>
				)}
			</ul>
		);
	}

	/**
	 * 感想を表示します。
	 * @return {string} 感想
	 */
	get content() {
		return (
			<>
				{this.reviews.map((review) =>
					<div key={review.id}>
						<h2 id={review.id}>
							{review.titleLink}
							<div className="float-end fs-6"><a href="#header"><i className="bi bi-chevron-double-up"></i></a></div>
						</h2>
						{review.content}
					</div>
				)}
			</>
		);
	}
}

/**
 * 刊行年月のフォーマットを変換して返します（"yyyymm"->"yyyy年m月"）。
 * @param {string} 刊行年月（yyyymm）
 * @return {string} 刊行年月（yyyy年m月）
 */
export function convertPubdateFormat(pubdate, doLink = false) {
	let yyyy = pubdate.substring(0, 4);
	let month = pubdate.substring(4);
	if (month.substring(0, 1) === '0') month = month.substring(1);
	let linkedText = (month === '0')? `${yyyy}年` : `${yyyy}年${month}月`;
	if (doLink){
		return (
			<NavLink to={`/pubdates/${yyyy}#${pubdate}`}>{linkedText}</NavLink>
		)
	}
	return (
		<>{linkedText}</>
	)
}
