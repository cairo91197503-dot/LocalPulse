package com.example.data.repository

import com.example.domain.model.Review
import com.example.domain.repository.ReviewRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ReviewRepository {

    private val reviewsCollection = firestore.collection("reviews")

    override suspend fun addReview(review: Review): Result<Unit> {
        return try {
            val documentReference = reviewsCollection.document()
            val reviewWithId = review.copy(id = documentReference.id)
            documentReference.set(reviewWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getReviews(): Flow<List<Review>> = callbackFlow {
        val listenerRegistration = reviewsCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val reviews = snapshot.toObjects(Review::class.java)
                    trySend(reviews)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun getReviewsFilteredByRating(minRating: Float): Flow<List<Review>> = callbackFlow {
        val listenerRegistration = reviewsCollection
            .whereGreaterThanOrEqualTo("rating", minRating)
            .orderBy("rating", Query.Direction.DESCENDING)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val reviews = snapshot.toObjects(Review::class.java)
                    trySend(reviews)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun getNewLowRatingReviews(): Flow<Review> = callbackFlow {
        val startTime = java.util.Date()
        val listenerRegistration = reviewsCollection
            .whereLessThanOrEqualTo("rating", 2f)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                snapshot?.documentChanges?.forEach { change ->
                    if (change.type == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                        val review = change.document.toObject(Review::class.java)
                        if (review.date.after(startTime)) {
                            trySend(review)
                        }
                    }
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun getReviewsFrom(date: java.util.Date): Flow<List<Review>> = callbackFlow {
        val listenerRegistration = reviewsCollection
            .whereGreaterThanOrEqualTo("date", date)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val reviews = snapshot.toObjects(Review::class.java)
                    trySend(reviews)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }
}
