import java.net.URL
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

tasks.register("checkZip") {
    doLast {
        val commitSha = "569b0bc3c22748609b8ac4bd654ed5955589b986"
        val zipUrl = "https://github.com/cairo91197503-dot/LocalPulse/archive/${commitSha}.zip"
        val zipFile = file("repo.zip")
        
        println("Downloading ${zipUrl}...")
        URL(zipUrl).openStream().use { input ->
            FileOutputStream(zipFile).use { output ->
                input.copyTo(output)
            }
        }
        
        println("Listing contents:")
        ZipInputStream(zipFile.inputStream()).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                if (!entry.isDirectory && entry.name.contains("MainActivity.kt")) {
                    println(entry.name)
                }
                entry = zis.nextEntry
            }
        }
        
        zipFile.delete()
    }
}
