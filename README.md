# BaseBle

#### 介绍
利用Nordic的蓝牙库进行基础的低功耗蓝牙操作Demo。
满足一般的1对1蓝牙扫描，连接，数据通讯操作

#### 软件架构
软件架构说明
[ble目录](https://github.com/elongpaoxiao/BaseBleDemo/tree/master/app/src/main/java/net/praycloud/basebledemo/ble)下是蓝牙主逻辑管理相关
[views](https://github.com/elongpaoxiao/BaseBleDemo/tree/master/app/src/main/java/net/praycloud/basebledemo/views)主要为UI部分逻辑


#### app使用说明
1.  开启app，权限授权，开关检测通过后会自动进行蓝牙扫描，刷新列表
2.  点击列表设备进行蓝牙扫描，跳转到设备管理页
3.  在设备管理页与设备进行数据通讯


#### 配置修改说明
[Manifest](https://github.com/elongpaoxiao/BaseBleDemo/blob/master/app/src/main/AndroidManifest.xml)内需要添加蓝牙与定位权限（安卓6.0以上需要定位授权，以及大部分机型需要开启定位服务才能进行蓝牙操作）
蓝牙类主要配置修改：
1.  蓝牙扫描连接主要配置集中在[ble目录下](https://github.com/elongpaoxiao/BaseBleDemo/blob/master/app/src/main/java/net/praycloud/basebledemo/ble/BleConfig.kt)的BleConfig类（替换成所需的蓝牙设备配置）
2.  设备对象类与设备互通数据在[ble.device_data目录下](https://github.com/elongpaoxiao/BaseBleDemo/blob/master/app/src/main/java/net/praycloud/basebledemo/ble/device_data)（根据设备通讯规则进行修改）
3.  扫描回调对象与扫描状态在[ble.scan_data目录下](https://github.com/elongpaoxiao/BaseBleDemo/blob/master/app/src/main/java/net/praycloud/basebledemo/ble/scan_data)（一般来说不需要改动）

#### 蓝牙相关控制
1.  蓝牙主要操作（扫描，连接，数据通讯，状态监听）通过[MyBleManager管理类](https://github.com/elongpaoxiao/BaseBleDemo/blob/master/app/src/main/java/net/praycloud/basebledemo/ble/MyBleManager.kt)完成

UI相关按需修改不再叙述

