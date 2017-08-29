package net.hashcoding.code.scucrawler.task.pages;

import net.hashcoding.code.scucrawler.entity.Page;
import net.hashcoding.code.scucrawler.task.BasePage;
import net.hashcoding.code.scucrawler.utils.HtmlEscapeFormatter;
import us.codecraft.webmagic.model.annotation.*;

import java.util.List;

@TargetUrl("http://jwc.scu.edu.cn/jwc/newsShow.action*")
@HelpUrl("http://jwc.scu.edu.cn/jwc/moreNotice.action")
public class JWCPage implements BasePage {

	@ExtractByUrl(".*")
	String url;

	@Formatter(formatter = HtmlEscapeFormatter.class)
	@ExtractBy(value = "//body/table[3]/tbody/tr[2]/td/b/text()")
	String title;

	@Formatter(formatter = HtmlEscapeFormatter.class)
	@ExtractBy(value = "//input[@id='news_content']/@value")
	String content;

	@ExtractBy(value = "//body/table[4]//a/text()")
	List<String> attachmentName;

	@ExtractBy(value = "//body/table[4]//a/@href")
	List<String> attachmentUrl;

	@Override
	public Page getPage() {
		return Page.create(url, "", title, content, attachmentUrl, attachmentName);
	}
}
