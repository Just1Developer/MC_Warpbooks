package warpbooks.config;

import net.justonedev.mc.warpbooks.WarpBooks;
import net.justonedev.mc.warpbooks.config.Config;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ConfigFixTests {

    @ParameterizedTest
    @CsvSource(textBlock = """
            abc,default
            https://drive.google.com/file?id=abc.zip,https://drive.google.com/file?id=abc.zip
            http://drive.google.com/file?id=abc.zip,https://drive.google.com/file?id=abc.zip
            http://www.dropbox.com/scl/fi/agtebdwngmlp2qrfyk6u1/5Warpbooks.zip?dl=1,https://www.dropbox.com/scl/fi/agtebdwngmlp2qrfyk6u1/5Warpbooks.zip?dl=1
            https://www.dropbox.com/scl/fi/agtebdwngmlp2qrfyk6u1/5Warpbooks.zip?rlkey=abc&st=a5imgznf&dl=0,https://www.dropbox.com/scl/fi/agtebdwngmlp2qrfyk6u1/5Warpbooks.zip?rlkey=abc&st=a5imgznf&dl=1
            https://www.dropbox.com/scl/fi/agtebdwngmlp2qrfyk6u1/5Warpbooks.zip?rlkey=a9brt8t27u5ruesm4l0g62x0x&st=a5imgznf&dl=1,https://www.dropbox.com/scl/fi/agtebdwngmlp2qrfyk6u1/5Warpbooks.zip?rlkey=a9brt8t27u5ruesm4l0g62x0x&st=a5imgznf&dl=1
            """)
    void testUrlSanitization(String url, String expectedOrNull) {
        String expected = expectedOrNull.equals("default") ? WarpBooks.DEFAULT_PACK_URL : expectedOrNull;
        String parsedUrl = Config.fixOrDefaultUrl(url);
        Assertions.assertEquals(expected, parsedUrl);
    }

}
