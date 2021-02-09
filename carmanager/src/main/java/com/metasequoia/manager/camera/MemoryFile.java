package com.metasequoia.manager.camera;

import android.os.ParcelFileDescriptor;

public interface MemoryFile {
    void setBufferYv12CallBack(FrameBufferCallBack callBack);

    ParcelFileDescriptor getParcelFileDescriptor();

    void readShareBuffer();

    void release();
}
