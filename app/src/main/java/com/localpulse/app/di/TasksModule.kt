package com.localpulse.app.di

import com.google.firebase.firestore.FirebaseFirestore
import com.localpulse.app.data.tasks.TasksRepository
import com.localpulse.app.data.tasks.TasksRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TasksModule {

    @Provides
    @Singleton
    fun provideTasksRepository(
        firestore: FirebaseFirestore
    ): TasksRepository {
        return TasksRepositoryImpl(firestore)
    }
}
