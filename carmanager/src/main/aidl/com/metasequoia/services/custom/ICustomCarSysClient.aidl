/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// ICustomCarSysClient.aidl
package com.metasequoia.services.custom;

/**
 * Interface a client of the IAccessibilityManager implements to
 * receive information about changes in the manager state.
 *
 */
interface ICustomCarSysClient {
    /**
     * mcu档位状态
     * @param gear mcu档位
     */
    oneway void onCarGear(int gear);

    /**
     * mcu车速
     * @param speed 车速
     */
    oneway void onCarSpeed(float speed);
    /**
     * mcu转速
     * @param speed 转速
     */
    oneway void onRotateSpeed(int speed);
    /**
     * 一键报警按键 1松开，0按下
     * @param key 按键
     * @param state 状态
     */
    oneway void onKeyEvent(int key, int state);
}
