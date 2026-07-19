package com.almashines.qa.factory;

import com.microsoft.playwright.*;
import com.almashines.qa.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-safe Playwright Factory to handle Browser initialization and Context lifecycle management.
 * Utilizes Java's ThreadLocal to ensure that tests running in parallel do not collide.
 */
public class PlaywrightFactory {

    private static final Logger logger = LoggerFactory.getLogger(PlaywrightFactory.class);

    private static final ThreadLocal<Playwright> tlPlaywright = new ThreadLocal<>();
    private static final ThreadLocal<Browser> tlBrowser = new ThreadLocal<>();
    private static final ThreadLocal<BrowserContext> tlBrowserContext = new ThreadLocal<>();
    private static final ThreadLocal<Page> tlPage = new ThreadLocal<>();

    /**
     * Initializes a thread-safe Playwright Browser and returns the Page instance based on properties config.
     * @return Initialized Playwright Page instance.
     */
    public Page initBrowser() {
        String browserName = ConfigManager.getProperty("browser", "chromium").trim().toLowerCase();
        boolean isHeadless = ConfigManager.getBooleanProperty("headless", true);
        int slowMoMs = ConfigManager.getIntProperty("slowMotion", 0);

        logger.info("Initializing Playwright: {} | Headless: {} | SlowMo: {}ms", browserName, isHeadless, slowMoMs);

        // Start Playwright instance for this thread
        Playwright playwright = Playwright.create();
        tlPlaywright.set(playwright);

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(isHeadless)
                .setSlowMo(slowMoMs);

        Browser browser;
        switch (browserName) {
            case "chromium":
                browser = playwright.chromium().launch(launchOptions);
                break;
            case "firefox":
                browser = playwright.firefox().launch(launchOptions);
                break;
            case "webkit":
                browser = playwright.webkit().launch(launchOptions);
                break;
            default:
                logger.warn("Unknown browser '{}'. Defaulting to Chromium.", browserName);
                browser = playwright.chromium().launch(launchOptions);
                break;
        }

        tlBrowser.set(browser);

        // Configure context with standard viewport size
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 720));
        tlBrowserContext.set(context);

        Page page = context.newPage();
        tlPage.set(page);

        logger.info("Browser session initialized successfully.");
        return getPage();
    }

    // ThreadLocal Getters
    public static Playwright getPlaywright() {
        return tlPlaywright.get();
    }

    public static Browser getBrowser() {
        return tlBrowser.get();
    }

    public static BrowserContext getBrowserContext() {
        return tlBrowserContext.get();
    }

    public static Page getPage() {
        return tlPage.get();
    }

    /**
     * Cleans up all Playwright instances associated with the current execution thread.
     */
    public void quitBrowser() {
        logger.info("Starting teardown of Playwright instances for thread: {}", Thread.currentThread().getName());
        
        if (tlPage.get() != null) {
            try {
                tlPage.get().close();
            } catch (Exception e) {
                logger.error("Error closing Page instance: ", e);
            }
            tlPage.remove();
        }
        if (tlBrowserContext.get() != null) {
            try {
                tlBrowserContext.get().close();
            } catch (Exception e) {
                logger.error("Error closing BrowserContext instance: ", e);
            }
            tlBrowserContext.remove();
        }
        if (tlBrowser.get() != null) {
            try {
                tlBrowser.get().close();
            } catch (Exception e) {
                logger.error("Error closing Browser instance: ", e);
            }
            tlBrowser.remove();
        }
        if (tlPlaywright.get() != null) {
            try {
                tlPlaywright.get().close();
            } catch (Exception e) {
                logger.error("Error closing Playwright instance: ", e);
            }
            tlPlaywright.remove();
        }
        logger.info("Playwright instances teardown complete.");
    }
}
