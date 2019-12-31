package pl.droidsonroids.gradle.localization

import groovy.io.FileType
import org.junit.Test
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import org.xmlunit.diff.DefaultNodeMatcher
import org.xmlunit.diff.ElementSelectors

import static org.assertj.core.api.Assertions.assertThat

class ParseXLSXTest extends LocalizationPluginTestBase {

    @Test
    void testParsePercent() {
        def value = "有個99%d"
        def format = "%(?![ds])"
        value = value.replaceAll(format, "%%")
        assertThat(value).isEqualTo("有個99%d")
        value = "有個99%s"
        value = value.replaceAll(format, "%%")
        assertThat(value).isEqualTo("有個99%s")
        value = "有個99%"
        value = value.replaceAll(format, "%%")
        assertThat(value).isEqualTo("有個99%%")
    }

    @Test
    void testXlsxFile() {
        def name = 'valid.xlsx'

        ConfigExtension config = new ConfigExtension()
        config.xlsFileURI = getClass().getResource(name).toString()
        config.allowEmptyTranslations = true
        config.skipInvalidName = true
        config.skipDuplicatedName = true
        config.defaultColumnName = 'EN'
        config.nameColumnName = 'Android'
        config.ignorableColumns.add('WinPhone')
        config.ignorableColumns.add('iOS')
        config.ignorableColumns.add('END')
        config.defaultLocaleQualifier = 'en'
        config.useAllSheets = true

        def resDir = parseTestFile(config)

        resDir.traverse(type: FileType.FILES) {
            def filePath = it.path.replace(resDir.path, '')
            def expectedFileURL = getClass().getResource("parsed_valid_xlsx/$filePath")
            def diff = DiffBuilder.compare(Input.fromFile(it))
                    .withTest(Input.fromURL(expectedFileURL))
                    .ignoreWhitespace()
                    .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes))
                    .checkForSimilar()
                    .build()

            assertThat(diff.hasDifferences()).as('file: %s is different than expected %s', filePath, diff.toString()).isFalse()
        }
    }

    @Test
    void testXlsxFileWithDefaults() {
        def name = 'valid_with_defaults.xlsx'

        ConfigExtension config = new ConfigExtension()
        config.xlsFileURI = getClass().getResource(name).toString()
        config.handleEmptyTranslationsAsDefault = true
        config.allowEmptyTranslations = true
        config.skipInvalidName = true
        config.skipDuplicatedName = true
        config.defaultColumnName = 'EN'
        config.nameColumnName = 'Android'
        config.ignorableColumns.add('WinPhone')
        config.ignorableColumns.add('iOS')
        config.ignorableColumns.add('END')
        config.defaultLocaleQualifier = 'en'
        config.useAllSheets = true

        def resDir = parseTestFile(config)

        resDir.traverse(type: FileType.FILES) {
            def filePath = it.path.replace(resDir.path, '')
            def expectedFileURL = getClass().getResource("parsed_valid_xlsx/$filePath")
            def diff = DiffBuilder.compare(Input.fromFile(it))
                    .withTest(Input.fromURL(expectedFileURL))
                    .ignoreWhitespace()
                    .withNodeMatcher(new DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes))
                    .checkForSimilar()
                    .build()
            assertThat(diff.hasDifferences()).as('file: %s is different than expected %s', filePath, diff.toString()).isFalse()
        }
    }

    @Test
    void testXlsxURI() {
        ConfigExtension config = new ConfigExtension()
        config.xlsFileURI = 'https://docs.google.com/a/droidsonroids.pl/spreadsheets/d/1sfE3Zk_7syHpq3HPKYQ9gRidm1W7c1IjIfdH1R8z9m4/export?format=xlsx'
        parseTestFile(config)
    }
}
