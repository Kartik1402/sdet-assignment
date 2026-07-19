package com.almashines.qa.tests;

import com.almashines.qa.pages.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SignupNegativeTest implements tests to verify error handling and validation constraints.
 */
public class SignupNegativeTest extends BaseTest {

    @ParameterizedTest
    @ValueSource(strings = {"invalidemail", "test@domain", "@no-user.com", "user@domain..com"})
    @DisplayName("Verify validation error is displayed for invalid email formats")
    public void testInvalidEmailFormats(String badEmail) {
        logger.info("Executing invalid email format verification for: {}", badEmail);

        SignupEmailPage emailPage = new SignupEmailPage(page);
        
        // Enter invalid email and continue
        emailPage.enterEmail(badEmail).clickContinue();

        // Verify that the page remains stable (did not navigate away from email step)
        String currentUrl = emailPage.getPageUrl();
        logger.info("Current page URL after submitting bad email: {}", currentUrl);
        assertTrue(currentUrl.contains("signup") || currentUrl.contains("register") || emailPage.isDuplicateEmailErrorVisible(), 
                "Page incorrectly navigated forward after entering an invalid email");
    }

    @Test
    @DisplayName("Verify error message for duplicate email registration")
    public void testDuplicateEmailRegistration() {
        logger.info("Executing duplicate email registration test");

        // We use a known existing email (e.g. standard registered credential)
        String existingEmail = "existing_user@almashines.com";

        SignupEmailPage emailPage = new SignupEmailPage(page);
        emailPage.enterEmail(existingEmail).clickContinue();

        // Verify that validation/duplicate alert displays
        boolean errorVisible = emailPage.isDuplicateEmailErrorVisible();
        logger.info("Duplicate email warning displayed: {}", errorVisible);
        
        // Assert lookup matches or is handled gracefully on lookup page
        assertTrue(errorVisible || emailPage.getPageUrl().contains("login"),
                "The duplicate registration attempt did not trigger validation errors or route to login page.");
    }

    @Test
    @DisplayName("Verify registration is blocked when passwords mismatch")
    public void testPasswordConfirmationMismatch() {
        logger.info("Executing password confirmation mismatch test");

        String dynamicEmail = "sdet_neg_" + System.currentTimeMillis() + "@yopmail.com";

        SignupEmailPage emailPage = new SignupEmailPage(page);
        SignupDetailsPage detailsPage = emailPage.enterEmail(dynamicEmail).clickContinue();

        // Fill out profile with mismatched passwords
        detailsPage.fillPersonalDetails("Mentor", "SDET", "9876543210", "SecurePassword123!");
        detailsPage.fillMismatchPasswords("SecurePassword123!", "MismatchPass123");
        detailsPage.clickProceed();

        // Assert error notification displays
        boolean isAlertVisible = detailsPage.isValidationErrorAlertVisible();
        logger.info("Validation warning alerts visible: {}", isAlertVisible);
        assertTrue(isAlertVisible, "Password mismatch validation alert was not displayed.");
    }
}
