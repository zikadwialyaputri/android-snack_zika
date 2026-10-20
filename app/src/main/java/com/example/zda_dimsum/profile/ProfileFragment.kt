package com.example.zda_dimsum.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.zda_dimsum.AuthActivity
import com.example.zda_dimsum.R

class ProfileFragment : Fragment() {

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

        val tvProfileName =
            view.findViewById<TextView>(R.id.tvProfileName)

        val btnLogout =
            view.findViewById<TextView>(R.id.btnLogout)

        val sharedPref =
            requireActivity().getSharedPreferences(
                "user_pref",
                android.content.Context.MODE_PRIVATE
            )
        val username =
            sharedPref.getString(
                "username",
                "Zika Dwi"
            )
        tvProfileName.text = username

        btnLogout.setOnClickListener {

            sharedPref.edit()
                .clear()
                .apply()

            val intent = Intent(
                requireContext(),
                AuthActivity::class.java
            )

            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK

            startActivity(intent)
        }
    }
}