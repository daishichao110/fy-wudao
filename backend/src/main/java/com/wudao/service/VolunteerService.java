package com.wudao.service;

import com.wudao.entity.VolunteerTask;
import com.wudao.entity.VolunteerEnrollment;
import java.util.List;

public interface VolunteerService {
    List<VolunteerTask> getAllTasks(String danceClassName);
    VolunteerTask createTask(VolunteerTask task);
    VolunteerEnrollment enrollTask(VolunteerEnrollment enrollment);
    VolunteerEnrollment assignTask(VolunteerEnrollment enrollment);
}
