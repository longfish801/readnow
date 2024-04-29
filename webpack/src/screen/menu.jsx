/**
 * メニュー。
 */
import * as React from 'react';
import { NavLink, Outlet, ScrollRestoration } from 'react-router-dom';

export function Menu() {
	return (
		<>
			<ScrollRestoration />
			<nav id="sidenav" className="col-lg-4 py-3">
				<ul id="toc" className="nav flex-column">
					<li className="nav-item toc-h2" key="pubyear"><NavLink to="/pubdates" className="nav-link">刊行年</NavLink></li>
					<li className="nav-item toc-h2" key="authors"><NavLink to="/authors" className="nav-link">著者名</NavLink></li>
					<li className="nav-item toc-h2" key="tags"><NavLink to="/tags" className="nav-link">タグ</NavLink></li>
					<li className="nav-item toc-h2" key="bests"><NavLink to="/bests" className="nav-link">ベスト</NavLink></li>
				</ul>
			</nav>
			<main id="main" className="col-lg-8 py-3">
				<Outlet />
			</main>
		</>
	);
}
