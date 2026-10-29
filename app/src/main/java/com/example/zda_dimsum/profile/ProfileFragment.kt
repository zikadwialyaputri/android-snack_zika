
package com.example.zda_dimsum.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.fragment.app.Fragment
import com.example.zda_dimsum.AuthActivity
import com.example.zda_dimsum.R
import com.google.android.material.imageview.ShapeableImageView

class ProfileFragment : Fragment() {

    private lateinit var imgProfile: ShapeableImageView

    private val pickMedia =
        registerForActivityResult(PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                try {
                    requireContext().contentResolver
                        .takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                } catch (_: SecurityException) {
                } catch (_: UnsupportedOperationException) {
                }

                imgProfile.setImageURI(uri)
                saveProfileImage(uri)

                Toast.makeText(
                    requireContext(),
                    "Foto profil berhasil diubah",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(
            R.layout.fragment_profile,
            container,
            false
        )
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        imgProfile = view.findViewById(R.id.imgProfile)

        val tvProfileName =
            view.findViewById<TextView>(R.id.tvProfileName)

        val btnLogout =
            view.findViewById<TextView>(R.id.btnLogout)

        val sharedPref = requireActivity().getSharedPreferences(
            "user_pref",
            Context.MODE_PRIVATE
        )

        tvProfileName.text =
            sharedPref.getString("username", "Zika Dwi") ?: "Zika Dwi"

        loadProfileImage()

        imgProfile.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(
                    PickVisualMedia.ImageOnly
                )
            )
        }

        btnLogout.setOnClickListener {
            sharedPref.edit().clear().apply()

            val intent = Intent(
                requireContext(),
                AuthActivity::class.java
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            startActivity(intent)
        }
    }

    private fun saveProfileImage(uri: Uri) {
        requireContext()
            .getSharedPreferences(
                "profile_pref",
                Context.MODE_PRIVATE
            )
            .edit()
            .putString("profile_image_uri", uri.toString())
            .apply()
    }

    private fun loadProfileImage() {
        val profilePref = requireContext().getSharedPreferences(
            "profile_pref",
            Context.MODE_PRIVATE
        )

        val savedUri = profilePref.getString(
            "profile_image_uri",
            null
        )

        if (savedUri != null) {
            try {
                imgProfile.setImageURI(Uri.parse(savedUri))
            } catch (_: Exception) {
                profilePref.edit()
                    .remove("profile_image_uri")
                    .apply()

                imgProfile.setImageResource(R.drawable.ic_profile)
            }
        }
    }
}
