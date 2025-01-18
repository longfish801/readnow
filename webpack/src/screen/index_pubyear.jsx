/**
 * 刊行年目次画面。
 */
import * as React from 'react';
import { NavLink, useLoaderData } from 'react-router-dom';
import { Helmet, HelmetProvider } from 'react-helmet-async';

export function PubyearIndex() {
	const { master } = useLoaderData();
	const pubyears = master.pubyears.reverse();
	// 刊行年毎の件数を算出します
	let numByYear = {};
	pubyears.forEach(yyyy => {
		numByYear[yyyy] = 0;
		Object.keys(master.pubdates[yyyy]).forEach(yyyymm => {
			numByYear[yyyy] += master.pubdates[yyyy][yyyymm].length;
		});
	});

	return (
		<HelmetProvider>
			<Helmet>
				<title>読了なう - 刊行年一覧</title>
			</Helmet>
			<h1 id="header">刊行年一覧</h1>
			<ul>
				{pubyears.map((pubyear) => (
					<li key={pubyear}>
						<NavLink to={`/pubdates/${pubyear}`}>
							{pubyear}年
						</NavLink>&ensp;{numByYear[pubyear]}件
					</li>
				))}
			</ul>
		</HelmetProvider>
	);
}
