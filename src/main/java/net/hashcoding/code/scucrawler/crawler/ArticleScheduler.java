package net.hashcoding.code.scucrawler.crawler;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import net.hashcoding.code.scucrawler.db.ArticleAPI;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.QueueScheduler;

public class ArticleScheduler extends QueueScheduler {
    @Override
    public void pushWhenNoDuplicate(Request request, Task task) {
        Observable<Boolean> isExists = Observable.create((emitter) -> {
            Boolean status = ArticleAPI.isURLExistsAndTimeout(request.getUrl());
            emitter.onNext(status);
            emitter.onComplete();
        });
        isExists.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(status -> {
                    if (!status)
                        ArticleScheduler.super.pushWhenNoDuplicate(request, task);
                });
    }
}
