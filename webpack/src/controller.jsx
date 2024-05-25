
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
		if (document.getElementById('main_external') == null) {
			const scriptTag = document.createElement('script');
			scriptTag.id = 'main_external';
			scriptTag.type = 'module';
			scriptTag.src = '/script/main_external.js';
			const head = document.getElementsByTagName('head')[0];
			head.appendChild(scriptTag);
		}
		window.dispatchEvent(new Event('newscreen'));
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
