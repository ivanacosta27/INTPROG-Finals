package com.mab.buwisbuddyph.home

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_JPEG
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.RESULT_FORMAT_PDF
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions.SCANNER_MODE_FULL
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import com.mab.buwisbuddyph.ProfileActivity
import com.mab.buwisbuddyph.R
import com.mab.buwisbuddyph.SignInActivity
import com.mab.buwisbuddyph.TaxCalculatorActivity
import com.mab.buwisbuddyph.accountant.AccountantHelpActivity
import com.mab.buwisbuddyph.forum.ForumFragment
import com.mab.buwisbuddyph.messages.MessagesFragment
import java.io.InputStream
import java.io.OutputStream

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        auth = FirebaseAuth.getInstance()

        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView: NavigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        updateProfileInfo()

        toHome()

        val forumIcon = findViewById<ImageView>(R.id.forumIcon)
        forumIcon.setOnClickListener {
            Log.d("forum", "forum icon clicked")
            toForum()
        }

        val homeIcon = findViewById<ImageView>(R.id.homeIcon)
        homeIcon.setOnClickListener {
            toHome()
        }

        val userProfileImage = findViewById<ImageView>(R.id.userProfileImage)
        userProfileImage.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        val messageIcon = findViewById<ImageView>(R.id.messagesIcon)
        messageIcon.setOnClickListener {
            toMessages()
        }

        val options = GmsDocumentScannerOptions.Builder()
            .setScannerMode(SCANNER_MODE_FULL)
            .setGalleryImportAllowed(true)
            .setResultFormats(RESULT_FORMAT_JPEG, RESULT_FORMAT_PDF)
            .build()

        val scanner = GmsDocumentScanning.getClient(options)

        val scannerLauncher = registerForActivityResult(StartIntentSenderForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val scanningResult = GmsDocumentScanningResult.fromActivityResultIntent(result.data)
                if (scanningResult != null) {
                    scanningResult.pages?.let { pages ->
                        for (page in pages) {
                            val imageUri = page.imageUri
                            // Save the image to the gallery
                            val inputStream: InputStream? =
                                contentResolver.openInputStream(imageUri)
                            val contentValues = ContentValues().apply {
                                put(MediaStore.MediaColumns.DISPLAY_NAME, "scanned_image.jpg")
                                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    put(
                                        MediaStore.MediaColumns.RELATIVE_PATH,
                                        "Pictures/Scanned Documents"
                                    )
                                }
                            }
                            val newImageUri = contentResolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                            )
                            val outputStream: OutputStream? =
                                newImageUri?.let { contentResolver.openOutputStream(it) }
                            if (inputStream != null && outputStream != null) {
                                inputStream.copyTo(outputStream)
                                inputStream.close()
                                outputStream.close()
                            }
                        }
                    }
                }
                if (scanningResult != null) {
                    scanningResult.pdf?.let { pdf ->
                        val pdfUri = pdf.uri
                        val pageCount = pdf.pageCount
                        val inputStream: InputStream? = contentResolver.openInputStream(pdfUri)
                        val contentValues = ContentValues().apply {
                            put(MediaStore.MediaColumns.DISPLAY_NAME, "scanned_document.pdf")
                            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                put(
                                    MediaStore.MediaColumns.RELATIVE_PATH,
                                    "Documents/Scanned Documents"
                                )
                            }
                        }
                        val newPdfUri = contentResolver.insert(
                            MediaStore.Files.getContentUri("external"),
                            contentValues
                        )
                        val outputStream: OutputStream? =
                            newPdfUri?.let { contentResolver.openOutputStream(it) }
                        if (inputStream != null && outputStream != null) {
                            inputStream.copyTo(outputStream)
                            inputStream.close()
                            outputStream.close()
                        }
                    }
                }
            }
        }

        val cameraIcon = findViewById<ImageView>(R.id.cameraIcon)
        cameraIcon.setOnClickListener {
            scanner.getStartScanIntent(this@HomeActivity)
                .addOnSuccessListener { intentSender ->
                    scannerLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                }
                .addOnFailureListener { exception ->
                    Log.e("HomeActivity", "Error starting scanner", exception)
                }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> {
                val intent = Intent(this,ProfileActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_create_budget -> {
                // Handle create budget navigation
            }

            R.id.nav_professional_help -> {
               val intent = Intent(this, AccountantHelpActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_tax_calculator -> {
                val intent = Intent(this, TaxCalculatorActivity::class.java)
                startActivity(intent)
            }

            R.id.nav_purchase -> {

            }

            R.id.nav_guides -> {
                // Handle guides navigation
            }

            R.id.nav_settings -> {
                // Handle settings navigation
            }

            R.id.nav_logout -> {
                showLogoutConfirmationDialog()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun toForum() {
        val forumFragment = ForumFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, forumFragment)
            commit()
        }
    }

    private fun toHome() {
        val homeFragment = HomeFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, homeFragment)
            commit()
        }
    }

    private fun toMessages() {
        val messagesFragment = MessagesFragment()
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameLayout, messagesFragment)
            commit()
        }
    }

    private fun updateProfileInfo() {
        val user = auth.currentUser
        if (user != null) {
            // Assume you have a users collection in Firestore
            val userId = user.uid
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(userId)

            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val img = document.getString("userProfileImage")
                        val name = document.getString("userFullName")
                        val email = document.getString("userEmail")
                        val headerView =
                            findViewById<NavigationView>(R.id.navigation_view).getHeaderView(0)
                        val profImageView =
                            headerView.findViewById<ImageView>(R.id.userProfileImage)
                        val nameTextView = headerView.findViewById<TextView>(R.id.user_name)
                        val emailTextView = headerView.findViewById<TextView>(R.id.user_email)

                        if (img != null) {
                            Glide.with(this).load(img).into(profImageView)
                        } else {
                            profImageView.setImageResource(R.drawable.default_profile_img)
                        }
                        nameTextView.text = name
                        emailTextView.text = email
                    } else {
                        Log.d("HomeActivity", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("HomeActivity", "get failed with ", exception)
                }
        }
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to log out?")
        builder.setPositiveButton("Yes") { dialog, _ ->
            dialog.dismiss()
            performLogout()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

}