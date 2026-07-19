package com.almashines.qa.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;
import com.almashines.qa.config.ConfigManager;
import com.almashines.qa.factory.PlaywrightFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;

/**
 * BaseTest manages the browser lifecycle, diagnostic captures (screenshots & traces) on failures.
 */
public class BaseTest {

    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);

    protected PlaywrightFactory playwrightFactory;
    protected Page page;
    private boolean isTraceSaved = false;

    @BeforeEach
    public void setup() {
        logger.info("Starting setup: Initializing browser context.");
        playwrightFactory = new PlaywrightFactory();
        page = playwrightFactory.initBrowser();

        // Initialize Playwright Tracing if enabled
        if (ConfigManager.getBooleanProperty("traceOnFailure", true)) {
            logger.info("Playwright trace recording started.");
            PlaywrightFactory.getBrowserContext().tracing().start(new Tracing.StartOptions()
                    .setScreenshots(true)
                    .setSnapshots(true)
                    .setSources(true));
        }

        String url = ConfigManager.getProperty("url");
        logger.info("Navigating to: {}", url);
        page.navigate(url);
        
        // Wait for base SPA selector to load to ensure page is initialized
        try {
            page.waitForSelector("#email", new Page.WaitForSelectorOptions().setTimeout(10000));
        } catch (Exception e) {
            logger.warn("Dynamic SPA email lookup field (#email) did not load in time.");
        }
    }

    @AfterEach
    public void teardown() {
        logger.info("Starting teardown: Cleaning up resources.");
        
        // Stop tracing for passing tests if it hasn't been saved and stopped already
        if (ConfigManager.getBooleanProperty("traceOnFailure", true) && !isTraceSaved) {
            try {
                PlaywrightFactory.getBrowserContext().tracing().stop(new Tracing.StopOptions());
                logger.info("Playwright trace discarded for successful run.");
            } catch (Exception e) {
                logger.warn("Could not stop tracing in teardown (it may have been closed on failure).");
            }
        }

        if (playwrightFactory != null) {
            playwrightFactory.quitBrowser();
        }
    }

    /**
     * JUnit 5 Extension to monitor test failures and capture screenshots and trace logs.
     */
    @RegisterExtension
    TestWatcher watchman = new TestWatcher() {
        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            String testMethodName = context.getRequiredTestMethod().getName();
            String timestamp = String.valueOf(System.currentTimeMillis());
            String currentDate = LocalDate.now().toString();

            logger.error("Test failed: '{}' due to: {}", testMethodName, cause.getMessage());

            // 1. Capture failure screenshot
            if (ConfigManager.getBooleanProperty("screenshotsOnFailure", true) && page != null) {
                String screenshotPathStr = "target/screenshots/" + currentDate + "/" + testMethodName + "_" + timestamp + ".png";
                Path screenshotPath = Paths.get(screenshotPathStr);

                try {
                    Files.createDirectories(screenshotPath.getParent());
                    page.screenshot(new Page.ScreenshotOptions()
                            .setPath(screenshotPath)
                            .setFullPage(true));
                    logger.info("Failure screenshot captured at: {}", screenshotPath.toAbsolutePath());
                } catch (IOException e) {
                    logger.error("Failed to create directories for screenshots: ", e);
                } catch (Exception e) {
                    logger.error("Failed to capture screenshot: ", e);
                }
            }

            // 2. Stop and save Playwright Trace
            if (ConfigManager.getBooleanProperty("traceOnFailure", true)) {
                String tracePathStr = "target/traces/" + currentDate + "/" + testMethodName + "_" + timestamp + ".zip";
                Path tracePath = Paths.get(tracePathStr);

                try {
                    Files.createDirectories(tracePath.getParent());
                    PlaywrightFactory.getBrowserContext().tracing().stop(new Tracing.StopOptions()
                            .setPath(tracePath));
                    isTraceSaved = true;
                    logger.info("Failure trace log saved at: {}", tracePath.toAbsolutePath());
                } catch (IOException e) {
                    logger.error("Failed to create directories for traces: ", e);
                } catch (Exception e) {
                    logger.error("Failed to save trace: ", e);
                }
            }
        }

        @Override
        public void testSuccessful(ExtensionContext context) {
            logger.info("Test passed successfully: '{}'", context.getRequiredTestMethod().getName());
        }

        @Override
        public void testAborted(ExtensionContext context, Throwable cause) {
            logger.info("Test aborted: '{}'", context.getRequiredTestMethod().getName());
        }

        @Override
        public void testDisabled(ExtensionContext context, Optional<String> reason) {
            logger.info("Test disabled: '{}' | Reason: {}", context.getRequiredTestMethod().getName(), reason.orElse("No reason"));
        }
    };
}
