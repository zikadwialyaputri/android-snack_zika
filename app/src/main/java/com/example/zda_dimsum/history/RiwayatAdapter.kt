
package com.example.zda_dimsum.history

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.zda_dimsum.databinding.ItemRiwayatBinding

class RiwayatAdapter(
    private val context: Context,
    private val daftarRiwayat: List<RiwayatModel>,
    private val onDetailClick: (RiwayatModel) -> Unit
) : BaseAdapter() {

    override fun getCount(): Int = daftarRiwayat.size

    override fun getItem(position: Int): RiwayatModel =
        daftarRiwayat[position]

    override fun getItemId(position: Int): Long =
        daftarRiwayat[position].id

    override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        val binding = if (convertView == null) {
            ItemRiwayatBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        } else {
            ItemRiwayatBinding.bind(convertView)
        }

        val item = getItem(position)

        binding.tvNama.text = item.namaLapangan
        binding.tvLokasi.text = item.lokasi
        binding.tvTanggal.text = item.tanggal
        binding.tvJam.text = item.jam
        binding.tvHarga.text = item.harga
        binding.tvStatus.text = item.status

        binding.tvStatus.setTextColor(
            when (item.status) {
                "Menunggu" -> Color.rgb(190, 125, 35)
                "Selesai" -> Color.rgb(40, 130, 75)
                "Dibatalkan" -> Color.RED
                else -> Color.DKGRAY
            }
        )

        binding.btnDetail.setOnClickListener {
            onDetailClick(item)
        }

        return binding.root
    }
}
