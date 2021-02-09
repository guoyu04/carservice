// IPowerService.aidl
package com.metasequoia.services.power;

// Declare any non-default types here with import statements
import com.metasequoia.services.power.IPowerClient;

interface IPowerService {
    int addClient(IPowerClient client);
    int getAccState();
}
