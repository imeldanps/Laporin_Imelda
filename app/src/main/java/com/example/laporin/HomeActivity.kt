package com.example.laporin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.laporin.databinding.ActivityHomeBinding
import com.example.laporin.entity.Report
import com.example.laporin.usecase.ReportUseCase
import java.io.Serializable

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val reportUseCase = ReportUseCase()
    private lateinit var adapter: ReportAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 🔹 Inisialisasi RecyclerView & Adapter
        adapter = ReportAdapter(
            mutableListOf(),
            onEdit = { report ->
                val intent = Intent(this, UpdateReportActivity::class.java)
                intent.putExtra("report", report)
                startActivity(intent)
            },
            onDelete = { report ->
                reportUseCase.deleteReport(report.id, {
                    Toast.makeText(this, "Laporan dihapus", Toast.LENGTH_SHORT).show()
                    loadReports()
                }, {
                    Toast.makeText(this, "Gagal hapus laporan", Toast.LENGTH_SHORT).show()
                })

            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // 🔹 Tombol tambah laporan baru
        binding.fabAdd.setOnClickListener {
            startActivity(Intent(this, CreateReportActivity::class.java))
        }

        // 🔹 Muat data pertama kali
        loadReports()
    }

    override fun onResume() {
        super.onResume()
        loadReports()
    }

    private fun loadReports() {
        reportUseCase.getReports(
            onSuccess = { reports ->
                adapter.updateData(reports)
            },
            onFailure = {
                Toast.makeText(this, "Gagal memuat data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
