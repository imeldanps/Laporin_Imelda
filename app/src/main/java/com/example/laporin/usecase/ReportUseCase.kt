package com.example.laporin.usecase

import com.example.laporin.entity.Report
import com.google.firebase.database.FirebaseDatabase

class ReportUseCase {

    // Realtime Database reference
    private val db = FirebaseDatabase.getInstance().getReference("reports")

    // ➕ Tambah laporan baru
    fun addReport(
        report: Report,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val reportId = db.push().key
        if (reportId == null) {
            onFailure(Exception("Gagal membuat ID laporan"))
            return
        }

        report.id = reportId
        db.child(reportId).setValue(report)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // 📖 Ambil semua laporan
    fun getReports(
        onSuccess: (List<Report>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.get()
            .addOnSuccessListener { snapshot ->
                val list = snapshot.children.mapNotNull { it.getValue(Report::class.java) }
                onSuccess(list)
            }
            .addOnFailureListener { onFailure(it) }
    }

    // ✏️ Update laporan
    fun updateReport(
        report: Report,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (report.id.isEmpty()) {
            onFailure(Exception("ID laporan tidak ditemukan"))
            return
        }

        db.child(report.id).setValue(report)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // 🗑️ Hapus laporan
    fun deleteReport(
        id: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (id.isEmpty()) {
            onFailure(Exception("ID laporan kosong"))
            return
        }

        db.child(id).removeValue()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
