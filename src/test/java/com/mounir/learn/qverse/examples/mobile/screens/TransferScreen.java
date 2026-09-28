package com.mounir.learn.qverse.examples.mobile.screens;

import com.mounir.learn.qverse.business.BaseScreen;
import com.mounir.learn.qverse.core.locator.Locator;
import com.mounir.learn.qverse.data.TestDataFactory;
import com.mounir.learn.qverse.examples.mobile.model.Transfer;
import com.mounir.learn.qverse.mobile.Direction;

/**
 * Business DSL for a (sample) banking app transfer screen. Accessibility ids work on Android and iOS,
 * so one screen object serves both platforms.
 */
public final class TransferScreen extends BaseScreen<TransferScreen> {

    private static final Locator TITLE = Locator.accessibilityId("Transfer title", "transferTitle");
    private static final Locator FROM = Locator.accessibilityId("From account", "fromAccount");
    private static final Locator TO = Locator.accessibilityId("To account", "toAccount");
    private static final Locator AMOUNT = Locator.accessibilityId("Amount", "amount");
    private static final Locator CONFIRM = Locator.accessibilityId("Confirm transfer", "confirmTransfer");
    private static final Locator SUCCESS = Locator.accessibilityId("Success message", "transferSuccess");
    private static final Locator CHART = Locator.accessibilityId("Balance chart", "balanceChart");

    private TransferScreen() {
    }

    public static TransferScreen open() {
        return new TransferScreen().verifyLoaded();
    }

    @Override
    protected Locator screenIdentity() {
        return TITLE;
    }

    /** Transfer with the default business data set. */
    public TransferScreen transferMoney() {
        return transferMoney(TestDataFactory.load("testdata/transfer.json", Transfer.class));
    }

    public TransferScreen transferMoney(Transfer transfer) {
        mobile.sendKeys(FROM, transfer.fromAccount())
                .sendKeys(TO, transfer.toAccount())
                .sendKeys(AMOUNT, transfer.amount())
                .hideKeyboard()
                .scroll(CONFIRM, Direction.UP, 3)
                .tap(CONFIRM);
        return this;
    }

    public TransferScreen verifyTransferSuccessful() {
        mobile.verifyElement(SUCCESS);
        return this;
    }

    public TransferScreen inspectBalanceChart() {
        mobile.zoom(CHART).pinch(CHART);
        return this;
    }
}
