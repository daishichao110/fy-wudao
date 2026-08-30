package com.wudao.service;

import com.wudao.entity.Mentorship;
import java.util.List;

public interface MentorshipService {
    List<Mentorship> getAllMentorships();
    Mentorship checkin(String pairId);
    Mentorship createMentorship(Mentorship pair);
}
