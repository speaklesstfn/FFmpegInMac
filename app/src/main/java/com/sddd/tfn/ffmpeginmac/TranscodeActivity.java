package com.sddd.tfn.ffmpeginmac;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by tfn on 11/16/16.
 */

public class TranscodeActivity extends AppCompatActivity {

    private RelativeLayout mWaitRL = null;
    private TextView mResultTxt = null;
    private long startMill = 0L;
    private long endMill = 0L;
    private long totalTime = 0L;

    private TranscodeService mRemoteService;
    private ITranscodeAidlInterface aidlInterface;
    private final String basePath = "/storage/emulated/0/mydata/vivo";
    private final String targetPath = basePath + File.separator + "outout111.mp4";
    private final String[] commands = {
            "ffmpeg",
            "-i",
            basePath + File.separator + "video_20161111_164706.mp4",
//            basePath + File.separator + "1479263099280.mp4",
//            "-b", "0.5M",
//            "-s","720x1080",
//            "-r", "24",
            targetPath,
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcode);

        mResultTxt = (TextView) findViewById(R.id.result_txt);
        mResultTxt.setText("");
        mWaitRL = (RelativeLayout) findViewById(R.id.wait_rl);
        mWaitRL.setVisibility(View.INVISIBLE);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.transcode_remote_process)
    public void transcodeInRemoteProcess() {
        Intent intent = new Intent(getApplicationContext(), TranscodeService.class);
        bindService(intent, sc, Context.BIND_AUTO_CREATE);
    }

    @OnClick(R.id.transcode_io_thread)
    public void transcode() {
        mWaitRL.setVisibility(View.VISIBLE);
        FileUtils.resetFile(targetPath);

        Observable.just(commands)
                .map(new Func1<String[], Integer>() {
                    @Override
                    public Integer call(String[] strings) {
                        Logger.d("start transcode");
                        startMill = System.currentTimeMillis();
                        return Player.transcodeVideo(strings);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        mWaitRL.setVisibility(View.INVISIBLE);
                        mResultTxt.setText("转码失败");
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        endMill = System.currentTimeMillis();
                        totalTime = (endMill - startMill) / 1000;
                        mResultTxt.setText("转码成功，总共用时：" + totalTime + "秒");
                        mWaitRL.setVisibility(View.INVISIBLE);
                        Logger.d("transcode result " + integer);
                    }
                });

    }


    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            aidlInterface = ITranscodeAidlInterface.Stub.asInterface(service);
            List<String> cmds = Arrays.asList(commands);
            try {
                aidlInterface.transcode(cmds);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            aidlInterface = null;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (aidlInterface != null) {
            unbindService(sc);
        }
    }
}
