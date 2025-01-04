/**
 * 刊行年目次画面。
 */
import * as React from 'react';
import { NavLink, useLoaderData } from 'react-router-dom';
import { Helmet, HelmetProvider } from 'react-helmet-async';

export function PubyearIndex() {
	const { master } = useLoaderData();
	const pubyears = master.pubyears.reverse()
	return (
		<HelmetProvider>
			<Helmet>
				<title>読了なう - 刊行年一覧</title>
			</Helmet>
			<h1 id="header">刊行年一覧</h1>
			<ul>
				{pubyears.map((pubyear) => (
					<li key={pubyear}><NavLink to={`/pubdates/${pubyear}`}>{pubyear}年</NavLink></li>
				))}
			</ul>
		</HelmetProvider>
	);
}
