package net.plumbing.msgbus.threads.utils;

import net.plumbing.msg    void setUp() {
        xmlMsgInput = new StringBuilder();
        xmlMsgClear = new StringBuilder();
        xmlMsgConfirmation = new StringBuilder();
        xmlRequestMethod = new StringBuilder();
        msgReason = new StringBuilder();

        // Инициализируем поля объекта напрямую, так как они публичные
        mockMessageDetails.XML_MsgInput = xmlMsgInput.toString();
        mockMessageDetails.XML_MsgClear = xmlMsgClear;
        mockMessageDetails.XML_MsgConfirmation = xmlMsgConfirmation;
        mockMessageDetails.XML_Request_Method = xmlRequestMethod;
        mockMessageDetails.MsgReason = msgReason;MLchars;
import net.plumbing.msgbus.model.MessageDetails;
import net.plumbing.msgbus.model.MessageTemplate;
import net.plumbing.msgbus.model.MessageTemplateVO;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import net.sf.saxon.s9api.Xslt30Transformer;
import net.sf.saxon.s9api.XsltCompiler;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.JDOMParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class XMLutilsComplexMethodsTest {

    @Mock
    private Logger mockLogger;

    private MessageDetails messageDetails; // Используем реальный объект вместо мока

    @Mock
    private Document mockDocument;

    @Mock
    private Element mockRootElement;

    @Mock
    private Element mockBodyElement;

    @Mock
    private Element mockHeaderElement;

    @Mock
    private Element mockRequestMethod;

    @Mock
    private Element mockContextElement;

    private StringBuilder xmlMsgInput;
    private StringBuilder xmlMsgClear;
    private StringBuilder xmlMsgConfirmation;
    private StringBuilder xmlRequestMethod;
    private StringBuilder msgReason;

    private String validSoapXml;
    private String invalidSoapXml;

    @BeforeEach
    void setUp() {
        xmlMsgInput = new StringBuilder();
        xmlMsgClear = new StringBuilder();
        xmlMsgConfirmation = new StringBuilder();
        xmlRequestMethod = new StringBuilder();
        msgReason = new StringBuilder();

        when(mockMessageDetails.XML_MsgInput).thenReturn(xmlMsgInput);
        when(mockMessageDetails.XML_MsgClear).thenReturn(xmlMsgClear);
        when(mockMessageDetails.XML_MsgConfirmation).thenReturn(xmlMsgConfirmation);
        when(mockMessageDetails.XML_Request_Method).thenReturn(xmlRequestMethod);
        when(mockMessageDetails.MsgReason).thenReturn(msgReason);

        validSoapXml = """
            <env:Envelope xmlns:env="http://schemas.xmlsoap.org/soap/envelope/">
                <env:Header>
                    <Context>
                        <EventInitiator>TEST_SYSTEM</EventInitiator>
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

        invalidSoapXml = "<invalid><unclosed>";

        xmlMsgInput.append(validSoapXml);
    }

    @Test
    @DisplayName("Test makeClearRequest with valid SOAP XML - no XSLT transformation")
    void testMakeClearRequest_ValidSoapXML_NoXSLT() throws Exception {
        // Given
        int messageTemplateVOkey = -1; // No template
        StringBuilder convXMLuseXSLTerr = new StringBuilder();
        boolean isDebugged = false;

        // Setup mock document structure
        setupMockDocumentStructure();

        // When
        String result = XMLutils.makeClearRequest(
                mockMessageDetails,
                messageTemplateVOkey,
                convXMLuseXSLTerr,
                isDebugged,
                mockLogger
        );

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(xmlMsgClear.toString(), result);
        verify(mockMessageDetails).Input_Clear_XMLDocument = any(Document.class);
        verify(mockMessageDetails).Request_Method = mockRequestMethod;
        verify(mockMessageDetails).Input_Header_Context = mockContextElement;
    }

    @Test
    @DisplayName("Test makeClearRequest with invalid XML input")
    void testMakeClearRequest_InvalidXML() {
        // Given
        xmlMsgInput.setLength(0);
        xmlMsgInput.append(invalidSoapXml);
        
        StringBuilder convXMLuseXSLTerr = new StringBuilder();
        boolean isDebugged = false;

        // When & Then
        JDOMParseException exception = assertThrows(
                JDOMParseException.class,
                () -> XMLutils.makeClearRequest(
                        mockMessageDetails,
                        -1,
                        convXMLuseXSLTerr,
                        isDebugged,
                        mockLogger
                )
        );

        assertTrue(exception.getMessage().contains("makeClearRequest"));
        verify(mockLogger).error(contains("makeClearRequest: documentBuilder.build"));
    }

    @Test
    @DisplayName("Test makeClearRequest with XSLT transformation")
    void testMakeClearRequest_WithXSLTTransformation() throws Exception {
        // Given
        int messageTemplateVOkey = 0;
        StringBuilder convXMLuseXSLTerr = new StringBuilder();
        boolean isDebugged = true;

        // Mock MessageTemplate
        try (MockedStatic<MessageTemplate> mockedTemplate = mockStatic(MessageTemplate.class)) {
            MessageTemplateVO mockTemplateVO = mock(MessageTemplateVO.class);
            when(mockTemplateVO.getEnvelopeInXSLT()).thenReturn("test xslt");
            when(mockTemplateVO.getEnvelopeInXSLT_processor()).thenReturn(mock(Processor.class));
            when(mockTemplateVO.getEnvelopeInXSLT_xsltCompiler()).thenReturn(mock(XsltCompiler.class));
            when(mockTemplateVO.getEnvelopeInXSLT_xslt30Transformer()).thenReturn(mock(Xslt30Transformer.class));

            List<MessageTemplateVO> templateList = new ArrayList<>();
            templateList.add(mockTemplateVO);
            mockedTemplate.when(() -> MessageTemplate.AllMessageTemplate).thenReturn(templateList);

            // Mock XMLutils.ConvXMLuseXSLT30 method
            try (MockedStatic<XMLutils> mockedXMLutils = mockStatic(XMLutils.class, CALLS_REAL_METHODS)) {
                mockedXMLutils.when(() -> XMLutils.ConvXMLuseXSLT30(
                        anyLong(), anyString(), any(), any(), any(), anyString(), any(), any(), any(), anyBoolean()
                )).thenReturn("<transformed>result</transformed>");

                setupMockDocumentStructure();

                // When
                String result = XMLutils.makeClearRequest(
                        mockMessageDetails,
                        messageTemplateVOkey,
                        convXMLuseXSLTerr,
                        isDebugged,
                        mockLogger
                );

                // Then
                assertNotNull(result);
                verify(mockLogger).info(contains("ProcessInputMessage(makeClearRequest): после XSLT"));
            }
        }
    }

    @Test
    @DisplayName("Test makeClearRequest with XSLT returning empty result")
    void testMakeClearRequest_XSLTEmptyResult() throws Exception {
        // Given
        int messageTemplateVOkey = 0;
        StringBuilder convXMLuseXSLTerr = new StringBuilder();
        boolean isDebugged = true;

        // Mock MessageTemplate
        try (MockedStatic<MessageTemplate> mockedTemplate = mockStatic(MessageTemplate.class)) {
            MessageTemplateVO mockTemplateVO = mock(MessageTemplateVO.class);
            when(mockTemplateVO.getEnvelopeInXSLT()).thenReturn("test xslt");
            when(mockTemplateVO.getEnvelopeInXSLT_processor()).thenReturn(mock(Processor.class));
            when(mockTemplateVO.getEnvelopeInXSLT_xsltCompiler()).thenReturn(mock(XsltCompiler.class));
            when(mockTemplateVO.getEnvelopeInXSLT_xslt30Transformer()).thenReturn(mock(Xslt30Transformer.class));

            List<MessageTemplateVO> templateList = new ArrayList<>();
            templateList.add(mockTemplateVO);
            mockedTemplate.when(() -> MessageTemplate.AllMessageTemplate).thenReturn(templateList);

            // Mock XMLutils.ConvXMLuseXSLT30 to return empty result
            try (MockedStatic<XMLutils> mockedXMLutils = mockStatic(XMLutils.class, CALLS_REAL_METHODS)) {
                mockedXMLutils.when(() -> XMLutils.ConvXMLuseXSLT30(
                        anyLong(), anyString(), any(), any(), any(), anyString(), any(), any(), any(), anyBoolean()
                )).thenReturn(XMLchars.nanXSLT_Result);

                setupMockDocumentStructure();

                // When & Then
                SaxonApiException exception = assertThrows(
                        SaxonApiException.class,
                        () -> XMLutils.makeClearRequest(
                                mockMessageDetails,
                                messageTemplateVOkey,
                                convXMLuseXSLTerr,
                                isDebugged,
                                mockLogger
                        )
                );

                assertTrue(exception.getMessage().contains("получен пустой XML"));
                verify(mockLogger).error(contains("получен пустой XML"));
            }
        }
    }

    @Test
    @DisplayName("Test makeClearRequest - SOAP envelope not found")
    void testMakeClearRequest_NoSoapEnvelope() throws Exception {
        // Given
        xmlMsgInput.setLength(0);
        xmlMsgInput.append("<NotEnvelope><Body></Body></NotEnvelope>");

        StringBuilder convXMLuseXSLTerr = new StringBuilder();
        boolean isDebugged = false;

        // When & Then
        XPathExpressionException exception = assertThrows(
                XPathExpressionException.class,
                () -> XMLutils.makeClearRequest(
                        mockMessageDetails,
                        -1,
                        convXMLuseXSLTerr,
                        isDebugged,
                        mockLogger
                )
        );

        assertTrue(exception.getMessage().contains("не найден RootElement=" + XMLchars.Envelope));
    }

    @Test
    @DisplayName("Test makeClearRequest - SOAP body not found")
    void testMakeClearRequest_NoSoapBody() throws Exception {
        // Given
        xmlMsgInput.setLength(0);
        xmlMsgInput.append("<env:Envelope xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\"><env:Header></env:Header></env:Envelope>");

        // Mock document structure without body
        when(mockRootElement.getName()).thenReturn(XMLchars.Envelope);
        when(mockRootElement.getChildren()).thenReturn(List.of(mockHeaderElement));
        when(mockHeaderElement.getName()).thenReturn(XMLchars.Header);
        when(mockMessageDetails.Input_Clear_XMLDocument).thenReturn(mockDocument);
        when(mockDocument.getRootElement()).thenReturn(mockRootElement);

        StringBuilder convXMLuseXSLTerr = new StringBuilder();
        boolean isDebugged = false;

        // When & Then
        XPathExpressionException exception = assertThrows(
                XPathExpressionException.class,
                () -> XMLutils.makeClearRequest(
                        mockMessageDetails,
                        -1,
                        convXMLuseXSLTerr,
                        isDebugged,
                        mockLogger
                )
        );

        assertTrue(exception.getMessage().contains("не найден Element=" + XMLchars.Body));
        verify(mockLogger).error(contains("documentBuilder.build"));
    }

    @Test
    @DisplayName("Test makeMessageDetailsRestApi with valid input")
    void testMakeMessageDetailsRestApi_ValidInput() throws Exception {
        // Given
        String pEnvelopeInXSLT = null; // No XSLT
        int messageTemplateVOkey = -1;
        StringBuilder convXMLuseXSLTerr = new StringBuilder();
        boolean isDebugged = false;

        setupMockDocumentStructure();

        // When
        String result = XMLutils.makeMessageDetailsRestApi(
                mockMessageDetails,
                pEnvelopeInXSLT,
                messageTemplateVOkey,
                convXMLuseXSLTerr,
                isDebugged,
                mockLogger
        );

        // Then
        assertNotNull(result);
        assertEquals(xmlMsgClear.toString(), result);
        verify(mockMessageDetails).Input_Clear_XMLDocument = any(Document.class);
        verify(mockMessageDetails).Request_Method = mockRequestMethod;
        assertNull(mockMessageDetails.Input_Header_Context); // REST API doesn't use header context
    }

    @Test
    @DisplayName("Test makeMessageDetailsRestApi with XSLT transformation")
    void testMakeMessageDetailsRestApi_WithXSLT() throws Exception {
        // Given
        String pEnvelopeInXSLT = "test xslt";
        int messageTemplateVOkey = 0;
        StringBuilder convXMLuseXSLTerr = new StringBuilder();
        boolean isDebugged = true;

        // Mock MessageTemplate
        try (MockedStatic<MessageTemplate> mockedTemplate = mockStatic(MessageTemplate.class)) {
            MessageTemplateVO mockTemplateVO = mock(MessageTemplateVO.class);
            when(mockTemplateVO.getEnvelopeInXSLT_processor()).thenReturn(mock(Processor.class));
            when(mockTemplateVO.getEnvelopeInXSLT_xsltCompiler()).thenReturn(mock(XsltCompiler.class));
            when(mockTemplateVO.getEnvelopeInXSLT_xslt30Transformer()).thenReturn(mock(Xslt30Transformer.class));

            List<MessageTemplateVO> templateList = new ArrayList<>();
            templateList.add(mockTemplateVO);
            mockedTemplate.when(() -> MessageTemplate.AllMessageTemplate).thenReturn(templateList);

            // Mock XMLutils.ConvXMLuseXSLT30 method
            try (MockedStatic<XMLutils> mockedXMLutils = mockStatic(XMLutils.class, CALLS_REAL_METHODS)) {
                mockedXMLutils.when(() -> XMLutils.ConvXMLuseXSLT30(
                        anyLong(), anyString(), any(), any(), any(), anyString(), any(), any(), any(), anyBoolean()
                )).thenReturn("<transformed>result</transformed>");

                setupMockDocumentStructure();

                // When
                String result = XMLutils.makeMessageDetailsRestApi(
                        mockMessageDetails,
                        pEnvelopeInXSLT,
                        messageTemplateVOkey,
                        convXMLuseXSLTerr,
                        isDebugged,
                        mockLogger
                );

                // Then
                assertNotNull(result);
                verify(mockLogger).info(contains("ProcessInputMessage(makeClearRequest): после XSLT"));
            }
        }
    }

    @Test
    @DisplayName("Test makeMessageDetailsRestApi with XSLT returning empty result")
    void testMakeMessageDetailsRestApi_XSLTEmptyResult() throws Exception {
        // Given
        String pEnvelopeInXSLT = "test xslt";
        int messageTemplateVOkey = 0;
        StringBuilder convXMLuseXSLTerr = new StringBuilder();
        boolean isDebugged = false;

        // Mock MessageTemplate
        try (MockedStatic<MessageTemplate> mockedTemplate = mockStatic(MessageTemplate.class)) {
            MessageTemplateVO mockTemplateVO = mock(MessageTemplateVO.class);
            when(mockTemplateVO.getEnvelopeInXSLT_processor()).thenReturn(mock(Processor.class));
            when(mockTemplateVO.getEnvelopeInXSLT_xsltCompiler()).thenReturn(mock(XsltCompiler.class));
            when(mockTemplateVO.getEnvelopeInXSLT_xslt30Transformer()).thenReturn(mock(Xslt30Transformer.class));

            List<MessageTemplateVO> templateList = new ArrayList<>();
            templateList.add(mockTemplateVO);
            mockedTemplate.when(() -> MessageTemplate.AllMessageTemplate).thenReturn(templateList);

            // Mock XMLutils.ConvXMLuseXSLT30 to return empty result
            try (MockedStatic<XMLutils> mockedXMLutils = mockStatic(XMLutils.class, CALLS_REAL_METHODS)) {
                mockedXMLutils.when(() -> XMLutils.ConvXMLuseXSLT30(
                        anyLong(), anyString(), any(), any(), any(), anyString(), any(), any(), any(), anyBoolean()
                )).thenReturn(XMLchars.nanXSLT_Result);

                // When & Then
                SaxonApiException exception = assertThrows(
                        SaxonApiException.class,
                        () -> XMLutils.makeMessageDetailsRestApi(
                                mockMessageDetails,
                                pEnvelopeInXSLT,
                                messageTemplateVOkey,
                                convXMLuseXSLTerr,
                                isDebugged,
                                mockLogger
                        )
                );

                assertTrue(exception.getMessage().contains("получен пустой XML"));
            }
        }
    }

    private void setupMockDocumentStructure() throws JDOMException, IOException {
        // Mock the document parsing behavior
        when(mockRootElement.getName()).thenReturn(XMLchars.Envelope);
        when(mockRootElement.getChildren()).thenReturn(List.of(mockHeaderElement, mockBodyElement));

        when(mockHeaderElement.getName()).thenReturn(XMLchars.Header);
        when(mockHeaderElement.getChild("Context")).thenReturn(mockContextElement);

        when(mockBodyElement.getName()).thenReturn(XMLchars.Body);
        when(mockBodyElement.getChildren()).thenReturn(List.of(mockRequestMethod));

        when(mockRequestMethod.getName()).thenReturn("TestMethod");

        when(mockMessageDetails.Input_Clear_XMLDocument).thenReturn(mockDocument);
        when(mockDocument.getRootElement()).thenReturn(mockRootElement);
    }
}
