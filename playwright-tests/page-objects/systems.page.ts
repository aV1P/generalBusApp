import { Page, Locator } from '@playwright/test';

export class SystemsPage {
  readonly page: Page;
  readonly createButton: Locator;
  readonly systemCodeInput: Locator;
  readonly systemNameInput: Locator;
  readonly groupNumberInput: Locator;
  readonly exportButton: Locator;
  readonly sortByUpdateDateButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.createButton = page.getByRole('link', { name: 'Создать' });
    this.systemCodeInput = page.getByLabel('Код системы');
    this.systemNameInput = page.getByLabel('Название');
    this.groupNumberInput = page.getByLabel('№ группы-Класс');
    this.exportButton = page.getByRole('button', { name: 'Экспорт' });
    this.sortByUpdateDateButton = page.getByRole('button', { name: /Сортировать по Обновлено/ });
  }

  async navigateTo() {
    await this.page.getByRole('link', { name: /Системы Системы/ }).click();
  }

  async searchForSystem(systemCode: string) {
    await this.systemCodeInput.clear();
    await this.systemCodeInput.fill(systemCode);
  }

  async clickEditForSystem(systemCode: string) {
    const systemRow = this.page.getByRole('row', { name: new RegExp(systemCode) });
    await systemRow.getByLabel('Изменить').click();
  }

  async deleteSystem() {
    const deleteButton = this.page.getByRole('button', { name: 'Удалить' });
    await deleteButton.click();
    
    // Confirm deletion in dialog
    await this.page.getByRole('button', { name: 'OK' }).click();
  }

  async sortByUpdateDate() {
    await this.sortByUpdateDateButton.click();
  }
}
