package com.example.laporin

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.laporin.databinding.ActivityUpdateReportBinding
import com.example.laporin.entity.Report
import com.example.laporin.usecase.ReportUseCase

class UpdateReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateReportBinding
    private val reportUseCase = ReportUseCase()
    private var report: Report? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Ambil data dari intent
        report = intent.getParcelableExtra("report")

        if (report == null) {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Tampilkan data lama
        binding.etTitle.setText(report!!.title)
        binding.etDescription.setText(report!!.description)
        binding.etLocation.setText(report!!.location)
        if (!report!!.imageUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load(report!!.imageUrl)
                .centerCrop()
                .into(binding.imgPreview)
        }

        // ✅ Tombol update
        binding.btnUpdate.setOnClickListener {
            val updatedTitle = binding.etTitle.text.toString().trim()
            val updatedDesc = binding.etDescription.text.toString().trim()
            val updatedLocation = binding.etLocation.text.toString().trim()

            if (updatedTitle.isEmpty() || updatedDesc.isEmpty() || updatedLocation.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            report!!.apply {
                title = updatedTitle
                description = updatedDesc
                location = updatedLocation
            }

            reportUseCase.updateReport(report!!,
                onSuccess = {
                    Toast.makeText(this, "Laporan berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                    finish()
                },
                onFailure = {
                    Toast.makeText(this, "Gagal memperbarui laporan: ${it.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

