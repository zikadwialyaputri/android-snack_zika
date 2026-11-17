package com.example.zda_dimsum.history

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.zda_dimsum.R
import com.example.zda_dimsum.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val daftarRiwayat = listOf(
        RiwayatModel(
            "GOR WIRA",
            "Jl. Siak, Gg. Melati, Pekanbaru",
            "22 November 2026",
            "16.00 - 17.00",
            "Rp 60.000",
            "Akan Datang"
        ),
        RiwayatModel(
            "GOR WIRA",
            "Jl. Siak, Gg. Melati, Pekanbaru",
            "10 November 2026",
            "15.00 - 16.00",
            "Rp 60.000",
            "Selesai"
        ),
        RiwayatModel(
            "GOR WIRA",
            "Jl. Siak, Gg. Melati, Pekanbaru",
            "5 November 2026",
            "19.00 - 20.00",
            "Rp 60.000",
            "Dibatalkan"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        tampilkanRiwayat(daftarRiwayat)
        ubahFilter("Semua")

        binding.filterSemua.setOnClickListener {
            tampilkanRiwayat(daftarRiwayat)
            ubahFilter("Semua")
        }

        binding.filterAkanDatang.setOnClickListener {
            val hasil = daftarRiwayat.filter {
                it.status == "Akan Datang"
            }

            tampilkanRiwayat(hasil)
            ubahFilter("Akan Datang")
        }

        binding.filterSelesai.setOnClickListener {
            val hasil = daftarRiwayat.filter {
                it.status == "Selesai"
            }

            tampilkanRiwayat(hasil)
            ubahFilter("Selesai")
        }
    }

    private fun tampilkanRiwayat(
        data: List<RiwayatModel>
    ) {
        binding.listRiwayat.adapter =
            RiwayatAdapter(requireContext(), data)
    }

    private fun ubahFilter(status: String) {

        val daftarFilter = listOf(
            binding.filterSemua to "Semua",
            binding.filterAkanDatang to "Akan Datang",
            binding.filterSelesai to "Selesai"
        )

        daftarFilter.forEach { (tombol, nama) ->

            val dipilih = nama == status

            tombol.setBackgroundResource(
                if (dipilih)
                    R.drawable.bg_filter_selected
                else
                    R.drawable.bg_filter_normal
            )

            tombol.setTextColor(
                if (dipilih)
                    Color.WHITE
                else
                    Color.rgb(35, 139, 69)
            )
        }
    }

    override fun onDestroyView() {
        binding.listRiwayat.adapter = null
        _binding = null
        super.onDestroyView()
    }
}
