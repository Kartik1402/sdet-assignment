package com.almashines.qa.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RoleSelectionPage captures institution affiliation, degree, and graduation details.
 */
public class RoleSelectionPage extends BasePage {

    private static final Logger logger = LoggerFactory.getLogger(RoleSelectionPage.class);

    // Private Locators
    private final Locator roleDropdown;
    private final Locator degreeInput;
    private final Locator graduationYearDropdown;
    private final Locator termsCheckbox;
    private final Locator submitBtn;
    private final Locator backBtn;

    public RoleSelectionPage(Page page) {
        super(page);
        this.roleDropdown = page.locator("select[name='role'], select[id*='role']");
        this.degreeInput = page.getByPlaceholder("Degree/Course");
        this.graduationYearDropdown = page.locator("select[name='grad_year'], select[id*='year']");
        this.termsCheckbox = page.locator("input[type='checkbox'][name='terms'], input[id*='terms']");
        this.submitBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit").setExact(false));
        this.backBtn = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Back").setExact(false));
    }

    public RoleSelectionPage selectRole(String role) {
        logger.info("Selecting role affiliation: {}", role);
        roleDropdown.selectOption(role);
        return this;
    }

    public RoleSelectionPage enterDegree(String degree) {
        logger.info("Entering degree: {}", degree);
        degreeInput.fill(degree);
        return this;
    }

    public RoleSelectionPage selectGraduationYear(String year) {
        logger.info("Selecting graduation year: {}", year);
        graduationYearDropdown.selectOption(year);
        return this;
    }

    public RoleSelectionPage acceptTerms() {
        logger.info("Checking Accept Terms checkbox.");
        if (!termsCheckbox.isChecked()) {
            termsCheckbox.check();
        }
        return this;
    }

    public OTPVerificationPage clickSubmit() {
        logger.info("Submitting registration details.");
        submitBtn.click();
        return new OTPVerificationPage(page);
    }

    public SignupDetailsPage clickBack() {
        logger.info("Clicking back button to return to personal details.");
        backBtn.click();
        return new SignupDetailsPage(page);
    }
}
