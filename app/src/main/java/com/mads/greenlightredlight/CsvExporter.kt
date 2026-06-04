package com.mads.greenlightredlight

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.OutputStream
import android.widget.Toast

object CsvExporter {
    fun exportSingleEntry(context: Context, entry: Entry) {
        val federalTax = TaxCalculator.calculateFederalTax(entry.weeklyAmount)
        val socialSecurity = TaxCalculator.calculateSocialSecurity(entry.weeklyAmount)
        val medicare = TaxCalculator.calculateMedicare(entry.weeklyAmount)
        val netTakeHome = TaxCalculator.calculateNetTakeHome(entry.weeklyAmount)

        val fileName = "${entry.name}_tax_breakdown.csv"
        val header = "Name, Gross Weekly Amount, Social Security, Medicare, Net Take Home\n"
        val row = "${entry.name}, ${String.format("%.2f", entry.weeklyAmount)}. ${
            String.format(
                "%.2f",
                federalTax
            )
        }, ${String.format("%.2f", socialSecurity)}, ${String.format(".2f", medicare)}, ${
            String.format(
                "%.2f",
                netTakeHome
            )
        }\n"
        val csvContent = header + row
        writeToDownloads(context, fileName, csvContent)
    }

    fun exportAllEntries(context: Context, entries: List<Entry>) {
        val fileName = "all_tax_breakdown.csv"
        val header = "Name, Gross Weekly Amount, Social Security, Medicare, Net Take Home\n"
        val rows = entries.filter { it.isIncome }.joinToString("") { entry ->
            val federalTax = TaxCalculator.calculateFederalTax(entry.weeklyAmount)
            val socialSecurity = TaxCalculator.calculateSocialSecurity(entry.weeklyAmount)
            val medicare = TaxCalculator.calculateMedicare(entry.weeklyAmount)
            val netTakeHome = TaxCalculator.calculateNetTakeHome(entry.weeklyAmount)
            "${entry.name}, ${String.format("%.2f", entry.weeklyAmount)}. ${
                String.format(
                    "%.2f",
                    federalTax
                )
            }, ${String.format("%.2f", socialSecurity)}, ${String.format(".2f", medicare)}, ${
                String.format(
                    "%.2f",
                    netTakeHome
                )
            }\n"
        }

        val csvContent = header + rows
        writeToDownloads(context, fileName, csvContent)
    }

    private fun writeToDownloads(context: Context, fileName: String, csvContent: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                uri?.let {
                    resolver.openOutputStream(it)?.use { stream ->
                        stream.write(csvContent.toByteArray())
                        stream.flush()
                    }
                    contentValues.clear()
                    contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                    resolver.update(it, contentValues, null, null)
                    Toast.makeText(context, "CSV saved to Downloads!", Toast.LENGTH_SHORT).show()
                } ?: run {
                    Toast.makeText(context, "Failed to save CSV", Toast.LENGTH_LONG).show()
                }
            } else {
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                downloadsDir.mkdirs()
                val file = java.io.File(downloadsDir, fileName)
                file.writeText(csvContent)
                android.media.MediaScannerConnection.scanFile(
                    context,
                    arrayOf(file.absolutePath),
                    arrayOf("text/csv"),
                    null
                )
                Toast.makeText(context, "CSV saved to Downloads!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error saving CSV: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}