package com.localpulse.app.domain.model

data class CourseLesson(
    val title: String,
    val description: String,
    val emoji: String,
    val bulletPoints: List<String> = emptyList()
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)
