# AlmaShines Sign Up Flow – Test Automation & Quality Assurance Suite

This repository contains the end-to-end automation framework and comprehensive manual testing artifacts designed to validate the **Sign Up flow** of the AlmaShines alumni platform.

---

## 1. Project Overview & Objective

The objective of this project is to implement a robust, production-ready quality assurance layer for the user onboarding funnel of the AlmaShines SaaS platform. 
By combining a **thread-safe, high-concurrency Playwright Java automation framework** with structured **Manual Test Case sheets**, **Jira-style Defect Reports**, and **CI/CD integration pipelines**, this suite ensures the registration gateway remains highly functional, secure, and compliant.

---

## 2. Technology Stack

* **Language:** Java 21 (LTS)
* **Automation Library:** Microsoft Playwright for Java
* **Testing Framework:** JUnit 5 (Jupiter)
* **Build Tool:** Maven
* **Logging:** SLF4J + Logback Classic
* **CI/CD:** GitHub Actions (Windows runtime)
* **Design Pattern:** Modular Page Object Model (POM)

---

## 3. Framework Architecture & Design Decisions

```
[ config.properties ]
          │ (loads properties)
          ▼
   [ ConfigManager ]
          │ (typed values)
          ▼
  [ PlaywrightFactory ] ◄─── (ThreadLocal isolation)
          │ (returns Page instance)
          ▼
     [ BaseTest ] ◄──────── (manages setup/teardown & failure triggers)
          │ (inherits base context)
          ▼
   [ Test Classes ] ◄────── (asserts UI behaviors)
          │
     (uses actions)
          ▼
   [ Page Objects ] ◄────── (encapsulates locators & actions)
```

* **Thread-Safety & Parallel Execution:** Uses `ThreadLocal` wrapper layers for `Playwright`, `BrowserContext`, and `Page` objects. This allows running tests in parallel across multiple threads without state leakage.
* **Encapsulation:** Page Object locators are strictly declared as `private`. Test scripts can only interact with elements through `public` action methods, ensuring that selector changes do not break test code.
* **No Assertions in Page Objects:** Assertions reside entirely in the `tests` directory. Page classes only retrieve elements, perform actions, and return state details, maximizing reusability.
* **Dynamic Test Data:** Implements a timestamped dynamic email generator to bypass "Duplicate Account" rules on regression cycles.

---

## 4. Directory Structure

```
sdet_assignment/
├── .github/workflows/
│   └── maven.yml                     # GitHub Actions CI workflow
├── src/
│   ├── main/
│   │   ├── java/com/almashines/qa/
│   │   │   ├── config/
│   │   │   │   └── ConfigManager.java    # Configuration property parser
│   │   │   ├── factory/
│   │   │   │   └── PlaywrightFactory.java # Thread-safe browser engine initiator
│   │   │   ├── pages/
│   │   │   │   ├── BasePage.java          # Shared page components
│   │   │   │   ├── SignupEmailPage.java   # Step 1: Email check
│   │   │   │   ├── SignupDetailsPage.java # Step 2: Personal details
│   │   │   │   ├── RoleSelectionPage.java # Step 3: Affiliation details
│   │   │   │   ├── OTPVerificationPage.java # Step 4: Verification gate
│   │   │   │   └── RegistrationSuccessPage.java # Step 5: Success dashboard
│   │   │   └── utils/
│   │   │       └── TestDataGenerator.java # Dynamic test data utilities
│   │   └── resources/
│   │       └── logback.xml               # SLF4J/Logback configurations
│   └── test/
│       ├── java/com/almashines/qa/tests/
│       │   ├── BaseTest.java              # Setup/Teardown hooks & Failure Watchers
│       │   ├── SignupHappyPathTest.java   # Happy path registration test
│       │   ├── SignupNegativeTest.java    # Input errors and duplicate registration tests
│       │   └── SignupBoundaryTest.java    # BVA limits and input sanitization tests
│       └── resources/
│           └── config.properties         # Runtime environmental properties
├── target/                                # Generated compiled classes & execution logs
├── .gitignore
├── pom.xml                                # Project configuration dependencies
└── README.md                              # Hand-off documentation
```

---

## 5. Prerequisites & Installation

### Prerequisites
* **Java Development Kit (JDK) 21** or later installed.
* **Apache Maven 3.8+** installed.
* System Operating System: Windows, macOS, or Linux.

### Installation
1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/AlmaShines-SDET.git
   cd AlmaShines-SDET
   ```
2. Download dependencies and install Playwright browser engines:
   ```bash
   mvn clean compile
   mvn exec:java -e -Dexec.mainClass=com.microsoft.playwright.CLI -Dexec.args="install"
   ```

---

## 6. Test Execution & Reporting

### Running Tests
Execute all tests headlessly (configured in `config.properties`):
```bash
mvn test
```

Execute a specific test class:
```bash
mvn test -Dtest=SignupHappyPathTest
```

### Visualizing Reports & Traces
* **HTML Reports:** Playwright HTML reports compile automatically. Open the report using:
  ```bash
  npx playwright show-report test-results/html
  ```
* **Failure Screenshots:** Captures full-page screenshot on test failure. Screenshots are stored under `target/screenshots/{current-date}/`.
* **Playwright Trace Viewer:** Complete timeline recordings are saved on test failure inside `target/traces/{current-date}/`. Load the ZIP trace file via:
  ```bash
  npx playwright show-trace path/to/trace.zip
  ```

---

## 7. Quality Coverage & Limitations

### Testing Coverage
* **Automated Coverage:** 55% (Happy path registration, duplicate validations, password constraints, name length boundaries, email casing, and whitespace sanitization).
* **Manual Coverage:** 45% (Third-party OAuth redirects, SMS/Email OTP delivery, visual element alignment, session timeout, and keyboard navigations).

### Known Limitations
* **OTP Gates:** OTP testing is kept manual. Bypass requires either mock API support on sandboxes or static developer keys.
* **Third-Party OAuth:** Visual redirects for Google/LinkedIn logins are verified manually due to vendor bot protection policies.

---

## 8. Future Enhancements

1. **Docker Containerization:** Dockerize runs to guarantee identical environment dependencies on local and CI machines.
2. **Visual Regression Testing:** Integrate visual libraries (like Applitools or Pixelmatch) to automate visual alignments.
3. **Mocking APIs:** Implement API mock interceptors to simulate server errors (500, 503) and validation responses.

---

**Author:** Kartik
