/**
 * 案内画面。
 */
import * as React from 'react';
import { useLoaderData } from 'react-router-dom';
import { useScript } from '../controller';

export function Guide() {
	useScript();
	const { master } = useLoaderData();
	// 感想の総件数を求めます
	let totalRevNum = 0;
	Object.keys(master.pubdates).forEach(yyyy => {
		Object.keys(master.pubdates[yyyy]).forEach(yyyymm => {
			totalRevNum += master.pubdates[yyyy][yyyymm].length;
		});
	});

	return (
		<>
			<h1>ご案内</h1>
			<h2>このサイトについて</h2>
			<p>　本の感想を載せています。<br />
			　国内のミステリ作品が大半です。</p>

			<p>　2010年からTwitter / X、2024年からBlueskyにて週に一冊ほどのペースで感想をつぶやいており、それをまとめたものとなります。<br />
			　新しい感想は年に一回まとめて追加します。<br />
			　現在、{totalRevNum.toLocaleString()}件の感想があります。</p>

			<h2>注意点</h2>
			<ul>
				<li>必ずしも Twitter / X / Blueskyでの公開時の文章のままではありません。<br />
					推敲するなど手を加えていることがあります。</li>
				<li>刊行年は、その作品が書籍として初めて刊行された年としています。<br />
					たとえば読んだのが文庫落ち、別の出版社からの再刊や復刊、新装改訂版の類の場合は、その本が刊行された年を記しているわけではないことにご注意ください。<br />
					また翻訳作品について、小説は海外での刊行年を、それ以外は国内での刊行年を記しています。</li>
				<li>デジタルフォントでは表示できない一部の文字について代替となる文字を使用しています。<br />
					たとえば泡坂妻夫の「泡」は、正しくは「己」ではなく「巳」です。</li>
				<li>書影には<a href="https://ndlsearch.ndl.go.jp/help/api/thumbnail" target="_base">国立国会図書館サーチが提供する書影API</a>を利用しています。<br />
					書影をクリックすると作品タイトルなどによる Google検索の結果が表示されます。</li>
				<li>シリーズ名はできるだけ帯やあらすじ紹介、版元のサイトに記されているものを使用していますが、みあたらない場合は独自に考案したものを用いる場合があります。</li>
				<li>作品の内容に踏みこみすぎて興趣を削ぐ（いわゆるネタバレ）ことにならないよう留意しています。<br />
					とはいえ、どこからがネタバレに相当するのかは明確な基準はなく、人によって判断が異なります。ご了承いただければと思います。</li>
				<li>Twitter上で2018年9月の終わり頃以降に投稿した感想については奥付に記された刊行年月を記しています。それ以前は出版社のサイトや<a href="https://ja.wikipedia.org/" target="_base">Wikipedia</a>、<a href="https://www.amazon.co.jp/gp/browse.html?node=465392&ref_=nav_em__jb_0_2_10_2"  target="_base">Amazon</a>の商品ページから刊行年月と思われるものを参照しているため、本来の刊行年月とは一月ほど前後している可能性があります。</li>
			</ul>
		</>
	);
}
