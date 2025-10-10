package pages;

import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BasePage {
    protected final Page page;
    protected BasePage(Page page){
        this.page = page;
        log.debug("{} constructed", this.getClass().getSimpleName());
    }
}
