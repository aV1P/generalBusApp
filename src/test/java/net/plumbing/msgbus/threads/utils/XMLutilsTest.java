package net.plumbing.msgbus.threads.utils;

import net.plumbing.msgbus.common.XMLchars;
import net.plumbing.msgbus.common.xlstErrorListener;
import net.plumbing.msgbus.model.*;
import net.sf.saxon.s9api.*;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.JDOMParseException;
import org.jdom2.input.SAXBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.xml.sax.SAXParseException;

import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class XMLutilsTest {

    @Mock
    private Logger mockLogger;

    @Mock
    private MessageQueueVO mockMessageQueueVO;

    @Mock
    private MessageDetails mockMessageDetails;

    @Mock
    private Element mockElement;

    @Mock
    private Element mockHeaderContext;

    @Mock
    private Document mockDocument;

    @Mock
    private Processor mockProcessor;

    @Mock
    private XsltCompiler mockXsltCompiler;

    @Mock
    private Xslt30Transformer mockXslt30Transformer;

    @Mock
    private xlstErrorListener mockXsltErrorListener;

    private String validSoapXml;
    private String validContextXml;
    private String invalidXml;

    @BeforeEach
    void setUp() {
        // Set up valid SOAP XML for testing
        validSoapXml = """
            <env:Envelope xmlns:env="http://schemas.xmlsoap.org/soap/envelope/">
                <env:Header>
                    <Context>
                        <EventInitiator>TEST_SYSTEM</EventInitiator>
                        <Source>SOURCE_SYSTEM</Source>
                        <BusOperationId>123</BusOperationId>
                        <EventKey>456789</EventKey>
                    </Context>
                </env:Header>
                <env:Body>
                    <TestMethod>
                        <param1>value1</param1>
                        <param2>value2</param2>
                    </TestMethod>
                </env:Body>
            </env:Envelope>
            """;

        validContextXml = """
            <Context>
                <EventInitiator>TEST_SYSTEM</EventInitiator>
                <Source>SOURCE_SYSTEM</Source>
                <BusOperationId>123</BusOperationId>
                <EventKey>456789</EventKey>
            </Context>
            """;

        invalidXml = "<invalid><unclosed>";
    }

    @Test
    @DisplayName("Test Soap_XMLDocument2messageQueueVO with valid context element")
    void testSoap_XMLDocument2messageQueueVO_ValidContext() throws Exception {
        // Given
        when(mockHeaderContext.getName()).thenReturn(XMLchars.TagContext);
        when(mockMessageQueueVO.getQueue_Id()).thenReturn(123L);

        // Mock the MessageRepositoryHelper static methods
        try (MockedStatic<MessageRepositoryHelper> mockedHelper = mockStatic(MessageRepositoryHelper.class)) {
            mockedHelper.when(() -> MessageRepositoryHelper.look4MessageDirectionsVO_2_MsgDirection_Cod(anyString(), any()))
                    .thenReturn(0);
            mockedHelper.when(() -> MessageRepositoryHelper.look4MessageTypeVO_2_Perform(anyInt(), any()))
                    .thenReturn(0);

            // Mock static collections
            try (MockedStatic<MessageDirections> mockedDirections = mockStatic(MessageDirections.class);
                 MockedStatic<MessageType> mockedType = mockStatic(MessageType.class)) {

                MessageDirectionsVO mockDirectionVO = mock(MessageDirectionsVO.class);
                when(mockDirectionVO.getMsgDirection_Id()).thenReturn(1);
                when(mockDirectionVO.getSubsys_Cod()).thenReturn("TEST");

                MessageTypeVO mockTypeVO = mock(MessageTypeVO.class);
                when(mockTypeVO.getMsg_Type()).thenReturn("TEST_TYPE");
                when(mockTypeVO.getMsg_Type_own()).thenReturn("TEST_OWN");

                mockedDirections.when(() -> MessageDirections.AllMessageDirections.get(0))
                        .thenReturn(mockDirectionVO);
                mockedType.when(() -> MessageType.AllMessageType.get(0))
                        .thenReturn(mockTypeVO);

                // Mock context element children
                Element mockEventInitiator = mock(Element.class);
                when(mockEventInitiator.getName()).thenReturn(XMLchars.TagEventInitiator);
                when(mockEventInitiator.getText()).thenReturn("TEST_SYSTEM");

                Element mockEventSource = mock(Element.class);
                when(mockEventSource.getName()).thenReturn(XMLchars.TagEventSource);
                when(mockEventSource.getText()).thenReturn("SOURCE_SYSTEM");

                Element mockEventOperationId = mock(Element.class);
                when(mockEventOperationId.getName()).thenReturn(XMLchars.TagEventOperationId);
                when(mockEventOperationId.getText()).thenReturn("123");

                Element mockEventKey = mock(Element.class);
                when(mockEventKey.getName()).thenReturn(XMLchars.TagEventKey);
                when(mockEventKey.getText()).thenReturn("456789");

                when(mockHeaderContext.getChildren()).thenReturn(
                        java.util.Arrays.asList(mockEventInitiator, mockEventSource, mockEventOperationId, mockEventKey)
                );

                // When
                int result = XMLutils.Soap_XMLDocument2messageQueueVO(mockHeaderContext, mockMessageQueueVO, mockLogger);

                // Then
                assertEquals(0, result);
                verify(mockMessageQueueVO).setEventInitiator(1, "TEST");
                verify(mockMessageQueueVO).setMsg_Type("TEST_TYPE");
                verify(mockMessageQueueVO).setMsg_Type_own("TEST_OWN");
                verify(mockMessageQueueVO).setOperation_Id(123);
            }
        }
    }

    @Test
    @DisplayName("Test Soap_XMLDocument2messageQueueVO with null header context")
    void testSoap_XMLDocument2messageQueueVO_NullContext() {
        // When & Then
        XPathExpressionException exception = assertThrows(
                XPathExpressionException.class,
                () -> XMLutils.Soap_XMLDocument2messageQueueVO(null, mockMessageQueueVO, mockLogger)
        );

        assertTrue(exception.getMessage().contains(XMLchars.TagContext));
        verify(mockLogger).error(contains("Header_Context == null"));
    }

    @Test
    @DisplayName("Test Soap_XMLDocument2messageQueueVO with wrong element name")
    void testSoap_XMLDocument2messageQueueVO_WrongElementName() {
        // Given
        when(mockHeaderContext.getName()).thenReturn("WrongElement");

        // When & Then
        XPathExpressionException exception = assertThrows(
                XPathExpressionException.class,
                () -> XMLutils.Soap_XMLDocument2messageQueueVO(mockHeaderContext, mockMessageQueueVO, mockLogger)
        );

        assertTrue(exception.getMessage().contains(XMLchars.TagContext));
        verify(mockLogger).error(contains("не найден Element=" + XMLchars.TagContext));
    }

    @Test
    @DisplayName("Test Soap_HeaderRequest2messageQueueVO with valid XML")
    void testSoap_HeaderRequest2messageQueueVO_ValidXML() throws Exception {
        // Given
        when(mockMessageQueueVO.getQueue_Id()).thenReturn(123L);
        when(mockMessageQueueVO.toSring()).thenReturn("MockedMessageQueueVO");

        try (MockedStatic<MessageRepositoryHelper> mockedHelper = mockStatic(MessageRepositoryHelper.class)) {
            mockedHelper.when(() -> MessageRepositoryHelper.look4MessageDirectionsVO_2_MsgDirection_Cod(anyString(), any()))
                    .thenReturn(0);
            mockedHelper.when(() -> MessageRepositoryHelper.look4MessageTypeVO_2_Perform(anyInt(), any()))
                    .thenReturn(0);

            try (MockedStatic<MessageDirections> mockedDirections = mockStatic(MessageDirections.class);
                 MockedStatic<MessageType> mockedType = mockStatic(MessageType.class)) {

                MessageDirectionsVO mockDirectionVO = mock(MessageDirectionsVO.class);
                when(mockDirectionVO.getMsgDirection_Id()).thenReturn(1);
                when(mockDirectionVO.getSubsys_Cod()).thenReturn("TEST");

                MessageTypeVO mockTypeVO = mock(MessageTypeVO.class);
                when(mockTypeVO.getMsg_Type()).thenReturn("TEST_TYPE");
                when(mockTypeVO.getMsg_Type_own()).thenReturn("TEST_OWN");

                mockedDirections.when(() -> MessageDirections.AllMessageDirections.get(0))
                        .thenReturn(mockDirectionVO);
                mockedType.when(() -> MessageType.AllMessageType.get(0))
                        .thenReturn(mockTypeVO);

                // When
                int result = XMLutils.Soap_HeaderRequest2messageQueueVO(validContextXml, mockMessageQueueVO, mockLogger);

                // Then
                assertEquals(0, result);
                verify(mockLogger).warn(contains("Soap_HeaderRequest2messageQueueVO"));
            }
        }
    }

    @Test
    @DisplayName("Test Soap_HeaderRequest2messageQueueVO with invalid XML")
    void testSoap_HeaderRequest2messageQueueVO_InvalidXML() {
        // Given
        when(mockMessageQueueVO.getQueue_Id()).thenReturn(123L);

        // When & Then
        JDOMParseException exception = assertThrows(
                JDOMParseException.class,
                () -> XMLutils.Soap_HeaderRequest2messageQueueVO(invalidXml, mockMessageQueueVO, mockLogger)
        );

        assertTrue(exception.getMessage().contains("client.post:Soap_HeaderRequest2messageQueueVO"));
        verify(mockLogger).error(eq("[123] Soap_HeaderRequest2messageQueueVO: documentBuilder.build ({})fault"), eq(invalidXml));
    }

    @Test
    @DisplayName("Test SoapBody2XML_String method")
    void testSoapBody2XML_String() {
        // Given
        StringBuilder xmlMsgClear = new StringBuilder();
        when(mockMessageDetails.XML_MsgClear).thenReturn(xmlMsgClear);

        Element mockChild1 = mock(Element.class);
        when(mockChild1.getName()).thenReturn("TestElement1");

        Element mockChild2 = mock(Element.class);
        when(mockChild2.getName()).thenReturn("TestElement2");

        when(mockElement.getChildren()).thenReturn(java.util.Arrays.asList(mockChild1, mockChild2));

        // Mock the XML_BodyElemets2StringB method behavior
        // Since it's a static method that modifies the StringBuilder, we can't easily mock it
        // but we can test the basic structure

        // When
        int result = XMLutils.SoapBody2XML_String(mockMessageDetails, mockElement, mockLogger);

        // Then
        assertEquals(0, result);
        assertTrue(xmlMsgClear.toString().contains("<TestElement1>"));
        assertTrue(xmlMsgClear.toString().contains("</TestElement1>"));
        assertTrue(xmlMsgClear.toString().contains("<TestElement2>"));
        assertTrue(xmlMsgClear.toString().contains("</TestElement2>"));
    }

    @Test
    @DisplayName("Test ConvXMLuseXSLT30 with null transformer")
    void testConvXMLuseXSLT30_NullTransformer() throws Exception {
        // Given
        StringBuilder msgResult = new StringBuilder();
        StringBuilder convXMLuseXSLTerr = new StringBuilder();

        // When
        String result = XMLutils.ConvXMLuseXSLT30(
                123L,
                "test xml",
                mockProcessor,
                mockXsltCompiler,
                null, // null transformer
                "test xslt",
                msgResult,
                convXMLuseXSLTerr,
                mockLogger,
                false
        );

        // Then
        assertEquals(XMLchars.EmptyXSLT_Result, result);
        assertTrue(convXMLuseXSLTerr.toString().contains("xslt30Processor/xslt30Transformer == null"));
        verify(mockLogger).error(eq("[{}] {}"), eq(123L), any());
    }

    @Test
    @DisplayName("Test ConvXMLuseXSLT30 with null XML data")
    void testConvXMLuseXSLT30_NullXMLData() throws Exception {
        // Given
        StringBuilder msgResult = new StringBuilder();
        StringBuilder convXMLuseXSLTerr = new StringBuilder();

        // When
        String result = XMLutils.ConvXMLuseXSLT30(
                123L,
                null, // null XML data
                mockProcessor,
                mockXsltCompiler,
                mockXslt30Transformer,
                "test xslt",
                msgResult,
                convXMLuseXSLTerr,
                mockLogger,
                true // debug mode
        );

        // Then
        assertEquals(XMLchars.EmptyXSLT_Result, result);
        verify(mockLogger).info(eq("[{}] ConvXMLuseXSLT30: length XMLdata 4 transform is null OR  < {}"), 
                               eq(123L), anyInt());
    }

    @Test
    @DisplayName("Test ConvXMLuseXSLT30 with short XML data")
    void testConvXMLuseXSLT30_ShortXMLData() throws Exception {
        // Given
        StringBuilder msgResult = new StringBuilder();
        StringBuilder convXMLuseXSLTerr = new StringBuilder();

        // When
        String result = XMLutils.ConvXMLuseXSLT30(
                123L,
                "x", // very short XML data
                mockProcessor,
                mockXsltCompiler,
                mockXslt30Transformer,
                "test xslt",
                msgResult,
                convXMLuseXSLTerr,
                mockLogger,
                true
        );

        // Then
        assertEquals(XMLchars.EmptyXSLT_Result, result);
    }

    @Test
    @DisplayName("Test ConvXMLuseXSLT30 with successful transformation")
    void testConvXMLuseXSLT30_SuccessfulTransformation() throws Exception {
        // Given
        StringBuilder msgResult = new StringBuilder();
        StringBuilder convXMLuseXSLTerr = new StringBuilder();
        String xmlData = "<test>data</test>";
        String expectedResult = "<transformed>result</transformed>";

        Serializer mockSerializer = mock(Serializer.class);
        when(mockProcessor.newSerializer()).thenReturn(mockSerializer);

        // Mock the transformation to write to the output stream
        doAnswer(invocation -> {
            // Simulate writing the result to the output stream
            return null;
        }).when(mockXslt30Transformer).transform(any(), any());

        // When
        String result = XMLutils.ConvXMLuseXSLT30(
                123L,
                xmlData,
                mockProcessor,
                mockXsltCompiler,
                mockXslt30Transformer,
                "test xslt",
                msgResult,
                convXMLuseXSLTerr,
                mockLogger,
                true
        );

        // Then
        verify(mockProcessor).newSerializer();
        verify(mockSerializer).setOutputProperty(Serializer.Property.METHOD, "xml");
        verify(mockSerializer).setOutputProperty(Serializer.Property.ENCODING, "utf-8");
        verify(mockSerializer).setOutputProperty(Serializer.Property.INDENT, "no");
        verify(mockSerializer).setOutputProperty(Serializer.Property.OMIT_XML_DECLARATION, "yes");
        verify(mockXslt30Transformer).transform(any(), eq(mockSerializer));
    }

    @Test
    @DisplayName("Test ConvXMLuseXSLT30 with SaxonApiException")
    void testConvXMLuseXSLT30_SaxonApiException() throws Exception {
        // Given
        StringBuilder msgResult = new StringBuilder();
        StringBuilder convXMLuseXSLTerr = new StringBuilder();
        String xmlData = "<test>data</test>";

        Serializer mockSerializer = mock(Serializer.class);
        when(mockProcessor.newSerializer()).thenReturn(mockSerializer);

        SaxonApiException testException = new SaxonApiException("Test exception");
        doThrow(testException).when(mockXslt30Transformer).transform(any(), any());

        // When & Then
        SaxonApiException exception = assertThrows(
                SaxonApiException.class,
                () -> XMLutils.ConvXMLuseXSLT30(
                        123L,
                        xmlData,
                        mockProcessor,
                        mockXsltCompiler,
                        mockXslt30Transformer,
                        "test xslt",
                        msgResult,
                        convXMLuseXSLTerr,
                        mockLogger,
                        false
                )
        );

        assertEquals(testException, exception);
        verify(mockLogger).error(eq("[{}] ConvXMLuseXSLT30.Transformer TransformerException: {}"), eq(123L), any());
        verify(mockLogger).error(eq("[{}] ConvXMLuseXSLT30.Transformer.Exception: {}"), eq(123L), any());
    }

    @Test
    @DisplayName("Test TestXMLByXSD with valid XML and XSD")
    void testTestXMLByXSD_ValidXMLAndXSD() {
        // Given
        String validXML = "<?xml version=\"1.0\"?><root>test</root>";
        String validXSD = """
            <?xml version="1.0"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                <xs:element name="root" type="xs:string"/>
            </xs:schema>
            """;
        StringBuilder msgResult = new StringBuilder();

        // When
        boolean result = XMLutils.TestXMLByXSD(123L, validXML, validXSD, msgResult, mockLogger);

        // Then
        assertTrue(result);
        assertEquals(0, msgResult.length()); // No error message should be set
    }

    @Test
    @DisplayName("Test TestXMLByXSD with invalid XML")
    void testTestXMLByXSD_InvalidXML() {
        // Given
        String invalidXML = "<root>unclosed";
        String validXSD = """
            <?xml version="1.0"?>
            <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                <xs:element name="root" type="xs:string"/>
            </xs:schema>
            """;
        StringBuilder msgResult = new StringBuilder();

        // When
        boolean result = XMLutils.TestXMLByXSD(123L, invalidXML, validXSD, msgResult, mockLogger);

        // Then
        assertFalse(result);
        assertTrue(msgResult.length() > 0);
        assertTrue(msgResult.toString().contains("TestXMLByXSD"));
        verify(mockLogger).error(contains("Exception:"));
    }

    @Test
    @DisplayName("Test legasyConvXMLuseXSLT with valid inputs")
    void testLegasyConvXMLuseXSLT_ValidInputs() throws Exception {
        // Given
        String xmlData = "<test>data</test>";
        String xsltData = """
            <?xml version="1.0"?>
            <xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
                <xsl:template match="/test">
                    <transformed><xsl:value-of select="."/></transformed>
                </xsl:template>
            </xsl:stylesheet>
            """;
        StringBuilder msgResult = new StringBuilder();
        StringBuilder convXMLuseXSLTerr = new StringBuilder();

        // When
        String result = XMLutils.legasyConvXMLuseXSLT(
                123L,
                xmlData,
                xsltData,
                msgResult,
                convXMLuseXSLTerr,
                mockXsltErrorListener,
                mockLogger,
                true
        );

        // Then
        assertNotNull(result);
        assertNotEquals(XMLchars.EmptyXSLT_Result, result);
        assertTrue(result.contains("transformed") || result.contains("data"));
    }

    @Test
    @DisplayName("Test makeMessageQueueVO_from_ContextElement with invalid EventKey")
    void testMakeMessageQueueVO_from_ContextElement_InvalidEventKey() {
        // Given
        when(mockHeaderContext.getName()).thenReturn(XMLchars.TagContext);
        when(mockMessageQueueVO.getQueue_Id()).thenReturn(123L);

        Element mockEventKey = mock(Element.class);
        when(mockEventKey.getName()).thenReturn(XMLchars.TagEventKey);
        when(mockEventKey.getText()).thenReturn("invalid_number");

        when(mockHeaderContext.getChildren()).thenReturn(java.util.Arrays.asList(mockEventKey));

        // When & Then
        IOException exception = assertThrows(
                IOException.class,
                () -> XMLutils.makeMessageQueueVO_from_ContextElement(mockHeaderContext, mockMessageQueueVO, mockLogger)
        );

        assertTrue(exception.getMessage().contains("Ошибка при преобразовании элемента SOAP-заголовка сообщения EventKey"));
        verify(mockLogger).error(contains("getClearRequest(makeMessageQueueVO_from_ContextElement):шибка при преобразовании"));
    }
}
