package com.metasequoia.manager.camera.base;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.metasequoia.manager.camera.FrameBufferCallBack;
import com.metasequoia.manager.camera.MemoryFile;
import com.metasequoia.manager.camera.utils.MemoryFileHelper;

import java.io.FileDescriptor;
import java.io.IOException;

import static com.metasequoia.manager.camera.config.Config.sIsNwd;

public abstract class BaseMemoryFile implements MemoryFile {

    private Handler mHander;
    protected final String TAG = "metasequoia" + this.getClass().getSimpleName();
    protected android.os.MemoryFile mMemoryFile;
    private byte[] isCanRead = new byte[1];
    private byte[] mBuffer = null;
    private byte[] Yv12Buffer = null;
    private HandlerThread mHandlerThread;
    private boolean needRead;
    private FrameBufferCallBack mYv12CallBack;
    private ParcelFileDescriptor parcelFileDescriptor;
    private FileDescriptor mFD;
    protected boolean mIsStream;
    private int mReadNum;


    protected BaseMemoryFile() {
        try {
            mMemoryFile = getMemoryFile();
            mBuffer = getBuffer();
            Yv12Buffer = getYv12Buffer();
            mFD = getFD();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHandlerThread = getHandlerThread();
        mHandlerThread.start();

        mHander = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg == null) return;
                switch (msg.what) {
                    case 0:
                        readShareBufferMsg();
                        break;
                    case 1:
                        readShareBufferCallback();
                        break;
                }
            }
        };
    }

    protected abstract FileDescriptor getFD();

    protected abstract HandlerThread getHandlerThread();

    protected abstract byte[] getYv12Buffer();

    protected abstract byte[] getBuffer();

    protected abstract android.os.MemoryFile getMemoryFile();

    protected abstract void setMemorySharedFD(FileDescriptor fd);

    @Override
    public void readShareBuffer() {
        if (mHander != null) {
            mHander.sendEmptyMessage(1);
        }
    }

    private void readShareBufferCallback() {
        try {
            if (mMemoryFile != null) {
                int count = mMemoryFile.readBytes(isCanRead, 0, 0, 1);
                if ((mReadNum++ % 255) == 0)
                    Log.d(TAG, " isCanRead = " + isCanRead[0] + " mReadNum:" + mReadNum);
                if (isCanRead[0] == 1) {
                    mMemoryFile.readBytes(mBuffer, 1, 0, mBuffer.length);
                    System.arraycopy(mBuffer, 0, Yv12Buffer, 0, mBuffer.length);
                    processData();
                    isCanRead[0] = 0;
                    mMemoryFile.writeBytes(isCanRead, 0, 0, 1);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void readShareBufferMsg() {
        try {
            if (mMemoryFile != null) {
                int count = mMemoryFile.readBytes(isCanRead, 0, 0, 1);
                if ((mReadNum++ % 5) == 0)
                    Log.d(TAG, " isCanRead = " + isCanRead[0]);
                if (isCanRead[0] == 1) {
                    mMemoryFile.readBytes(mBuffer, 1, 0, mBuffer.length);
                    System.arraycopy(mBuffer, 0, Yv12Buffer, 0, mBuffer.length);
                    processData();
                    isCanRead[0] = 0;
                    mMemoryFile.writeBytes(isCanRead, 0, 0, 1);
                }
            }

            if (mHander != null && needRead) {
                mHander.removeCallbacksAndMessages(0);
                mHander.sendEmptyMessageDelayed(0, sIsNwd ? 65 : 25);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processData() {
        if (mYv12CallBack != null) {
            mYv12CallBack.onFrame(Yv12Buffer, Yv12Buffer.length);
        }
    }

    public void startStream() {
        if (mIsStream) return;
        mIsStream = true;
        mHander.removeCallbacksAndMessages(0);
        needRead = true;
        setMemorySharedFD(mFD);
    }

    public void stopStream() {
        needRead = false;
        mIsStream = false;
        setMemorySharedFD(null);
        mHander.removeCallbacksAndMessages(0);
    }

    @Override
    public void release() {
        mHander.removeCallbacksAndMessages(0);
        mHandlerThread.quit();
        mHander = null;
        if (mMemoryFile != null) {
            mMemoryFile.close();
            mMemoryFile = null;
        }
    }

    public ParcelFileDescriptor getParcelFileDescriptor() {
        if (parcelFileDescriptor == null) {
            parcelFileDescriptor = MemoryFileHelper.getParcelFileDescriptor(mMemoryFile);
        }
        return parcelFileDescriptor;
    }

    @Override
    public void setBufferYv12CallBack(FrameBufferCallBack callBack) {
        mYv12CallBack = callBack;
        if (callBack == null) {
            stopStream();
        } else {
            startStream();
        }
    }
}
