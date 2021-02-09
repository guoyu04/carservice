package com.metasequoia.manager.system.setter;

import android.content.Context;

import com.metasequoia.manager.system.state.IState;
import com.metasequoia.manager.system.state.IntState;

/**
 * 系统设置状态值监听类:封装了状态设置
 *
 * Created by guoyu on 2020/8/12.
 */
public class SettingStateListener<State extends IntState<State>> extends IValueListener<String, State> implements IState<State> {

    private State mDefaultState = null;

    private SettingsIntListener mSystemProviderIntegerValueSetter = null;
    protected Context mContext;
    public SettingStateListener(Context context, String keyName, State defaultState) {
        this(context,keyName,defaultState, SettingsListener.TableType.SYSTEM);
        mContext = context.getApplicationContext();
    }
    
    public SettingStateListener(Context context, String keyName, State defaultState, SettingsListener.TableType table) {
        super(keyName);
        mSystemProviderIntegerValueSetter = new SettingsIntListener(context, keyName,table);
        mDefaultState = defaultState;
    }

    @Override
    public boolean setValue(State value) {
        return setValue(value,true);
    }
    
    @Override
    public boolean setValue(State value,boolean notify) {
        mSystemProviderIntegerValueSetter.setValue(value.getValue(),notify);
        return true;
    }

    @Override
    public State getValue() {
        return mDefaultState.translateValue(mSystemProviderIntegerValueSetter.getValue());
    }

    @Override
    public void registerOnChangedListener(OnChangedListener<State> listener) {
        super.registerOnChangedListener(listener);
        mSystemProviderIntegerValueSetter.registerOnChangedListener(mOnValueChangedListener);
    }

    @Override
    public void unregisterOnChangedListener(OnChangedListener<State> listener) {
        mSystemProviderIntegerValueSetter.unregisterOnChangedListener(mOnValueChangedListener);
        super.unregisterOnChangedListener(listener);
    }

    private OnChangedListener<Integer> mOnValueChangedListener = new OnChangedListener<Integer>() {
        @Override
        public void onChanged(Integer state) {
            dispatchOnChanged(mDefaultState.translateValue(state));
        }
    };

    @Override
    public State getState() {
        return getValue();
    }

    @Override
    public void setState(State state) {
        setValue(state);
    }

    @Override
    public State getValue(State defaultValue) {
        return null;
    }

}
