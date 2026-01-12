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
			{master.categories.map(category => (
				tagsEachCategoryLinks(category, master)
			))}
		</HelmetProvider>
	);
}

/**
 * カテゴリ毎のタグ別一覧画面へのリンク一覧です。
 * @param {Object} category カテゴリ情報
 * @param {Master} master マスタ
 * @return {String} カテゴリ配下のタグ別一覧画面へのリンク
 */
function tagsEachCategoryLinks(category, master) {
	return (
		<div key={category.id}>
			<h2 id={category.id}>
				{category.name}
				<div className="float-end fs-6"><a href="#header"><i className="bi bi-chevron-double-up"></i></a></div>
			</h2>
			<ul>
			{category.tags.map(tagID => (
				reviewTagLink(master.tags[tagID])
			))}
			</ul>
		</div>
	);
}

/**
 * タグ別一覧画面へのリンクです。
 * @param {Object} tag タグ情報
 * @return {String} タグ別一覧画面へのリンク
 */
function reviewTagLink(tag) {
	return (
		<li key={tag.id}>
			<NavLink to={`/tags/${tag.id}`}>
				{tag.name}
			</NavLink>&ensp;{tag.reviews.length}件
		</li>
	);
}
