package com.wudao.service.impl;

import com.wudao.entity.Mentorship;
import com.wudao.mapper.MentorshipMapper;
import com.wudao.service.MentorshipService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class MentorshipServiceImpl implements MentorshipService {

    private static final Logger log = LoggerFactory.getLogger(MentorshipServiceImpl.class);

    @Autowired
    private MentorshipMapper mentorshipMapper;

    @Override
    public List<Mentorship> getAllMentorships() {
        log.info("[MentorshipService] Executing getAllMentorships()...");
        List<Mentorship> list = mentorshipMapper.selectAll();
        log.info("[MentorshipService] Fetched {} mentorship pairs", list != null ? list.size() : 0);
        return list;
    }

    @Override
    @Transactional
    public Mentorship checkin(Long pairId) {
        log.info("[MentorshipService] Executing checkin() for pairId={}", pairId);

        if (pairId == null || pairId <= 0) {
            log.error("[MentorshipService] Invalid pairId: {}", pairId);
            throw new IllegalArgumentException("结对子记录ID不合法");
        }

        Mentorship pair = mentorshipMapper.selectById(pairId);
        if (pair == null) {
            log.error("[MentorshipService] Checkin failed: Mentorship pair ID {} not found", pairId);
            throw new IllegalArgumentException("指定的结对子互助记录不存在(ID: " + pairId + ")");
        }

        mentorshipMapper.incrementStarAndCheckin(pairId, 5);
        Mentorship updated = mentorshipMapper.selectById(pairId);

        log.info("[MentorshipService] Checkin success! Updated pair {}-{}, new star points: {}, total checkins: {}",
                updated.getSeniorStudentName(), updated.getJuniorStudentName(), updated.getStarPoints(), updated.getCheckinCount());
        return updated;
    }

    @Override
    @Transactional
    public Mentorship createMentorship(Mentorship pair) {
        log.info("[MentorshipService] Executing createMentorship()...");
        if (pair == null) throw new IllegalArgumentException("结对子参数不可为空");
        if (!StringUtils.hasText(pair.getSeniorStudentName())) pair.setSeniorStudentName("张悦悦(高年级学姐)");
        if (!StringUtils.hasText(pair.getJuniorStudentName())) pair.setJuniorStudentName("李小桐(新学员)");
        if (pair.getSeniorStudentId() == null) pair.setSeniorStudentId(5L);
        if (pair.getJuniorStudentId() == null) pair.setJuniorStudentId(6L);
        if (!StringUtils.hasText(pair.getTermName())) pair.setTermName("芭蕾与中国舞联训班");
        if (pair.getStarPoints() == null) pair.setStarPoints(50);
        if (pair.getCheckinCount() == null) pair.setCheckinCount(0);

        if (pair.getPairId() == null || pair.getPairId() <= 0) {
            pair.setPairId(com.wudao.common.SnowflakeIdWorker.generateId());
        }

        mentorshipMapper.insert(pair);
        log.info("[MentorshipService] Mentorship created successfully, assigned pairId: {}", pair.getPairId());
        return pair;
    }
}
