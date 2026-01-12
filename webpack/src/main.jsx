/**
 * メイン処理。
 * URLパスと表示処理との関係を定義します。
 */
import * as React from 'react';
import * as ReactDOM from 'react-dom/client';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { masterLoader } from './controller';
import { Reviews } from './view';
import { Error } from './screen/error';
import { Guide } from './screen/guide';
import { Top, topLoader } from './screen/top';
import { Menu } from './screen/menu';
import { PubyearIndex } from './screen/index_pubyear';
import { AuthorsIndex } from './screen/index_author';
import { TagsIndex } from './screen/index_tag';
import { singleLoader, SingleReview } from './screen/review_single';
import { pubyearLoader, PubyearReviews } from './screen/review_pubyear';
import { authorLoader } from './screen/review_author';
import { tagLoader } from './screen/review_tag';
import './style.css';

const router = createBrowserRouter([
	{
		path: "/",
		element: <Menu />,
		errorElement: <Error />,
		loader: masterLoader,
		children: [
			{
				index: true, 
				element: <Top />,
				loader: topLoader,
			},
			{
				path: "guide/",
				element: <Guide />,
				loader: masterLoader,
			},
			{
				path: "reviews/:reviewID",
				element: <SingleReview />,
				loader: singleLoader,
			},
			{
				path: "pubdates",
				element: <PubyearIndex />,
				loader: masterLoader,
			},
			{
				path: "pubdates/:yyyy",
				element: <PubyearReviews />,
				loader: pubyearLoader,
			},
			{
				path: "authors",
				element: <AuthorsIndex />,
				loader: masterLoader,
			},
			{
				path: "authors/:authorID",
				element: <Reviews />,
				loader: authorLoader,
			},
			{
				path: "tags",
				element: <TagsIndex />,
				loader: masterLoader,
			},
			{
				path: "tags/:tagID",
				element: <Reviews />,
				loader: tagLoader,
			},
		],
	}
], {
	basename: "/readnow",
});

ReactDOM.createRoot(document.getElementById('center')).render(
	<RouterProvider router={router} />
);
