package com.sddd.tfn.ffmpeginmac;

/**
 * Created by fanzhengchen on 10/22/16.
 */

public class Player {

    static {
        System.loadLibrary("VideoPlayer");
    }

    public static native int play(Object surface);


//    public static native int transcodeVideo(String[] cmds);

    public static native int transcodeVideo(String[] cmds);
}
