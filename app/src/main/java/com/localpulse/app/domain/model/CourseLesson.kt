package com.localpulse.app.domain.model

data class CourseLesson(
    val title: String,
    val description: String,
    val emoji: String,
    val bulletPoints: List<String> = emptyList()
)
