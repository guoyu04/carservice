package com.metasequoia.manager.system.power;

import com.metasequoia.manager.system.state.BooleanState;

public interface IPowerModel {
    public void setScreenOff();
    public void setPowerReboot();
    public BooleanState getAccState();
}
