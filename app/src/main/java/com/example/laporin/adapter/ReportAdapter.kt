package com.example.laporin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.laporin.databinding.ItemReportBinding
import com.example.laporin.entity.Report

class ReportAdapter(
    private var reports: MutableList<Report>,
    private val onEdit: (Report) -> Unit,
    private val onDelete: (Report) -> Unit
) : RecyclerView.Adapter<ReportAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemReportBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = reports.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val report = reports[position]
        holder.binding.tvTitle.text = report.title
        holder.binding.tvDescription.text = report.description
        holder.binding.tvLocation.text = report.location
        holder.binding.tvStatus.text = report.status

        Glide.with(holder.itemView.context)
            .load(report.imageUrl)
            .into(holder.binding.imgReport)

        holder.binding.btnEdit.setOnClickListener { onEdit(report) }
        holder.binding.btnDelete.setOnClickListener { onDelete(report) }
    }

    fun updateData(newData: List<Report>) {
        reports.clear()
        reports.addAll(newData)
        notifyDataSetChanged()
    }
}
