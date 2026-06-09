package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Business::class, Academy::class, Course::class, Review::class, Enrollment::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun businessDao(): BusinessDao
    abstract fun academyDao(): AcademyDao
    abstract fun reviewDao(): ReviewDao
    abstract fun enrollmentDao(): EnrollmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "localpulse_database"
                )
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database)
                }
            }
        }

        suspend fun populateDatabase(db: AppDatabase) {
            val bizDao = db.businessDao()
            val academyDao = db.academyDao()
            val reviewDao = db.reviewDao()

            // Pre-populate Businesses
            val b1 = bizDao.insertBusiness(
                Business(
                    name = "The Local Grind",
                    category = "Restaurant",
                    description = "Charming specialty coffee shop serving single-origin espresso, artisan baked goods, and featuring cozy corner spaces perfect for working or studying.",
                    rating = 4.8f,
                    address = "102 Spruce St, Downtown",
                    contact = "555-0192",
                    recommendedCount = 42
                )
            )

            val b2 = bizDao.insertBusiness(
                Business(
                    name = "Page Turner Books",
                    category = "Retail",
                    description = "Multilevel independent bookstore with an extensively curated selection of literature, comfortable armchairs, and regular author events.",
                    rating = 4.7f,
                    address = "450 Main St, Old Town",
                    contact = "555-0245",
                    recommendedCount = 31
                )
            )

            val b3 = bizDao.insertBusiness(
                Business(
                    name = "Zenith Yoga & Pilates",
                    category = "Health",
                    description = "A serene wellness center welcoming all experience levels. Offering hot yoga, reformer Pilates, and sound bath meditation sessions.",
                    rating = 4.9f,
                    address = "33 Pine Blvd, West End",
                    contact = "555-0311",
                    recommendedCount = 59
                )
            )

            val b4 = bizDao.insertBusiness(
                Business(
                    name = "Downtown Hardware & Repair",
                    category = "Services",
                    description = "Family-owned hardware store serving the neighborhood for over 30 years. Offers knife sharpening, lock rekeying, and home renovation advice.",
                    rating = 4.5f,
                    address = "78 Broad St, Downtown",
                    contact = "555-0762",
                    recommendedCount = 18
                )
            )

            val b5 = bizDao.insertBusiness(
                Business(
                    name = "Apex Fitness Gym",
                    category = "Leisure",
                    description = "24/7 fitness club featuring cardio machines, heavy-lifting racks, and active bootcamp classes led by personal trainers.",
                    rating = 4.6f,
                    address = "12 Oak Ave, Midtown",
                    contact = "555-0988",
                    recommendedCount = 28
                )
            )

            // Pre-populate Academies
            val a1 = academyDao.insertAcademy(
                Academy(
                    name = "Code Craft Academy",
                    category = "Technology",
                    description = "Pioneering technology academy focusing on visual software development, mobile apps, and machine learning. Small group cohorts guided by industry veteran educators.",
                    coursesCount = 3,
                    address = "200 Cyber Way, Innovation District",
                    contact = "555-8822",
                    certificateInfo = "Accredited Software Development Certificate"
                )
            ).toInt()

            val a2 = academyDao.insertAcademy(
                Academy(
                    name = "The Palette Studio",
                    category = "Arts",
                    description = "Charming studio space specializing in watercolor painting, clay pottery, and digital character design for aspiring hobbyists and professionals.",
                    coursesCount = 2,
                    address = "85 Artisan Alley, Arts Quarter",
                    contact = "555-1443",
                    certificateInfo = "Fine Arts Completion Certificate"
                )
            ).toInt()

            val a3 = academyDao.insertAcademy(
                Academy(
                    name = "Global Language Institute",
                    category = "Language",
                    description = "Immersive language center hosting conversation circles, business language prep, and certified cultural workshops for travelers.",
                    coursesCount = 2,
                    address = "500 Plaza Blvd, International District",
                    contact = "555-9001",
                    certificateInfo = "CEFR-Aligned Fluency Certificate"
                )
            ).toInt()

            // Pre-populate Courses
            // Code Craft Academy Courses
            academyDao.insertCourse(
                Course(
                    academyId = a1,
                    title = "Modern Kotlin & Jetpack Compose",
                    level = "Intermediate",
                    duration = "8 weeks",
                    syllabusOverview = "Learn to build high-performance native Android apps using modern declarative UI, state management patterns, Room Database, and beautiful animations."
                )
            )
            academyDao.insertCourse(
                Course(
                    academyId = a1,
                    title = "Building with AI: LLM Integration",
                    level = "Advanced",
                    duration = "4 weeks",
                    syllabusOverview = "Integrate large language models like Gemini into client-side Android and web apps. Create smart agents, customized chatbots, and structured outputs."
                )
            )
            academyDao.insertCourse(
                Course(
                    academyId = a1,
                    title = "Introduction to Computer Science",
                    level = "Beginner",
                    duration = "10 weeks",
                    syllabusOverview = "Master variables, loops, object-oriented concepts, basic sorting algorithms, and logical problem solving."
                )
            )

            // The Palette Studio Courses
            academyDao.insertCourse(
                Course(
                    academyId = a2,
                    title = "Watercolor Painting Fundamentals",
                    level = "Beginner",
                    duration = "6 weeks",
                    syllabusOverview = "Exploring wash techniques, color theory, landscape compositions, and paper texture interaction."
                )
            )
            academyDao.insertCourse(
                Course(
                    academyId = a2,
                    title = "Advanced Ceramic & Pottery Throwing",
                    level = "Advanced",
                    duration = "8 weeks",
                    syllabusOverview = "Master the potter's wheel, advanced glazing methods, firing profiles, and hollow-ware assembly."
                )
            )

            // Global Language Institute Courses
            academyDao.insertCourse(
                Course(
                    academyId = a3,
                    title = "Immersive Conversational Spanish",
                    level = "Beginner",
                    duration = "12 weeks",
                    syllabusOverview = "Acquire rapid, everyday conversational skills, vocabulary for travelers, and grammar basics taught entirely interactively."
                )
            )
            academyDao.insertCourse(
                Course(
                    academyId = a3,
                    title = "Business English Communication",
                    level = "Advanced",
                    duration = "4 weeks",
                    syllabusOverview = "Master corporate pitch decks, negotiation dialog, email copywriting, and interviewing concepts."
                )
            )

            // Sample Reviews
            reviewDao.insertReview(
                Review(
                    targetId = b1.toInt(),
                    targetType = "business",
                    userEmail = "sam@example.com",
                    reviewText = "Absolutely incredible cold brew! The baristas are super friendly, and the high-speed wifi makes it my favorite study spot in the city.",
                    rating = 5.0f
                )
            )
            reviewDao.insertReview(
                Review(
                    targetId = b1.toInt(),
                    targetType = "business",
                    userEmail = "maria@example.com",
                    reviewText = "Lovely sourdough pastries, but it can get pretty crowded on Saturday mornings.",
                    rating = 4.0f
                )
            )
            reviewDao.insertReview(
                Review(
                    targetId = a1,
                    targetType = "academy",
                    userEmail = "dev_alex@example.com",
                    reviewText = "I took the Kotlin & Compose bootcamp here and landed an Android dev internship a month later! Extremely hands-on projects.",
                    rating = 5.0f
                )
            )
        }
    }
}
