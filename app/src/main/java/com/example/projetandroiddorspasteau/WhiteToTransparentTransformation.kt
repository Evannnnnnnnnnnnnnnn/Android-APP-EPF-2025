package com.example.projetandroiddorspasteau

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class WhiteToTransparentTransformation(private val tolerance: Int = 10) : BitmapTransformation() {

    // A unique identifier for caching
    private val id: String = "com.example.projetandroiddorspasteau.transformations.WhiteToTransparentTransformation.$tolerance"

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(id.toByteArray(Charsets.UTF_8))
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val width = toTransform.width
        val height = toTransform.height

        // Get a ARGB_8888 bitmap from the pool or create a new one
        val config = Bitmap.Config.ARGB_8888
        var transformedBitmap = pool.get(width, height, config)
        if (transformedBitmap == null) {
            transformedBitmap = Bitmap.createBitmap(width, height, config)
        }

        // For JPEGs, the original bitmap might not have an alpha channel.
        // We draw it onto our ARGB_8888 canvas to ensure we can manipulate alpha.
        val canvas = Canvas(transformedBitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        canvas.drawBitmap(toTransform, 0f, 0f, paint)

        // Efficiently get all pixels
        val pixels = IntArray(width * height)
        transformedBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val color = pixels[i]
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)

            // Check if the pixel is "white-ish" within the tolerance
            // (255 - tolerance) to 255 for R, G, and B
            if (red >= (255 - tolerance) && green >= (255 - tolerance) && blue >= (255 - tolerance)) {
                pixels[i] = Color.TRANSPARENT // Make it transparent
            }
        }

        // Set the modified pixels back to the bitmap
        transformedBitmap.setPixels(pixels, 0, width, 0, 0, width, height)

        return transformedBitmap
    }

    override fun equals(other: Any?): Boolean {
        return other is WhiteToTransparentTransformation && other.tolerance == tolerance
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}