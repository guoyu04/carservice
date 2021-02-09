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

// ICustomPowerClient.aidl
package com.metasequoia.services.custom;

/**
 * Interface a client of the IAccessibilityManager implements to
 * receive information about changes in the manager state.
 *
 */
interface ICustomPowerClient {
    oneway void onAccStateChange(int state);
    /**
     * mcu电池电量
     * @param battery
     */
    oneway void onBatteryPower(float battery);

    /**
     * mcu电池状态
     * @param state
     */
    oneway void onBatteryState(int state);

    /**
     * mcu总里程
     * @param mileage 总里程
     */
    oneway void onTotalMileage(int mileage);

    /**
     * mcu剩余里程
     * @param mileage 剩余里程
     */
    oneway void onRemMileage(int mileage);

    /**
     * device id
     * @param devID id
     */
    oneway void onDeviceID(String devID);

    /**
     * mcu电池电压
     * @param voltage 电压
     */
    oneway void onBatteryVoltage(float voltage);
}
