package com.metasequoia.manager.camera.impl;

import android.hardware.Camera;
import android.os.HandlerThread;
import android.os.MemoryFile;
import android.util.Log;

import com.metasequoia.manager.camera.base.BaseMemoryFile;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MemoryFilePvetFront extends BaseMemoryFile {

    private static volatile com.metasequoia.manager.camera.MemoryFile sMemoryFile;


    public MemoryFilePvetFront() {
        super();
    }

    public static com.metasequoia.manager.camera.MemoryFile getInstance() {
        if (sMemoryFile == null) {
            synchronized (MemoryFilePvetFront.class) {
                if (sMemoryFile == null) {
                    sMemoryFile = new MemoryFilePvetFront();
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
        return new HandlerThread("front_thread");
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
        Log.d(TAG, "new MemoryFile Fail");
        return null;
    }

    @Override
    protected void setMemorySharedFD(FileDescriptor fd) {
        try {
            Class clazz = Class.forName("android.hardware.Camera");
            Method methodUninitialized = clazz.getDeclaredMethod("openUninitialized");
            Camera camera = (Camera) methodUninitialized.invoke(null);
            Method method = clazz.getDeclaredMethod("setShareFD", FileDescriptor.class);
            method.invoke(camera, fd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startStream() {
        if(mIsStream) return;
        super.startStream();
        readShareBufferMsg();
    }

    @Override
    public void release() {
        super.release();
        sMemoryFile = null;
    }
}
