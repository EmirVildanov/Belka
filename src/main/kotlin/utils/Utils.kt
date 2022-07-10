package utils

import com.google.common.io.Resources
import java.io.InputStream

fun getResourcesFile(fileName: String): InputStream? {
    return Resources.getResource(fileName).openStream()
}