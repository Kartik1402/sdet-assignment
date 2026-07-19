package com.almashines.qa.pages;

import com.microsoft.playwright.Page;

/**
 * BasePage encapsulates common behaviors and properties shared across all Page Objects.
 */
public class BasePage {

    protected Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    /**
     * Navigates to a specific URL.
     * @param url Absolute destination URL.
     */
    public void navigateTo(String url) {
        page.navigate(url);
    }

    /**
     * Gets the page title.
     * @return String page title.
     */
    public String getPageTitle() {
        return page.title();
    }

    /**
     * Gets the current URL of the page.
     * @return String current URL.
     */
    public String getPageUrl() {
        return page.url();
    }
}
