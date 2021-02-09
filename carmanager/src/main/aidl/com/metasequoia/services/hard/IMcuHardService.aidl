// IMcuHardService.aidl
package com.metasequoia.services.hard;

// Declare any non-default types here with import statements
import com.metasequoia.services.hard.IMcuHardClient;

interface IMcuHardService {
    int addClient(IMcuHardClient client);
    int requestMcuVersion();
    int requestMcuUpdate(String binPath);
}
