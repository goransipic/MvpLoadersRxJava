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
public class NetWorkLoader extends Loader<NetWorkLoader.OperationWrapper> {

    private Result mResult = new Result();

    private Operation mOperation = new Operation();

    private GitHubService service;

    private Retrofit mRetrofit = new Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public NetWorkLoader(Context context) {
        super(context);

        service = mRetrofit.create(GitHubService.class);
    }

    public Result getResult() {
        return mResult;
    }

    @Override
    protected void onStartLoading() {

        if (takeContentChanged()) {
            deliverResult(mOperation,Operation.REFRESH_UI);
        }
    }

    public void setPosts() {

        service.listRepos("shrenk")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Repo>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        deliverResult(e, Operation.ERROR);
                    }

                    @Override
                    public void onNext(List<Repo> repos) {
                        deliverResult(repos, Operation.LOAD_COMPLETE);
                    }
                });
    }

    public void deliverResult(Object result, int operation) {

        if (operation == Operation.LOAD_COMPLETE) {
            mResult.setRepos((List<Repo>) result);
            mOperation.setLoadComplete(true);
        }

        if (operation == Operation.ERROR) {
            mOperation.setError(true);
        }

        if (isStarted() || (operation == Operation.REFRESH_UI)) {
            super.deliverResult(new OperationWrapper(mOperation));
        } else {
            onContentChanged();
        }


    }

    public static class OperationWrapper {

        protected Operation mOperation;

        public OperationWrapper(Operation mOperation) {
            this.mOperation = mOperation;
        }

        public Operation getOperation() {
            return mOperation;
        }
    }

    public static class Operation {

        boolean mLoadComplete;
        boolean mError;

        public static final int LOAD_COMPLETE = 1;
        public static final int ERROR = 2;
        private static final int REFRESH_UI = 3;

        public boolean executeLoadComplete() {
            boolean loadComplete = mLoadComplete;
            mLoadComplete = false;
            return loadComplete;
        }

        public boolean setLoadComplete(boolean mLoadComplete) {
            return this.mLoadComplete = mLoadComplete;
        }

        public boolean executeHasError() {
            boolean error = mError;
            mError = false;
            return error;
        }

        public void setError(boolean mError) {
            this.mError = mError;
        }
    }

    public class Result {

        private List<Repo> mRepos;

        public List<Repo> getRepos() {
            return mRepos;
        }

        public void setRepos(List<Repo> mRepos) {
            this.mRepos = mRepos;
        }
    }
}
