describe('spec', () => {
	it('トップ画面', () => {
		cy.visit('/readnow/')
			.get('h1')
			.should('have.text', '読了なう')
	})
	it('案内画面', () => {
		cy.visit('/readnow/guide')
			.get('h1')
			.should('have.text', 'ご案内')
	})
	it('刊行年目次画面', () => {
		cy.visit('/readnow/pubdates')
			.get('h1')
			.should('have.text', '刊行年一覧')
	})
	it('刊行年別一覧画面', () => {
		cy.visit('/readnow/pubdates/2022')
			.get('h1')
			.should('have.text', '2022年刊行')
	})
	it('著者目次画面', () => {
		cy.visit('/readnow/authors')
			.get('h1')
			.should('have.text', '著者一覧')
	})
	it('著者別一覧画面', () => {
		cy.visit('/readnow/authors/0001')
			.get('h1')
			.contains('方丈貴恵')
	})
	it('タグ目次画面', () => {
		cy.visit('/readnow/tags')
			.get('h1')
			.should('have.text', 'タグ一覧')
	})
	it('タグ別一覧画面', () => {
		cy.visit('/readnow/tags/0001')
			.get('h1')
			.should('have.text', '特殊設定ミステリ')
	})
	it('感想表示画面', () => {
		cy.visit('/readnow/reviews/20220101')
			.get('h1')
			.should('have.text', '名探偵に甘美なる死を')
	})
})
