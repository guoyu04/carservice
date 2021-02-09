// ICustomCarSysService.aidl
package com.metasequoia.services.custom;

// Declare any non-default types here with import statements
import com.metasequoia.services.custom.ICustomCarSysClient;
interface ICustomCarSysService {
    int addClient(ICustomCarSysClient client);
    int sendSpeak(String data);
    int setNavOpenState(boolean isOpen);
    int setBleOpenState(boolean isOpen) ;
    int setFMOpenState(boolean isOpen);
    
    int getCarGear();//获取档位状态
    float getCarSpeed();//获取车速
    int getRotateSpeed();//获取转速

     //设置人脸认证状态
    int setFaceState(int state);
    //设置评分等级
    int setGrading(int grade);
    //目的地周边优惠信息
    int setDestPerInfo(int type);
    //订单信息
    int setOrderInfo(int type);
}
