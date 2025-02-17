package seig.ljm.xkckserver.service;

public interface MQTTMessageService {
    /**
     * 处理MQTT消息
     * @param topic 主题
     * @param payload 消息内容
     */
    void handleMessage(String topic, String payload);

    /**
     * 处理连接消息
     * @param payload 消息内容
     */
    void handleConnect(String payload);

    /**
     * 处理卡片验证消息
     * @param payload 消息内容
     */
    void handleVerifyCard(String payload);

    /**
     * 处理心跳消息
     * @param payload 消息内容
     */
    void handleHeartbeat(String payload);

    /**
     * 处理状态上报消息
     * @param payload 消息内容
     */
    void handleStatusReport(String payload);

}
