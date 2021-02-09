package com.metasequoia.manager.listener;

public interface OnListener<State> {

    public static interface OnChangedListener<State> {
        public void onChanged(State state);
    }
    public void registerOnChangedListener(OnChangedListener<State> obverser);

    public void unregisterOnChangedListener(OnChangedListener<State> obverser);

    public void dispatchOnChanged(State state);

}
