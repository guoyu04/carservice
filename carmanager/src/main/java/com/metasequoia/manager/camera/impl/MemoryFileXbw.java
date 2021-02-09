package com.metasequoia.manager.camera.impl;

import android.os.HandlerThread;
import android.os.MemoryFile;
import android.util.Log;

import com.metasequoia.manager.camera.base.BaseMemoryFile;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;

public class MemoryFileXbw extends BaseMemoryFile {
    private static volatile com.metasequoia.manager.camera.MemoryFile sMemoryFile;

    private MemoryFileXbw() {
        super();
    }

    public static com.metasequoia.manager.camera.MemoryFile getInstance() {
        if (sMemoryFile == null) {
            synchronized (MemoryFileXbw.class) {
                if (sMemoryFile == null) {
                    sMemoryFile = new MemoryFileXbw();
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
        return new byte[610560];
    }

    @Override
    protected byte[] getBuffer() {
        return new byte[610560];
    }

    @Override
    protected MemoryFile getMemoryFile() {
        try {
            return new MemoryFile(TAG, 610561);
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
