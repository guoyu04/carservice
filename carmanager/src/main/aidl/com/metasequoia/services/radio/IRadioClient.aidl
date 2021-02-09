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

// IRadioClient.aidl
package com.metasequoia.services.radio;

import com.metasequoia.manager.radio.bean.Frequency;

/**
 * Interface a client of the IAccessibilityManager implements to
 * receive information about changes in the manager state.
 *
 */
interface IRadioClient {
    oneway void setState(int stateFlags);
    oneway void setSearchResult(in Frequency frequency, int effect);
    oneway void setCurrentFrequencyChange(in Frequency frequency);
    oneway void setSearchEnd(String result);
    oneway void setIsStereoOn(boolean isStereoOn);
    oneway void setIsRadioDX(boolean isNearOn);
}
