package com.almashines.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OTPVerificationPage represents the OTP checkpoint. Tests do not automate this directly
 * but map elements for manual validations or mock verifications.
 */
public class OTPVerificationPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(OTPVerificationPage.class);

    // Private Locators
    private final Locator otpInput;
    private final Locator verifyBtn;
    private final Locator resendOtpLink;
    private final Locator timerDisplay;
    private final Locator invalidOtpError;

    public OTPVerificationPage(Page page) {
        super(page);
        this.otpInput = page.getByPlaceholder("Enter OTP");
        this.verifyBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Verify").setExact(false));
        this.resendOtpLink = page.getByText("Resend OTP", new Page.GetByTextOptions().setExact(false));
        this.timerDisplay = page.locator(".otp-timer, #timer");
        this.invalidOtpError = page.locator(".error, .alert, [id*='otp-error']");
    }

    public OTPVerificationPage enterOTP(String otp) {
        logger.info("Entering OTP digits: {}", otp);
        otpInput.fill(otp);
        return this;
    }

    public RegistrationSuccessPage clickVerify() {
        logger.info("Clicking Verify OTP button.");
        verifyBtn.click();
        return new RegistrationSuccessPage(page);
    }

    public OTPVerificationPage clickResendOTP() {
        logger.info("Clicking Resend OTP link.");
        resendOtpLink.click();
        return this;
    }

    public boolean isTimerVisible() {
        boolean visible = timerDisplay.isVisible();
        logger.info("OTP timer visible: {}", visible);
        return visible;
    }

    public String getTimerText() {
        String text = timerDisplay.textContent();
        logger.info("Timer value: {}", text);
        return text;
    }

    public boolean isInvalidOtpErrorVisible() {
        boolean visible = invalidOtpError.first().isVisible();
        logger.info("Invalid OTP error visible: {}", visible);
        return visible;
    }
}
