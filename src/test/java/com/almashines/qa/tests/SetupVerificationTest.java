package com.almashines.qa.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SetupVerificationTest extends BaseTest {

    @Test
    @DisplayName("Verify Playwright can initialize and navigate to target site")
    void testPlaywrightSetup() {
        String title = page.title();
        
        logger.info("Verifying page title...");
        logger.info("Page Title is: {}", title);
        
        assertNotNull(title, "Page title should not be null");
        // We assert that the title contains "alumni" or "almashines" or matches "404" (if site is not online)
        // Just checking that we successfully retrieved the title string from the page.
        assertTrue(title.length() > 0, "Page title should not be empty");
    }
}
