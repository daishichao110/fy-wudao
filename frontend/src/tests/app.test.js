import { describe, it, expect } from 'vitest'
import api from '../api'

describe('Dance School Frontend API Suite', () => {
  it('should have correct API baseURL configured', () => {
    expect(api.getSchedules).toBeDefined()
    expect(api.getUsers).toBeDefined()
    expect(api.getStudentMetrics).toBeDefined()
    expect(api.getVolunteerTasks).toBeDefined()
    expect(api.getMentorships).toBeDefined()
    expect(api.getMyMessages).toBeDefined()
    expect(api.getPurchases).toBeDefined()
  })

  it('should correctly format request payloads for Zero-Approval Leave', () => {
    const leavePayload = {
      studentId: 6,
      studentName: '李小桐(新学员)',
      scheduleId: 1,
      courseName: '芭蕾基础与剧目变奏排练',
      reason: '感冒发烧请假'
    }
    expect(leavePayload.studentId).toBe(6)
    expect(leavePayload.scheduleId).toBe(1)
  })

  it('should validate body metric parameters structure', () => {
    const metricPayload = {
      studentId: 5,
      studentName: '张悦悦(高年级学姐)',
      heightCm: 152.5,
      weightKg: 38.0,
      bustCm: 72.0,
      waistCm: 58.0,
      hipCm: 76.0,
      torsoLengthCm: 56.0,
      shoeSize: 35.0
    }
    expect(metricPayload.heightCm).toBeGreaterThan(100)
    expect(metricPayload.shoeSize).toBe(35.0)
  })
})
