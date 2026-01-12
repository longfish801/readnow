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
		// 過去10年からランダムに5年を選び、そこから1件ずつランダムに選びます
		const masterData = await getMasterData();
		let randomReviewIDs = [];
		getRandomNums(masterData.pubyears.slice(-10), 5).forEach(yyyy => {
			getRandomNums(Object.keys(masterData.pubdates[yyyy]), 1).forEach(yyyymm => {
				getRandomNums(masterData.pubdates[yyyy][yyyymm], 1).forEach(reviewID => {
					randomReviewIDs.push(reviewID);
				});
			});
		});
		// 感想データを取得します
		const reviewsData = await ReviewHandler.getByIDs(randomReviewIDs);
		view = TopView.build(reviewsData.reverse(), masterData);
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

/**
 * 配列の要素からランダムかつ重複無しの要素を必要個数だけ返します。
 * なお必要個数が配列の要素数以上ならば、配列をそのまま返します。
 * @param {Array} list 配列
 * @param {number} size 必要個数
 * @return {Array} ランダムな要素のリスト
 */
function getRandomNums(list, size){
	let idxList = [];
	if (list.length <= size) return list;
	while (idxList.length < size) {
		const idx = Math.floor(Math.random() * list.length);
		if (!idxList.includes(idx)) idxList.push(idx);
	}
	return idxList.sort().map((idx) => list[idx]);
}
