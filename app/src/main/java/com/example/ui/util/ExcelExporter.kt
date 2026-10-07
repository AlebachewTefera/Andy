package com.example.ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.entity.BusinessProfile
import com.example.ui.ReportPeriod
import com.example.ui.ReportSummary
import com.example.ui.components.formatCurrency
import com.example.ui.components.formatDate
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ExcelExporter {

    private fun escapeXml(str: String): String {
        return str.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }

    private fun getColLetter(colIdx: Int): String {
        return if (colIdx < 26) {
            ('A'.code + colIdx).toChar().toString()
        } else {
            val first = ('A'.code + (colIdx / 26) - 1).toChar()
            val second = ('A'.code + (colIdx % 26)).toChar()
            "$first$second"
        }
    }

    /**
     * Builds standard OpenXML (.xlsx) bytes directly into an OutputStream.
     */
    fun writeExcelToStream(
        outputStream: OutputStream,
        report: ReportSummary,
        profile: BusinessProfile,
        strings: AppStrings
    ) {
        val zos = ZipOutputStream(outputStream)

        // 1. [Content_Types].xml
        zos.putNextEntry(ZipEntry("[Content_Types].xml"))
        val contentTypes = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/>
  <Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/>
  <Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/>
</Types>""".trimIndent()
        zos.write(contentTypes.toByteArray(Charsets.UTF_8))
        zos.closeEntry()

        // 2. _rels/.rels
        zos.putNextEntry(ZipEntry("_rels/.rels"))
        val rootRels = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>
</Relationships>""".trimIndent()
        zos.write(rootRels.toByteArray(Charsets.UTF_8))
        zos.closeEntry()

        // 3. xl/_rels/workbook.xml.rels
        zos.putNextEntry(ZipEntry("xl/_rels/workbook.xml.rels"))
        val workbookRels = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/>
  <Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>""".trimIndent()
        zos.write(workbookRels.toByteArray(Charsets.UTF_8))
        zos.closeEntry()

        // 4. xl/workbook.xml
        zos.putNextEntry(ZipEntry("xl/workbook.xml"))
        val workbook = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">
  <sheets>
    <sheet name="Business Report" sheetId="1" r:id="rId1"/>
  </sheets>
</workbook>""".trimIndent()
        zos.write(workbook.toByteArray(Charsets.UTF_8))
        zos.closeEntry()

        // 5. xl/styles.xml
        zos.putNextEntry(ZipEntry("xl/styles.xml"))
        val styles = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">
  <fonts count="4">
    <font><name val="Calibri"/><sz val="11"/></font>
    <font><b/><name val="Calibri"/><sz val="11"/></font>
    <font><b/><name val="Calibri"/><sz val="15"/><color rgb="FFE11D48"/></font>
    <font><b/><name val="Calibri"/><sz val="11"/><color rgb="FF1F2937"/></font>
  </fonts>
  <fills count="3">
    <fill><patternFill patternType="none"/></fill>
    <fill><patternFill patternType="gray125"/></fill>
    <fill><patternFill patternType="solid"><fgColor rgb="FFFFE4E6"/></patternFill></fill>
  </fills>
  <borders count="2">
    <border><left/><right/><top/><bottom/><diagonal/></border>
    <border>
      <left style="thin"><color rgb="FFCBD5E1"/></left>
      <right style="thin"><color rgb="FFCBD5E1"/></right>
      <top style="thin"><color rgb="FFCBD5E1"/></top>
      <bottom style="thin"><color rgb="FFCBD5E1"/></bottom>
    </border>
  </borders>
  <cellStyleXfs count="1">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0"/>
  </cellStyleXfs>
  <cellXfs count="4">
    <xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/>
    <xf numFmtId="0" fontId="1" fillId="0" borderId="0" xfId="0" applyFont="1"/>
    <xf numFmtId="0" fontId="2" fillId="0" borderId="0" xfId="0" applyFont="1"/>
    <xf numFmtId="0" fontId="3" fillId="2" borderId="1" xfId="0" applyFont="1" applyFill="1" applyBorder="1"/>
  </cellXfs>
</styleSheet>""".trimIndent()
        zos.write(styles.toByteArray(Charsets.UTF_8))
        zos.closeEntry()

        // 6. xl/worksheets/sheet1.xml
        zos.putNextEntry(ZipEntry("xl/worksheets/sheet1.xml"))
        val sheetXml = buildSheetXml(report, profile, strings)
        zos.write(sheetXml.toByteArray(Charsets.UTF_8))
        zos.closeEntry()

        zos.finish()
    }

    private fun buildSheetXml(
        report: ReportSummary,
        profile: BusinessProfile,
        strings: AppStrings
    ): String {
        val sb = StringBuilder()
        sb.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""")
        sb.append("""<worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main">""")
        sb.append("""<cols>""")
        sb.append("""<col min="1" max="1" width="22" customWidth="1"/>""")
        sb.append("""<col min="2" max="2" width="28" customWidth="1"/>""")
        sb.append("""<col min="3" max="3" width="16" customWidth="1"/>""")
        sb.append("""<col min="4" max="4" width="16" customWidth="1"/>""")
        sb.append("""<col min="5" max="5" width="18" customWidth="1"/>""")
        sb.append("""<col min="6" max="6" width="22" customWidth="1"/>""")
        sb.append("""<col min="7" max="7" width="25" customWidth="1"/>""")
        sb.append("""</cols>""")
        sb.append("""<sheetData>""")

        var rowNum = 1

        fun addRow(cells: List<CellData>) {
            sb.append("""<row r="$rowNum">""")
            cells.forEachIndexed { colIdx, cell ->
                val ref = "${getColLetter(colIdx)}$rowNum"
                val styleAttr = if (cell.styleId > 0) """ s="${cell.styleId}"""" else ""
                if (cell.isNumber) {
                    sb.append("""<c r="$ref"$styleAttr><v>${cell.value}</v></c>""")
                } else {
                    sb.append("""<c r="$ref" t="inlineStr"$styleAttr><is><t>${escapeXml(cell.value)}</t></is></c>""")
                }
            }
            sb.append("""</row>""")
            rowNum++
        }

        fun addEmptyRow() {
            sb.append("""<row r="$rowNum"/>""")
            rowNum++
        }

        val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
        val periodName = when (report.period) {
            ReportPeriod.DAILY -> strings.daily
            ReportPeriod.WEEKLY -> strings.weekly
            ReportPeriod.MONTHLY -> strings.monthly
        }

        // Header Title
        addRow(listOf(CellData("${profile.businessName} - ${strings.reports}", styleId = 2)))
        addRow(listOf(
            CellData("Report Period: $periodName"),
            CellData("Generated On: $dateStr"),
            CellData("Currency: ${profile.currencySymbol}")
        ))
        addEmptyRow()

        // KPI Summary Block
        addRow(listOf(CellData("--- FINANCIAL SUMMARY ---", styleId = 1)))
        addRow(listOf(
            CellData(strings.totalSales, styleId = 3),
            CellData(strings.totalExpenses, styleId = 3),
            CellData(strings.netProfit, styleId = 3),
            CellData(strings.productsSold, styleId = 3),
            CellData("Remaining Stock", styleId = 3)
        ))
        addRow(listOf(
            CellData("${profile.currencySymbol} %.2f".format(report.totalSales)),
            CellData("${profile.currencySymbol} %.2f".format(report.totalExpenses)),
            CellData("${profile.currencySymbol} %.2f".format(report.totalProfit)),
            CellData("${report.totalUnitsSold} units"),
            CellData("${report.totalStockRemaining} units")
        ))
        addEmptyRow()

        // Top Selling Products
        if (report.topSellingProducts.isNotEmpty()) {
            addRow(listOf(CellData("--- ${strings.topSellingProducts.replace("🏆", "").trim()} ---", styleId = 1)))
            addRow(listOf(
                CellData("Rank", styleId = 3),
                CellData("Product Name", styleId = 3),
                CellData("Units Sold", styleId = 3)
            ))
            report.topSellingProducts.forEachIndexed { idx, (name, qty) ->
                addRow(listOf(
                    CellData("${idx + 1}"),
                    CellData(name),
                    CellData("$qty")
                ))
            }
            addEmptyRow()
        }

        // Detailed Sales
        addRow(listOf(CellData("--- SALES TRANSACTIONS (${report.recentTransactions.size}) ---", styleId = 1)))
        addRow(listOf(
            CellData("Date & Time", styleId = 3),
            CellData("Product Name", styleId = 3),
            CellData("Qty", styleId = 3),
            CellData("Unit Price", styleId = 3),
            CellData("Total Amount", styleId = 3),
            CellData("Customer", styleId = 3),
            CellData("Notes", styleId = 3)
        ))

        if (report.recentTransactions.isEmpty()) {
            addRow(listOf(CellData(strings.noSalesInPeriod)))
        } else {
            report.recentTransactions.forEach { sale ->
                addRow(listOf(
                    CellData(formatDate(sale.timestamp)),
                    CellData(sale.productName),
                    CellData("${sale.quantity}"),
                    CellData("${profile.currencySymbol} %.2f".format(sale.productPrice)),
                    CellData("${profile.currencySymbol} %.2f".format(sale.totalAmount)),
                    CellData(sale.customerName ?: "-"),
                    CellData(sale.note)
                ))
            }
            // Sales Total Row
            addRow(listOf(
                CellData("TOTAL SALES", styleId = 1),
                CellData(""),
                CellData("${report.totalUnitsSold}", styleId = 1),
                CellData(""),
                CellData("${profile.currencySymbol} %.2f".format(report.totalSales), styleId = 1),
                CellData(""),
                CellData("")
            ))
        }
        addEmptyRow()

        // Detailed Expenses
        addRow(listOf(CellData("--- EXPENSES (${report.recentExpenses.size}) ---", styleId = 1)))
        addRow(listOf(
            CellData("Date & Time", styleId = 3),
            CellData("Expense Title", styleId = 3),
            CellData("Category", styleId = 3),
            CellData("Amount", styleId = 3)
        ))

        if (report.recentExpenses.isEmpty()) {
            addRow(listOf(CellData("No expenses logged in this period.")))
        } else {
            report.recentExpenses.forEach { exp ->
                addRow(listOf(
                    CellData(formatDate(exp.timestamp)),
                    CellData(exp.title),
                    CellData(exp.category),
                    CellData("${profile.currencySymbol} %.2f".format(exp.amount))
                ))
            }
            // Expense Total Row
            addRow(listOf(
                CellData("TOTAL EXPENSES", styleId = 1),
                CellData(""),
                CellData(""),
                CellData("${profile.currencySymbol} %.2f".format(report.totalExpenses), styleId = 1)
            ))
        }

        sb.append("""</sheetData>""")
        sb.append("""</worksheet>""")
        return sb.toString()
    }

    private data class CellData(
        val value: String,
        val styleId: Int = 0,
        val isNumber: Boolean = false
    )

    /**
     * Generates a .xlsx file in the application cache/reports folder and returns it.
     */
    fun createExcelFile(
        context: Context,
        report: ReportSummary,
        profile: BusinessProfile,
        strings: AppStrings
    ): File {
        val reportsDir = File(context.cacheDir, "reports")
        if (!reportsDir.exists()) {
            reportsDir.mkdirs()
        }

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault()).format(Date())
        val cleanBizName = profile.businessName.replace(Regex("[^a-zA-Z0-9_]"), "_").take(15)
        val periodName = report.period.name.lowercase()
        val file = File(reportsDir, "TinyBiz_${cleanBizName}_Report_${periodName}_$timestamp.xlsx")

        FileOutputStream(file).use { fos ->
            writeExcelToStream(fos, report, profile, strings)
        }
        return file
    }

    /**
     * Composes formatted text body for sharing or sending via email.
     */
    fun buildEmailBody(
        report: ReportSummary,
        profile: BusinessProfile,
        strings: AppStrings
    ): String {
        val curr = profile.currencySymbol
        val periodName = when (report.period) {
            ReportPeriod.DAILY -> strings.daily
            ReportPeriod.WEEKLY -> strings.weekly
            ReportPeriod.MONTHLY -> strings.monthly
        }
        val dateFormatted = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date())

        val sb = StringBuilder()
        sb.append("📊 ${profile.businessName} - $periodName Report\n")
        sb.append("Date: $dateFormatted\n")
        sb.append("------------------------------------------\n\n")

        sb.append("💰 FINANCIAL OVERVIEW:\n")
        sb.append("• ${strings.totalSales}: ${formatCurrency(report.totalSales, curr)}\n")
        sb.append("• ${strings.totalExpenses}: ${formatCurrency(report.totalExpenses, curr)}\n")
        val profitEmoji = if (report.totalProfit >= 0) "✅" else "⚠️"
        sb.append("• ${strings.netProfit}: ${formatCurrency(report.totalProfit, curr)} $profitEmoji\n")
        sb.append("• ${strings.productsSold}: ${report.totalUnitsSold} units\n")
        sb.append("• Remaining Inventory: ${report.totalStockRemaining} units\n\n")

        if (report.topSellingProducts.isNotEmpty()) {
            sb.append("🏆 TOP PERFORMING PRODUCTS:\n")
            report.topSellingProducts.forEachIndexed { i, (name, qty) ->
                sb.append("${i + 1}. $name: $qty sold\n")
            }
            sb.append("\n")
        }

        sb.append("📝 ACTIVITY SUMMARY:\n")
        sb.append("• Completed Sales: ${report.recentTransactions.size}\n")
        sb.append("• Operating Expenses: ${report.recentExpenses.size}\n\n")

        sb.append("📎 The full detailed spreadsheet report is attached as an Excel (.xlsx) file.\n\n")
        sb.append("Generated with TinyBiz Manager.")
        return sb.toString()
    }

    /**
     * Launches Android email client with subject, body, and attached Excel (.xlsx) file.
     */
    fun sendEmail(
        context: Context,
        recipient: String,
        subject: String,
        body: String,
        excelFile: File?
    ) {
        try {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                if (recipient.isNotBlank()) {
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient.trim()))
                }
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)

                if (excelFile != null && excelFile.exists()) {
                    val uri: Uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        excelFile
                    )
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }

            val chooser = Intent.createChooser(intent, "Send Business Report via Email")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Error opening email app: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shares the Excel file via Android Sharesheet.
     */
    fun shareExcelFile(
        context: Context,
        excelFile: File,
        subject: String
    ) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                excelFile
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(intent, "Share Excel Report (.xlsx)")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            Toast.makeText(context, "Error sharing file: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Opens the Excel file directly in Excel / Google Sheets / Office.
     */
    fun openExcelFile(context: Context, excelFile: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                excelFile
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Open Excel Report"))
        } catch (e: Exception) {
            Toast.makeText(context, "No app found to open Excel (.xlsx) file.", Toast.LENGTH_SHORT).show()
        }
    }
}
