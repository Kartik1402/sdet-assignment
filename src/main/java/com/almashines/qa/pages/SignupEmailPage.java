package com.almashines.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SignupEmailPage handles the initial gate of entering an email and looking up if it's new or existing.
 */
public class SignupEmailPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(SignupEmailPage.class);

    // Private Locators using Playwright's best locator strategies
    private final Locator emailInput;
    private final Locator continueBtn;
    private final Locator duplicateEmailErrorMsg;

    public SignupEmailPage(Page page) {
        super(page);
        // Prioritize getByPlaceholder and getByRole
        this.emailInput = page.getByPlaceholder("Email Address");
        this.continueBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Get Started").setExact(false));
        // Fallback for visual error banners
        this.duplicateEmailErrorMsg = page.locator(".error-message, .alert-danger, [id*='error']");
    }

    /**
     * Enters an email into the input field.
     * @param email Email to register.
     * @return This page instance for chaining.
     */
    public SignupEmailPage enterEmail(String email) {
        logger.info("Entering email address: {}", email);
        emailInput.fill(email);
        return this;
    }

    /**
     * Clicks continue to proceed to the registration details form.
     * @return The next step SignupDetailsPage.
     */
    public SignupDetailsPage clickContinue() {
        logger.info("Clicking Continue/Get Started button.");
        continueBtn.click();
        return new SignupDetailsPage(page);
    }

    /**
     * Checks if the duplicate registration error banner is displayed.
     * @return boolean true if error is visible.
     */
    public boolean isDuplicateEmailErrorVisible() {
        boolean visible = duplicateEmailErrorMsg.isVisible();
        logger.info("Duplicate email lookup error visible: {}", visible);
        return visible;
    }

    /**
     * Gets the duplicate email error message text.
     * @return String error message.
     */
    public String getDuplicateEmailErrorText() {
        String text = duplicateEmailErrorMsg.textContent();
        logger.info("Error text retrieved: {}", text);
        return text;
    }
}
