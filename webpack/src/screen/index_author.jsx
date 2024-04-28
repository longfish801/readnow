/**
 * 著者目次画面。
 */
import * as React from 'react';
import { NavLink, useLoaderData } from 'react-router-dom';
import { Helmet, HelmetProvider } from 'react-helmet-async';

export function AuthorsIndex() {
	const { master } = useLoaderData();
	const sortedAuthors = Object.values(master.authors).sort(function(pre, nxt) {
		return (pre.hiraName < nxt.hiraName) ? -1 : 1;
	});
	let authorMap = {};
	for (const author of sortedAuthors) {
		// 著者名の読みの一文字目を取得します
		let firstChr = '他';
		if (author.hiraName.length > 0) {
			firstChr = author.hiraName.substring(0, 1);
		}
		if (!(firstChr in authorMap)) {
			authorMap[firstChr] = [];
		}
		authorMap[firstChr].push(author);
	}
	const sortedKeys = Object.keys(authorMap).sort()
	return (
		<HelmetProvider>
			<Helmet>
				<title>読了なう - 著者一覧</title>
			</Helmet>
			<h1 id="header">著者一覧</h1>
			<div className="toc">
			{sortedKeys.map((firstChr, idx) => (
				<a href={'#author' + idx} key={'toc' + idx}>{firstChr}</a>
			)).reduce((pre, cur) => {
				return [...pre, cur, ' / ']
			}, []).slice(0, -1)}
			</div>
			{sortedKeys.map((firstChr, idx) => (
				<div key={'author' + idx}>
					<h2 id={'author' + idx}>
						{firstChr}
						<div className="float-end fs-6"><a href="#header"><i className="bi bi-chevron-double-up"></i></a></div>
					</h2>
					<ul>
					{authorMap[firstChr].map(author => (
						<li key={author.id}><NavLink to={`/authors/${author.id}`}>{master.getAuthorHira(author.id)}</NavLink></li>
					))}
					</ul>
				</div>
			))}
		</HelmetProvider>
	);
}
