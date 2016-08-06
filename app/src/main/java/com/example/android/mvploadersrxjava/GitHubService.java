package com.example.android.mvploadersrxjava;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by User on 6.8.2016..
 */
public interface GitHubService {

    @GET("users/{user}/repos")
    Observable<List<Repo>> listRepos(@Path("user") String user);

}
