package seig.ljm.xkckserver.common.constant;

import java.time.ZoneId;

/**
 * 时区常量
 */
public class TimeZoneConstant {
    /**
     * 系统默认时区
     */
    public static final String ZONE_NAME = "Asia/Shanghai";
    
    /**
     * 系统默认时区ID
     */
    public static final ZoneId ZONE_ID = ZoneId.of(ZONE_NAME);

    /**
     * 时间格式
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
} 