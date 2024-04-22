/**
 * データソース。
 * 公開原稿からデータを取得します。
 * セッションストレージにキャッシュします。
 */
import axios from 'axios';

/**
 * 公開マスタファイルからマスタデータを参照して返します。
 * セッションストレージにキャッシュします。
 * 取得失敗時は例外を投げます。
 * @return {Object} マスタデータ
 */
export async function getMasterData() {
	if (sessionStorage.getItem('master') === null) {
		let res;
		try {
			res = await axios.get(`/readnow/json/master.json`);
		} catch (err) {
			res = err.response;
		}
		if (res.status != 200) {
			const msg = 'マスタの取得に失敗しました。';
			console.error(msg, res);
			throw new Error(msg);
		}
		sessionStorage.setItem('master', JSON.stringify(res.data));
	}
	const jsonStr = sessionStorage.getItem('master');
	return JSON.parse(jsonStr);
}

/**
 * 指定された刊行年について感想データを返します。
 * セッションストレージにキャッシュします。
 * 取得失敗時は例外を投げます。
 * @param {string} pubyear 刊行年
 * @return {Object} 感想データ
 */
export async function getReviwsData(pubyear) {
	if (sessionStorage.getItem(pubyear) === null) {
		let res;
		try {
			res = await axios.get(`/readnow/json/reviews${pubyear}.json`);
		} catch (err) {
			res = err.response;
		}
		if (res.status != 200) {
			const msg = `レビュー${pubyear}年の取得に失敗しました。`;
			console.error(msg, res);
			throw new Error(msg);
		}
		sessionStorage.setItem(pubyear, JSON.stringify(res.data));
	}
	const jsonStr = sessionStorage.getItem(pubyear);
	return JSON.parse(jsonStr);
}
