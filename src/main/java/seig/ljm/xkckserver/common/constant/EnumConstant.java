package seig.ljm.xkckserver.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 枚举常量类
 */
public class EnumConstant {
    // 访客表枚举
    public static final class Visitor {
        // 证件类型
        public static final Map<String, String> ID_TYPE = new HashMap<>() {{
            put("school_id", "1");
            put("id_card", "2");
            put("passport", "3");
            put("other", "4");
        }};
        public static final Map<String, String> ID_TYPE_TEXT = new HashMap<>() {{
            put("1", "school_id");
            put("2", "id_card");
            put("3", "passport");
            put("4", "other");
        }};

        // 角色
        public static final Map<String, String> ROLE = new HashMap<>() {{
            put("admin", "1");
            put("visitor", "2");
        }};
        public static final Map<String, String> ROLE_TEXT = new HashMap<>() {{
            put("1", "admin");
            put("2", "visitor");
        }};

        // 账号状态
        public static final Map<String, String> ACCOUNT_STATUS = new HashMap<>() {{
            put("normal", "1");
            put("disabled", "2");
            put("blacklist", "3");
        }};
        public static final Map<String, String> ACCOUNT_STATUS_TEXT = new HashMap<>() {{
            put("1", "normal");
            put("2", "disabled");
            put("3", "blacklist");
        }};
    }

    // 门禁设备表枚举
    public static final class AccessDevice {
        // 设备状态
        public static final Map<String, String> STATUS = new HashMap<>() {{
            put("online", "1");
            put("offline", "2");
            put("maintenance", "3");
        }};
        public static final Map<String, String> STATUS_TEXT = new HashMap<>() {{
            put("1", "online");
            put("2", "offline");
            put("3", "maintenance");
        }};

        // 设备类型
        public static final Map<String, String> DEVICE_TYPE = new HashMap<>() {{
            put("campus_gate", "1");
            put("facility_gate", "2");
            put("management", "3");
        }};
        public static final Map<String, String> DEVICE_TYPE_TEXT = new HashMap<>() {{
            put("1", "campus_gate");
            put("2", "facility_gate");
            put("3", "management");
        }};

        // 门禁状态
        public static final Map<String, String> DOOR_STATUS = new HashMap<>() {{
            put("open", "1");
            put("closed", "2");
        }};
        public static final Map<String, String> DOOR_STATUS_TEXT = new HashMap<>() {{
            put("1", "open");
            put("2", "closed");
        }};
    }

    // 预约表枚举
    public static final class Reservation {
        // 被访人确认状态
        public static final Map<String, String> HOST_CONFIRM = new HashMap<>() {{
            put("pending", "1");
            put("confirmed", "2");
            put("rejected", "3");
        }};
        public static final Map<String, String> HOST_CONFIRM_TEXT = new HashMap<>() {{
            put("1", "pending");
            put("2", "confirmed");
            put("3", "rejected");
        }};

        // 预约状态
        public static final Map<String, String> STATUS = new HashMap<>() {{
            put("pending", "1");
            put("confirmed", "2");
            put("used", "3");
            put("canceled", "4");
        }};
        public static final Map<String, String> STATUS_TEXT = new HashMap<>() {{
            put("1", "pending");
            put("2", "confirmed");
            put("3", "used");
            put("4", "canceled");
        }};
    }

    // RFID卡表枚举
    public static final class RfidCard {
        // 卡片状态
        public static final Map<String, String> STATUS = new HashMap<>() {{
            put("available", "1");
            put("issued", "2");
            put("lost", "3");
            put("deactivated", "4");
            put("damage", "5");
        }};
        public static final Map<String, String> STATUS_TEXT = new HashMap<>() {{
            put("1", "available");
            put("2", "issued");
            put("3", "lost");
            put("4", "deactivated");
            put("5", "damage");
        }};
    }

    // RFID卡使用记录表枚举
    public static final class RfidCardRecord {
        // 操作类型
        public static final Map<String, String> OPERATION_TYPE = new HashMap<>() {{
            put("issue", "1");
            put("return", "2");
            put("report_lost", "3");
            put("deactivate", "4");
        }};
        public static final Map<String, String> OPERATION_TYPE_TEXT = new HashMap<>() {{
            put("1", "issue");
            put("2", "return");
            put("3", "report_lost");
            put("4", "deactivate");
        }};
    }

    // 进出记录表枚举
    public static final class AccessLog {
        // 进出类型
        public static final Map<String, String> ACCESS_TYPE = new HashMap<>() {{
            put("entry", "1");
            put("exit", "2");
        }};
        public static final Map<String, String> ACCESS_TYPE_TEXT = new HashMap<>() {{
            put("1", "entry");
            put("2", "exit");
        }};

        // 访问结果
        public static final Map<String, String> RESULT = new HashMap<>() {{
            put("allowed", "1");
            put("denied", "2");
        }};
        public static final Map<String, String> RESULT_TEXT = new HashMap<>() {{
            put("1", "allowed");
            put("2", "denied");
        }};
    }

    // 操作日志表枚举
    public static final class OperationLog {
        // 操作类型
        public static final Map<String, String> OPERATION_TYPE = new HashMap<>() {{
            put("issue", "1");
            put("return", "2");
            put("report_lost", "3");
            put("deactivate", "4");
        }};
        public static final Map<String, String> OPERATION_TYPE_TEXT = new HashMap<>() {{
            put("1", "issue");
            put("2", "return");
            put("3", "report_lost");
            put("4", "deactivate");
        }};
    }
} 