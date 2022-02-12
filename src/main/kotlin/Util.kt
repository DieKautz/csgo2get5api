import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.FileWriter

val csvMapper = CsvMapper().apply {
    registerModule(
        KotlinModule.Builder().build()
    )
    disable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
    configure(CsvParser.Feature.FAIL_ON_MISSING_COLUMNS, true)
}

inline fun <reified T> writeCsvFile(data: Collection<T>, fileName: String) {
    FileWriter(fileName).use { writer ->
        csvMapper.writer(csvMapper.schemaFor(T::class.java))
            .writeValues(writer)
            .writeAll(data)
            .close()
    }
}