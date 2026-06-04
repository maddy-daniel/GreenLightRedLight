package com.mads.greenlightredlight

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.OutputStream

object CsvExporter{
    fun exportSingleEntry(context: Context, entry: Entry){
        val federalTax = TaxCalculator.calculateFederalTax(entry.weeklyAmount)
        val socialSecurity = TaxCalculator.calculateSocialSecurity(entry.weeklyAmount)
        val medicare = TaxCalculator.calculateMedicare(entry.weeklyAmount)
        val netTakeHome = TaxCalculator.calculateNetTakeHome(entry.weeklyAmount)

        val fileName="${entry.name}_tax_breakdown.csv"
        val header = "Name, Gross Weekly Amount, Social Security, Medicare, Net Take Home\n"
        val row = "${entry.name}, ${String.format("%.2f", entry.weeklyAmount)}. ${String.format("%.2f",federalTax)}, ${String.format("%.2f", socialSecurity)}, ${String.format(".2f",medicare)}, ${String.format("%.2f",netTakeHome)}\n"
        val csvContent = header + row
        writeToDownloads(context, fileName, csvContent)
    }

    fun exportAllEntries(context: Context, entries: List<Entry>){
        val fileName = "all_tax_breakdown.csv"
        val header = "Name, Gross Weekly Amount, Social Security, Medicare, Net Take Home\n"
        val rows = entries.filter{it.isIncome}.joinToString(""){ entry->
            val federalTax = TaxCalculator.calculateFederalTax(entry.weeklyAmount)
            val socialSecurity = TaxCalculator.calculateSocialSecurity(entry.weeklyAmount)
            val medicare = TaxCalculator.calculateMedicare(entry.weeklyAmount)
            val netTakeHome = TaxCalculator.calculateNetTakeHome(entry.weeklyAmount)
            "${entry.name}, ${String.format("%.2f", entry.weeklyAmount)}. ${String.format("%.2f", federalTax)}, ${String.format("%.2f", socialSecurity)}, ${String.format(".2f", medicare)}, ${String.format("%.2f", netTakeHome)}\n"
        }

        val csvContent = header + rows
        writeToDownloads(context, fileName, csvContent)
    }

    private fun writeToDownloads(context: Context, fileName: String, csvContent: String){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            val contentValues = ContentValues().apply{
                put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val uri = context.contentResolver.insert(
                MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                contentValues
            )
            uri?.let{
                val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)
                outputStream?.use{
                    stream->stream.write(csvContent.toByteArray())
                }
            }
        }
        else{
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = java.io.File(downloadsDir, fileName)
            file.writeText(csvContent)
        }
    }
}