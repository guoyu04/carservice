// IAudioService.aidl
package com.metasequoia.services.audio;

// Declare any non-default types here with import statements
import com.metasequoia.services.audio.IAudioClient;
interface IAudioService {
    int addClient(IAudioClient client);
    int setVolume(int value, int flag);
    int setMute(boolean isMute);
    int setEffect(int low, int mid, int high);
    int setGain(int fl, int fr, int bl, int br);
    int setChannel(int channel);
}
