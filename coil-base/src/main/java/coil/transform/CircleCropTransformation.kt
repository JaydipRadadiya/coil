@file:Suppress("unused")

package coil.transform

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import androidx.core.graphics.applyCanvas
import coil.bitmappool.BitmapPool
import kotlin.math.min

/**
 * A [Transformation] that crops an image using a centered circle as the mask.
 */
class CircleCropTransformation : Transformation {

    companion object {
        private val XFERMODE = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    }

    override fun key(): String = CircleCropTransformation::class.java.name

    override suspend fun transform(pool: BitmapPool, input: Bitmap): Bitmap {
        val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply { xfermode = XFERMODE }

        val minSize = min(input.width, input.height)
        val radius = minSize / 2f
        val output = pool.get(minSize, minSize, input.config)
        output.applyCanvas {
            drawCircle(radius, radius, radius, circlePaint)
            drawBitmap(input, 0f, 0f, bitmapPaint)
        }
        pool.put(input)

        return output
    }
}
