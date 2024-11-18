package com.android.modules

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.modules.databinding.ActivityEmailAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class EmailAuthActivity : AppCompatActivity() {

    lateinit var binding : ActivityEmailAuthBinding
    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Login authentication with email, password, name and profile picture
        // Email and password validations are not taken into consideration
        auth = FirebaseAuth.getInstance()
        checkStatus()
        binding.register.setOnClickListener {
            registerUser(binding.emailText.text.toString(),binding.passwordText.text.toString())
        }
        binding.login.setOnClickListener {
            loginUser(binding.emailText.text.toString(),binding.passwordText.text.toString())
        }
        binding.logout.setOnClickListener {
            auth.signOut()
            checkStatus()
        }

    }

    fun registerUser(email:String,password:String) {
        CoroutineScope(Dispatchers.IO).launch{
            try
            {
                auth.createUserWithEmailAndPassword(email,password).await()
                val userProfile=UserProfileChangeRequest.Builder()
                    .setDisplayName("Jay")
                    .setPhotoUri(Uri.parse("android.resource://$packageName/${R.drawable.profile}"))
                    .build()
                auth.currentUser?.updateProfile(userProfile)?.await()
                withContext(Dispatchers.Main){
                    checkStatus()
                }
            }
            catch (e:Exception)
            {
                withContext(Dispatchers.Main){
                    binding.status.text=e.message
                }
            }
        }
    }

    fun loginUser(email:String,password:String) {
        CoroutineScope(Dispatchers.IO).launch{
            try
            {
                auth.signInWithEmailAndPassword(email,password).await()
                withContext(Dispatchers.Main){
                    checkStatus()
                }
            }
            catch (e:Exception)
            {
                withContext(Dispatchers.Main){
                    binding.status.text=e.message
                }
            }
        }
    }

    fun checkStatus(){
        if(auth.currentUser != null)
        {
            binding.status.text = "User is logged in"
            binding.name.text = auth.currentUser!!.displayName
            binding.profile.setImageURI(auth.currentUser!!.photoUrl)
        }
        else
        {
            binding.status.text = "User is not logged in"
            binding.name.text=""
            binding.profile.setImageURI(null)
        }
    }
}