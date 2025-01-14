/**
 * データモデル。
 * データソースからのデータ取得を仲介します。
 */
import { getReviwsData } from './datasource';

/**
 * 感想オブジェクトを取得するためのクラスです。
 */
export class ReviewHandler {
	/**
	 * 指定されたIDの感想オブジェクトを返します。
	 * IDに対応する感想がない場合は例外をスローします。
	 * @param {string} reviewID ID
	 * @return {Object} 感想オブジェクト
	 */
	static async get(reviewID) {
		const reviews = await ReviewHandler.getByIDs([reviewID]);
		return reviews[0];
	}

	/**
	 * IDのリストに対応する感想オブジェクトのリストを返します。
	 * IDに対応する感想がない場合は例外をスローします。
	 * @param {Array} reviewIDs IDのリスト
	 * @return {Array} 感想オブジェクトのリスト
	 */
	static async getByIDs(reviewIDs) {
		// IDを刊行年毎に分類します
		let idsMap = {};
		for (const reviewID of reviewIDs) {
			const pubyear = reviewID.substring(0, 4);
			if (!idsMap.hasOwnProperty(pubyear)) {
				idsMap[pubyear] = [];
			}
			idsMap[pubyear].push(reviewID);
		}

		// IDが一致するレビューを参照します
		let reviews = [];
		let missIDs = [];
		const pubyears = Object.keys(idsMap);
		pubyears.sort();
		for (const pubyear of pubyears) {
			const reviewMap = await getReviwsData(pubyear);
			const ids = idsMap[pubyear];
			ids.sort();
			for (const reviewID of ids) {
				if (reviewMap[reviewID] === undefined) {
					missIDs.push(reviewID);
					continue;
				}
				reviews.push(reviewMap[reviewID]);
			}
		}
		if (missIDs.length > 0) {
			const msg = 'IDに対応する感想がありません。';
			console.error(msg, missIDs, reviewIDs);
			throw new Error(msg);
		}
		return reviews;
	}
}

