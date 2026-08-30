package com.wudao.controller;

import com.wudao.common.Result;
import com.wudao.entity.Mentorship;
import com.wudao.service.MentorshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mentorship")
public class MentorshipController {

    private static final Logger log = LoggerFactory.getLogger(MentorshipController.class);

    @Autowired
    private MentorshipService mentorshipService;

    @GetMapping("/list")
    public Result<List<Mentorship>> listMentorships() {
        log.info("[REST API GET /api/mentorship/list] Querying all mentorship pairs");
        List<Mentorship> list = mentorshipService.getAllMentorships();
        return Result.success(list);
    }

    @PostMapping("/checkin")
    public Result<Mentorship> checkinMentorship(@RequestBody Map<String, Object> params) {
        String pairId = params.get("pairId").toString();
        log.info("[REST API POST /api/mentorship/checkin] Checkin for pairId: {}", pairId);
        Mentorship res = mentorshipService.checkin(pairId);
        return Result.success("结对子打卡成功！姐妹星 +5", res);
    }

    @PostMapping("/create")
    public Result<Mentorship> createMentorship(@RequestBody Mentorship pair) {
        log.info("[REST API POST /api/mentorship/create] Creating mentorship pair");
        Mentorship res = mentorshipService.createMentorship(pair);
        return Result.success("结对子绑定成功！已存入教务档案", res);
    }
}
