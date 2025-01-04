
/**
 * コントローラの共通処理。
 */
import * as React from 'react';
import { useLocation } from 'react-router-dom';
import { getMasterData } from './datasource';
import { Master } from './view';

/**
 * 外部のJavaScriptを利用します。
 */
export function useScript() {
	const location = useLocation();
	React.useEffect(() => {
		let isScriptLoad = false;
		if (document.getElementById('main_external') == null) {
			isScriptLoad = true;
			const scriptTag = document.createElement('script');
			scriptTag.id = 'main_external';
			scriptTag.type = 'module';
			scriptTag.src = '/script/main_external.js';
			const head = document.getElementsByTagName('head')[0];
			head.appendChild(scriptTag);
		}
		window.dispatchEvent(new Event('newscreen'));
		// 初回はJavaScriptの読込に時間を要するため、
		// 一秒だけ待機してからイベントを起こす
		if (isScriptLoad){
			setTimeout(() => {
				window.dispatchEvent(new Event('newscreen'));
			}, 1000);
		}
	}, [location.pathname]);
}

/**
 * マスタを取得するローダーです。
 * @return {Master} Master
 */
export async function masterLoader() {
	const masterData = await getMasterData();
	const master = new Master(masterData);
	return { master };
}
