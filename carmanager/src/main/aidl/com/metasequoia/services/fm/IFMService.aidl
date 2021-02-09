// IFMService.aidl
package com.metasequoia.services.fm;

// Declare any non-default types here with import statements
import com.metasequoia.services.fm.IFMClient;

interface IFMService {
    int addClient(IFMClient client);
    void openFMPower();
    void closeFMPower();
    int setFreq(int freq);
    int getFreq();
}
