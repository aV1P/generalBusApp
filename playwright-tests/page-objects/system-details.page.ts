import { Page, Locator } from '@playwright/test';

export class SystemDetailsPage {
  readonly page: Page;
  readonly systemCodeInput: Locator;
  readonly groupNumberInput: Locator;
  readonly nameInput: Locator;
  readonly accessTypeDropdown: Locator;
  readonly urlInput: Locator;
  readonly startThreadNumberInput: Locator;
  readonly threadCountInput: Locator;
  readonly systemUsernameInput: Locator;
  readonly systemPasswordInput: Locator;
  readonly shortRetryAttemptsInput: Locator;
  readonly shortRetryIntervalInput: Locator;
  readonly longRetryAttemptsInput: Locator;
  readonly longRetryIntervalInput: Locator;
  readonly helpingThreadsCountInput: Locator;
  readonly helpingThreadsListInput: Locator;
  readonly saveButton: Locator;
  readonly deleteButton: Locator;

  constructor(page: Page) {
    this.page = page;
    this.systemCodeInput = page.getByLabel('Код системы');
    this.groupNumberInput = page.getByLabel('№ группы-Класс');
    this.nameInput = page.getByLabel('Название');
    this.accessTypeDropdown = page.getByLabel('Тип доступа');
    this.urlInput = page.getByLabel('URL доступа');
    this.startThreadNumberInput = page.getByLabel('Начальный № потока');
    this.threadCountInput = page.getByLabel('Количество потоков');
    this.systemUsernameInput = page.getByLabel('Учетная запись системы');
    this.systemPasswordInput = page.getByLabel('Пароль к системе');
    this.shortRetryAttemptsInput = page.getByLabel('Количество попыток через короткий интервал');
    this.shortRetryIntervalInput = page.getByLabel('Короткий интервал между попытками');
    this.longRetryAttemptsInput = page.getByLabel('Количество попыток через длинный интервал');
    this.longRetryIntervalInput = page.getByLabel('Длинный интервал между попытками');
    this.helpingThreadsCountInput = page.getByLabel('Количество потоков, которые могут помогать по списку');
    this.helpingThreadsListInput = page.getByLabel('Список № потоков через запятую которым будет оказана помощь');
    this.saveButton = page.getByRole('button', { name: 'Сохранить' });
    this.deleteButton = page.getByRole('button', { name: 'Удалить' });
  }

  async fillSystemDetails(details: {
    systemCode?: string;
    groupNumber?: string;
    name?: string;
    accessType?: string;
    url?: string;
    startThreadNumber?: string;
    threadCount?: string;
    username?: string;
    password?: string;
    shortRetryAttempts?: string;
    shortRetryInterval?: string;
    longRetryAttempts?: string;
    longRetryInterval?: string;
    helpingThreadsCount?: string;
    helpingThreadsList?: string;
  }) {
    if (details.systemCode) {
      await this.systemCodeInput.fill(details.systemCode);
    }
    if (details.groupNumber) {
      await this.groupNumberInput.fill(details.groupNumber);
    }
    if (details.name) {
      await this.nameInput.fill(details.name);
    }
    if (details.accessType) {
      await this.accessTypeDropdown.selectOption({ label: details.accessType });
    }
    if (details.url) {
      await this.urlInput.fill(details.url);
    }
    if (details.startThreadNumber) {
      await this.startThreadNumberInput.fill(details.startThreadNumber);
    }
    if (details.threadCount) {
      await this.threadCountInput.fill(details.threadCount);
    }
    if (details.username) {
      await this.systemUsernameInput.fill(details.username);
    }
    if (details.password) {
      await this.systemPasswordInput.fill(details.password);
    }
    if (details.shortRetryAttempts) {
      await this.shortRetryAttemptsInput.fill(details.shortRetryAttempts);
    }
    if (details.shortRetryInterval) {
      await this.shortRetryIntervalInput.fill(details.shortRetryInterval);
    }
    if (details.longRetryAttempts) {
      await this.longRetryAttemptsInput.fill(details.longRetryAttempts);
    }
    if (details.longRetryInterval) {
      await this.longRetryIntervalInput.fill(details.longRetryInterval);
    }
    if (details.helpingThreadsCount) {
      await this.helpingThreadsCountInput.fill(details.helpingThreadsCount);
    }
    if (details.helpingThreadsList) {
      await this.helpingThreadsListInput.fill(details.helpingThreadsList);
    }
  }

  async save() {
    await this.saveButton.click();
  }

  async delete() {
    await this.deleteButton.click();
    // Confirm deletion (may need to be adjusted based on actual dialog)
    await this.page.getByRole('button', { name: 'OK' }).click();
  }
}
