package com.example.zda_dimsum.history

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.example.zda_dimsum.databinding.ItemRiwayatBinding

class RiwayatAdapter(
    private val context: Context,
    private val daftarRiwayat: List<RiwayatModel>
) : BaseAdapter() {

    override fun getCount(): Int {
        return daftarRiwayat.size
    }

    override fun getItem(position: Int): RiwayatModel {
        return daftarRiwayat[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        val binding: ItemRiwayatBinding

        if (convertView == null) {
            binding = ItemRiwayatBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
            binding.root.tag = binding
        } else {
            binding = convertView.tag as ItemRiwayatBinding
        }

        val riwayat = getItem(position)

        binding.tvNama.text = riwayat.namaLapangan
        binding.tvLokasi.text = riwayat.lokasi
        binding.tvTanggal.text = riwayat.tanggal
        binding.tvJam.text = riwayat.jam
        binding.tvHarga.text = riwayat.harga
        binding.tvStatus.text = riwayat.status

        binding.tvStatus.setTextColor(
            when (riwayat.status) {
                "Akan Datang" -> Color.rgb(138, 106, 32)
                "Selesai" -> Color.rgb(35, 139, 69)
                "Dibatalkan" -> Color.rgb(192, 68, 64)
                else -> Color.GRAY
            }
        )

        binding.btnDetail.setOnClickListener {
            Toast.makeText(
                context,
                "${riwayat.namaLapangan} - ${riwayat.status}",
                Toast.LENGTH_SHORT
            ).show()
        }

        return binding.root
    }
}
