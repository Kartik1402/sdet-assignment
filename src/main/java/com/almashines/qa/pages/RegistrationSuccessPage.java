package com.almashines.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RegistrationSuccessPage handles elements on the final successful landing dashboard page.
 */
public class RegistrationSuccessPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationSuccessPage.class);

    // Private Locators
    private final Locator welcomeHeading;
    private final Locator successToast;

    public RegistrationSuccessPage(Page page) {
        super(page);
        this.welcomeHeading = page.locator("h1, h2, .welcome-message");
        this.successToast = page.locator(".toast-success, .alert-success");
    }

    public boolean isWelcomeMessageDisplayed() {
        boolean displayed = welcomeHeading.first().isVisible();
        logger.info("Welcome message display status: {}", displayed);
        return displayed;
    }

    public String getWelcomeMessageText() {
        String text = welcomeHeading.first().textContent();
        logger.info("Welcome heading text: {}", text);
        return text;
    }

    public boolean isSuccessToastDisplayed() {
        boolean displayed = successToast.first().isVisible();
        logger.info("Success toast notification displayed: {}", displayed);
        return displayed;
    }
}
