package com.metasequoia.manager.system.setter;

import com.metasequoia.manager.listener.BaseListener;

public abstract class IValueListener<keyType, valueType> extends BaseListener<valueType> {
    private keyType mkeyName = null;
    public void setKeyName(keyType key) {
        mkeyName = key;
    }

    public keyType getKeyName() {
        return mkeyName;
    }

    protected IValueListener(keyType keyName) {
        mkeyName = keyName;
    }

    abstract public boolean setValue(valueType value);

    abstract public boolean setValue(valueType value,boolean notify);

    abstract public valueType getValue(valueType defaultValue);

    abstract public valueType getValue();

}
