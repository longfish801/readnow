/**
 * メニュー。
 */
import * as React from 'react';
import { NavLink, Outlet, ScrollRestoration, useLoaderData } from 'react-router-dom';

export function Menu() {
	const { master } = useLoaderData();
	return (
		<>
			<ScrollRestoration />
			<nav id="sidenav" className="col-lg-4 py-3">
				<ul id="toc" className="nav flex-column">
				<li className="nav-item toc-h2" key="guide"><NavLink to="/guide" className="nav-link">ご案内</NavLink></li>
				<li className="nav-item toc-h2" key="pubyear"><NavLink to="/pubdates" className="nav-link">刊行年</NavLink></li>
				<li className="nav-item toc-h2" key="authors"><NavLink to="/authors" className="nav-link">著者名</NavLink></li>
					<li className="nav-item toc-h2" key="tags"><NavLink to="/tags" className="nav-link">タグ</NavLink></li>
					{master.categories.map(category => ( categoryLink(category) ))}
				</ul>
			</nav>
			<main id="main" className="col-lg-8 py-3">
				<Outlet />
			</main>
		</>
	);
}

/**
 * タグ目次画面内のカテゴリへのリンクです。
 * @param {Object} category カテゴリ情報
 * @return {String} タグ目次画面内のカテゴリへのリンク
 */
function categoryLink(category) {
	return (
		<li className="nav-item toc-h3" key={category.id}><NavLink to={'/tags#' + category.id} className="nav-link">{category.name}</NavLink></li>
	);
}
