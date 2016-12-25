package com.cpoopc.rxcache;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cpoopc.retrofitrxcache.RxCacheResult;
import com.cpoopc.rxcache.api.APIManager;
import com.cpoopc.rxcache.model.User;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

    @Bind(R.id.avatarIv)
    ImageView mAvatarIv;
    @Bind(R.id.userNameTv)
    TextView mUserNameTv;
    @Bind(R.id.companyTv)
    TextView mCompanyTv;
    @Bind(R.id.locationTv)
    TextView mLocationTv;
    @Bind(R.id.createAtTv)
    TextView mCreateAtTv;
    @Bind(R.id.followersCountTv)
    TextView mFollowersCountTv;
    @Bind(R.id.starredCountTv)
    TextView mStarredCountTv;
    @Bind(R.id.followingCountTv)
    TextView mFollowingCountTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // 联网获取数据
        getUserDetail();

        // 点击用户名去 联网获取数据
        mUserNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("cp:", "onclick");
                getUserDetail();

            }
        });

    }


    /**
     * 获取用户数据
     */
    public void getUserDetail() {

        // 1
        Observable<RxCacheResult<User>> userObservable = APIManager.buildAPI("haha",1).userDetail("cpoopc");

        // 2
        userObservable
                .subscribeOn(Schedulers.io())
                .materialize()
                .observeOn(AndroidSchedulers.mainThread())// 主线程
                .<RxCacheResult<User>>dematerialize()
                .subscribe(new Subscriber<RxCacheResult<User>>() {

                    // 完成
                    @Override
                    public void onCompleted() {
                        Log.e("cp:", "onCompleted");
                    }

                    // 出错
                    @Override
                    public void onError(Throwable e) {
                        Log.e("cp:", "onError:" + e.getMessage());
                    }

                    // 成功
                    @Override
                    public void onNext(RxCacheResult<User> user) {
                        Log.e("cp:", user.isCache() + " onNext:" + user.getResultModel());
                        bindUser(user.getResultModel()); // 显示数据
                    }

                });

    }

    /**
     * 显示数据
     */
    private void bindUser(User user) {
        if (user != null) {
            // user 不为空加载真实数据
            Glide.with(this).load(user.getAvatar_url()).into(mAvatarIv);
            mUserNameTv.setText(user.getLogin());
            mCompanyTv.setText(user.getCompany());
            mLocationTv.setText(user.getLocation());
            mCreateAtTv.setText(user.getCreated_at());
            mFollowersCountTv.setText(String.valueOf(user.getFollowers()));
            mStarredCountTv.setText("?");
            mFollowingCountTv.setText(String.valueOf(user.getFollowing()));
        } else {
            // 默认数据
            mAvatarIv.setImageResource(R.mipmap.ic_launcher);
            mUserNameTv.setText("noData");
            mCompanyTv.setText("noData");
            mLocationTv.setText("noData");
            mCreateAtTv.setText("noData");
            mFollowersCountTv.setText("noData");
            mStarredCountTv.setText("noData");
            mFollowingCountTv.setText("noData");
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
