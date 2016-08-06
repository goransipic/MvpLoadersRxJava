package com.example.android.mvploadersrxjava;

import android.content.Context;
import android.support.v4.content.Loader;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by User on 6.8.2016..
 */
public class NetWorkLoader extends Loader<NetWorkLoader.Result> {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    GitHubService service;

    GetPostResults result = new GetPostResults(null);

    public NetWorkLoader(Context context) {
        super(context);

        service = retrofit.create(GitHubService.class);
    }

    public GetPostResults getResult() {
        return this.result;
    }

    @Override
    protected void onStartLoading() {

        if (takeContentChanged()) {
            deliverResult(result);
        }
    }

    public void setPosts() {

        service.listRepos("shrenk")
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<List<Repo>, Result>() {
                    @Override
                    public Result call(List<Repo> repos) {
                        result.setRepos(repos);
                        return result;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Result>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Result repos) {
                        deliverResult(result);
                    }
                });


    }


    @Override
    public void deliverResult(Result data) {
        if (isStarted()) {
            super.deliverResult(data);
        } else {
            onContentChanged();
        }


    }

    public static interface Result {

    }

    public static class GetPostResults implements Result {

        public GetPostResults(List<Repo> repos) {
            this.repos = repos;
        }

        public List<Repo> getRepos() {
            return repos;
        }

        public void setRepos(List<Repo> repos) {
            this.repos = repos;
        }

        List<Repo> repos;

    }


}
