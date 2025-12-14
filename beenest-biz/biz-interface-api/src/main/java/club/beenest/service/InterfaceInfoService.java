package club.beenest.service;

import club.beenest.model.InterfaceInfo;

import java.util.List;

public interface  InterfaceInfoService {

    /**
     * 发送接口信息列表
     * 服务提供者实现此方法来接收和处理接口信息
     *
     * @param interfaceInfos 接口信息列表
     * @return 处理结果，true表示成功，false表示失败
     */
    boolean sendInterfaceInfos(List<InterfaceInfo> interfaceInfos);

}
