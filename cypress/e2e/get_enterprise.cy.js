describe('Логин и изменение часового пояса предприятия', () => {
  it('логинится, находит Lada, меняет часовой пояс на +05:00 и сохраняет', () => {
    const username = 'manager1';
    const password = '12345';

    cy.visit('http://localhost:8080/login');

    cy.get('#username').type(username);
    cy.get('#password').type(password, { log: false });
    cy.get('button[type="submit"]').click();

    cy.url().should('include', '/view/enterprises');

    cy.get('#enterpriseTableBody tr')
        .contains('td', 'Lada')
        .parents('tr')
        .as('ladaRow');

    cy.get('@ladaRow').find('td').eq(4).find('input')
        .clear()
        .type('+05:00');

    cy.get('@ladaRow').find('td').eq(6).contains('button', 'Сохранить').click();

    cy.get('@ladaRow').find('td').eq(4).find('input')
        .should('have.value', '+05:00');
  });
});