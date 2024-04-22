/**
 * ベスト目次画面。
 */
import * as React from 'react';
import { NavLink, useLoaderData } from 'react-router-dom';
import { Helmet, HelmetProvider } from 'react-helmet-async';

export function BestsIndex() {
	const { master } = useLoaderData();
	return (
		<HelmetProvider>
			<Helmet>
				<title>読了なう - ベスト一覧</title>
			</Helmet>
			<h1 id="header">ベスト一覧</h1>
			<ul>
				{Object.values(master.bests).sort((pre, nxt) => {
					return (pre.name < nxt.name) ? -1 : 1;
				}).map(best => (
					<li key={best.id}><NavLink to={`/bests/${best.id}`}>{best.name}</NavLink></li>
				))}
			</ul>
		</HelmetProvider>
	);
}
