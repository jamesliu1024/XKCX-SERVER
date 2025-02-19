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
        public static final class IdType {
            public static final String SCHOOL_ID = "school_id";
            public static final String ID_CARD = "id_card";
            public static final String PASSPORT = "passport";
            public static final String OTHER = "other";
        }
        public static final Map<String, String> ID_TYPE = new HashMap<>() {{
            put(IdType.SCHOOL_ID, "1");
            put(IdType.ID_CARD, "2");
            put(IdType.PASSPORT, "3");
            put(IdType.OTHER, "4");
        }};
        public static final Map<String, String> ID_TYPE_TEXT = new HashMap<>() {{
            put("1", IdType.SCHOOL_ID);
            put("2", IdType.ID_CARD);
            put("3", IdType.PASSPORT);
            put("4", IdType.OTHER);
        }};
        
        // 角色
        public static final class Role {
            public static final String ADMIN = "admin";
            public static final String VISITOR = "visitor";
        }
        public static final Map<String, String> ROLE = new HashMap<>() {{
            put(Role.ADMIN, "1");
            put(Role.VISITOR, "2");
        }};
        public static final Map<String, String> ROLE_TEXT = new HashMap<>() {{
            put("1", Role.ADMIN);
            put("2", Role.VISITOR);
        }};

        // 账号状态
        public static final class AccountStatus {
            public static final String NORMAL = "normal";
            public static final String DISABLED = "disabled";
            public static final String BLACKLIST = "blacklist";
        }
        public static final Map<String, String> ACCOUNT_STATUS = new HashMap<>() {{
            put(AccountStatus.NORMAL, "1");
            put(AccountStatus.DISABLED, "2");
            put(AccountStatus.BLACKLIST, "3");
        }};
        public static final Map<String, String> ACCOUNT_STATUS_TEXT = new HashMap<>() {{
            put("1", AccountStatus.NORMAL);
            put("2", AccountStatus.DISABLED);
            put("3", AccountStatus.BLACKLIST);
        }};
    }

    // 门禁设备表枚举
    public static final class AccessDevice {
        // 设备状态
        public static final class Status {
            public static final String ONLINE = "online";
            public static final String OFFLINE = "offline";
            public static final String MAINTENANCE = "maintenance";
        }
        public static final Map<String, String> STATUS = new HashMap<>() {{
            put(Status.ONLINE, "1");
            put(Status.OFFLINE, "2");
            put(Status.MAINTENANCE, "3");
        }};
        public static final Map<String, String> STATUS_TEXT = new HashMap<>() {{
            put("1", Status.ONLINE);
            put("2", Status.OFFLINE);
            put("3", Status.MAINTENANCE);
        }};

        // 设备类型
        public static final class DeviceType {
            public static final String CAMPUS_GATE = "campus_gate";
            public static final String FACILITY_GATE = "facility_gate";
            public static final String MANAGEMENT = "management";
        }
        public static final Map<String, String> DEVICE_TYPE = new HashMap<>() {{
            put(DeviceType.CAMPUS_GATE, "1");
            put(DeviceType.FACILITY_GATE, "2");
            put(DeviceType.MANAGEMENT, "3");
        }};
        public static final Map<String, String> DEVICE_TYPE_TEXT = new HashMap<>() {{
            put("1", DeviceType.CAMPUS_GATE);
            put("2", DeviceType.FACILITY_GATE);
            put("3", DeviceType.MANAGEMENT);
        }};

        // 门禁状态
        public static final class DoorStatus {
            public static final String OPEN = "open";
            public static final String CLOSED = "closed";
        }
        public static final Map<String, String> DOOR_STATUS = new HashMap<>() {{
            put(DoorStatus.OPEN, "1");
            put(DoorStatus.CLOSED, "2");
        }};
        public static final Map<String, String> DOOR_STATUS_TEXT = new HashMap<>() {{
            put("1", DoorStatus.OPEN);
            put("2", DoorStatus.CLOSED);
        }};
    }

    // 预约表枚举
    public static final class Reservation {
        // 被访人确认状态
        public static final class HostConfirm {
            public static final String PENDING = "pending";
            public static final String CONFIRMED = "confirmed";
            public static final String REJECTED = "rejected";
        }
        public static final Map<String, String> HOST_CONFIRM = new HashMap<>() {{
            put(HostConfirm.PENDING, "1");
            put(HostConfirm.CONFIRMED, "2");
            put(HostConfirm.REJECTED, "3");
        }};
        public static final Map<String, String> HOST_CONFIRM_TEXT = new HashMap<>() {{
            put("1", HostConfirm.PENDING);
            put("2", HostConfirm.CONFIRMED);
            put("3", HostConfirm.REJECTED);
        }};

        // 预约状态
        public static final class Status {
            public static final String PENDING = "pending";
            public static final String CONFIRMED = "confirmed";
            public static final String USED = "used";
            public static final String CANCELED = "canceled";
        }
        public static final Map<String, String> STATUS = new HashMap<>() {{
            put(Status.PENDING, "1");
            put(Status.CONFIRMED, "2");
            put(Status.USED, "3");
            put(Status.CANCELED, "4");
        }};
        public static final Map<String, String> STATUS_TEXT = new HashMap<>() {{
            put("1", Status.PENDING);
            put("2", Status.CONFIRMED);
            put("3", Status.USED);
            put("4", Status.CANCELED);
        }};
    }

    // RFID卡表枚举
    public static final class RfidCard {
        // 卡片状态
        public static final class Status {
            public static final String AVAILABLE = "available";
            public static final String ISSUED = "issued";
            public static final String LOST = "lost";
            public static final String DEACTIVATED = "deactivated";
            public static final String DAMAGE = "damage";
        }
        public static final Map<String, String> STATUS = new HashMap<>() {{
            put(Status.AVAILABLE, "1");
            put(Status.ISSUED, "2");
            put(Status.LOST, "3");
            put(Status.DEACTIVATED, "4");
            put(Status.DAMAGE, "5");
        }};
        public static final Map<String, String> STATUS_TEXT = new HashMap<>() {{
            put("1", Status.AVAILABLE);
            put("2", Status.ISSUED);
            put("3", Status.LOST);
            put("4", Status.DEACTIVATED);
            put("5", Status.DAMAGE);
        }};
    }

    // RFID卡使用记录表枚举
    public static final class RfidCardRecord {
        // 操作类型
        public static final class OperationType {
            public static final String ISSUE = "issue";
            public static final String RETURN = "return";
            public static final String REPORT_LOST = "report_lost";
            public static final String DEACTIVATE = "deactivate";
        }
        public static final Map<String, String> OPERATION_TYPE = new HashMap<>() {{
            put(OperationType.ISSUE, "1");
            put(OperationType.RETURN, "2");
            put(OperationType.REPORT_LOST, "3");
            put(OperationType.DEACTIVATE, "4");
        }};
        public static final Map<String, String> OPERATION_TYPE_TEXT = new HashMap<>() {{
            put("1", OperationType.ISSUE);
            put("2", OperationType.RETURN);
            put("3", OperationType.REPORT_LOST);
            put("4", OperationType.DEACTIVATE);
        }};
    }

    // 进出记录表枚举
    public static final class AccessLog {
        // 进出类型
        public static final class AccessType {
            public static final String ENTRY = "entry";
            public static final String EXIT = "exit";
        }
        public static final Map<String, String> ACCESS_TYPE = new HashMap<>() {{
            put(AccessType.ENTRY, "1");
            put(AccessType.EXIT, "2");
        }};
        public static final Map<String, String> ACCESS_TYPE_TEXT = new HashMap<>() {{
            put("1", AccessType.ENTRY);
            put("2", AccessType.EXIT);
        }};

        // 访问结果
        public static final class Result {
            public static final String ALLOWED = "allowed";
            public static final String DENIED = "denied";
        }
        public static final Map<String, String> RESULT = new HashMap<>() {{
            put(Result.ALLOWED, "1");
            put(Result.DENIED, "2");
        }};
        public static final Map<String, String> RESULT_TEXT = new HashMap<>() {{
            put("1", Result.ALLOWED);
            put("2", Result.DENIED);
        }};
    }

    // 操作日志表枚举
    public static final class OperationLog {
        // 操作类型
        public static final class OperationType {
            public static final String ISSUE = "issue";
            public static final String RETURN = "return";
            public static final String REPORT_LOST = "report_lost";
            public static final String DEACTIVATE = "deactivate";
        }
        public static final Map<String, String> OPERATION_TYPE = new HashMap<>() {{
            put(OperationType.ISSUE, "1");
            put(OperationType.RETURN, "2");
            put(OperationType.REPORT_LOST, "3");
            put(OperationType.DEACTIVATE, "4");
        }};
        public static final Map<String, String> OPERATION_TYPE_TEXT = new HashMap<>() {{
            put("1", OperationType.ISSUE);
            put("2", OperationType.RETURN);
            put("3", OperationType.REPORT_LOST);
            put("4", OperationType.DEACTIVATE);
        }};
    }
} 