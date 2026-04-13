package ru.naburnm8.queueserver

import jakarta.transaction.Transactional
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.annotation.Commit
import ru.naburnm8.queueserver.discipline.entity.Discipline
import ru.naburnm8.queueserver.discipline.entity.WorkType
import ru.naburnm8.queueserver.discipline.repository.DisciplineRepository
import ru.naburnm8.queueserver.discipline.repository.WorkTypeRepository
import ru.naburnm8.queueserver.profile.entity.Student
import ru.naburnm8.queueserver.profile.entity.Teacher
import ru.naburnm8.queueserver.profile.repository.StudentRepository
import ru.naburnm8.queueserver.profile.repository.TeacherRepository
import ru.naburnm8.queueserver.queue.entity.QueueRuntimeState
import ru.naburnm8.queueserver.queue.repository.QueueRuntimeStateRepository
import ru.naburnm8.queueserver.queue.service.QueueInteractionService
import ru.naburnm8.queueserver.queue.service.QueueRuntimeService
import ru.naburnm8.queueserver.queuePlan.entity.QueuePlan
import ru.naburnm8.queueserver.queuePlan.entity.QueueStatus
import ru.naburnm8.queueserver.queuePlan.repository.QueuePlanRepository
import ru.naburnm8.queueserver.queuePlan.service.QueuePlanService
import ru.naburnm8.queueserver.queuePlan.transporter.TransporterMapper
import ru.naburnm8.queueserver.queueRule.repository.QueueRuleRepository
import ru.naburnm8.queueserver.security.RoleName
import ru.naburnm8.queueserver.security.entity.Role
import ru.naburnm8.queueserver.security.entity.User
import ru.naburnm8.queueserver.security.repository.RoleRepository
import ru.naburnm8.queueserver.security.repository.UserRepository
import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionRequest
import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionRequestItem
import ru.naburnm8.queueserver.submissionRequest.entity.SubmissionStatus
import ru.naburnm8.queueserver.submissionRequest.repository.SubmissionRequestRepository
import ru.naburnm8.queueserver.submissionRequest.service.SubmissionRequestService

// configure environment variables for test db connection

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class QueueFlowTest {

    @Value("\${spring.datasource.url}")
    lateinit var dbUrl: String

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var teacherRepository: TeacherRepository

    @Autowired
    lateinit var studentRepository: StudentRepository

    @Autowired
    lateinit var disciplineRepository: DisciplineRepository

    @Autowired
    lateinit var queuePlanRepository: QueuePlanRepository

    @Autowired
    lateinit var queueRuntimeStateRepository: QueueRuntimeStateRepository

    @Autowired
    lateinit var submissionRequestRepository: SubmissionRequestRepository

    @Autowired
    lateinit var workTypeRepository: WorkTypeRepository

    @Autowired
    lateinit var queueRuleRepository: QueueRuleRepository

    @Autowired
    lateinit var queueInteractionService: QueueInteractionService

    @Autowired
    lateinit var queueRuntimeService: QueueRuntimeService

    @Autowired
    lateinit var queuePlanService: QueuePlanService

    @Autowired
    lateinit var submissionRequestService: SubmissionRequestService

    private lateinit var teacherUser: User
    private lateinit var studentUser: User
    private lateinit var teacher: Teacher
    private lateinit var student: Student
    private lateinit var discipline: Discipline
    private lateinit var queuePlan: QueuePlan
    private lateinit var runtimeState: QueueRuntimeState
    private lateinit var workType: WorkType
    private lateinit var request: SubmissionRequest

    @BeforeAll
    fun setup() {
        if (!dbUrl.contains("testdb")) throw IllegalStateException("Please use TEST DB URL")
        val teacherRole = roleRepository.findByName(RoleName.ROLE_QOPERATOR) ?: roleRepository.save(
            Role(
                name = RoleName.ROLE_QOPERATOR,
            )
        )

        val studentRole = roleRepository.findByName(RoleName.ROLE_QCONSUMER) ?: roleRepository.save(
            Role(
                name = RoleName.ROLE_QCONSUMER,
            )
        )

        teacherUser = userRepository.save(
            User(
                email = "teacher@test.com",
                passwordHash = passwordEncoder.encode("1234567") ?: "",
                isEnabled = true,
                roles = mutableSetOf(teacherRole)
            )
        )

        studentUser = userRepository.save(
            User(
                email = "student@test.com",
                passwordHash = passwordEncoder.encode("1234567") ?: "",
                isEnabled = true,
                roles = mutableSetOf(studentRole)
            )
        )

        teacher = teacherRepository.save(
            Teacher(
                user = teacherUser,
                firstName = "Ivan",
                lastName = "Ivanov",
                patronymic = "",
                department = "IU",
                telegram = "teacher",
                avatarUrl = null
            )
        )

        student = studentRepository.save(
            Student(
                user = studentUser,
                firstName = "Petr",
                lastName = "Petrov",
                patronymic = "",
                academicGroup = "IU3-72B",
                telegram = "student",
                avatarUrl = null
            )
        )

        discipline = disciplineRepository.save(Discipline(
            name = "Algorithms",
            owners = mutableSetOf(teacher)
        )
        )

        workType = workTypeRepository.save(WorkType(
            discipline = discipline,
            name = "Lab 1",
            estimatedTimeMinutes = 15,
        ))

        queuePlan = QueuePlan(
                discipline = discipline,
                createdBy = teacher,
                title = "Test queue",
                status = QueueStatus.ACTIVE,
                useDebts = true,
                wDebts = 1.0,
                useTime = true,
                wTime = 1.0,
                useAchievements = true,
                wAchievements = 1.0,
            )
        request = SubmissionRequest(
            queuePlan = queuePlan,
            student = student,
            status = SubmissionStatus.ENQUEUED,
        )

        val item = SubmissionRequestItem(
            request = request,
            workType = workType,
            quantity = 1,
            minutesOverride = null,
        )

        request.addItem(item)
        //request = submissionRequestRepository.save(request)
    }

    @Transactional
    @Test
    @Commit
    @Order(1)
    fun `queue plan is created successfully`() {
        var transporter = TransporterMapper.toTransporter(queuePlan)
        transporter = transporter.copy(id = null)

        val created = queuePlanService.createPlan(transporter)
        queuePlanService.changeStatus(teacherUser.id, created.id!!, QueueStatus.ACTIVE)

        val fromDb = queuePlanService.getPlansByTeacher(teacherUser.id)[0]
        assertEquals("Test queue", fromDb.title)
        assertEquals(QueueStatus.ACTIVE, fromDb.status)
        assertEquals(discipline.id, fromDb.disciplineId)
        assertEquals(teacherUser.id, fromDb.createdByTeacherId)
    }

    @Transactional
    @Test
    @Commit
    @Order(2)
    fun `submission request is successfully added to queue plan`() {
        val outTransporter = ru.naburnm8.queueserver.submissionRequest.transporter.TransporterMapper.toTransporter(request)
        var transporter = ru.naburnm8.queueserver.submissionRequest.transporter.TransporterMapper.outToIn(outTransporter)
        transporter = transporter.copy(id = null)

        val queuePlans = queuePlanService.getAllPlans()
        assertEquals(1, queuePlans.size)

        val created = submissionRequestService.createForStudent(queuePlans[0].id, studentUser.id, transporter)

        assertEquals(queuePlans[0].id, created.queuePlanId)
        assertEquals(studentUser.id, created.studentId)
        assertEquals(SubmissionStatus.PENDING, created.status)
    }

    @Transactional
    @Test
    @Commit
    @Order(3)
    fun `takeNext sets current request in runtime state`() {
        val myPlan = queuePlanService.getPlansByTeacher(teacherUser.id)[0]
        val requests = submissionRequestService.getAllRequests(
            queuePlanId = myPlan.id ?: throw IllegalStateException("No queue plan id"),
            requesterId = teacherUser.id,
            status = SubmissionStatus.PENDING
        )


        submissionRequestService.changeStatus(
            queuePlanId = myPlan.id,
            teacherId = teacherUser.id,
            submissionRequestId = requests[0].id,
            newStatus = SubmissionStatus.ENQUEUED
        )


        queueInteractionService.takeNext(myPlan.id)

        runtimeState = queueRuntimeStateRepository.findById(myPlan.id).get()
        assertNotNull(runtimeState.currentRequest)
        assertEquals(requests[0].id, runtimeState.currentRequest!!.id)
        assertEquals(SubmissionStatus.DEQUEUED, runtimeState.currentRequest!!.status)
    }

    @AfterAll
    fun teardown() {
        queueRuleRepository.deleteAll()
        queueRuntimeStateRepository.deleteAll()
        submissionRequestRepository.deleteAll()
        queuePlanRepository.deleteAll()
        workTypeRepository.deleteAll()
        disciplineRepository.deleteAll()
        studentRepository.deleteAll()
        teacherRepository.deleteAll()
        userRepository.deleteAll()
    }
}