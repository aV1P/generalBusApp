package net.plumbing.msgbus.threads.utils;

import net.plumbing.msgbus.common.XMLchars;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for XMLutils class that test actual functionality
 * without extensive mocking.
 */
class XMLutilsIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(XMLutilsIntegrationTest.class);

    @Test
    @DisplayName("Test TestXMLByXSD with valid XML and XSD")
    void testTestXMLByXSD_ValidXMLAndXSD() {
        // Given
        String validXML = """
            <?xml version="1.0" encoding="UTF-8"?>
            <person>
                <name>John Doe</name>
                <age>30</age>
            </person>
            """;

        String validXSD = """
            <?xml version="1.0" encoding="UTF-8"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                <xs:element name="person">
                    <xs:complexType>
                        <xs:sequence>
                            <xs:element name="name" type="xs:string"/>
                            <xs:element name="age" type="xs:int"/>
                        </xs:sequence>
                    </xs:complexType>
                </xs:element>
            </xs:schema>
            """;

        StringBuilder msgResult = new StringBuilder();

        // When
        boolean result = XMLutils.TestXMLByXSD(123L, validXML, validXSD, msgResult, logger);

        // Then
        assertTrue(result, "Valid XML should pass XSD validation");
        assertEquals(0, msgResult.length(), "No error message should be set for valid XML");
    }

    @Test
    @DisplayName("Test TestXMLByXSD with invalid XML against XSD")
    void testTestXMLByXSD_InvalidXMLAgainstXSD() {
        // Given
        String invalidXML = """
            <?xml version="1.0" encoding="UTF-8"?>
            <person>
                <name>John Doe</name>
                <invalidElement>Invalid</invalidElement>
            </person>
            """;

        String validXSD = """
            <?xml version="1.0" encoding="UTF-8"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                <xs:element name="person">
                    <xs:complexType>
                        <xs:sequence>
                            <xs:element name="name" type="xs:string"/>
                            <xs:element name="age" type="xs:int"/>
                        </xs:sequence>
                    </xs:complexType>
                </xs:element>
            </xs:schema>
            """;

        StringBuilder msgResult = new StringBuilder();

        // When
        boolean result = XMLutils.TestXMLByXSD(123L, invalidXML, validXSD, msgResult, logger);

        // Then
        assertFalse(result, "Invalid XML should fail XSD validation");
        assertTrue(msgResult.length() > 0, "Error message should be set for invalid XML");
        assertTrue(msgResult.toString().contains("TestXMLByXSD"), "Error message should contain method name");
    }

    @Test
    @DisplayName("Test TestXMLByXSD with malformed XML")
    void testTestXMLByXSD_MalformedXML() {
        // Given
        String malformedXML = "<person><name>John<unclosed>";

        String validXSD = """
            <?xml version="1.0" encoding="UTF-8"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                <xs:element name="person" type="xs:string"/>
            </xs:schema>
            """;

        StringBuilder msgResult = new StringBuilder();

        // When
        boolean result = XMLutils.TestXMLByXSD(123L, malformedXML, validXSD, msgResult, logger);

        // Then
        assertFalse(result, "Malformed XML should fail validation");
        assertTrue(msgResult.length() > 0, "Error message should be set for malformed XML");
        assertTrue(msgResult.toString().contains("TestXMLByXSD"), "Error message should contain method name");
    }

    @Test
    @DisplayName("Test TestXMLByXSD with malformed XSD")
    void testTestXMLByXSD_MalformedXSD() {
        // Given
        String validXML = """
            <?xml version="1.0" encoding="UTF-8"?>
            <person>John Doe</person>
            """;

        String malformedXSD = "<xs:schema><unclosed>";

        StringBuilder msgResult = new StringBuilder();

        // When
        boolean result = XMLutils.TestXMLByXSD(123L, validXML, malformedXSD, msgResult, logger);

        // Then
        assertFalse(result, "Malformed XSD should cause validation to fail");
        assertTrue(msgResult.length() > 0, "Error message should be set for malformed XSD");
        assertTrue(msgResult.toString().contains("TestXMLByXSD"), "Error message should contain method name");
    }

    @Test
    @DisplayName("Test ConvXMLuseXSLT30 parameter validation")
    void testConvXMLuseXSLT30_ParameterValidation() throws Exception {
        // Given
        StringBuilder msgResult = new StringBuilder();
        StringBuilder convXMLuseXSLTerr = new StringBuilder();

        // Test with null XML data
        String result1 = XMLutils.ConvXMLuseXSLT30(
                123L,
                null, // null XML data
                null,
                null,
                null,
                "test xslt",
                msgResult,
                convXMLuseXSLTerr,
                logger,
                false
        );

        assertEquals(XMLchars.EmptyXSLT_Result, result1, "Should return empty result for null XML");

        // Test with empty XML data
        convXMLuseXSLTerr.setLength(0);
        String result2 = XMLutils.ConvXMLuseXSLT30(
                123L,
                "", // empty XML data
                null,
                null,
                null,
                "test xslt",
                msgResult,
                convXMLuseXSLTerr,
                logger,
                false
        );

        assertEquals(XMLchars.EmptyXSLT_Result, result2, "Should return empty result for empty XML");

        // Test with short XML data
        convXMLuseXSLTerr.setLength(0);
        String result3 = XMLutils.ConvXMLuseXSLT30(
                123L,
                "x", // very short XML data
                null,
                null,
                null,
                "test xslt",
                msgResult,
                convXMLuseXSLTerr,
                logger,
                false
        );

        assertEquals(XMLchars.EmptyXSLT_Result, result3, "Should return empty result for short XML");
    }

    @Test
    @DisplayName("Test constants from XMLchars class")
    void testXMLcharsConstants() {
        // Verify that the constants we use in tests are properly defined
        assertNotNull(XMLchars.TagContext, "TagContext should be defined");
        assertNotNull(XMLchars.TagEventInitiator, "TagEventInitiator should be defined");
        assertNotNull(XMLchars.TagEventSource, "TagEventSource should be defined");
        assertNotNull(XMLchars.TagEventOperationId, "TagEventOperationId should be defined");
        assertNotNull(XMLchars.TagEventKey, "TagEventKey should be defined");
        assertNotNull(XMLchars.Envelope, "Envelope should be defined");
        assertNotNull(XMLchars.Body, "Body should be defined");
        assertNotNull(XMLchars.Header, "Header should be defined");
        assertNotNull(XMLchars.EmptyXSLT_Result, "EmptyXSLT_Result should be defined");
        assertNotNull(XMLchars.nanXSLT_Result, "nanXSLT_Result should be defined");
        assertNotNull(XMLchars.OpenTag, "OpenTag should be defined");
        assertNotNull(XMLchars.CloseTag, "CloseTag should be defined");
        assertNotNull(XMLchars.EndTag, "EndTag should be defined");

        // Verify values
        assertEquals("Context", XMLchars.TagContext);
        assertEquals("Envelope", XMLchars.Envelope);
        assertEquals("Body", XMLchars.Body);
        assertEquals("Header", XMLchars.Header);
        assertEquals("<", XMLchars.OpenTag);
        assertEquals(">", XMLchars.CloseTag);
        assertEquals("/", XMLchars.EndTag);
    }
}
