package com.apps.orenc.detectandrecognize;

import android.os.Build;

/**
 * Created by orenc on 7/18/15.
 *
 * Some enviroment variables.
 */
public class EnvironmentVariables {

    // Indication if the application run on device or emulator.
    public static final boolean iAmEmulator;

    static {
        iAmEmulator = Build.FINGERPRINT.startsWith("generic");
    }

}
