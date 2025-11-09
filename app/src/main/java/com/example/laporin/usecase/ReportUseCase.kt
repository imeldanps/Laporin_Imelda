package com.example.laporin.usecase

import android.net.Uri
import com.example.laporin.entity.Report
import com.example.laporin.utils.CloudinaryManager
import com.google.firebase.database.FirebaseDatabase

class ReportUseCase {

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

    // ✏️ Update laporan tanpa ubah gambar
    fun updateReport(
        report: Report,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (report.id.isNullOrEmpty()) {
            onFailure(Exception("ID laporan tidak ditemukan"))
            return
        }

        db.child(report.id!!).setValue(report)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // ✏️ Update laporan dengan gambar baru
    fun updateReportWithImage(
        report: Report,
        imageUri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (report.id.isNullOrEmpty()) {
            onFailure(Exception("ID laporan tidak ditemukan"))
            return
        }

        val oldImageUrl = report.imageUrl

        // Upload gambar baru ke Cloudinary
        CloudinaryManager.uploadImage(
            imageUri,
            onSuccess = { newImageUrl ->
                // Hapus gambar lama kalau ada
                if (!oldImageUrl.isNullOrEmpty()) {
                    CloudinaryManager.deleteImage(oldImageUrl) { }
                }

                // Simpan URL baru ke Firebase
                report.imageUrl = newImageUrl
                db.child(report.id!!).setValue(report)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            },
            onFailure = { e ->
                onFailure(Exception("Gagal upload gambar baru: ${e.message}"))
            }
        )
    }

    // 🗑️ Hapus laporan (Firebase + Cloudinary)
    fun deleteReport(
        report: Report,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (report.id.isNullOrEmpty()) {
            onFailure(Exception("ID laporan tidak ditemukan"))
            return
        }

        db.child(report.id!!).removeValue()
            .addOnSuccessListener {
                if (!report.imageUrl.isNullOrEmpty()) {
                    CloudinaryManager.deleteImage(report.imageUrl!!) { onSuccess() }
                } else {
                    onSuccess()
                }
            }
            .addOnFailureListener { onFailure(it) }
    }
}
