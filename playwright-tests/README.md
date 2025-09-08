# HermesWL Test Automation

This project contains automated tests for the HermesWL system management application, focusing on the "Системы" (Systems) section and the VLG.APTV system code.

## Prerequisites

- Node.js (v14 or higher)
- npm or yarn

## Setup

1. Install dependencies:

```bash
npm install
```

2. Install Playwright browsers:

```bash
npx playwright install
```

## Running Tests

Run all tests:

```bash
npm test
```

Run tests in debug mode:

```bash
npm run test:debug
```

Run a specific test file:

```bash
npx playwright test tests/system-management.spec.ts
```

## Project Structure

- `page-objects/` - Page Object Models for better test organization
- `tests/` - Test files
  - `system-management.spec.ts` - Basic tests for VLG.APTV system
  - `system-crud.spec.ts` - CRUD operations tests for systems
  - `system-pom.spec.ts` - Tests using the Page Object Model pattern

## Test Cases

The automated tests cover the following scenarios:

### VLG.APTV System Management
1. View system details - Verifies all fields are correctly displayed
2. Validate required fields - Tests form validation for required fields
3. Filter systems by code - Tests the filtering functionality
4. Sort systems by update date - Tests the sorting functionality

### System CRUD Operations
1. Create a new test system
2. Read the details of the created system
3. Update system information
4. Delete the test system

## Page Objects

- `LoginPage` - Handles authentication actions
- `SystemsPage` - Represents the systems listing page
- `SystemDetailsPage` - Represents the system details/edit page
