/**
 * 感想表示画面。
 */
import * as React from 'react';
import { useLoaderData } from 'react-router-dom';
import { Helmet, HelmetProvider } from 'react-helmet-async';
import { getMasterData } from '../datasource';
import { ReviewHandler } from '../model';
import { useScript } from '../controller';
import { Master, Review } from '../view';

/**
 * 感想表示画面に必要なデータを返します。
 * @param {Object} params パスパラメータ
 * @return {Review} Review
 */
export async function singleLoader({ params }) {
	let review;
	try {
		const reviewData = await ReviewHandler.get(params.reviewID);
		const masterData = await getMasterData();
		const master = new Master(masterData);
		review = new Review(reviewData, master);
	} catch (err) {
		const msg = '感想表示画面に必要なデータを取得できませんでした。';
		console.trace(msg, err, params);
		throw new Response(null, { status: 404, statusText: 'Not Found' });
	}
	return { review };
}

/**
 * 感想表示画面を表示します。
 */
export function SingleReview() {
	useScript();
	const { review } = useLoaderData();
	return (
		<HelmetProvider>
			<Helmet>
				<title>読了なう - {review.titleWithoutTag}</title>
			</Helmet>
			<h1 id="header">{review.title}</h1>
			{review.content}
		</HelmetProvider>
	);
}
