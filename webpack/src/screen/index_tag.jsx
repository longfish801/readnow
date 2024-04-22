/**
 * タグ目次画面。
 */
import * as React from 'react';
import { NavLink, useLoaderData } from 'react-router-dom';
import { Helmet, HelmetProvider } from 'react-helmet-async';

export function TagsIndex() {
	const { master } = useLoaderData();
	return (
		<HelmetProvider>
			<Helmet>
				<title>読了なう - タグ一覧</title>
			</Helmet>
			<h1 id="header">タグ一覧</h1>
			<ul>
				{Object.values(master.tags).sort((pre, nxt) => {
					return (pre.name < nxt.name) ? -1 : 1;
				}).map(tag => (
					<li key={tag.id}><NavLink to={`/tags/${tag.id}`}>{tag.name}</NavLink></li>
				))}
			</ul>
		</HelmetProvider>
	);
}
