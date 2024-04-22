/**
 * 著者別一覧画面。
 */
import { getMasterData } from '../datasource';
import { ReviewHandler } from '../model';
import { Master, Review, ReviewsView } from '../view';

/**
 * 著者別一覧画面に必要なデータを返します。
 * @param {Object} params パスパラメータ
 * @return {ReviewView} ReviewView
 */
export async function authorLoader({ params }) {
	let view;
	try {
		const masterData = await getMasterData();
		const reviewIDs = masterData['authors'][params.authorID]['reviews'];
		const reviewsData = await ReviewHandler.getByIDs(reviewIDs);
		view = AuthorView.build(reviewsData, masterData, params.authorID);
	} catch (err) {
		const msg = '著者別一覧画面に必要なデータを取得できませんでした。';
		console.trace(msg, err, params);
		throw new Response(null, { status: 404, statusText: 'Not Found' });
	}
	return { view };
}

/**
 * 著者別一覧画面の表示内容です。
 */
class AuthorView extends ReviewsView {
	/**
	 * コンストラクタです。
	 * @param {Array} reviews Reviewのリスト
	 * @param {Master} master Master
	 * @param {String} authorID 著者ID
	 */
	constructor(reviews, master, authorID) {
		super(reviews);
		this.master = master;
		this.authorID = authorID;
	}

	/**
	 * AuthorViewインスタンスを生成します。
	 * @param {Array} reviewsData 感想オブジェクトのリスト
	 * @param {Object} masterData マスタデータ
	 * @param {String} authorID 著者ID
	 * @return {AuthorView} AuthorView
	 */
	static build(reviewsData, masterData, authorID) {
		let reviews = [];
		const master = new Master(masterData)
		for (const reviewData of reviewsData){
			const review = new Review(reviewData, master);
			reviews.push(review);
		}
		return new AuthorView(reviews, master, authorID);
	}

	/** @inheritdoc */
	get headTitle() {
		const author = this.master.authors[this.authorID];
		return author.name;
	}

	/** @inheritdoc */
	get pageTitle() {
		return (
			this.master.getAuthorRuby(this.authorID)
		);
	}
}
