package com.metasequoia.manager.camera.impl;

import android.os.HandlerThread;
import android.os.MemoryFile;
import android.util.Log;

import com.metasequoia.manager.camera.base.BaseMemoryFile;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;

public class MemoryFileHuiying extends BaseMemoryFile {
    private static volatile com.metasequoia.manager.camera.MemoryFile sMemoryFile;

    private MemoryFileHuiying() {
        super();
    }

    public static com.metasequoia.manager.camera.MemoryFile getInstance() {
        if (sMemoryFile == null) {
            synchronized (MemoryFileHuiying.class) {
                if (sMemoryFile == null) {
                    sMemoryFile = new MemoryFileHuiying();
                }
            }
        }
        return sMemoryFile;
    }

    @Override
    protected FileDescriptor getFD() {
        try {
            Field mFDFiled = mMemoryFile.getClass().getDeclaredField("mFD");
            mFDFiled.setAccessible(true);
            return (FileDescriptor) mFDFiled.get(mMemoryFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected HandlerThread getHandlerThread() {
        return new HandlerThread("video_thread");
    }

    @Override
    protected byte[] getYv12Buffer() {
        return new byte[1382400];
    }

    @Override
    protected byte[] getBuffer() {
        return new byte[1382400];
    }

    @Override
    protected MemoryFile getMemoryFile() {
        try {
            return new MemoryFile(TAG, 1382401);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.w(TAG, "create MemoryFile Fail");
        return null;
    }

    @Override
    protected void setMemorySharedFD(FileDescriptor fd) {
    }

    @Override
    public void release() {
        super.release();
        sMemoryFile = null;
    }
}
