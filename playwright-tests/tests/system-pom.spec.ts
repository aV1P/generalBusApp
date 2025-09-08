import { test, expect } from '@playwright/test';
import { LoginPage, SystemsPage, SystemDetailsPage } from '../page-objects';

// Test data
const credentials = {
  username: 'pppwww',
  password: 'pppwww'
};

const vlgAptvDetails = {
  systemCode: 'VLG.APTV',
  groupNumber: '18',
  subsystem: '2',
  accessType: 'WS-SOAP',
  name: '[ Пул №3 ] APTV СЛТУ Аргус МРФ Волга',
  url: 'http://VLG.ARGUS:80/rt-gate/NetworkCapabilityWebService/',
  startThreadNumber: '111',
  threadCount: '6',
  username: 'hermes_user',
  password: 'IXWEB78gAY-134',
  shortRetryAttempts: '3',
  shortRetryInterval: '30',
  longRetryAttempts: '5',
  longRetryInterval: '600',
  helpingThreadsCount: '6',
  helpingThreadsList: '117,118,119'
};

test.describe('VLG.APTV System Management with Page Objects', () => {
  test.beforeEach(async ({ page }) => {
    const loginPage = new LoginPage(page);
    
    // Navigate to the application
    await page.goto('/');
    
    // Check if already logged in
    const isLoggedIn = await loginPage.isLoggedIn(credentials.username);
    
    if (!isLoggedIn) {
      // Log in if not already logged in
      await loginPage.login(credentials.username, credentials.password);
    }
    
    // Navigate to Systems section
    const systemsPage = new SystemsPage(page);
    await systemsPage.navigateTo();
  });

  test('should view VLG.APTV system details', async ({ page }) => {
    const systemsPage = new SystemsPage(page);
    const systemDetailsPage = new SystemDetailsPage(page);
    
    // Search for VLG.APTV system
    await systemsPage.searchForSystem(vlgAptvDetails.systemCode);
    
    // Click on the edit link to view details
    await systemsPage.clickEditForSystem(vlgAptvDetails.systemCode);
    
    // Verify URL is correct
    await expect(page).toHaveURL(`/MessageDirections/${vlgAptvDetails.systemCode}`);
    
    // Verify system details
    await expect(systemDetailsPage.systemCodeInput).toHaveValue(vlgAptvDetails.systemCode);
    await expect(systemDetailsPage.groupNumberInput).toHaveValue(vlgAptvDetails.groupNumber);
    await expect(systemDetailsPage.nameInput).toHaveValue(vlgAptvDetails.name);
    await expect(systemDetailsPage.urlInput).toHaveValue(vlgAptvDetails.url);
    await expect(systemDetailsPage.startThreadNumberInput).toHaveValue(vlgAptvDetails.startThreadNumber);
    await expect(systemDetailsPage.threadCountInput).toHaveValue(vlgAptvDetails.threadCount);
    await expect(systemDetailsPage.systemUsernameInput).toHaveValue(vlgAptvDetails.username);
  });

  test('should filter systems by code', async ({ page }) => {
    const systemsPage = new SystemsPage(page);
    
    // Clear any existing filters and search for VLG prefix
    await systemsPage.searchForSystem('VLG');
    
    // Verify only VLG systems are shown
    const rows = page.getByRole('row').filter({ hasText: /VLG/ });
    await expect(rows).not.toHaveCount(0);
    
    // Check that all visible rows contain VLG
    const nonVlgRows = page.getByRole('row').filter({ hasNotText: /VLG/ });
    // Skip header row in the check
    const visibleNonVlgRows = nonVlgRows.filter({ hasNotText: 'Сортировать' });
    await expect(visibleNonVlgRows).toHaveCount(0);
  });

  test('should sort systems by update date', async ({ page }) => {
    const systemsPage = new SystemsPage(page);
    
    // Click on update date column to sort
    await systemsPage.sortByUpdateDate();
    
    // Click again to reverse sort order
    await systemsPage.sortByUpdateDate();
    
    // Verify the sort indicator changed (visual verification or more complex logic would be needed)
  });
});
