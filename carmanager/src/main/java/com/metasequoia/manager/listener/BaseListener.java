package com.metasequoia.manager.listener;

import java.util.ArrayList;
import java.util.List;

public class BaseListener<State> implements OnListener<State> {

    private List<OnChangedListener<State>> mOnChangedListener = new ArrayList<OnChangedListener<State>>();

    public void registerOnChangedListener(OnChangedListener<State> obverser) {
        mOnChangedListener.remove(obverser);
        mOnChangedListener.add(obverser);
    }

    public void unregisterOnChangedListener(OnChangedListener<State> obverser) {
        mOnChangedListener.remove(obverser);
    }

    public void dispatchOnChanged(State state) {
        for (OnChangedListener<State> obverser : mOnChangedListener) {
            if (obverser != null) obverser.onChanged(state);
        }
    }

    protected List<OnChangedListener<State>> getChangedListeners() {
        return mOnChangedListener;
    }
}
