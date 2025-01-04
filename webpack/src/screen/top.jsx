/**
 * トップ画面。
 */
import * as React from 'react';
import { Helmet, HelmetProvider } from 'react-helmet-async';
import { NavLink, useLoaderData } from 'react-router-dom';
import { useScript } from '../controller';
import { getMasterData } from '../datasource';
import { ReviewHandler } from '../model';
import { Master, Review, ReviewsView } from '../view';

/**
 * トップ画面の表示内容です。
 */
export function Top() {
	useScript();
	const { view } = useLoaderData();
	return (
		<HelmetProvider>
			<Helmet>
				<title>読了なう</title>
			</Helmet>
			<h1 id="header">読了なう</h1>
			<p>　主に国内ミステリについて本の感想を載せています。<br />
			　詳細は<NavLink to="/guide/">ご案内</NavLink>を参照してください。<br />
			　以下はランダムに感想を５件表示しています。</p>
			{view.toc}
			{view.content}
		</HelmetProvider>
	);
}

/**
 * トップ画面に必要なデータを返します。
 * @param {Object} params パスパラメータ
 * @return {ReviewView} ReviewView
 */
export async function topLoader({ params }) {
	let view;
	try {
		// 過去10年分のレビューIDのリストを作成します
		const masterData = await getMasterData();
		const pubyears = masterData.pubyears.slice(-10);
		let reviewIDs = [];
		for (const yyyy of pubyears){
			Object.values(masterData.pubdates[yyyy]).forEach(yyyymmRevIDs => {
				reviewIDs.push(...yyyymmRevIDs);
			});
		}
		// 5件のレビューIDをランダムに選びます
		const randomReviewIDs = [];
		while (randomReviewIDs.length < 5) {
			const randomIdx = Math.floor(Math.random() * reviewIDs.length);
			if (!randomReviewIDs.includes(reviewIDs[randomIdx])){
				randomReviewIDs.push(reviewIDs[randomIdx]);
			}
		}
		// 感想データを取得します
		const reviewsData = await ReviewHandler.getByIDs(randomReviewIDs);
		view = TopView.build(reviewsData, masterData);
	} catch (err) {
		const msg = 'トップ画面に必要なデータを取得できませんでした。';
		console.trace(msg, err, params);
		throw new Response(null, { status: 404, statusText: 'Not Found' });
	}
	return { view };
}

/**
 * トップ画面の表示内容です。
 */
class TopView extends ReviewsView {
	/**
	 * コンストラクタです。
	 * @param {Array} reviews Reviewのリスト
	 * @param {Master} master Master
	 */
	constructor(reviews, master) {
		super(reviews);
		this.master = master;
	}

	/**
	 * TopViewインスタンスを生成します。
	 * @param {Array} reviewsData 感想オブジェクトのリスト
	 * @param {Object} masterData マスタデータ
	 * @param {String} tagID タグID
	 * @return {TagView} TagView
	 */
	static build(reviewsData, masterData) {
		let reviews = [];
		const master = new Master(masterData);
		for (const reviewData of reviewsData){
			const review = new Review(reviewData, master);
			reviews.push(review);
		}
		return new TopView(reviews, master);
	}
}
