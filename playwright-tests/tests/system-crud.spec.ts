import { test, expect } from '@playwright/test';

// Test data
const credentials = {
  username: 'pppwww',
  password: 'pppwww'
};

// New system test data - we'll use a test prefix to avoid conflicts with real systems
const testSystemDetails = {
  systemCode: 'TEST.APTV.TEMP',
  groupNumber: '18',
  subsystem: '2',
  accessType: 'WS-SOAP',
  name: 'Test APTV System for Automation',
  url: 'http://test.example.com/service/',
  startThreadNumber: '900',
  threadCount: '2',
  username: 'test_user',
  password: 'test_password',
  shortRetryAttempts: '2',
  shortRetryInterval: '15',
  longRetryAttempts: '3',
  longRetryInterval: '300'
};

test.describe('System CRUD Operations', () => {
  test.beforeEach(async ({ page }) => {
    // Navigate to the application
    await page.goto('/');
    
    // Check if already logged in by looking for the profile button with username
    const isLoggedIn = await page.getByRole('button', { name: new RegExp(`Учетная запись: ${credentials.username}`) }).isVisible();
    
    if (!isLoggedIn) {
      // Log in if not already logged in
      await page.fill('input[name="username"]', credentials.username);
      await page.fill('input[name="password"]', credentials.password);
      await page.click('button[type="submit"]');
    }
    
    // Navigate to Systems section
    await page.getByRole('link', { name: /Системы Системы/ }).click();
  });
  
  // Note: This is a sequence test that should run as a whole
  test('should perform full CRUD lifecycle on a test system', async ({ page }) => {
    // 1. CREATE: Navigate to create new system page
    await page.getByRole('link', { name: 'Создать' }).click();
    
    // Fill in required fields
    await page.getByLabel('Код системы').fill(testSystemDetails.systemCode);
    await page.getByLabel('№ группы-Класс').fill(testSystemDetails.groupNumber);
    await page.getByLabel('Название').fill(testSystemDetails.name);
    await page.getByLabel('Тип доступа').selectOption({ label: testSystemDetails.accessType });
    await page.getByLabel('URL доступа').fill(testSystemDetails.url);
    await page.getByLabel('Начальный № потока').fill(testSystemDetails.startThreadNumber);
    await page.getByLabel('Количество потоков').fill(testSystemDetails.threadCount);
    await page.getByLabel('Учетная запись системы').fill(testSystemDetails.username);
    await page.getByLabel('Пароль к системе').fill(testSystemDetails.password);
    await page.getByLabel('Количество попыток через короткий интервал').fill(testSystemDetails.shortRetryAttempts);
    await page.getByLabel('Короткий интервал между попытками').fill(testSystemDetails.shortRetryInterval);
    await page.getByLabel('Количество попыток через длинный интервал').fill(testSystemDetails.longRetryAttempts);
    await page.getByLabel('Длинный интервал между попытками').fill(testSystemDetails.longRetryInterval);
    
    // Save the new system
    await page.getByRole('button', { name: 'Сохранить' }).click();
    
    // 2. READ: Verify system was created and appears in the list
    await page.getByLabel('Код системы').fill(testSystemDetails.systemCode);
    const systemRow = page.getByRole('row', { name: new RegExp(`${testSystemDetails.systemCode}`) });
    await expect(systemRow).toBeVisible();
    
    // Click on the system to view details
    await systemRow.getByLabel('Изменить').click();
    
    // Verify all fields match what we entered
    await expect(page.getByLabel('Код системы')).toHaveValue(testSystemDetails.systemCode);
    await expect(page.getByLabel('№ группы-Класс')).toHaveValue(testSystemDetails.groupNumber);
    await expect(page.getByLabel('Название')).toHaveValue(testSystemDetails.name);
    await expect(page.getByLabel('URL доступа')).toHaveValue(testSystemDetails.url);
    await expect(page.getByLabel('Начальный № потока')).toHaveValue(testSystemDetails.startThreadNumber);
    await expect(page.getByLabel('Количество потоков')).toHaveValue(testSystemDetails.threadCount);
    
    // 3. UPDATE: Modify the system
    const updatedName = `${testSystemDetails.name} - Updated`;
    await page.getByLabel('Название').clear();
    await page.getByLabel('Название').fill(updatedName);
    
    // Save changes
    await page.getByRole('button', { name: 'Сохранить' }).click();
    
    // Verify changes were saved
    await page.getByLabel('Код системы').fill(testSystemDetails.systemCode);
    await systemRow.getByLabel('Изменить').click();
    await expect(page.getByLabel('Название')).toHaveValue(updatedName);
    
    // 4. DELETE: Delete the test system
    await page.getByRole('button', { name: 'Удалить' }).click();
    
    // Confirm deletion in the dialog (adjust if confirmation dialog is different)
    await page.getByRole('button', { name: 'OK' }).click();
    
    // Verify system no longer exists
    await page.getByLabel('Код системы').fill(testSystemDetails.systemCode);
    await expect(page.getByText('No records found')).toBeVisible();
  });
});
