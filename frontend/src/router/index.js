import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import ScheduleView from '../views/ScheduleView.vue'
import MetricView from '../views/MetricView.vue'
import VolunteerView from '../views/VolunteerView.vue'
import MentorshipView from '../views/MentorshipView.vue'
import ProfileView from '../views/ProfileView.vue'
import QaView from '../views/QaView.vue'
import PurchaseView from '../views/PurchaseView.vue'
import LoginView from '../views/LoginView.vue'

const routes = [
  { path: '/login', name: 'Login', component: LoginView },
  { path: '/', name: 'Home', component: HomeView },
  { path: '/schedule', name: 'Schedule', component: ScheduleView },
  { path: '/metrics', name: 'Metrics', component: MetricView },
  { path: '/volunteers', name: 'Volunteers', component: VolunteerView },
  { path: '/mentorship', name: 'Mentorship', component: MentorshipView },
  { path: '/profile', name: 'Profile', component: ProfileView },
  { path: '/qa', name: 'Qa', component: QaView },
  { path: '/purchases', name: 'Purchases', component: PurchaseView }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
