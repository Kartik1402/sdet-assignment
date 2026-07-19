package com.almashines.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SignupDetailsPage collects personal profile info and account credentials.
 */
public class SignupDetailsPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(SignupDetailsPage.class);

    // Private Locators
    private final Locator firstNameInput;
    private final Locator lastNameInput;
    private final Locator phoneInput;
    private final Locator passwordInput;
    private final Locator confirmPasswordInput;
    private final Locator proceedBtn;
    private final Locator validationErrorAlert;

    public SignupDetailsPage(Page page) {
        super(page);
        this.firstNameInput = page.getByPlaceholder("First Name");
        this.lastNameInput = page.getByPlaceholder("Last Name");
        this.phoneInput = page.getByPlaceholder("Mobile Number");
        this.passwordInput = page.getByPlaceholder("Password").first();
        this.confirmPasswordInput = page.getByPlaceholder("Confirm Password");
        this.proceedBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Proceed").setExact(false));
        this.validationErrorAlert = page.locator(".invalid-feedback, .error-msg, [id*='validation']");
    }

    public SignupDetailsPage fillPersonalDetails(String firstName, String lastName, String phone, String password) {
        logger.info("Filling personal details: {} {}, phone: {}", firstName, lastName, phone);
        firstNameInput.fill(firstName);
        lastNameInput.fill(lastName);
        phoneInput.fill(phone);
        passwordInput.fill(password);
        confirmPasswordInput.fill(password);
        return this;
    }

    public SignupDetailsPage fillMismatchPasswords(String password, String confirmPassword) {
        logger.info("Filling mismatched passwords: '{}' and '{}'", password, confirmPassword);
        passwordInput.fill(password);
        confirmPasswordInput.fill(confirmPassword);
        return this;
    }

    public RoleSelectionPage clickProceed() {
        logger.info("Clicking Proceed to navigate to affiliation details.");
        proceedBtn.click();
        return new RoleSelectionPage(page);
    }

    public boolean isValidationErrorAlertVisible() {
        boolean visible = validationErrorAlert.first().isVisible();
        logger.info("Validation error alert visible: {}", visible);
        return visible;
    }

    public String getValidationErrorText() {
        String text = validationErrorAlert.first().textContent();
        logger.info("Validation error text: {}", text);
        return text;
    }
}
