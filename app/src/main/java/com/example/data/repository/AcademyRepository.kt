package com.example.data.repository

import com.example.data.api.GeminiClient
import com.example.data.db.AcademyDao
import com.example.data.db.EnrollmentDao
import com.example.data.db.ReviewDao
import com.example.data.models.Academy
import com.example.data.models.Course
import com.example.data.models.Enrollment
import com.example.data.models.Review
import kotlinx.coroutines.flow.Flow

class AcademyRepository(
    private val academyDao: AcademyDao,
    private val enrollmentDao: EnrollmentDao,
    private val reviewDao: ReviewDao
) {
    val allAcademies: Flow<List<Academy>> = academyDao.getAllAcademies()
    val allEnrollments: Flow<List<Enrollment>> = enrollmentDao.getAllEnrollments()

    fun getAcademiesByCategory(category: String): Flow<List<Academy>> {
        return academyDao.getAcademiesByCategory(category)
    }

    suspend fun getAcademyById(id: Int): Academy? {
        return academyDao.getAcademyById(id)
    }

    fun getCoursesByAcademyId(academyId: Int): Flow<List<Course>> {
        return academyDao.getCoursesByAcademyId(academyId)
    }

    suspend fun getCourseById(id: Int): Course? {
        return academyDao.getCourseById(id)
    }

    suspend fun insertAcademy(academy: Academy): Long {
        return academyDao.insertAcademy(academy)
    }

    suspend fun insertCourse(course: Course): Long {
        return academyDao.insertCourse(course)
    }

    fun getReviewsForAcademy(academyId: Int): Flow<List<Review>> {
        return reviewDao.getReviewsForTarget(academyId, "academy")
    }

    suspend fun insertReview(review: Review): Long {
        return reviewDao.insertReview(review)
    }

    /**
     * Enrolls the user in a course and handles calling Gemini to generate a custom Weekly Study Path
     * grounded in the user's personal learning goals and the course syllabus!
     */
    suspend fun enrollInCourse(
        courseId: Int,
        academyId: Int,
        userEmail: String,
        userGoal: String
    ): Long {
        val course = academyDao.getCourseById(courseId) ?: return -1L
        
        // 1. Mark course as enrolled in Room database
        academyDao.setCourseEnrollmentStatus(courseId, true)

        // 2. Call Gemini to create a personalized study path
        val studyPath = if (userGoal.isNotBlank()) {
            generatePersonalizedStudyPath(course.title, course.syllabusOverview, userGoal)
        } else {
            "A standard study path will be provided by your instructor. Best of luck on your learning journey!"
        }

        // 3. Create and save the enrollment with the generated AI study path
        val enrollment = Enrollment(
            courseId = courseId,
            academyId = academyId,
            courseTitle = course.title,
            userEmail = userEmail,
            status = "Enrolled",
            studyPathSuggestion = studyPath
        )

        return enrollmentDao.insertEnrollment(enrollment)
    }

    suspend fun leaveCourse(enrollment: Enrollment) {
        academyDao.setCourseEnrollmentStatus(enrollment.courseId, false)
        enrollmentDao.deleteEnrollment(enrollment)
    }

    /**
     * Use Gemini to build a structured week-by-week study schedule.
     */
    private suspend fun generatePersonalizedStudyPath(
        courseTitle: String,
        syllabus: String,
        userGoal: String
    ): String {
        val prompt = """
            You are an expert tutor at our local learning academy. A new student wants a personalized weekly study strategy.
            
            Course Title: $courseTitle
            Standard Course Syllabus: $syllabus
            Student's Personal Goal: "$userGoal"

            Please design a custom, highly motivating study schedule. 
            Map their personal goal directly to the syllabus topics, breaking down:
            - Week 1 & 2 Focus (Fundamental alignment)
            - Week 3 & 4 Focus (Practical application toward their goal)
            - Week 5 & 6 Focus (Project-building steps)
            - Pro-tips for accelerated learning.

            Keep it exciting, structured, and easy to read. Use bullet points and headers.
        """.trimIndent()

        val systemInstruction = """
            You are a supportive, insightful educational mentor for 'LocalPulse Academy'. 
            Your goal is to make learning actionable, enjoyable, and closely tied to students' personal goals.
        """.trimIndent()

        return GeminiClient.generateResponse(prompt, systemInstruction)
    }

    /**
     * General local study course suggestions via Gemini.
     */
    suspend fun getAiLearningRecommendations(userGoal: String, actualAcademies: List<Academy>, actualCourses: List<Course>): String {
        val academiesContext = actualAcademies.joinToString("\n") { academy ->
            val courses = actualCourses.filter { it.academyId == academy.id }
            val coursesStr = courses.joinToString(", ") { it.title }
            "- ${academy.name} (${academy.category}). Courses: $coursesStr. Desc: ${academy.description}"
        }

        val prompt = """
            A resident in our local city wants to learn something new. Here's what they are interested in or want to achieve:
            "$userGoal"

            Here are the current learning courses and academies available in LocalPulse:
            $academiesContext

            Please recommend the absolute best course and academy match from the options above. 
            Explain why this specific program aligns with their aspirations and what they will gain from enrolling. 
            Keep it inspiring, supportive, and conversational (about 2 paragraphs).
        """.trimIndent()

        val systemInstruction = """
            You are 'LocalPulse Academy Advisor', an inspiring educational guidance counselor. 
            You excel at recommending matching study paths and building confidence in learners.
        """.trimIndent()

        return GeminiClient.generateResponse(prompt, systemInstruction)
    }
}
