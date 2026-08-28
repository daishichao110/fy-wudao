package com.wudao.controller;

import com.wudao.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/export")
public class StudentExportController {

    @GetMapping("/students")
    public Result<String> exportStudents() {
        StringBuilder csv = new StringBuilder();
        csv.append("学员ID,学员姓名,所属班级,年纪/年级,语文成绩,数学成绩,英语成绩,身高(cm),体重(kg),家长姓名,家长电话\n");
        csv.append("6,李小桐,芭蕾高级班,小学三年级,95.5,98.0,94.0,138.5,31.2,李妈妈,13900000006\n");
        csv.append("5,张悦悦,芭蕾高级班,小学四年级,92.0,96.5,95.0,152.5,38.0,张爸爸,13800000005\n");
        csv.append("7,王小敏,芭蕾基础班,小学二年级,88.5,90.0,91.5,128.0,26.5,王妈妈,13700000007\n");
        return Result.success(csv.toString());
    }
}
