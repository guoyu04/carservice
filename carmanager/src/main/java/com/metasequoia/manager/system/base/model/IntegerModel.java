package com.metasequoia.manager.system.base.model;

import com.metasequoia.manager.listener.BaseListener;
import com.metasequoia.manager.listener.OnListener;
import com.metasequoia.manager.system.base.sender.ISender;
import com.metasequoia.manager.system.setter.IValueListener;

public abstract class IntegerModel<Arg> extends BaseListener<Integer> implements OnListener.OnChangedListener<Integer>,Model {

    /**
     * 用于获取数据
     */
    private IValueListener<String, Integer> mIntegerSetter = null;

    /**
     * 用于发送数据
     */
    private ISender<Integer> mIntegerSender = null;

    private Arg mArg = null;

    public IntegerModel(Arg arg) {
        mArg = arg;
    }

    public void onCreate() {
        mIntegerSender=createIntegerSender(mArg);
        mIntegerSetter = createIntegerSetter(mArg);
        mIntegerSetter.registerOnChangedListener(this);
    }

    public void onDestroy() {
        mIntegerSetter.unregisterOnChangedListener(this);
    }

    abstract protected IValueListener<String, Integer> createIntegerSetter(Arg arg);

    abstract protected ISender<Integer> createIntegerSender(Arg arg);

    protected void setIntValue(Integer value) {
        mIntegerSender.send(value);
    }

    protected Integer getIntValue(Integer defaultValue) {
        return mIntegerSetter.getValue(defaultValue);
    }

    public void onChanged(Integer value) {
        this.dispatchOnChanged(value);

    }
}
