/**
 * 刊行年別一覧画面。
 */
import * as React from 'react';
import { useLoaderData } from 'react-router-dom';
import { Helmet, HelmetProvider } from 'react-helmet-async';
import { getMasterData } from '../datasource';
import { ReviewHandler } from '../model';
import { useScript } from '../controller';
import { Master, Review, convertPubdateFormat } from '../view';

/**
 * 刊行年別一覧画面に必要なデータを返します。
 * @param {Object} params パスパラメータ
 * @return {PubyearView} PubyearView
 */
export async function pubyearLoader({ params }) {
	let view;
	try {
		const reviewsData = await ReviewHandler.getByPubyear(params.yyyy);
		const masterData = await getMasterData();
		view = PubyearView.build(reviewsData, masterData, params.yyyy);
	} catch (err) {
		const msg = '刊行年別一覧画面に必要なデータを取得できませんでした。';
		console.trace(msg, err, params);
		throw new Response(null, { status: 404, statusText: 'Not Found' });
	}
	return { view };
}

/**
 * 刊行年別一覧画面を表示します。
 */
export function PubyearReviews() {
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
 * 刊行年別一覧画面の表示内容です。
 */
class PubyearView {
	/**
	 * コンストラクタです。
	 * @param {Array} pubdateViews PubdateViewのリスト
	 * @param {String} pubyear 刊行年
	 */
	constructor(pubdateViews, pubyear) {
		this.pubdateViews = pubdateViews;
		this.pubyear = pubyear;
	}

	/**
	 * PubyearViewインスタンスを生成します。
	 * @param {Map} reviewsData 感想オブジェクトを格納したマップ
	 * @param {Object} masterData マスタデータ
	 * @param {String} pubyear 刊行年
	 * @return {PubyearView} PubyearView
	 */
	static build(reviewsData, masterData, pubyear) {
		let pubdateViews = [];
		const master = new Master(masterData)
		reviewsData.forEach((reviewsByPubdate, pubdate) => {
			const view = PubdateView.build(reviewsByPubdate, master, pubdate);
			pubdateViews.push(view);
		})
		return new PubyearView(pubdateViews, pubyear);
	}

	/**
	 * ヘッダ用のタイトルを返します。
	 * @return {string} ヘッダ用のタイトル
	 */
	get headTitle() {
		return `${this.pubyear}年刊行`;
	}

	/**
	 * タイトルを返します。
	 * @return {string} タイトル
	 */
	get pageTitle() {
		return (<>{this.pubyear}年刊行</>);
	}

	/**
	 * ページ内目次を表示します。
	 * @return {string} ページ内目次
	 */
	get toc() {
		return (
			<ul className="toc">
				{this.pubdateViews.map((view) =>
					<li key={view.pubdate}>{convertPubdateFormat(view.pubdate)}
						<ul>{view.toc}</ul>
					</li>
				)}
			</ul>
		);
	}

	/**
	 * 同じ刊行年の感想を表示します。
	 * @return {string} 感想
	 */
	get content() {
		return (
			<>
				{this.pubdateViews.map((view) =>
					<div key={view.pubdate}>
						<h2 id={view.pubdate}>{view.heading}</h2>
						{view.content}
					</div>
				)}
			</>
		);
	}
}

/**
 * 刊行年別一覧画面の刊行年月見出しに表示する内容を格納するクラスです。
 */
class PubdateView {
	/**
	 * コンストラクタです。
	 * @param {Array} reviews Reviewのリスト
	 * @param {String} pubdate 刊行年月
	 */
	constructor(reviews, pubdate) {
		this.reviews = reviews;
		this.pubdate = pubdate;
	}

	/**
	 * PubdateViewインスタンスを生成します。
	 * @param {Map} reviewsByPubdate 感想オブジェクトを格納したマップ
	 * @param {Master} master Master
	 * @param {String} pubdate 刊行年月
	 * @return {PubdateView} PubdateView
	 */
	static build(reviewsByPubdate, master, pubdate) {
		let reviews = [];
		for (const reviewData of reviewsByPubdate.values()) {
			const review = new Review(reviewData, master);
			reviews.push(review);
		}
		return new PubdateView(reviews, pubdate);
	}

	/**
	 * タイトルを返します。
	 * @return {string} タイトル
	 */
	get heading() {
		return (<>{convertPubdateFormat(this.pubdate)}</>);
	}

	/**
	 * ページ内目次を表示します。
	 * @return {string} ページ内目次
	 */
	get toc() {
		return (
			<>
				{this.reviews.map((review) =>
					<li key={review.id}>{review.tocLink}</li>
				)}
			</>
		);
	}

	/**
	 * 同じ刊行年月の感想を表示します。
	 * @return {string} 感想
	 */
	get content() {
		return (
			<>
				{this.reviews.map((review) =>
					<div key={review.id}>
						<h3 id={review.id}>
							{review.titleLink}
							<div className="float-end fs-6"><a href="#header"><i className="bi bi-chevron-double-up"></i></a></div>
						</h3>
						{review.content}
					</div>
				)}
			</>
		);
	}
}
