package TestFunctionality.Tests;

import Factories.Browsers.Selenium.SeleniumBrowser;
import Factories.Browsers.Terminal.TerminalBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class NestedScriptTest extends LeakTest {

    private static NestedScriptTest ourInstance = new NestedScriptTest();
    public static NestedScriptTest getInstance() {
        return ourInstance;
    }
    private NestedScriptTest() {}

    @Override
    public boolean run(SeleniumBrowser browser, int scenarioId) {
        if (!browser.supportsSW())
            return true;

        String url = LeakTest.getUrl(scenarioId, getName());

        // Install SW
        if (!LeakTest.loadUrl(browser, url, ExpectedConditions.visibilityOfElementLocated(By.id("message"))))
            return true;
        // SW is activated at the second visit
        try {
            LeakTest.loadUrl(browser, url, ExpectedConditions.textToBePresentInElementLocated(By.id("message"), "The SW is done"));
        } catch (TimeoutException e) {
            // End test if SW is prevented from running
            if (LeakTest.loadUrl(browser, url, ExpectedConditions.textToBePresentInElementLocated(By.id("message"), "First visit")))
                return true;
            else
                throw e;
        }
        LeakTest.loadUrl(browser, url, ExpectedConditions.textToBePresentInElementLocated(By.id("message"), "The SW is done"));

        // Checking to be sure that the SW is active and has control over the page
        int i = 0;
        browser.checkAndSwitchToFrame();
        while (!browser.checkElementText("message", "The SW is active") && i < 3) {
            LeakTest.loadUrl(browser, url, ExpectedConditions.visibilityOfElementLocated(By.id("message")));
            i++;
        }
        browser.switchToTopFrame();
        return true;
    }

    @Override
    public boolean run(TerminalBrowser browser, int scenarioId) {
        return false;
    }

    private static TestName getName() {
        return TestName.NESTED_SCRIPT;
    }
}
