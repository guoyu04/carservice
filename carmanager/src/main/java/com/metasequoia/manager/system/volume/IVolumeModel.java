package com.metasequoia.manager.system.volume;

public interface IVolumeModel {
    public static int FLAG_SHOW_IU=0x10000;
    public void setVolume(int level);
    public void setVolume(int level, int flag);
    public int getVolume();
}
