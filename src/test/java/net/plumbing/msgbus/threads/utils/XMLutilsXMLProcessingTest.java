package net.plumbing.msgbus.threads.utils;

import net.plumbing.msgbus.common.XMLchars;
import net.plumbing.msgbus.model.*;
import org.jdom2.Attribute;
import org.jdom2.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import javax.xml.xpath.XPathExpressionException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class XMLutilsXMLProcessingTest {

    @Mock
    private Logger mockLogger;

    @Mock
    private MessageDetails mockMessageDetails;

    @Mock
    private Element mockElement;

    private StringBuilder xmlMsgClear;
    private StringBuilder xmlRequestMethod;

    @BeforeEach
    void setUp() {
        xmlMsgClear = new StringBuilder();
        xmlRequestMethod = new StringBuilder();
        when(mockMessageDetails.XML_MsgClear).thenReturn(xmlMsgClear);
        when(mockMessageDetails.XML_Request_Method).thenReturn(xmlRequestMethod);
    }

    @Test
    @DisplayName("Test XML_BodyElemets2StringB with simple elements")
    void testXML_BodyElemets2StringB_SimpleElements() {
        // Given
        Element mockChild1 = createMockElement("param1", "value1", new ArrayList<>());
        Element mockChild2 = createMockElement("param2", "value2", new ArrayList<>());

        when(mockElement.getChildren()).thenReturn(List.of(mockChild1, mockChild2));

        // When
        int result = XMLutils.XML_BodyElemets2StringB(mockMessageDetails, mockElement, mockLogger);

        // Then
        assertEquals(0, result);
        String xmlContent = xmlMsgClear.toString();
        assertTrue(xmlContent.contains("<param1>"));
        assertTrue(xmlContent.contains("value1"));
        assertTrue(xmlContent.contains("</param1>"));
        assertTrue(xmlContent.contains("<param2>"));
        assertTrue(xmlContent.contains("value2"));
        assertTrue(xmlContent.contains("</param2>"));
    }

    @Test
    @DisplayName("Test XML_BodyElemets2StringB with elements containing attributes")
    void testXML_BodyElemets2StringB_ElementsWithAttributes() {
        // Given
        Attribute mockAttr1 = mock(Attribute.class);
        when(mockAttr1.getName()).thenReturn("id");
        when(mockAttr1.getValue()).thenReturn("123");

        Attribute mockAttr2 = mock(Attribute.class);
        when(mockAttr2.getName()).thenReturn("type");
        when(mockAttr2.getValue()).thenReturn("text");

        List<Attribute> attributes = List.of(mockAttr1, mockAttr2);
        Element mockChild = createMockElement("param", "value", attributes);

        when(mockElement.getChildren()).thenReturn(List.of(mockChild));

        // When
        int result = XMLutils.XML_BodyElemets2StringB(mockMessageDetails, mockElement, mockLogger);

        // Then
        assertEquals(0, result);
        String xmlContent = xmlMsgClear.toString();
        assertTrue(xmlContent.contains("<param"));
        assertTrue(xmlContent.contains("id=\"123\""));
        assertTrue(xmlContent.contains("type=\"text\""));
        assertTrue(xmlContent.contains("value"));
        assertTrue(xmlContent.contains("</param>"));
    }

    @Test
    @DisplayName("Test XML_BodyElemets2StringB with empty element")
    void testXML_BodyElemets2StringB_EmptyElement() {
        // Given
        Element mockChild = createMockElement("empty", "", new ArrayList<>());
        when(mockElement.getChildren()).thenReturn(List.of(mockChild));

        // When
        int result = XMLutils.XML_BodyElemets2StringB(mockMessageDetails, mockElement, mockLogger);

        // Then
        assertEquals(0, result);
        String xmlContent = xmlMsgClear.toString();
        assertTrue(xmlContent.contains("<empty>"));
        assertTrue(xmlContent.contains("</empty>"));
        // Should not contain content between tags since it's empty
        assertFalse(xmlContent.contains("><"));
    }

    @Test
    @DisplayName("Test XML_BodyElemets2StringB with nested elements")
    void testXML_BodyElemets2StringB_NestedElements() {
        // Given
        Element mockGrandChild = createMockElement("nested", "nestedValue", new ArrayList<>());
        Element mockChild = createMockElement("parent", "parentValue", new ArrayList<>());
        when(mockChild.getChildren()).thenReturn(List.of(mockGrandChild));

        when(mockElement.getChildren()).thenReturn(List.of(mockChild));

        // When
        int result = XMLutils.XML_BodyElemets2StringB(mockMessageDetails, mockElement, mockLogger);

        // Then
        assertEquals(0, result);
        String xmlContent = xmlMsgClear.toString();
        assertTrue(xmlContent.contains("<parent>"));
        assertTrue(xmlContent.contains("parentValue"));
        assertTrue(xmlContent.contains("<nested>"));
        assertTrue(xmlContent.contains("nestedValue"));
        assertTrue(xmlContent.contains("</nested>"));
        assertTrue(xmlContent.contains("</parent>"));
    }

    @Test
    @DisplayName("Test XML_RequestElemets2StringB with simple elements")
    void testXML_RequestElemets2StringB_SimpleElements() {
        // Given
        Element mockChild1 = createMockElement("param1", "value1", new ArrayList<>());
        Element mockChild2 = createMockElement("param2", "value2", new ArrayList<>());

        when(mockElement.getChildren()).thenReturn(List.of(mockChild1, mockChild2));

        // When
        int result = XMLutils.XML_RequestElemets2StringB(mockMessageDetails, mockElement, mockLogger);

        // Then
        assertEquals(0, result);
        String xmlContent = xmlRequestMethod.toString();
        assertTrue(xmlContent.contains("<param1>"));
        assertTrue(xmlContent.contains("value1"));
        assertTrue(xmlContent.contains("</param1>"));
        assertTrue(xmlContent.contains("<param2>"));
        assertTrue(xmlContent.contains("value2"));
        assertTrue(xmlContent.contains("</param2>"));
    }

    @Test
    @DisplayName("Test XML_RequestElemets2StringB with attributes")
    void testXML_RequestElemets2StringB_WithAttributes() {
        // Given
        Attribute mockAttr = mock(Attribute.class);
        when(mockAttr.getName()).thenReturn("id");
        when(mockAttr.getValue()).thenReturn("test123");

        Element mockChild = createMockElement("param", "value", List.of(mockAttr));
        when(mockElement.getChildren()).thenReturn(List.of(mockChild));

        // When
        int result = XMLutils.XML_RequestElemets2StringB(mockMessageDetails, mockElement, mockLogger);

        // Then
        assertEquals(0, result);
        String xmlContent = xmlRequestMethod.toString();
        assertTrue(xmlContent.contains("<param"));
        assertTrue(xmlContent.contains("id=\"test123\""));
        assertTrue(xmlContent.contains("value"));
        assertTrue(xmlContent.contains("</param>"));
    }

    @Test
    @DisplayName("Test XML_RequestElemets2StringB with special characters")
    void testXML_RequestElemets2StringB_SpecialCharacters() {
        // Given
        Element mockChild = createMockElement("param", "<special>&\"'chars", new ArrayList<>());
        when(mockElement.getChildren()).thenReturn(List.of(mockChild));

        // When
        int result = XMLutils.XML_RequestElemets2StringB(mockMessageDetails, mockElement, mockLogger);

        // Then
        assertEquals(0, result);
        String xmlContent = xmlRequestMethod.toString();
        assertTrue(xmlContent.contains("<param>"));
        // The content should be XML-escaped
        assertTrue(xmlContent.contains("&lt;") || xmlContent.contains("&amp;") || xmlContent.contains("&quot;"));
        assertTrue(xmlContent.contains("</param>"));
    }

    @Test
    @DisplayName("Test makeMessageQueueVO_from_ContextElement with unknown EventInitiator")
    void testMakeMessageQueueVO_from_ContextElement_UnknownEventInitiator() {
        // Given
        MessageQueueVO mockMessageQueueVO = mock(MessageQueueVO.class);
        when(mockMessageQueueVO.getQueue_Id()).thenReturn(123L);

        Element mockContext = mock(Element.class);
        when(mockContext.getName()).thenReturn(XMLchars.TagContext);

        Element mockEventInitiator = mock(Element.class);
        when(mockEventInitiator.getName()).thenReturn(XMLchars.TagEventInitiator);
        when(mockEventInitiator.getText()).thenReturn("UNKNOWN_SYSTEM");

        when(mockContext.getChildren()).thenReturn(List.of(mockEventInitiator));

        try (MockedStatic<MessageRepositoryHelper> mockedHelper = mockStatic(MessageRepositoryHelper.class)) {
            mockedHelper.when(() -> MessageRepositoryHelper.look4MessageDirectionsVO_2_MsgDirection_Cod(eq("UNKNOWN_SYSTEM"), any()))
                    .thenReturn(-1); // Return -1 to indicate unknown system

            // When & Then
            XPathExpressionException exception = assertThrows(
                    XPathExpressionException.class,
                    () -> XMLutils.makeMessageQueueVO_from_ContextElement(mockContext, mockMessageQueueVO, mockLogger)
            );

            assertTrue(exception.getMessage().contains("неизвестый код системы-инициатора"));
            assertTrue(exception.getMessage().contains("UNKNOWN_SYSTEM"));
        }
    }

    @Test
    @DisplayName("Test makeMessageQueueVO_from_ContextElement with unknown EventSource")
    void testMakeMessageQueueVO_from_ContextElement_UnknownEventSource() {
        // Given
        MessageQueueVO mockMessageQueueVO = mock(MessageQueueVO.class);
        when(mockMessageQueueVO.getQueue_Id()).thenReturn(123L);

        Element mockContext = mock(Element.class);
        when(mockContext.getName()).thenReturn(XMLchars.TagContext);

        Element mockEventInitiator = mock(Element.class);
        when(mockEventInitiator.getName()).thenReturn(XMLchars.TagEventInitiator);
        when(mockEventInitiator.getText()).thenReturn("KNOWN_SYSTEM");

        Element mockEventSource = mock(Element.class);
        when(mockEventSource.getName()).thenReturn(XMLchars.TagEventSource);
        when(mockEventSource.getText()).thenReturn("UNKNOWN_SOURCE");

        when(mockContext.getChildren()).thenReturn(List.of(mockEventInitiator, mockEventSource));

        try (MockedStatic<MessageRepositoryHelper> mockedHelper = mockStatic(MessageRepositoryHelper.class)) {
            // Return valid result for initiator
            mockedHelper.when(() -> MessageRepositoryHelper.look4MessageDirectionsVO_2_MsgDirection_Cod(eq("KNOWN_SYSTEM"), any()))
                    .thenReturn(0);
            // Return -1 for unknown source
            mockedHelper.when(() -> MessageRepositoryHelper.look4MessageDirectionsVO_2_MsgDirection_Cod(eq("UNKNOWN_SOURCE"), any()))
                    .thenReturn(-1);

            try (MockedStatic<MessageDirections> mockedDirections = mockStatic(MessageDirections.class)) {
                MessageDirectionsVO mockDirectionVO = mock(MessageDirectionsVO.class);
                when(mockDirectionVO.getMsgDirection_Id()).thenReturn(1);
                when(mockDirectionVO.getSubsys_Cod()).thenReturn("TEST");

                mockedDirections.when(() -> MessageDirections.AllMessageDirections.get(0))
                        .thenReturn(mockDirectionVO);

                // When & Then
                XPathExpressionException exception = assertThrows(
                        XPathExpressionException.class,
                        () -> XMLutils.makeMessageQueueVO_from_ContextElement(mockContext, mockMessageQueueVO, mockLogger)
                );

                assertTrue(exception.getMessage().contains("неизвестый код системы-источника"));
                assertTrue(exception.getMessage().contains("UNKNOWN_SOURCE"));
                verify(mockLogger).error(contains("неизвестый код системы-источника"));
            }
        }
    }

    @Test
    @DisplayName("Test makeMessageQueueVO_from_ContextElement with unknown OperationId")
    void testMakeMessageQueueVO_from_ContextElement_UnknownOperationId() {
        // Given
        MessageQueueVO mockMessageQueueVO = mock(MessageQueueVO.class);
        when(mockMessageQueueVO.getQueue_Id()).thenReturn(123L);

        Element mockContext = mock(Element.class);
        when(mockContext.getName()).thenReturn(XMLchars.TagContext);

        Element mockEventInitiator = mock(Element.class);
        when(mockEventInitiator.getName()).thenReturn(XMLchars.TagEventInitiator);
        when(mockEventInitiator.getText()).thenReturn("KNOWN_SYSTEM");

        Element mockEventSource = mock(Element.class);
        when(mockEventSource.getName()).thenReturn(XMLchars.TagEventSource);
        when(mockEventSource.getText()).thenReturn("KNOWN_SOURCE");

        Element mockEventOperationId = mock(Element.class);
        when(mockEventOperationId.getName()).thenReturn(XMLchars.TagEventOperationId);
        when(mockEventOperationId.getText()).thenReturn("999");

        when(mockContext.getChildren()).thenReturn(List.of(mockEventInitiator, mockEventSource, mockEventOperationId));

        try (MockedStatic<MessageRepositoryHelper> mockedHelper = mockStatic(MessageRepositoryHelper.class)) {
            // Return valid results for initiator and source
            mockedHelper.when(() -> MessageRepositoryHelper.look4MessageDirectionsVO_2_MsgDirection_Cod(anyString(), any()))
                    .thenReturn(0);
            // Return -1 for unknown operation
            mockedHelper.when(() -> MessageRepositoryHelper.look4MessageTypeVO_2_Perform(eq(999), any()))
                    .thenReturn(-1);

            try (MockedStatic<MessageDirections> mockedDirections = mockStatic(MessageDirections.class)) {
                MessageDirectionsVO mockDirectionVO = mock(MessageDirectionsVO.class);
                when(mockDirectionVO.getMsgDirection_Id()).thenReturn(1);
                when(mockDirectionVO.getSubsys_Cod()).thenReturn("TEST");

                mockedDirections.when(() -> MessageDirections.AllMessageDirections.get(0))
                        .thenReturn(mockDirectionVO);

                // When & Then
                XPathExpressionException exception = assertThrows(
                        XPathExpressionException.class,
                        () -> XMLutils.makeMessageQueueVO_from_ContextElement(mockContext, mockMessageQueueVO, mockLogger)
                );

                assertTrue(exception.getMessage().contains("неизвестый № операцмм"));
                assertTrue(exception.getMessage().contains("999"));
                verify(mockLogger).error(contains("неизвестый № операцмм"));
            }
        }
    }

    private Element createMockElement(String name, String text, List<Attribute> attributes) {
        Element mockElement = mock(Element.class);
        when(mockElement.getName()).thenReturn(name);
        when(mockElement.getTextTrim()).thenReturn(text);
        when(mockElement.getAttributes()).thenReturn(attributes);
        when(mockElement.getChildren()).thenReturn(new ArrayList<>());
        return mockElement;
    }
}
