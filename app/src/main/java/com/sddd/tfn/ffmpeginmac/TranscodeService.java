package com.sddd.tfn.ffmpeginmac;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by fanzhengchen on 10/22/16.
 */

public class TranscodeService extends Service {


    ITranscodeAidlInterface.Stub aidlInterface = new ITranscodeAidlInterface.Stub() {
        @Override
        public void transcode(List<String> commands) throws RemoteException {
            TranscodeService.this.transcode(commands);
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("Remote", "create");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Logger.d("remote on bind " + aidlInterface);
        return aidlInterface;
    }

    @Override
    public void onDestroy() {
        Logger.d("remote on destroy");
        super.onDestroy();
    }

    private void transcode(List<String> commands) {
        String[] cmds = new String[commands.size()];
        commands.toArray(cmds);
        for (String cmd : cmds) {
            Logger.d("remote " + cmd);
        }
        int ret = Player.transcodeVideo(cmds);
        Logger.d("remote transcode result %d", ret);
    }
}
