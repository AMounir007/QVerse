package com.mounir.learn.qverse.examples.mobile;

import com.mounir.learn.qverse.examples.mobile.model.Transfer;
import com.mounir.learn.qverse.examples.mobile.screens.TransferScreen;
import com.mounir.learn.qverse.testng.MobileTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

/**
 * Mobile example. Requires a running Appium server and a device/emulator with the target app
 * (configure mobile.* keys). Run with: mvn test -Pmobile
 */
@Epic("Mobile")
@Feature("Money transfer")
public class MobileTransferTest extends MobileTest {

    @Test(description = "Customer transfers money with default data")
    public void customerTransfersMoney() {
        TransferScreen.open()
                .transferMoney()
                .verifyTransferSuccessful();
    }

    @Test(description = "Customer transfers a custom amount and inspects the balance chart")
    public void customerTransfersCustomAmount() {
        TransferScreen.open()
                .transferMoney(new Transfer("CHECKING-001", "SAVINGS-002", "25.50"))
                .verifyTransferSuccessful()
                .inspectBalanceChart();
    }
}
