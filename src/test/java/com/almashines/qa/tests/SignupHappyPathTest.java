package com.almashines.qa.tests;

import com.almashines.qa.pages.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * SignupHappyPathTest automates the primary user registration scenario up to the OTP verification gate.
 */
public class SignupHappyPathTest extends BaseTest {

    @Test
    @DisplayName("Verify successful user registration onboarding flow (Happy Path up to OTP Gate)")
    public void testSuccessfulUserRegistrationFlow() {
        logger.info("Starting TC-01: Successful User Registration Flow (Happy Path)");

        // 1. Initial Email Step
        SignupEmailPage emailPage = new SignupEmailPage(page);
        
        // Assert initial landing page title is correct
        String pageTitle = emailPage.getPageTitle();
        logger.info("Validating Landing Page Title: {}", pageTitle);
        assertTrue(pageTitle.toLowerCase().contains("alumni") || pageTitle.toLowerCase().contains("almashines"),
                "Landing page title does not match AlmaShines platform brand");

        String dynamicEmail = "sdet_candidate_" + System.currentTimeMillis() + "@yopmail.com";
        logger.info("Using dynamically generated email: {}", dynamicEmail);
        
        // Input email and navigate to step 2 details page
        SignupDetailsPage detailsPage = emailPage
                .enterEmail(dynamicEmail)
                .clickContinue();

        // 2. Personal Profile Details Step
        logger.info("Filling step 2: Personal Profile details");
        RoleSelectionPage rolePage = detailsPage
                .fillPersonalDetails("Mentor", "SDET", "9876543210", "SecurePassword123!")
                .clickProceed();

        // 3. Affiliation and Selection Step
        logger.info("Filling step 3: Affiliation details & accepting terms");
        // In this happy path we select a role, degree, graduation year, check terms, and click submit
        OTPVerificationPage otpPage = rolePage
                .selectRole("Alumni")
                .enterDegree("Bachelor of Technology")
                .selectGraduationYear("2020")
                .acceptTerms()
                .clickSubmit();

        // 4. OTP Verification Gate (Mock / Assert State)
        logger.info("Verifying redirection to Step 4: OTP Verification page");
        
        // As defined in the Test Strategy, OTP is kept manual due to external delivery dependency.
        // We assert that we have successfully navigated to the OTP Entry screen.
        boolean timerIsVisible = otpPage.isTimerVisible();
        logger.info("Validating OTP verification timer is active: {}", timerIsVisible);
        
        assertTrue(timerIsVisible, "OTP Verification screen timer was not displayed. Verification gate redirect failed.");
        logger.info("TC-01: Happy Path registration reached the OTP verification gate successfully.");
    }
}
