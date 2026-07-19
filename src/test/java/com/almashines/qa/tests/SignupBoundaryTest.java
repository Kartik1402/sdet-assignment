package com.almashines.qa.tests;

import com.almashines.qa.pages.*;
import com.almashines.qa.config.ConfigManager;
import com.almashines.qa.utils.TestDataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SignupBoundaryTest automates Boundary Value Analysis (BVA) and sanitization edge cases.
 */
public class SignupBoundaryTest extends BaseTest {

    @ParameterizedTest
    @CsvSource({
        "1, false, 'Single character first name should be rejected'",
        "2, true, 'Two character first name should be accepted'",
        "49, true, 'Forty-nine character first name should be accepted'",
        "50, true, 'Fifty character first name should be accepted'",
        "51, false, 'Fifty-one character first name should be rejected'"
    })
    @DisplayName("Verify Name length boundary values (min 2, max 50)")
    public void testNameLengthBoundaries(int length, boolean shouldPass, String assertionMessage) {
        logger.info("Executing Name boundary test for length: {} | expected validity: {}", length, shouldPass);

        String dynamicEmail = TestDataGenerator.generateRandomEmail();
        String testName = TestDataGenerator.generateStringOfLength(length);

        SignupEmailPage emailPage = new SignupEmailPage(page);
        SignupDetailsPage detailsPage = emailPage.enterEmail(dynamicEmail).clickContinue();

        // Fill name and trigger proceed
        detailsPage.fillPersonalDetails(testName, "SDET", "9876543210", "SecurePassword123!");
        detailsPage.clickProceed();

        if (shouldPass) {
            // Verify navigation successfully proceeds past step 2 (i.e. validation error is NOT displayed)
            boolean errorVisible = detailsPage.isValidationErrorAlertVisible();
            if (errorVisible) {
                logger.error("Validation error displayed unexpectedly: '{}'", detailsPage.getValidationErrorText());
            }
            logger.info("Validation errors display status: {}", errorVisible);
            assertTrue(!errorVisible, assertionMessage);
        } else {
            // Verify that navigation is blocked or error is displayed
            boolean errorVisible = detailsPage.isValidationErrorAlertVisible();
            if (errorVisible) {
                logger.info("Validation error text: '{}'", detailsPage.getValidationErrorText());
            }
            logger.info("Validation errors display status: {}", errorVisible);
            assertTrue(errorVisible, assertionMessage);
        }
    }

    @Test
    @DisplayName("Verify Password length boundary limits (min 8 characters)")
    public void testPasswordLengthBoundaries() {
        logger.info("Executing Password BVA limits (7 vs 8 characters)");

        String email7 = "sdet_pw7_" + System.currentTimeMillis() + "@yopmail.com";
        String email8 = "sdet_pw8_" + System.currentTimeMillis() + "@yopmail.com";

        // Test 7 characters password (should fail)
        SignupEmailPage emailPage = new SignupEmailPage(page);
        SignupDetailsPage detailsPage = emailPage.enterEmail(email7).clickContinue();
        detailsPage.fillPersonalDetails("Mentor", "SDET", "9876543210", "Pw123!4");
        detailsPage.clickProceed();
        if (!detailsPage.isValidationErrorAlertVisible()) {
            logger.error("Expected validation error for password length 7, but none was visible.");
        } else {
            logger.info("Password 7 validation warning: '{}'", detailsPage.getValidationErrorText());
        }
        assertTrue(detailsPage.isValidationErrorAlertVisible(), "Password of length 7 was incorrectly accepted");

        // Test 8 characters password (should pass verification check)
        detailsPage.navigateTo(ConfigManager.getProperty("url"));
        emailPage.enterEmail(email8).clickContinue();
        detailsPage.fillPersonalDetails("Mentor", "SDET", "9876543210", "Pw123!45");
        detailsPage.clickProceed();
        if (detailsPage.isValidationErrorAlertVisible()) {
            logger.error("Password of length 8 was rejected with error: '{}'", detailsPage.getValidationErrorText());
        }
        // Should successfully transition and NOT display validation errors
        assertTrue(!detailsPage.isValidationErrorAlertVisible(), "Password of length 8 was incorrectly rejected");
    }

    @Test
    @DisplayName("Verify input sanitization: mixed casing and whitespaces in email")
    public void testEmailCasingAndWhitespaceSanitization() {
        logger.info("Executing email sanitization edge case test");

        // Entering mixed uppercase email and trailing spaces
        String inputEmail = "  SDET_Mentor_" + System.currentTimeMillis() + "@YOPmail.COM  ";
        logger.info("Input email with edge spaces and casing: '{}'", inputEmail);

        SignupEmailPage emailPage = new SignupEmailPage(page);
        SignupDetailsPage detailsPage = emailPage.enterEmail(inputEmail).clickContinue();

        // Verify lookup accepts and navigates to Details step
        String currentUrl = detailsPage.getPageUrl();
        logger.info("Current URL after submitting sanitized email: {}", currentUrl);
        if (emailPage.isDuplicateEmailErrorVisible()) {
            logger.error("Duplicate email error displayed: '{}'", emailPage.getDuplicateEmailErrorText());
        }
        assertTrue(!emailPage.isDuplicateEmailErrorVisible(), "Lookup rejected sanitized email string");
    }
}
