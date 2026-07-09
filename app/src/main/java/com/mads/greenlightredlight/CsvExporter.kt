package com.mads.greenlightredlight

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast

object CsvExporter {
    fun exportSingleEntry(context: Context, entry: Entry) {
        val gross = entry.weeklyAmount
        val federalTax = TaxCalculator.calculateFederalTax(gross)
        val socialSecurity = TaxCalculator.calculateSocialSecurity(gross)
        val medicare = TaxCalculator.calculateMedicare(gross)
        val njStateTax = TaxCalculator.calculateNJStateTax(gross)
        val sdi = TaxCalculator.calculateSDI(gross)
        val sui = TaxCalculator.calculateSUI(gross)
        val fli = TaxCalculator.calculateFLI(gross)
        val netTakeHome = TaxCalculator.calculateNetTakeHome(gross)

        val fileName = "${entry.name}_tax_breakdown.csv"
        val header = "Name, Gross Weekly Amount, Social Security, Medicare, NJ State Tax, SDI, SUI, FLI, Net Take Home\n"
        val row = "${entry.name}," +
                "${String.format("%.2f", gross)}," +
                "${String.format("%.2f", federalTax)}," +
                "${String.format("%.2f", socialSecurity)}," +
                "${String.format("%.2f", medicare)}," +
                "${String.format("%.2f", njStateTax)}," +
                "${String.format("%.2f", sdi)}," +
                "${String.format("%.2f", sui)}," +
                "${String.format("%.2f", fli)}," +
                "${String.format("%.2f", netTakeHome)}\n"

        val csvContent = header + row

        writeToDownloads(context, fileName, csvContent)
    }

    fun exportAllEntries(context: Context, entries: List<Entry>) {
        val fileName = "all_tax_breakdown.csv"
        val header = "Name, Gross Weekly Amount, Social Security, Medicare, NJ State Tax, SDI, SUI, FLI, Net Take Home\n"
        val rows = entries.filter { it.isIncome }.joinToString("") { entry ->
            val gross = entry.weeklyAmount
            val federalTax = TaxCalculator.calculateFederalTax(gross)
            val socialSecurity = TaxCalculator.calculateSocialSecurity(gross)
            val medicare = TaxCalculator.calculateMedicare(gross)
            val njStateTax = TaxCalculator.calculateNJStateTax(gross)
            val sdi = TaxCalculator.calculateSDI(gross)
            val sui = TaxCalculator.calculateSUI(gross)
            val fli = TaxCalculator.calculateFLI(gross)
            val netTakeHome = TaxCalculator.calculateNetTakeHome(gross)
            "${entry.name}, " +
                    "${String.format("%.2f", gross)}," +
                    "${String.format("%.2f", federalTax)}," +
                    "${String.format("%.2f", socialSecurity)}," +
                    "${String.format("%.2f", medicare)}," +
                    "${String.format("%.2f", njStateTax)}," +
                    "${String.format("%.2f", sdi)}," +
                    "${String.format("%.2f", sui)}," +
                    "${String.format("%.2f", fli)}," +
                    "${String.format("%.2f", netTakeHome)} \n"
        }

        val csvContent = header + rows
        writeToDownloads(context, fileName, csvContent)
    }

    private fun writeToDownloads(context: Context, fileName: String, csvContent: String) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues()

                contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                contentValues.put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                contentValues.put(MediaStore.Downloads.IS_PENDING, 1)

                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

                if(uri!=null) {
                    val outputStream = resolver.openOutputStream(uri)
                    if(outputStream != null) {
                        outputStream.write(csvContent.toByteArray())
                        outputStream.flush()
                        outputStream.close()
                    }
                    val updateValues = ContentValues()
                    contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                    resolver.update(uri, updateValues, null, null)
                    Toast.makeText(context, "CSV saved to Downloads!", Toast.LENGTH_SHORT).show()
                }
                    else{
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