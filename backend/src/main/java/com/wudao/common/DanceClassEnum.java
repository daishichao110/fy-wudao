package com.wudao.common;

import java.util.Calendar;

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

    /**
     * 根据当前学年与入学年份，动态计算年级显示名称（方案一：入学年份届别动态升学）
     */
    public static String getGradeNameByEnrollmentYear(Integer enrollmentYear) {
        if (enrollmentYear == null || enrollmentYear <= 0) return "全校/公共";
        
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int academicYear = (month >= 9) ? currentYear : (currentYear - 1);
        
        int gradeNum = academicYear - enrollmentYear + 1;
        if (gradeNum <= 0) return "预备班";
        if (gradeNum == 1) return "一年级";
        if (gradeNum == 2) return "二年级";
        if (gradeNum == 3) return "三年级";
        if (gradeNum == 4) return "四年级";
        if (gradeNum == 5) return "五年级";
        if (gradeNum == 6) return "六年级";
        return "毕业校友 (" + enrollmentYear + "届)";
    }

    /**
     * 根据当前学年与年级名称/代号，动态推算入学年份
     */
    public static int getEnrollmentYearByGrade(String gradeStr) {
        Calendar cal = Calendar.getInstance();
        int currentYear = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int academicYear = (month >= 9) ? currentYear : (currentYear - 1);

        if (gradeStr == null || gradeStr.trim().isEmpty()) return academicYear - 1; // 默认二年级入学年份

        if (gradeStr.contains("1") || gradeStr.contains("一年级")) return academicYear;
        if (gradeStr.contains("2") || gradeStr.contains("二年级")) return academicYear - 1;
        if (gradeStr.contains("3") || gradeStr.contains("三年级")) return academicYear - 2;
        if (gradeStr.contains("4") || gradeStr.contains("四年级")) return academicYear - 3;
        if (gradeStr.contains("5") || gradeStr.contains("五年级")) return academicYear - 4;
        if (gradeStr.contains("6") || gradeStr.contains("六年级")) return academicYear - 5;

        return academicYear - 1;
    }

    public static String getNameByCode(String code) {
        if (code == null || code.trim().isEmpty()) return "全校/公共";
        
        // 兼容数字或字符串如 "2024" 年份输入
        if (code.matches("^20\\d{2}$")) {
            return getGradeNameByEnrollmentYear(Integer.parseInt(code));
        }

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
        
        if (name.matches("^20\\d{2}$")) {
            int year = Integer.parseInt(name);
            Calendar cal = Calendar.getInstance();
            int currentYear = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int academicYear = (month >= 9) ? currentYear : (currentYear - 1);
            int gradeNum = academicYear - year + 1;
            if (gradeNum >= 1 && gradeNum <= 6) {
                return "GRADE_" + gradeNum;
            }
        }

        for (DanceClassEnum e : values()) {
            if (e.getName().equals(name) || e.getCode().equalsIgnoreCase(name)) {
                return e.getCode();
            }
        }
        return "GRADE_ALL";
    }
}
