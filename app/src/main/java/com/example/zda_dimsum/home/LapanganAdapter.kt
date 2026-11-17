package com.example.zda_dimsum.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zda_dimsum.databinding.ItemLapanganBinding

class LapanganAdapter(
    private val daftarLapangan: List<LapanganModel>,
    private val onKlik: (LapanganModel) -> Unit
) : RecyclerView.Adapter<LapanganAdapter.ViewHolder>() {

    inner class ViewHolder(
        val binding: ItemLapanganBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding = ItemLapanganBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        val lapangan = daftarLapangan[position]

        holder.binding.apply {
            tvNamaLapangan.text = lapangan.nama
            tvLokasiLapangan.text = lapangan.lokasi
            tvHargaLapangan.text = lapangan.harga
            imgLapangan.setImageResource(lapangan.gambar)

            root.setOnClickListener {
                onKlik(lapangan)
            }
        }
    }

    override fun getItemCount(): Int {
        return daftarLapangan.size
    }
}