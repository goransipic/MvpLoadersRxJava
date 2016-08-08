package com.example.android.mvploadersrxjava;

import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import rx.Observable;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<NetWorkLoader.OperationWrapper> {

    private Loader<String> stringLoader;
    private NetWorkLoader mNetWorkLoader;
    private TextView mTextView;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.hello_world);
        mButton = (Button) findViewById(R.id.button);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNetWorkLoader.setPosts();
            }
        });
        if (savedInstanceState != null) {
            mNetWorkLoader = (NetWorkLoader) getSupportLoaderManager().initLoader(0, null, this);
            if (mNetWorkLoader.getResult().getRepos() != null){
                // Restore Data after Configuration change
                mTextView.setText(mNetWorkLoader.getResult().getRepos().toString());
            }

        } else {
            getSupportLoaderManager().initLoader(0, null, this);
        }

    }

    @Override
    public Loader<NetWorkLoader.OperationWrapper> onCreateLoader(int id, Bundle args) {
        mNetWorkLoader = new NetWorkLoader(this);
        return mNetWorkLoader;
    }

    @Override
    public void onLoadFinished(Loader<NetWorkLoader.OperationWrapper> loader, NetWorkLoader.OperationWrapper operationResult) {

        NetWorkLoader.Operation operation = operationResult.getOperation();

        if (operation.executeLoadComplete()){
            mTextView.setText("");
            List<Repo> repos = mNetWorkLoader.getResult().getRepos();

            for (Repo repo : repos) {
                mTextView.setText(repo.getName() + "\n" + mTextView.getText());
            }
        }

        if (operation.executeHasError()) {
            mTextView.setText("Has Error");
        }
    }

    @Override
    public void onLoaderReset(Loader<NetWorkLoader.OperationWrapper> loader) {
        mNetWorkLoader = null;
    }

}
