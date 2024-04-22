
// 想定外の例外は無視します。
Cypress.on('uncaught:exception', (err, runnable) => {
	return false
})

// ターミナルにログを出力します。
Cypress.Commands.overwrite('log', (subject, message) => {
	cy.task('log', message)
})

// 指定されたキーワードでAmazonを検索しISBNを取得します。
Cypress.Commands.add('get_isbn', (name, keywords) => {
	// 負荷の集中を避けるため一秒間待機する
	cy.wait(1000)
	// 本を検索する
	cy.visit('/')
		.get('input#twotabsearchtextbox')
		.type(keywords)
		.get('select#searchDropdownBox')
		.select('本', { force: true })
		.get('input#nav-search-submit-button')
		.click()
	// 検索結果の先頭のリンクをクリックする
	cy.get('h2.a-size-mini a')
		.first()
		.then(($anchor) => {
			cy.get_isbn_prodpage($anchor.attr('href'), name, keywords, true)
		})
})

// 商品ページからISBN-13を探してログ出力します。
Cypress.Commands.add('get_isbn_prodpage', (path, name, keywords, more) => {
	cy.visit(path)
		.get('div#detailBullets_feature_div')
		.then(($div) => {
			let $isbn = $div.find("span:contains('ISBN-13')")
			if ($isbn !== undefined && $isbn.length > 0) {
				let isbn_str = $isbn.next().text().replace(/\-/g, '')
				cy.log(name + "\t" + isbn_str + "\t" + path)
			} else {
				if (!more) {
					cy.log(name + '=FAILED')
					return
				}
				// ISBN-13がなければ他の商品ページを探します
				cy.get('div#formats a')
					.then(($anchors) => {
						cy.get_isbn_prodpage($anchors.last().attr('href'), name, keywords, false)
					})
			}
		})
})
