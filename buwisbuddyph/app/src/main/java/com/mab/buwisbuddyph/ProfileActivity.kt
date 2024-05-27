package com.mab.buwisbuddyph

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mab.buwisbuddyph.home.HomeActivity
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance()

        val currentUser = auth.currentUser

        val userProfileImg: CircleImageView = findViewById(R.id.userProfileImg)
        val nameTV: EditText = findViewById(R.id.nameTV)
        val numberTV: EditText = findViewById(R.id.numberTV)
        val genderTV: EditText = findViewById(R.id.genderTV)
        val tinTV: EditText = findViewById(R.id.tinTV)
        val emailTV: EditText = findViewById(R.id.emailTV)

        val returnIcon = findViewById<ImageView>(R.id.returnIcon)
        returnIcon.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        onBackPressedDispatcher.addCallback(this) {
            val intent = Intent(this@ProfileActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (currentUser != null) {
            db.collection("users").document(currentUser.uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val name = document.getString("userFullName")
                        val number = document.getString("userNumber")
                        val gender = document.getString("userGender")
                        val tin = document.getString("userTin")
                        val email = document.getString("userEmail")
                        val userProfileImgPath = document.getString("userProfileImg")

                        name?.let { nameTV.setText(it) }
                        number?.let { numberTV.setText(it) }
                        gender?.let { genderTV.setText(it) }
                        tin?.let { tinTV.setText(it) }
                        email?.let { emailTV.setText(it) }

                        if (userProfileImgPath != null) {
                            val storageRef = storage.getReference(userProfileImgPath)
                            storageRef.downloadUrl.addOnSuccessListener { uri ->
                                Picasso.get().load(uri).into(userProfileImg)
                            }.addOnFailureListener { exception ->
                                Log.d("image","Error:",exception)
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("profile","Error:",exception)
                }
        }
    }
}
