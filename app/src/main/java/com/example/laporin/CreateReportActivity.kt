package com.example.laporin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.laporin.databinding.ActivityCreateReportBinding
import com.example.laporin.entity.Report
import com.example.laporin.usecase.ReportUseCase

class CreateReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateReportBinding
    private val reportUseCase = ReportUseCase()
    private var imageUri: Uri? = null
    private val PICK_IMAGE = 1001
    private var cloudinaryInitialized = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCloudinary()

        // 🖼️ Pilih gambar dari galeri
        binding.btnChooseImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE)
        }

        // 💾 Simpan laporan
        binding.btnSubmit.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val desc = binding.etDescription.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()

            if (title.isEmpty() || desc.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua data!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageUri == null) {
                Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            uploadImageAndSaveReport(title, desc, location)
        }
    }

    // 🔧 Inisialisasi Cloudinary (hindari crash jika init dipanggil lebih dari 1x)
    private fun initCloudinary() {
        if (cloudinaryInitialized) return

        val config = mapOf(
            "cloud_name" to "dsqvxfk8t", // ganti dengan cloud name kamu
            "api_key" to "893149145974224",
            "api_secret" to "8A6x1d1oUWnbNhcDmBvlY-j1fkY",
            "secure" to true
        )

        try {
            MediaManager.init(this, config)
            cloudinaryInitialized = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ☁️ Upload gambar ke Cloudinary (pakai unsigned preset)
    private fun uploadImageAndSaveReport(title: String, desc: String, location: String) {
        MediaManager.get().upload(imageUri)
            .option("upload_preset", "unsigned_laporin")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String?) {
                    Toast.makeText(this@CreateReportActivity, "Mengupload gambar...", Toast.LENGTH_SHORT).show()
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                    val imageUrl = resultData?.get("secure_url")?.toString().orEmpty()
                    if (imageUrl.isEmpty()) {
                        Toast.makeText(this@CreateReportActivity, "Gagal mendapatkan URL gambar", Toast.LENGTH_SHORT).show()
                        return
                    }

                    Toast.makeText(this@CreateReportActivity, "Upload berhasil!", Toast.LENGTH_SHORT).show()
                    saveReport(title, desc, location, imageUrl)
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Toast.makeText(
                        this@CreateReportActivity,
                        "Upload gagal: ${error?.description}",
                        Toast.LENGTH_LONG
                    ).show()
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
            })
            .dispatch()
    }

    // 🧾 Simpan data laporan ke Firebase Realtime Database
    private fun saveReport(title: String, desc: String, location: String, imageUrl: String) {
        val report = Report(
            title = title,
            description = desc,
            location = location,
            imageUrl = imageUrl
        )

        reportUseCase.addReport(report,
            onSuccess = {
                Toast.makeText(this, "Laporan berhasil disimpan!", Toast.LENGTH_SHORT).show()
                finish()
            },
            onFailure = {
                Toast.makeText(this, "Gagal menyimpan laporan: ${it.message}", Toast.LENGTH_SHORT).show()
            })
    }

    // 🔙 Preview gambar
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            imageUri = data?.data
            binding.imgPreview.setImageURI(imageUri)
        }
    }
}
