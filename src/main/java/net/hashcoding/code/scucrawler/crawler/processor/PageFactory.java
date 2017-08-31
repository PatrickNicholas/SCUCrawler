package net.hashcoding.code.scucrawler.crawler.processor;

import net.hashcoding.code.scucrawler.crawler.processor.solver.PageSolver;
import net.hashcoding.code.scucrawler.entity.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class PageFactory {
    private volatile static PageFactoryImpl instance;

    private static PageFactoryImpl getInstance() {
        if (instance == null) synchronized (PageFactory.class) {
            if (instance == null)
                instance = new PageFactoryImpl();
        }
        return instance;
    }

    public static void push(PageSolver solver) {
        getInstance().push(solver);
    }

    public static void exit() {
        getInstance().exit();
    }

    public static void exitAsync() {
        getInstance().exitAsync();
    }

    public static void solve(String host, Page page) {
        getInstance().solve(host, page);
    }

    private static class PageFactoryImpl {
        private AtomicBoolean exit = new AtomicBoolean(false);
        private List<PageSolver> solvers = new ArrayList<>();
        private ThreadPoolExecutor executor =
                new ThreadPoolExecutor(
                        5,
                        9,
                        1,
                        TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>());

        private void push(PageSolver solver) {
            solvers.add(solver);
        }

        private void exit() {
            exitAsync();
            while (!executor.isTerminated()) {
                try {
                    TimeUnit.MICROSECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void exitAsync() {
            exit.set(true);
            executor.shutdown();
        }

        private void solve(String host, Page page) {
            if (exit.get())
                return;
            executor.execute(dispatch(host, page));
        }

        private PageProcessor dispatch(String host, Page page) {
            return new PageProcessor(host, page, (p) -> {
                for (PageSolver solver : solvers) {
                    page.content = solver.solve(page.content);
                }
            });
        }
    }

}
