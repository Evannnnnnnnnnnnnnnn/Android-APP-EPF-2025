@file:Suppress("DEPRECATION")

package com.example.projetandroiddorspasteau

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult

class ScanActivity : AppCompatActivity() {

    private lateinit var barcodeLauncher: ActivityResultLauncher<Intent>

    companion object {
        const val SCANNED_PRODUCT_ID = "scanned_product_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        barcodeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intentResult: IntentResult? = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
            if (intentResult != null) {
                if (intentResult.contents == null) {
                    finish()
                } else {
                    val returnIntent = Intent()
                    returnIntent.putExtra(SCANNED_PRODUCT_ID, intentResult.contents)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
            } else {
                super.onActivityResult(result.resultCode, result.resultCode, result.data)
                finish()
            }
        }

        startScan()
    }

    private fun startScan() {
        @Suppress("DEPRECATION")
        val integrator = IntentIntegrator(this)
        @Suppress("DEPRECATION")
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scannez un QRCode produit")
        integrator.setCameraId(0)
        integrator.setBarcodeImageEnabled(false)
        integrator.setOrientationLocked(false)
        barcodeLauncher.launch(integrator.createScanIntent())
    }
}