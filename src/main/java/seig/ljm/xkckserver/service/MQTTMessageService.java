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

    /**
     * 处理门禁验证消息
     * @param payload 消息内容
     */
    void handleVerify(String payload);

    /**
     * 发送紧急控制命令
     * @param deviceId 设备ID
     * @param action 动作（emergency_open或emergency_close）
     * @param reason 原因
     * @return 发送是否成功
     */
    boolean sendEmergencyControl(Integer deviceId, String action, String reason);
}
