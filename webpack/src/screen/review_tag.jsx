/**
 * タグ別一覧画面。
 */
import * as React from 'react';
import { getMasterData } from '../datasource';
import { ReviewHandler } from '../model';
import { Master, Review, ReviewsView } from '../view';

/**
 * タグ別一覧画面に必要なデータを返します。
 * @param {Object} params パスパラメータ
 * @return {ReviewView} ReviewView
 */
export async function tagLoader({ params }) {
	let view;
	try {
		const masterData = await getMasterData();
		const reviewIDs = masterData['tags'][params.tagID]['reviews'];
		const reviewsData = await ReviewHandler.getByIDs(reviewIDs);
		view = TagView.build(reviewsData, masterData, params.tagID);
	} catch (err) {
		const msg = 'タグ別一覧画面に必要なデータを取得できませんでした。';
		console.trace(msg, err, params);
		throw new Response(null, { status: 404, statusText: 'Not Found' });
	}
	return { view };
}

/**
 * タグ別一覧画面の表示内容です。
 */
class TagView extends ReviewsView {
	/**
	 * コンストラクタです。
	 * @param {Array} reviews Reviewのリスト
	 * @param {Master} master Master
	 * @param {String} tagID タグID
	 */
	constructor(reviews, master, tagID) {
		// タグ値がある場合はそれでソートします
		reviews.sort((pre, nxt) => {
			const preTag = pre._tags.find((tag) => tag.id === tagID);
			const nxtTag = nxt._tags.find((tag) => tag.id === tagID);
			const preValue = ('value' in preTag)? preTag.value : '';
			const nxtValue = ('value' in nxtTag)? nxtTag.value : '';
			return (preValue < nxtValue) ? 1 : -1;
		})
		super(reviews);
		this.master = master;
		this.tagID = tagID;
	}

	/**
	 * TagViewインスタンスを生成します。
	 * @param {Array} reviewsData 感想オブジェクトのリスト
	 * @param {Object} masterData マスタデータ
	 * @param {String} tagID タグID
	 * @return {TagView} TagView
	 */
	static build(reviewsData, masterData, tagID) {
		let reviews = [];
		const master = new Master(masterData);
		for (const reviewData of reviewsData){
			const review = new Review(reviewData, master);
			reviews.push(review);
		}
		return new TagView(reviews, master, tagID);
	}

	/** @inheritdoc */
	get headTitle() {
		const tag = this.master.tags[this.tagID];
		return tag.name;
	}

	/** @inheritdoc */
	get pageTitle() {
		const tag = this.master.tags[this.tagID];
		return (tag.name);
	}

	/** @inheritdoc */
	get toc() {
		if (this.reviews.length <= 1) return (<></>);
		return (
			<ul className="toc">
				{this.reviews.map((review) =>
					<li key={review.id}>
						{review.tagValue(this.tagID, '%s ')}{review.tocLink}
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
							{review.tagValue(this.tagID, '%s ')}{review.titleLink}
							<div className="float-end fs-6"><a href="#header"><i className="bi bi-chevron-double-up"></i></a></div>
						</h2>
						{review.content}
					</div>
				)}
			</>
		);
	}
}
