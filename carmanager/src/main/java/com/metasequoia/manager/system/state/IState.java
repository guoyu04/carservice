package com.metasequoia.manager.system.state;

public interface  IState<X> {

    public X getState();
    
    public void setState(X state);
    
}
