/**
 * ベスト別一覧画面。
 */
import * as React from 'react';
import { getMasterData } from '../datasource';
import { ReviewHandler } from '../model';
import { Master, Review, ReviewsView } from '../view';

/**
 * ベスト別一覧画面に必要なデータを返します。
 * @param {Object} params パスパラメータ
 * @return {ReviewView} ReviewView
 */
export async function bestLoader({ params }) {
	let view;
	try {
		const masterData = await getMasterData();
		const reviewIDs = masterData['bests'][params.bestID]['reviews'];
		const reviewsData = await ReviewHandler.getByIDs(reviewIDs);
		view = BestView.build(reviewsData, masterData, params.bestID);
	} catch (err) {
		const msg = 'ベスト別一覧画面に必要なデータを取得できませんでした。';
		console.trace(msg, err, params);
		throw new Response(null, { status: 404, statusText: 'Not Found' });
	}
	return { view };
}

/**
 * ベスト別一覧画面の表示内容です。
 */
class BestView extends ReviewsView {
	/**
	 * コンストラクタです。
	 * @param {Array} reviews Reviewのリスト
	 * @param {Master} master Master
	 * @param {String} bestID ベストID
	 */
	constructor(reviews, master, bestID) {
		reviews.sort((pre, nxt) => {
			const preBest = pre._bests.find((best) => best.id === bestID);
			const nxtBest = nxt._bests.find((best) => best.id === bestID);
			return (preBest.value < nxtBest.value) ? 1 : -1;
		})
		super(reviews);
		this.master = master;
		this.bestID = bestID;
	}

	/**
	 * BestViewインスタンスを生成します。
	 * @param {Array} reviewsData 感想オブジェクトのリスト
	 * @param {Object} masterData マスタデータ
	 * @param {String} bestID ベストID
	 * @return {BestView} BestView
	 */
	static build(reviewsData, masterData, bestID) {
		let reviews = [];
		const master = new Master(masterData);
		for (const reviewData of reviewsData){
			const review = new Review(reviewData, master);
			reviews.push(review);
		}
		return new BestView(reviews, master, bestID);
	}

	/** @inheritdoc */
	get headTitle() {
		const best = this.master.bests[this.bestID];
		return best.name;
	}

	/** @inheritdoc */
	get pageTitle() {
		const best = this.master.bests[this.bestID];
		return (best.name);
	}

	/** @inheritdoc */
	get toc() {
		if (this.reviews.length <= 1) return (<></>);
		return (
			<ul className="toc">
				{this.reviews.map((review) =>
					<li key={review.id}>
						{review.bestValue(this.bestID)} {review.tocLink}
					</li>
				)}
			</ul>
		);
	}

	/** @inheritdoc */
	get content() {
		return (
			<>
				{this.reviews.map((review) =>
					<div key={review.id}>
						<h2 id={review.id}>
							{review.bestValue(this.bestID)} {review.titleLink}
							<div className="float-end fs-6"><a href="#header"><i className="bi bi-chevron-double-up"></i></a></div>
						</h2>
						{review.content}
					</div>
				)}
			</>
		);
	}
}
