package com.example.data.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository(private val context: Context) {

    private val firebaseAuth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseAuth.getInstance()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun isFirebaseConfigured(): Boolean {
        return firebaseAuth != null
    }

    fun getCurrentUser(): FirebaseUser? {
        return try {
            firebaseAuth?.currentUser
        } catch (e: Exception) {
            null
        }
    }

    suspend fun login(email: String, password: String): Result<FirebaseUser?> {
        val auth = firebaseAuth ?: return Result.failure(
            IllegalStateException("Firebase is not configured yet. Please follow Firebase setup instructions.")
        )
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<FirebaseUser?> {
        val auth = firebaseAuth ?: return Result.failure(
            IllegalStateException("Firebase is not configured yet. Please follow Firebase setup instructions.")
        )
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                val profileUpdates = userProfileChangeRequest {
                    displayName = name
                }
                user.updateProfile(profileUpdates).await()
            }
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendPasswordReset(email: String): Result<Unit> {
        val auth = firebaseAuth ?: return Result.failure(
            IllegalStateException("Firebase is not configured yet. Please follow Firebase setup instructions.")
        )
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogleCredential(idToken: String): Result<FirebaseUser?> {
        val auth = firebaseAuth ?: return Result.failure(
            IllegalStateException("Firebase is not configured yet. Please follow Firebase setup instructions.")
        )
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        try {
            firebaseAuth?.signOut()
        } catch (e: Exception) {
            // Ignored if not initialized
        }
    }
}
