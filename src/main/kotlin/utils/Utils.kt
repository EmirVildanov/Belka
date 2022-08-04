package utils

import com.google.common.io.Resources
import java.io.InputStream
import java.util.*

object Utils {
    private const val RATE_MIN_VALUE = 1
    private const val RATE_MAX_VALUE = 10
    private const val TEXT_FEEDBACK_SYMBOLS_BOUND = 150

    private fun getResourcesFile(fileName: String): InputStream? {
        return Resources.getResource(fileName).openStream()
    }

    fun getProperty(fileName: String, propertyName: String): String {
        val prop = Properties()
        prop.load(getResourcesFile(fileName))
        return prop.getProperty(propertyName)
    }

    fun checkRateInBounds(rate: Int) {

    }

    fun checkTextInBounds(rate: Int) {

    }
}