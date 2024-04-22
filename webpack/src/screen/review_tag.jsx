/**
 * タグ別一覧画面。
 */
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
}
