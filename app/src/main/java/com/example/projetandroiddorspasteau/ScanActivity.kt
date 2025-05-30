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

    // ActivityResultLauncher pour gérer le résultat du scan
    private lateinit var barcodeLauncher: ActivityResultLauncher<Intent>

    companion object {
        const val SCANNED_PRODUCT_ID = "scanned_product_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pas besoin de layout XML pour cette activité, elle lance directement le scanner

        barcodeLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val intentResult: IntentResult? = IntentIntegrator.parseActivityResult(result.resultCode, result.data)
            if (intentResult != null) {
                if (intentResult.contents == null) {
                    Toast.makeText(this, "Scan annulé", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    val returnIntent = Intent()
                    returnIntent.putExtra(SCANNED_PRODUCT_ID, intentResult.contents)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }
            } else {
                // Ceci ne devrait pas arriver avec zxing-android-embedded
                super.onActivityResult(result.resultCode, result.resultCode, result.data)
                finish()
            }
        }

        // Lancer le scan dès que l'activité est créée
        startScan()
    }

    private fun startScan() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE) // Scanner uniquement les QRCodes
        integrator.setPrompt("Scannez un QRCode produit") // Message affiché à l'utilisateur
        integrator.setCameraId(0)  // Utiliser la caméra arrière
        integrator.setBarcodeImageEnabled(false) // Ne pas sauvegarder l'image du code-barres
        integrator.setOrientationLocked(true) // Permettre la rotation
        // Lancer l'activité de scan fournie par la bibliothèque
        barcodeLauncher.launch(integrator.createScanIntent())
    }
}