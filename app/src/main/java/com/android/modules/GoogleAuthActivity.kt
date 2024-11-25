package com.android.modules

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.modules.databinding.ActivityGoogleAuthBinding
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.google.firebase.auth.FirebaseAuth

class GoogleAuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoogleAuthBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityGoogleAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        updateUI()

        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build()
        binding.signIn.setOnClickListener {
            signInLauncher.launch(signInIntent)
        }
        binding.logout.setOnClickListener {
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    updateUI()
                }
        }
    }

    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
        if (result.resultCode == RESULT_OK) {
            updateUI()
        }
    }


    private fun updateUI() {
        val user=auth.currentUser
        if(user == null)
        {
            binding.details.text = "User is not logged in"
            Glide.with(this).clear(binding.img)
        }
        else
        {
            binding.details.text = "User is logged in\n${user.uid}\n${user.displayName}\n${user.email}"
            Glide.with(this).load(user.photoUrl).into(binding.img)
        }
    }
}