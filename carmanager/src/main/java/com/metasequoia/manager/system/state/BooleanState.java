package com.metasequoia.manager.system.state;


public enum BooleanState implements IntState<BooleanState> {
    UNKOWN(-1),TRUE(1), FALSE(0);
    private int mValue = -1;

    private BooleanState(int value) {
        mValue = value;
    }

    @Override
    public int getValue() {
        return mValue;
    }
    @Override
    public BooleanState translateValue(int value) {
        return doTranslateValue(value);
    }

    public static BooleanState doTranslateValue(int value) {
        if (value == TRUE.getValue()) return TRUE;
        if (value == FALSE.getValue()) return FALSE;
        return FALSE;
    }

}
