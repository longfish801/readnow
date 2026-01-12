/**
 * エラー画面。
 */
import * as React from 'react';
import { useRouteError } from 'react-router-dom';

export function Error() {
	const error = useRouteError();
	console.error(error);
	return (
		<main id="main" className="col-lg-8 py-3">
			<h1>問題が発生しました</h1>
			<p>　この画面はURLを直接編集した場合などに表示されることがあります。<br />
			　しばらく時間を措いてから再度試してみてください。</p>
			<p>
				<i>{error.statusText || error.message}</i>
			</p>
		</main>
	);
}
