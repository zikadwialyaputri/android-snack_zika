package com.example.zda_dimsum.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zda_dimsum.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(
            inflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        val halaman = requireArguments().getInt("halaman")

        when (halaman) {
            0 -> {
                binding.tvJudul.text =
                    "Cari Lapangan Badminton"

                binding.tvDeskripsi.text =
                    "Temukan GOR badminton favoritmu dengan mudah."
            }

            1 -> {
                binding.tvJudul.text =
                    "Booking Lebih Mudah"

                binding.tvDeskripsi.text =
                    "Pilih lapangan, tanggal, dan jam bermain sesuai kebutuhanmu."
            }

            2 -> {
                binding.tvJudul.text =
                    "Siap Bermain!"

                binding.tvDeskripsi.text =
                    "Lihat informasi booking dan nikmati permainan badmintonmu."
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        fun newInstance(halaman: Int): OnboardingFragment {
            return OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putInt("halaman", halaman)
                }
            }
        }
    }
}
