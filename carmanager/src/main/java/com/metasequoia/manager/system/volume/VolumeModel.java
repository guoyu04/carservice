package com.metasequoia.manager.system.volume;

import android.content.Context;

import com.metasequoia.manager.listener.BaseListener;
import com.metasequoia.manager.listener.OnListener;
import com.metasequoia.manager.system.base.model.Model;

/**
 * 音量管理类
 * Created by guoyu on 2020/8/12.
 */
public class VolumeModel extends BaseListener<Integer> implements OnListener.OnChangedListener<Integer>, IVolumeModel, Model {
    private VolumeSetterSender mVolumeSetterSender = null;

    public VolumeModel(Context context) {
        mVolumeSetterSender = new VolumeSetterSender(context.getApplicationContext());
    }

    @Override
    public void onCreate() {
        mVolumeSetterSender.registerOnChangedListener(this);
    }

    @Override
    public void onDestroy() {
        mVolumeSetterSender.unregisterOnChangedListener(this);
    }

    /**
     * 获取音量
     * @return  value 音量值
     */
    @Override
    public int getVolume() {
        return mVolumeSetterSender.getVolume();
    }

    /**
     * 设置音量
     * @param value 音量值
     */
    @Override
    public void setVolume(int value) {
        mVolumeSetterSender.setVolume(value);
    }

    /**
     * 设置音量
     * @param value 音量值
     * @param flag FLAG_SHOW_IU、FLAG_PLAY_SOUND扩展字段
     */
    @Override
    public void setVolume(int value, int flag) {
        mVolumeSetterSender.setVolume(value, flag);
    }

    /**
     * 音量值改变状态回调，并分发到监听者
     * @param value
     */
    @Override
    public void onChanged(Integer value) {
        this.dispatchOnChanged(VolumeSetterSender.normalizeValue(value));
    }



}
