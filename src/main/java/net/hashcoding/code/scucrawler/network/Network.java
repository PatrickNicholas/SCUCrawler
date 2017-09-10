package net.hashcoding.code.scucrawler.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.hashcoding.code.scucrawler.Constant;
import net.hashcoding.code.scucrawler.network.service.ArticleService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Network {

    private static NetworkImpl instance;

    private static NetworkImpl getInstance() {
        if (instance == null) synchronized (Network.class) {
            if (instance == null)
                instance = new NetworkImpl();
        }
        return instance;
    }

    public static ArticleService getArticleService() {
        return getInstance().getArticleService();
    }

    private static class NetworkImpl {
        private Retrofit retrofit;

        NetworkImpl() {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();
            retrofit = new Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }

        private ArticleService getArticleService() {
            return retrofit.create(ArticleService.class);
        }
    }
}
