# XMLutils Unit Tests

This document describes the comprehensive unit tests created for the `XMLutils` class in the `net.plumbing.msgbus.threads.utils` package.

## Test Files Created

### 1. XMLutilsTest.java
Main unit test class with extensive mocking for testing core XMLutils functionality:

**Test Methods:**
- `testSoap_XMLDocument2messageQueueVO_ValidContext()` - Tests parsing valid SOAP context elements
- `testSoap_XMLDocument2messageQueueVO_NullContext()` - Tests handling of null context
- `testSoap_XMLDocument2messageQueueVO_WrongElementName()` - Tests handling of invalid element names
- `testSoap_HeaderRequest2messageQueueVO_ValidXML()` - Tests parsing valid SOAP header requests
- `testSoap_HeaderRequest2messageQueueVO_InvalidXML()` - Tests handling of malformed XML
- `testSoapBody2XML_String()` - Tests SOAP body to XML string conversion
- `testConvXMLuseXSLT30_NullTransformer()` - Tests XSLT transformation with null transformer
- `testConvXMLuseXSLT30_NullXMLData()` - Tests XSLT transformation with null XML data
- `testConvXMLuseXSLT30_ShortXMLData()` - Tests XSLT transformation with insufficient data
- `testConvXMLuseXSLT30_SuccessfulTransformation()` - Tests successful XSLT transformation
- `testConvXMLuseXSLT30_SaxonApiException()` - Tests error handling in XSLT transformation
- `testTestXMLByXSD_ValidXMLAndXSD()` - Tests XML validation against XSD schema
- `testTestXMLByXSD_InvalidXML()` - Tests validation failure with invalid XML
- `testLegasyConvXMLuseXSLT_ValidInputs()` - Tests legacy XSLT transformation
- `testMakeMessageQueueVO_from_ContextElement_InvalidEventKey()` - Tests handling of invalid event keys

### 2. XMLutilsXMLProcessingTest.java
Focused tests for XML processing methods:

**Test Methods:**
- `testXML_BodyElemets2StringB_SimpleElements()` - Tests simple XML element processing
- `testXML_BodyElemets2StringB_ElementsWithAttributes()` - Tests elements with attributes
- `testXML_BodyElemets2StringB_EmptyElement()` - Tests empty element handling
- `testXML_BodyElemets2StringB_NestedElements()` - Tests nested XML structure processing
- `testXML_RequestElemets2StringB_SimpleElements()` - Tests request element processing
- `testXML_RequestElemets2StringB_WithAttributes()` - Tests request elements with attributes
- `testXML_RequestElemets2StringB_SpecialCharacters()` - Tests XML character escaping
- `testMakeMessageQueueVO_from_ContextElement_UnknownEventInitiator()` - Tests unknown initiator handling
- `testMakeMessageQueueVO_from_ContextElement_UnknownEventSource()` - Tests unknown source handling
- `testMakeMessageQueueVO_from_ContextElement_UnknownOperationId()` - Tests unknown operation handling

### 3. XMLutilsComplexMethodsTest.java
Tests for complex methods like `makeClearRequest` and `makeMessageDetailsRestApi`:

**Test Methods:**
- `testMakeClearRequest_ValidSoapXML_NoXSLT()` - Tests SOAP request processing without XSLT
- `testMakeClearRequest_InvalidXML()` - Tests invalid XML handling
- `testMakeClearRequest_WithXSLTTransformation()` - Tests SOAP processing with XSLT
- `testMakeClearRequest_XSLTEmptyResult()` - Tests empty XSLT result handling
- `testMakeClearRequest_NoSoapEnvelope()` - Tests missing SOAP envelope
- `testMakeClearRequest_NoSoapBody()` - Tests missing SOAP body
- `testMakeMessageDetailsRestApi_ValidInput()` - Tests REST API message processing
- `testMakeMessageDetailsRestApi_WithXSLT()` - Tests REST API with XSLT transformation
- `testMakeMessageDetailsRestApi_XSLTEmptyResult()` - Tests REST API with empty XSLT result

### 4. XMLutilsIntegrationTest.java
Integration tests that test actual functionality without extensive mocking:

**Test Methods:**
- `testTestXMLByXSD_ValidXMLAndXSD()` - Real XML validation test
- `testTestXMLByXSD_InvalidXMLAgainstXSD()` - Real validation failure test
- `testTestXMLByXSD_MalformedXML()` - Malformed XML handling test
- `testTestXMLByXSD_MalformedXSD()` - Malformed XSD handling test
- `testConvXMLuseXSLT30_ParameterValidation()` - Parameter validation tests
- `testXMLcharsConstants()` - Constants verification

### 5. XMLutilsTestRunner.java
Simple test runner that can be executed without Maven for basic functionality verification:

**Features:**
- Can be run as a standalone Java application
- Tests basic XMLutils functionality
- Provides console output for test results
- Uses assertion-based testing

## Test Dependencies Added

The following test dependencies were added to `pom.xml`:

```xml
<!-- Test Dependencies -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

## Running the Tests

### Using Maven (if available):
```bash
mvn test
```

### Running specific test classes:
```bash
mvn test -Dtest=XMLutilsTest
mvn test -Dtest=XMLutilsIntegrationTest
```

### Running the standalone test runner:
```bash
java -cp target/classes:target/test-classes net.plumbing.msgbus.threads.utils.XMLutilsTestRunner
```

## Test Coverage

The unit tests cover the following XMLutils methods:

1. **Soap_XMLDocument2messageQueueVO** - SOAP document parsing
2. **makeMessageQueueVO_from_ContextElement** - Context element processing
3. **Soap_HeaderRequest2messageQueueVO** - SOAP header parsing
4. **makeClearRequest** - Complex SOAP request processing
5. **makeMessageDetailsRestApi** - REST API message processing
6. **SoapBody2XML_String** - SOAP body conversion
7. **XML_RequestElemets2StringB** - Request element processing
8. **XML_BodyElemets2StringB** - Body element processing
9. **ConvXMLuseXSLT30** - XSLT 3.0 transformation
10. **legasyConvXMLuseXSLT** - Legacy XSLT transformation
11. **TestXMLByXSD** - XML schema validation

## Test Strategy

The tests use a combination of approaches:

1. **Unit Tests with Mocking** - Extensive use of Mockito to isolate units under test
2. **Integration Tests** - Testing actual functionality with real data
3. **Parameter Validation Tests** - Edge cases and boundary conditions
4. **Error Handling Tests** - Exception scenarios and error conditions
5. **Data Transformation Tests** - XML processing and conversion logic

## Error Scenarios Tested

- Null input parameters
- Empty or malformed XML
- Invalid XSD schemas
- XSLT transformation failures
- Missing SOAP elements
- Unknown system codes
- Invalid event keys
- Transformation exceptions

## Notes

- The tests are designed to work with the existing codebase structure
- Mock objects are used extensively to avoid dependencies on external systems
- Integration tests provide real functionality verification
- The standalone test runner allows basic verification without build tools
- All test methods include descriptive names and documentation
