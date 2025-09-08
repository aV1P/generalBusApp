package net.plumbing.msgbus.threads.utils;

import net.plumbing.msgbus.common.XMLchars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple test runner to verify XMLutils functionality without needing Maven.
 * This class can be executed directly to verify basic functionality.
 */
public class XMLutilsTestRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(XMLutilsTestRunner.class);
    
    public static void main(String[] args) {
        XMLutilsTestRunner runner = new XMLutilsTestRunner();
        
        System.out.println("=== XMLutils Test Runner ===");
        System.out.println("Running basic tests for XMLutils class...\n");
        
        try {
            runner.testXMLConstants();
            runner.testXMLValidation();
            runner.testXSLTParameterValidation();
            
            System.out.println("\n=== All Tests Completed Successfully! ===");
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void testXMLConstants() {
        System.out.println("1. Testing XML Constants...");
        
        // Test constants existence and values
        assert XMLchars.TagContext != null : "TagContext should not be null";
        assert "Context".equals(XMLchars.TagContext) : "TagContext should be 'Context'";
        
        assert XMLchars.Envelope != null : "Envelope should not be null";
        assert "Envelope".equals(XMLchars.Envelope) : "Envelope should be 'Envelope'";
        
        assert XMLchars.Body != null : "Body should not be null";
        assert "Body".equals(XMLchars.Body) : "Body should be 'Body'";
        
        assert XMLchars.Header != null : "Header should not be null";
        assert "Header".equals(XMLchars.Header) : "Header should be 'Header'";
        
        assert XMLchars.EmptyXSLT_Result != null : "EmptyXSLT_Result should not be null";
        assert XMLchars.nanXSLT_Result != null : "nanXSLT_Result should not be null";
        
        System.out.println("   ✓ XML Constants test passed");
    }
    
    private void testXMLValidation() {
        System.out.println("2. Testing XML Validation...");
        
        // Test valid XML against valid XSD
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
        boolean result = XMLutils.TestXMLByXSD(123L, validXML, validXSD, msgResult, logger);
        
        assert result : "Valid XML should pass XSD validation";
        assert msgResult.length() == 0 : "No error message should be set for valid XML";
        
        System.out.println("   ✓ Valid XML validation test passed");
        
        // Test invalid XML against XSD
        String invalidXML = """
            <?xml version="1.0" encoding="UTF-8"?>
            <person>
                <name>John Doe</name>
                <invalidElement>Invalid</invalidElement>
            </person>
            """;
        
        msgResult.setLength(0);
        result = XMLutils.TestXMLByXSD(123L, invalidXML, validXSD, msgResult, logger);
        
        assert !result : "Invalid XML should fail XSD validation";
        assert msgResult.length() > 0 : "Error message should be set for invalid XML";
        
        System.out.println("   ✓ Invalid XML validation test passed");
        
        // Test malformed XML
        String malformedXML = "<person><name>John<unclosed>";
        
        msgResult.setLength(0);
        result = XMLutils.TestXMLByXSD(123L, malformedXML, validXSD, msgResult, logger);
        
        assert !result : "Malformed XML should fail validation";
        assert msgResult.length() > 0 : "Error message should be set for malformed XML";
        
        System.out.println("   ✓ Malformed XML validation test passed");
    }
    
    private void testXSLTParameterValidation() throws Exception {
        System.out.println("3. Testing XSLT Parameter Validation...");
        
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
        
        assert XMLchars.EmptyXSLT_Result.equals(result1) : "Should return empty result for null XML";
        
        System.out.println("   ✓ Null XML parameter test passed");
        
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
        
        assert XMLchars.EmptyXSLT_Result.equals(result2) : "Should return empty result for empty XML";
        
        System.out.println("   ✓ Empty XML parameter test passed");
        
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
        
        assert XMLchars.EmptyXSLT_Result.equals(result3) : "Should return empty result for short XML";
        
        System.out.println("   ✓ Short XML parameter test passed");
    }
}
