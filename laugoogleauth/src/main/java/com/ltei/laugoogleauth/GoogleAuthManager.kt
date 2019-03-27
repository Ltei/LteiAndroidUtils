package com.ltei.laugoogleauth

import android.app.Activity
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.ltei.ljubase.Result
import java8.util.concurrent.CompletableFuture


class GoogleAuthManager(
        private val activity: Activity,
        options: GoogleSignInOptions,
        initialRequestCode: Int
) {

    private val mAwaitingSignIns = mutableMapOf<Int, CompletableFuture<Result<GoogleSignInAccount, Unit>>>()
    private var mNextRequestCode = initialRequestCode

    private val mGoogleSignInClient = GoogleSignIn.getClient(activity, options)
    private var mGoogleSignAccount: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(activity)

    fun signIn(): CompletableFuture<Result<GoogleSignInAccount, Unit>> {
        var account = mGoogleSignAccount
        if (account != null) {
            return CompletableFuture.completedFuture(Result.ok(account))
        }

        account = GoogleSignIn.getLastSignedInAccount(activity)
        if (account != null) {
            mGoogleSignAccount = account
            return CompletableFuture.completedFuture(Result.ok(account))
        }

        val future = CompletableFuture<Result<GoogleSignInAccount, Unit>>()
        val signInIntent = mGoogleSignInClient.signInIntent

        activity.startActivityForResult(signInIntent, mNextRequestCode)
        mAwaitingSignIns[mNextRequestCode] = future
        mNextRequestCode += 1

        return future
    }

    fun signOut() {
        mGoogleSignAccount = null
    }

    fun isSignedIn(): Boolean {
        return mGoogleSignAccount != null
    }

    fun onActivityResult(requestCode: Int, data: Intent?): Boolean {
        val awaiting = mAwaitingSignIns[requestCode]

        return if (awaiting != null) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                awaiting.complete(Result.ok(account.result))
            } catch (e: ApiException) {
                awaiting.complete(Result.err(Unit))
            }
            true
        } else {
            false
        }
    }

}