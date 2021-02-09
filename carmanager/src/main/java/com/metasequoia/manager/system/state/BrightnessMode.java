package com.metasequoia.manager.system.state;

/**
 * 亮度模式：白天-默认，黑夜
 * Created by guoyu on 2020/8/12.
 */
public enum BrightnessMode implements IntState<BrightnessMode> {
    UNKOWN(-1),DAYTIME(1), NIHGT(2);
    private int mValue = -1;

    private BrightnessMode(int value) {
        mValue = value;
    }

    @Override
    public int getValue() {
        return mValue;
    }
    @Override
    public BrightnessMode translateValue(int value) {
        return doTranslateValue(value);
    }

    public static BrightnessMode doTranslateValue(int value) {
        if (value == DAYTIME.getValue()) return DAYTIME;
        if (value == NIHGT.getValue()) return NIHGT;
        return DAYTIME;
    }

}
