package com.metasequoia.manager.system.state;

public interface IntState<State>{
    public int getValue();
    public State translateValue(int value);
}