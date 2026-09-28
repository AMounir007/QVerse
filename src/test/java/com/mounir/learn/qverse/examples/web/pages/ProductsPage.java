package com.mounir.learn.qverse.examples.web.pages;

import com.mounir.learn.qverse.business.BasePage;
import com.mounir.learn.qverse.core.locator.Locator;

public final class ProductsPage extends BasePage<ProductsPage> {

    private static final Locator TITLE = Locator.css("Page title", "span.title");
    private static final Locator CART_BADGE = Locator.css("Cart badge", ".shopping_cart_badge");
    private static final Locator SORT = Locator.css("Sort dropdown", "select[data-test='product-sort-container']");
    /** Dynamic locator: resolved per product name with .with(name). */
    private static final Locator ADD_TO_CART = Locator.xpath("Add to cart for %s",
            "//div[@class='inventory_item'][.//div[text()='%s']]//button");

    ProductsPage() {
    }

    @Override
    protected String path() {
        return "/inventory.html";
    }

    @Override
    protected Locator pageIdentity() {
        return TITLE;
    }

    public ProductsPage verifyTitle(String expected) {
        web.verifyText(TITLE, expected);
        return this;
    }

    public ProductsPage addToCart(String productName) {
        web.scroll(ADD_TO_CART.with(productName)).click(ADD_TO_CART.with(productName));
        return this;
    }

    public ProductsPage sortBy(String option) {
        web.select(SORT, option);
        return this;
    }

    public ProductsPage verifyCartCount(int expected) {
        web.verifyText(CART_BADGE, String.valueOf(expected));
        return this;
    }
}
