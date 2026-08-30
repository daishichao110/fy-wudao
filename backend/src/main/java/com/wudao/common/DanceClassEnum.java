package com.wudao.common;

public enum DanceClassEnum {
    GRADE_ALL("GRADE_ALL", "全校/公共"),
    GRADE_1("GRADE_1", "一年级"),
    GRADE_2("GRADE_2", "二年级"),
    GRADE_3("GRADE_3", "三年级"),
    GRADE_4("GRADE_4", "四年级"),
    GRADE_5("GRADE_5", "五年级"),
    GRADE_6("GRADE_6", "六年级");

    private final String code;
    private final String name;

    DanceClassEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(String code) {
        if (code == null || code.trim().isEmpty()) return "全校/公共";
        for (DanceClassEnum e : values()) {
            if (e.getCode().equalsIgnoreCase(code) || e.name().equalsIgnoreCase(code)) {
                return e.getName();
            }
        }
        if ("全校公共".equals(code) || "全校全部".equals(code) || "全校全局管理".equals(code)) return "全校/公共";
        return code;
    }

    public static String getCodeByName(String name) {
        if (name == null || name.trim().isEmpty()) return "GRADE_ALL";
        if ("全校/公共".equals(name) || "全校公共".equals(name) || "全校全部".equals(name) || "全校全局管理".equals(name)) return "GRADE_ALL";
        for (DanceClassEnum e : values()) {
            if (e.getName().equals(name) || e.getCode().equalsIgnoreCase(name)) {
                return e.getCode();
            }
        }
        return "GRADE_ALL";
    }
}
