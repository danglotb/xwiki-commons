/**
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.xml.stax;


import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stax.StAXResult;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.xml.XMLUtils;
import org.xwiki.xml.html.AmplHTMLUtilsTest;
import org.xwiki.xml.html.HTMLCleanerConfiguration;
import org.xwiki.xml.html.HTMLUtils;
import org.xwiki.xml.html.filter.HTMLFilter;
import org.xwiki.xml.internal.html.AmplDefaultHTMLCleanerTest;


/**
 * Unit tests for {@link org.xwiki.xml.stax.StAXUtils}.
 *
 * @version $Id$
 * @since 5.2M1
 */
public class AmplStAXUtilsTest {
    @Test
    public void getXMLStreamReader() throws XMLStreamException {
        // TODO: add support for XMLEventReader
        // Assert.assertNotNull(StAXUtils.getXMLStreamReader(new StAXSource(mockXMLEventReader())));
        Assert.assertNotNull(StAXUtils.getXMLStreamReader(new StAXSource(mockXMLStreamReader())));
        Assert.assertNotNull(StAXUtils.getXMLStreamReader(new StreamSource(new StringReader("<element/>"))));
    }

    @Test
    public void getXMLEventReader() throws XMLStreamException {
        Assert.assertNotNull(StAXUtils.getXMLEventReader(new StAXSource(mockXMLEventReader())));
        Assert.assertNotNull(StAXUtils.getXMLEventReader(new StAXSource(mockXMLStreamReader())));
        Assert.assertNotNull(StAXUtils.getXMLEventReader(new StreamSource(new StringReader("<element/>"))));
    }

    @Test
    public void getXMLStreamWriter() throws XMLStreamException {
        Assert.assertNotNull(StAXUtils.getXMLStreamWriter(new StAXResult(Mockito.mock(XMLEventWriter.class))));
        Assert.assertNotNull(StAXUtils.getXMLStreamWriter(new StAXResult(Mockito.mock(XMLStreamWriter.class))));
        Assert.assertNotNull(StAXUtils.getXMLStreamWriter(new StreamResult(new StringWriter())));
    }

    @Test
    public void getXMLStreamWriterWithSAXREsult() throws XMLStreamException, TransformerConfigurationException {
        TransformerFactory tf = TransformerFactory.newInstance();
        if (!(tf.getFeature(SAXTransformerFactory.FEATURE))) {
            throw new RuntimeException("Did not find a SAX-compatible TransformerFactory.");
        }
        StringWriter output = new StringWriter();
        SAXTransformerFactory stf = ((SAXTransformerFactory) (tf));
        TransformerHandler th = stf.newTransformerHandler();
        th.setResult(new StreamResult(output));
        XMLStreamWriter writer = StAXUtils.getXMLStreamWriter(new SAXResult(th));
        writer.writeStartElement("element");
        writer.writeAttribute("attribute", "value");
        writer.writeCharacters("characters");
        writer.writeCData("cdata");
        writer.writeEndElement();
        Assert.assertEquals(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + ((("<element attribute=\"value\">" + "characters") + "<![CDATA[cdata]]>") + "</element>")), output.getBuffer().toString());
    }

    private XMLStreamReader mockXMLStreamReader() {
        XMLStreamReader xmlStreamReader = Mockito.mock(XMLStreamReader.class);
        Mockito.when(xmlStreamReader.getEventType()).thenReturn(XMLStreamConstants.START_DOCUMENT);
        Location location = Mockito.mock(Location.class);
        Mockito.when(xmlStreamReader.getLocation()).thenReturn(location);
        return xmlStreamReader;
    }

    private XMLEventReader mockXMLEventReader() throws XMLStreamException {
        XMLEventReader xmlEventReader = Mockito.mock(XMLEventReader.class);
        XMLEvent event = Mockito.mock(XMLEvent.class);
        Mockito.when(event.getEventType()).thenReturn(XMLStreamConstants.START_DOCUMENT);
        Mockito.when(xmlEventReader.peek()).thenReturn(event);
        Location location = Mockito.mock(Location.class);
        Mockito.when(event.getLocation()).thenReturn(location);
        return xmlEventReader;
    }

    @Test(timeout = 10000)
    public void escapeAttributeValue() {
        String escapedText = XMLUtils.escapeAttributeValue("a < a\' && a\' < a\" => a < a\" {");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a&#39; &#38;&#38; a&#39; &#60; a&#34; =&#62; a &#60; a&#34; &#123;", escapedText);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue__3 = escapedText.contains("<");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue__4 = escapedText.contains(">");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue__5 = escapedText.contains("'");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue__6 = escapedText.contains("\"");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue__7 = escapedText.contains("&&");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue__8 = escapedText.contains("{");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue__8);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue__5);
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a&#39; &#38;&#38; a&#39; &#60; a&#34; =&#62; a &#60; a&#34; &#123;", escapedText);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue__4);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue__7);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue__3);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue__6);
    }

    @Test(timeout = 10000)
    public void escapeElementContent() {
        String escapedText = XMLUtils.escapeElementContent("a < a\' && a\' < a\" => a < a\"");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a\' &#38;&#38; a\' &#60; a\" =&#62; a &#60; a\"", escapedText);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent__3 = escapedText.contains("<");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeElementContent__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent__4 = escapedText.contains(">");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeElementContent__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent__5 = escapedText.contains("'");
        // AssertGenerator add assertion
        Assert.assertTrue(o_escapeElementContent__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent__6 = escapedText.contains("\"");
        // AssertGenerator add assertion
        Assert.assertTrue(o_escapeElementContent__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent__7 = escapedText.contains("&&");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeElementContent__7);
        // AssertGenerator add assertion
        Assert.assertTrue(o_escapeElementContent__5);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeElementContent__3);
        // AssertGenerator add assertion
        Assert.assertTrue(o_escapeElementContent__6);
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a\' &#38;&#38; a\' &#60; a\" =&#62; a &#60; a\"", escapedText);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeElementContent__4);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escape */
    @Test(timeout = 10000)
    public void escape_literalMutationString2() {
        String escapedText = XMLUtils.escape("a * a\' && a\' < a\" => a < a\"");
        // AssertGenerator add assertion
        Assert.assertEquals("a * a&#39; &#38;&#38; a&#39; &#60; a&#34; =&#62; a &#60; a&#34;", escapedText);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString2__3 = escapedText.contains("<");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escape_literalMutationString2__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString2__4 = escapedText.contains(">");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escape_literalMutationString2__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString2__5 = escapedText.contains("'");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escape_literalMutationString2__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString2__6 = escapedText.contains("\"");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString2__7 = escapedText.contains("&&");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escape_literalMutationString2__7);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escape_literalMutationString2__4);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escape_literalMutationString2__5);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escape_literalMutationString2__6);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escape_literalMutationString2__3);
        // AssertGenerator add assertion
        Assert.assertEquals("a * a&#39; &#38;&#38; a&#39; &#60; a&#34; =&#62; a &#60; a&#34;", escapedText);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escape */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escape_literalMutationString3 */
    @Test(timeout = 10000)
    public void escape_literalMutationString3_literalMutationString128() {
        String escapedText = XMLUtils.escape("A=SO/woO!OKS@Rl&{ha!&Bcvg[?i!rb0/|]6^FT)-e");
        // AssertGenerator add assertion
        Assert.assertEquals("A=SO/woO!OKS@Rl&#38;&#123;ha!&#38;Bcvg[?i!rb0/|]6^FT)-e", escapedText);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString3__3 = escapedText.contains("<");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString3__4 = escapedText.contains(">");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString3__5 = escapedText.contains("'");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString3__6 = escapedText.contains("\"");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString3__7 = escapedText.contains("&&");
        // AssertGenerator add assertion
        Assert.assertEquals("A=SO/woO!OKS@Rl&#38;&#123;ha!&#38;Bcvg[?i!rb0/|]6^FT)-e", escapedText);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escape */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escape_literalMutationString20 */
    @Test(timeout = 10000)
    public void escape_literalMutationString20_literalMutationString495() {
        String escapedText = XMLUtils.escape("a < a\' && a\'  a\" => a < a\"");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a&#39; &#38;&#38; a&#39;  a&#34; =&#62; a &#60; a&#34;", escapedText);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString20__3 = escapedText.contains("<");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString20__4 = escapedText.contains(">");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString20__5 = escapedText.contains("'");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString20__6 = escapedText.contains("\"");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString20__7 = escapedText.contains("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a&#39; &#38;&#38; a&#39;  a&#34; =&#62; a &#60; a&#34;", escapedText);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escape */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escape_literalMutationString14 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escape_literalMutationString14_literalMutationString369 */
    @Test(timeout = 10000)
    public void escape_literalMutationString14_literalMutationString369_literalMutationString3066() {
        String escapedText = XMLUtils.escape("a < a\' && a\' < a\" => a < a\"");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a&#39; &#38;&#38; a&#39; &#60; a&#34; =&#62; a &#60; a&#34;", escapedText);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString14__3 = escapedText.contains("y");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString14__4 = escapedText.contains(">");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString14__5 = escapedText.contains("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString14__6 = escapedText.contains("J");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString14__7 = escapedText.contains("&&");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a&#39; &#38;&#38; a&#39; &#60; a&#34; =&#62; a &#60; a&#34;", escapedText);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escape */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escape_literalMutationString18 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escape_literalMutationString18_literalMutationString449 */
    @Test(timeout = 10000)
    public void escape_literalMutationString18_literalMutationString449_literalMutationString1891() {
        String escapedText = XMLUtils.escape("Failed to serialize node to XML String: [{}]");
        // AssertGenerator add assertion
        Assert.assertEquals("Failed to serialize node to XML String: [&#123;}]", escapedText);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString18__3 = escapedText.contains("<");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString18__4 = escapedText.contains(">");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString18__5 = escapedText.contains("'");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString18__6 = escapedText.contains("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escape_literalMutationString18__7 = escapedText.contains("&&");
        // AssertGenerator add assertion
        Assert.assertEquals("Failed to serialize node to XML String: [&#123;}]", escapedText);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeApos */
    @Test(timeout = 10000)
    public void escapeApos_literalMutationString6172() {
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeApos_literalMutationString6172__1 = XMLUtils.escape("'").equals("");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeApos_literalMutationString6172__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValue */
    @Test(timeout = 10000)
    public void escapeAttributeValue_literalMutationString7881() {
        String escapedText = XMLUtils.escapeAttributeValue("a < a\' && a\' < a => a < a\" {");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a&#39; &#38;&#38; a&#39; &#60; a =&#62; a &#60; a&#34; &#123;", escapedText);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7881__3 = escapedText.contains("<");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7881__4 = escapedText.contains(">");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue_literalMutationString7881__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7881__5 = escapedText.contains("'");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue_literalMutationString7881__5);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7881__6 = escapedText.contains("\"");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue_literalMutationString7881__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7881__7 = escapedText.contains("&&");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue_literalMutationString7881__7);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7881__8 = escapedText.contains("{");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue_literalMutationString7881__8);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue_literalMutationString7881__6);
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a&#39; &#38;&#38; a&#39; &#60; a =&#62; a &#60; a&#34; &#123;", escapedText);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue_literalMutationString7881__3);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue_literalMutationString7881__4);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue_literalMutationString7881__7);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValue_literalMutationString7881__5);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValue */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValue_literalMutationString7886 */
    @Test(timeout = 10000)
    public void escapeAttributeValue_literalMutationString7886_literalMutationString8226() {
        String escapedText = XMLUtils.escapeAttributeValue("a < a\' && a\' < a\" => a < a\" {");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a&#39; &#38;&#38; a&#39; &#60; a&#34; =&#62; a &#60; a&#34; &#123;", escapedText);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7886__3 = escapedText.contains("<");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7886__4 = escapedText.contains("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;{&#x3E;");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7886__5 = escapedText.contains("'");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7886__6 = escapedText.contains("\"");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7886__7 = escapedText.contains("&&");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7886__8 = escapedText.contains("{");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a&#39; &#38;&#38; a&#39; &#60; a&#34; =&#62; a &#60; a&#34; &#123;", escapedText);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValue */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValue_literalMutationString7882 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValue_literalMutationString7882_literalMutationString8117 */
    @Test(timeout = 10000)
    public void escapeAttributeValue_literalMutationString7882_literalMutationString8117_literalMutationString11191() {
        String escapedText = XMLUtils.escapeAttributeValue("a < a\' && a\' < a\" => ha < a\" {");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7882__3 = escapedText.contains("");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7882__4 = escapedText.contains(">");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7882__5 = escapedText.contains("'");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7882__6 = escapedText.contains("\"");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7882__7 = escapedText.contains("");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7882__8 = escapedText.contains("{");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a&#39; &#38;&#38; a&#39; &#60; a&#34; =&#62; ha &#60; a&#34; &#123;", escapedText);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValue */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValue_literalMutationString7882 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValue_literalMutationString7882_literalMutationString8116 */
    @Test(timeout = 10000)
    public void escapeAttributeValue_literalMutationString7882_literalMutationString8116_literalMutationString13845() {
        String escapedText = XMLUtils.escapeAttributeValue("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", escapedText);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7882__3 = escapedText.contains("");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7882__4 = escapedText.contains("n");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7882__5 = escapedText.contains("'");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7882__6 = escapedText.contains("\"");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7882__7 = escapedText.contains("&&");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValue_literalMutationString7882__8 = escapedText.contains("{");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", escapedText);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueApos */
    @Test(timeout = 10000)
    public void escapeAttributeValueApos_literalMutationString14866() {
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeAttributeValueApos_literalMutationString14866__1 = XMLUtils.escapeAttributeValue("'").equals("");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeAttributeValueApos_literalMutationString14866__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii */
    @Test(timeout = 10000)
    public void escapeAttributeValueNonAscii_literalMutationString16572() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeAttributeValueNonAscii_literalMutationString16572__1 = XMLUtils.escapeAttributeValue("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeAttributeValueNonAscii_literalMutationString16572__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii_literalMutationString16572 */
    @Test(timeout = 10000)
    public void escapeAttributeValueNonAscii_literalMutationString16572_literalMutationString16592() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeAttributeValueNonAscii_literalMutationString16572__1 = XMLUtils.escapeAttributeValue("iZMpEd$M8+GQj`j#*h($BG6`-V9i:WfY{<wxAR]s7%");
        // AssertGenerator add assertion
        Assert.assertEquals("iZMpEd$M8+GQj`j#*h($BG6`-V9i:WfY&#123;&#60;wxAR]s7%", o_escapeAttributeValueNonAscii_literalMutationString16572__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii_literalMutationString16572 */
    @Test(timeout = 10000)
    public void escapeAttributeValueNonAscii_literalMutationString16572_literalMutationString16590() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeAttributeValueNonAscii_literalMutationString16572__1 = XMLUtils.escapeAttributeValue("&t#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;t#x26;&#38;#x27;&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeAttributeValueNonAscii_literalMutationString16572__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii_literalMutationString16570 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii_literalMutationString16570_literalMutationString16583 */
    @Test(timeout = 10000)
    public void escapeAttributeValueNonAscii_literalMutationString16570_literalMutationString16583_literalMutationString16635() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeAttributeValueNonAscii_literalMutationString16570__1 = XMLUtils.escapeAttributeValue("Failed to serialize node to XML String: [{}]");
        // AssertGenerator add assertion
        Assert.assertEquals("Failed to serialize node to XML String: [&#123;}]", o_escapeAttributeValueNonAscii_literalMutationString16570__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii_literalMutationString16571 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii_literalMutationString16571_literalMutationString16586 */
    @Test(timeout = 10000)
    public void escapeAttributeValueNonAscii_literalMutationString16571_literalMutationString16586_literalMutationString16650() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeAttributeValueNonAscii_literalMutationString16571__1 = XMLUtils.escapeAttributeValue("(}s3)UOH}@R :=1*S0T@9g>fkgOdTr(w8bgp:r#J1z");
        // AssertGenerator add assertion
        Assert.assertEquals("(}s3)UOH}@R :=1*S0T@9g&#62;fkgOdTr(w8bgp:r#J1z", o_escapeAttributeValueNonAscii_literalMutationString16571__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii_literalMutationString16570 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii_literalMutationString16570_literalMutationString16584 */
    @Test(timeout = 10000)
    public void escapeAttributeValueNonAscii_literalMutationString16570_literalMutationString16584_literalMutationString16641() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeAttributeValueNonAscii_literalMutationString16570__1 = XMLUtils.escapeAttributeValue("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeAttributeValueNonAscii_literalMutationString16570__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii_literalMutationString16572 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeAttributeValueNonAscii_literalMutationString16572_literalMutationString16589 */
    @Test(timeout = 10000)
    public void escapeAttributeValueNonAscii_literalMutationString16572_literalMutationString16589_literalMutationString16660() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeAttributeValueNonAscii_literalMutationString16572__1 = XMLUtils.escapeAttributeValue("I.qTbdZ_[[ej32M-vO+hw3017GWv83L,`Jk68V!<@Z_y");
        // AssertGenerator add assertion
        Assert.assertEquals("I.qTbdZ_[[ej32M-vO+hw3017GWv83L,`Jk68V!&#60;@Z_y", o_escapeAttributeValueNonAscii_literalMutationString16572__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContent */
    @Test(timeout = 10000)
    public void escapeElementContent_literalMutationString16866() {
        String escapedText = XMLUtils.escapeElementContent("a < a\' && a\' < a\" => a < a\"");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a\' &#38;&#38; a\' &#60; a\" =&#62; a &#60; a\"", escapedText);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16866__3 = escapedText.contains("s");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeElementContent_literalMutationString16866__3);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16866__4 = escapedText.contains(">");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeElementContent_literalMutationString16866__4);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16866__5 = escapedText.contains("'");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16866__6 = escapedText.contains("\"");
        // AssertGenerator add assertion
        Assert.assertTrue(o_escapeElementContent_literalMutationString16866__6);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16866__7 = escapedText.contains("&&");
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeElementContent_literalMutationString16866__7);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeElementContent_literalMutationString16866__3);
        // AssertGenerator add assertion
        Assert.assertFalse(o_escapeElementContent_literalMutationString16866__4);
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a\' &#38;&#38; a\' &#60; a\" =&#62; a &#60; a\"", escapedText);
        // AssertGenerator add assertion
        Assert.assertTrue(o_escapeElementContent_literalMutationString16866__6);
        // AssertGenerator add assertion
        Assert.assertTrue(o_escapeElementContent_literalMutationString16866__5);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContent */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContent_literalMutationString16864 */
    @Test(timeout = 10000)
    public void escapeElementContent_literalMutationString16864_literalMutationString17077() {
        String escapedText = XMLUtils.escapeElementContent("a < a\' && a\' < a\" => a < a\"");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a\' &#38;&#38; a\' &#60; a\" =&#62; a &#60; a\"", escapedText);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16864__3 = escapedText.contains("");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16864__4 = escapedText.contains(">");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16864__5 = escapedText.contains(" ");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16864__6 = escapedText.contains("\"");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16864__7 = escapedText.contains("&&");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a\' &#38;&#38; a\' &#60; a\" =&#62; a &#60; a\"", escapedText);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContent */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContent_literalMutationString16866 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContent_literalMutationString16866_literalMutationString17123 */
    @Test(timeout = 10000)
    public void escapeElementContent_literalMutationString16866_literalMutationString17123_literalMutationString19172() {
        String escapedText = XMLUtils.escapeElementContent("a < a\' && a\' < a\" => a < a\"");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a\' &#38;&#38; a\' &#60; a\" =&#62; a &#60; a\"", escapedText);
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16866__3 = escapedText.contains("s");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16866__4 = escapedText.contains(">");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16866__5 = escapedText.contains("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16866__6 = escapedText.contains("\"");
        // AssertGenerator create local variable with return value of invocation
        boolean o_escapeElementContent_literalMutationString16866__7 = escapedText.contains("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("a &#60; a\' &#38;&#38; a\' &#60; a\" =&#62; a &#60; a\"", escapedText);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentEmptyString */
    @Test(timeout = 10000)
    public void escapeElementContentEmptyString_literalMutationString23006() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeElementContentEmptyString_literalMutationString23006__1 = XMLUtils.escapeElementContent("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeElementContentEmptyString_literalMutationString23006__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentEmptyString_literalMutationString23006 */
    @Test(timeout = 10000)
    public void escapeElementContentEmptyString_literalMutationString23006_literalMutationString23025() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeElementContentEmptyString_literalMutationString23006__1 = XMLUtils.escapeElementContent("S<23V.+a^tBVOA>`kM;fCG*<GJ![ySpS4ms3&2B._B");
        // AssertGenerator add assertion
        Assert.assertEquals("S&#60;23V.+a^tBVOA&#62;`kM;fCG*&#60;GJ![ySpS4ms3&#38;2B._B", o_escapeElementContentEmptyString_literalMutationString23006__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentEmptyString_literalMutationString23006 */
    @Test(timeout = 10000)
    public void escapeElementContentEmptyString_literalMutationString23006_literalMutationString23021() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeElementContentEmptyString_literalMutationString23006__1 = XMLUtils.escapeElementContent("&#x26;&#x27;&#x22;&#x3c;&#x3-C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22;&#38;#x3c;&#38;#x3-C;&#38;#x3e;&#38;#x3E;", o_escapeElementContentEmptyString_literalMutationString23006__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentEmptyString_literalMutationString23006 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentEmptyString_literalMutationString23006_literalMutationString23021 */
    @Test(timeout = 10000)
    public void escapeElementContentEmptyString_literalMutationString23006_literalMutationString23021_literalMutationString23082() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeElementContentEmptyString_literalMutationString23006__1 = XMLUtils.escapeElementContent("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeElementContentEmptyString_literalMutationString23006__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentEmptyString_literalMutationString23006 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentEmptyString_literalMutationString23006_literalMutationString23025 */
    @Test(timeout = 10000)
    public void escapeElementContentEmptyString_literalMutationString23006_literalMutationString23025_literalMutationString23105() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeElementContentEmptyString_literalMutationString23006__1 = XMLUtils.escapeElementContent("S<23V.+a^tBVOA>`kM;fCG*<G![ySpS4ms3&2B._B");
        // AssertGenerator add assertion
        Assert.assertEquals("S&#60;23V.+a^tBVOA&#62;`kM;fCG*&#60;G![ySpS4ms3&#38;2B._B", o_escapeElementContentEmptyString_literalMutationString23006__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentNonAscii */
    @Test(timeout = 10000)
    public void escapeElementContentNonAscii_literalMutationString23294() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeElementContentNonAscii_literalMutationString23294__1 = XMLUtils.escapeElementContent("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeElementContentNonAscii_literalMutationString23294__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentNonAscii_literalMutationString23294 */
    @Test(timeout = 10000)
    public void escapeElementContentNonAscii_literalMutationString23294_literalMutationString23313() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeElementContentNonAscii_literalMutationString23294__1 = XMLUtils.escapeElementContent("&#x26;&#x27;&#x2#2;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x2#2;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeElementContentNonAscii_literalMutationString23294__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentNonAscii_literalMutationString23295 */
    @Test(timeout = 10000)
    public void escapeElementContentNonAscii_literalMutationString23295_literalMutationString23316() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeElementContentNonAscii_literalMutationString23295__1 = XMLUtils.escapeElementContent("<");
        // AssertGenerator add assertion
        Assert.assertEquals("&#60;", o_escapeElementContentNonAscii_literalMutationString23295__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentNonAscii_literalMutationString23294 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentNonAscii_literalMutationString23294_literalMutationString23310 */
    @Test(timeout = 10000)
    public void escapeElementContentNonAscii_literalMutationString23294_literalMutationString23310_literalMutationString23375() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeElementContentNonAscii_literalMutationString23294__1 = XMLUtils.escapeElementContent("&#x26;&#xn27;&#x`2;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#xn27;&#38;#x`2;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeElementContentNonAscii_literalMutationString23294__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentNonAscii_literalMutationString23294 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentNonAscii_literalMutationString23294_literalMutationString23311 */
    @Test(timeout = 10000)
    public void escapeElementContentNonAscii_literalMutationString23294_literalMutationString23311_literalMutationString23381() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeElementContentNonAscii_literalMutationString23294__1 = XMLUtils.escapeElementContent("h`>KD}fgTL(x1Q2.EbeOub1<c|>W!@08E,gpif<w8M1^");
        // AssertGenerator add assertion
        Assert.assertEquals("h`&#62;KD}fgTL(x1Q2.EbeOub1&#60;c|&#62;W!@08E,gpif&#60;w8M1^", o_escapeElementContentNonAscii_literalMutationString23294__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentNonAscii_literalMutationString23293 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeElementContentNonAscii_literalMutationString23293_literalMutationString23306 */
    @Test(timeout = 10000)
    public void escapeElementContentNonAscii_literalMutationString23293_literalMutationString23306_literalMutationString23361() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeElementContentNonAscii_literalMutationString23293__1 = XMLUtils.escapeElementContent("B$wg=|KS|IYO7ejG>_@=ct j0@XwHH=d9nhq70kZOn");
        // AssertGenerator add assertion
        Assert.assertEquals("B$wg=|KS|IYO7ejG&#62;_@=ct j0@XwHH=d9nhq70kZOn", o_escapeElementContentNonAscii_literalMutationString23293__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString */
    @Test(timeout = 10000)
    public void escapeEmptyString_literalMutationString23582() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeEmptyString_literalMutationString23582__1 = XMLUtils.escape("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeEmptyString_literalMutationString23582__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString_literalMutationString23582 */
    @Test(timeout = 10000)
    public void escapeEmptyString_literalMutationString23582_literalMutationString23597() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeEmptyString_literalMutationString23582__1 = XMLUtils.escape("Failed to serialize node to XML String: [{}]");
        // AssertGenerator add assertion
        Assert.assertEquals("Failed to serialize node to XML String: [&#123;}]", o_escapeEmptyString_literalMutationString23582__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString_literalMutationString23582 */
    @Test(timeout = 10000)
    public void escapeEmptyString_literalMutationString23582_literalMutationString23601() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeEmptyString_literalMutationString23582__1 = XMLUtils.escape("&#x26;&#x27;&#x22&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeEmptyString_literalMutationString23582__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString_literalMutationString23582 */
    @Test(timeout = 10000)
    public void escapeEmptyString_literalMutationString23582_literalMutationString23600() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeEmptyString_literalMutationString23582__1 = XMLUtils.escape("v[.ovlirAc[/ GP(Y-T:oUcT>8_8#uH[[tF5N@x)(U");
        // AssertGenerator add assertion
        Assert.assertEquals("v[.ovlirAc[/ GP(Y-T:oUcT&#62;8_8#uH[[tF5N@x)(U", o_escapeEmptyString_literalMutationString23582__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString_literalMutationString23582 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString_literalMutationString23582_literalMutationString23599 */
    @Test(timeout = 10000)
    public void escapeEmptyString_literalMutationString23582_literalMutationString23599_literalMutationString23672() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeEmptyString_literalMutationString23582__1 = XMLUtils.escape("`9[s`#> H%KkMCs+vK-[&^]rDf}&y4@J8(L,UuRlLdT");
        // AssertGenerator add assertion
        Assert.assertEquals("`9[s`#&#62; H%KkMCs+vK-[&#38;^]rDf}&#38;y4@J8(L,UuRlLdT", o_escapeEmptyString_literalMutationString23582__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString_literalMutationString23581 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString_literalMutationString23581_literalMutationString23595 */
    @Test(timeout = 10000)
    public void escapeEmptyString_literalMutationString23581_literalMutationString23595_literalMutationString23652() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeEmptyString_literalMutationString23581__1 = XMLUtils.escape("&l_&)<#}v4SlscH/p(rIa}cOjYv$S/|@W- c+}k`N$");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;l_&#38;)&#60;#}v4SlscH/p(rIa}cOjYv$S/|@W- c+}k`N$", o_escapeEmptyString_literalMutationString23581__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString_literalMutationString23581 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString_literalMutationString23581_literalMutationString23595 */
    @Test(timeout = 10000)
    public void escapeEmptyString_literalMutationString23581_literalMutationString23595_literalMutationString23651() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeEmptyString_literalMutationString23581__1 = XMLUtils.escape("&#x26;#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;#x27;&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeEmptyString_literalMutationString23581__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString_literalMutationString23583 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeEmptyString_literalMutationString23583_literalMutationString23603 */
    @Test(timeout = 10000)
    public void escapeEmptyString_literalMutationString23583_literalMutationString23603_literalMutationString23691() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeEmptyString_literalMutationString23583__1 = XMLUtils.escape("Failed to serialize node to XML String: [{}]");
        // AssertGenerator add assertion
        Assert.assertEquals("Failed to serialize node to XML String: [&#123;}]", o_escapeEmptyString_literalMutationString23583__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeFAttributeValueEmptyString */
    @Test(timeout = 10000)
    public void escapeFAttributeValueEmptyString_literalMutationString23870() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeFAttributeValueEmptyString_literalMutationString23870__1 = XMLUtils.escapeAttributeValue("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeFAttributeValueEmptyString_literalMutationString23870__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeFAttributeValueEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeFAttributeValueEmptyString_literalMutationString23870 */
    @Test(timeout = 10000)
    public void escapeFAttributeValueEmptyString_literalMutationString23870_literalMutationString23886() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeFAttributeValueEmptyString_literalMutationString23870__1 = XMLUtils.escapeAttributeValue("Failed to serialize node to XML String: [{}]");
        // AssertGenerator add assertion
        Assert.assertEquals("Failed to serialize node to XML String: [&#123;}]", o_escapeFAttributeValueEmptyString_literalMutationString23870__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeFAttributeValueEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeFAttributeValueEmptyString_literalMutationString23870 */
    @Test(timeout = 10000)
    public void escapeFAttributeValueEmptyString_literalMutationString23870_literalMutationString23887() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeFAttributeValueEmptyString_literalMutationString23870__1 = XMLUtils.escapeAttributeValue("&#x26;&#x27;&#x22;&#x3;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22;&#38;#x3;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeFAttributeValueEmptyString_literalMutationString23870__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeFAttributeValueEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeFAttributeValueEmptyString_literalMutationString23870 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeFAttributeValueEmptyString_literalMutationString23870_literalMutationString23886 */
    @Test(timeout = 10000)
    public void escapeFAttributeValueEmptyString_literalMutationString23870_literalMutationString23886_literalMutationString23951() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeFAttributeValueEmptyString_literalMutationString23870__1 = XMLUtils.escapeAttributeValue("RvcZIG!`v2hHi4$b,|`z!^. n><HJo[IiE??huB|YS0]");
        // AssertGenerator add assertion
        Assert.assertEquals("RvcZIG!`v2hHi4$b,|`z!^. n&#62;&#60;HJo[IiE??huB|YS0]", o_escapeFAttributeValueEmptyString_literalMutationString23870__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeFAttributeValueEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeFAttributeValueEmptyString_literalMutationString23870 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeFAttributeValueEmptyString_literalMutationString23870_literalMutationString23885 */
    @Test(timeout = 10000)
    public void escapeFAttributeValueEmptyString_literalMutationString23870_literalMutationString23885_literalMutationString23949() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeFAttributeValueEmptyString_literalMutationString23870__1 = XMLUtils.escapeAttributeValue("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeFAttributeValueEmptyString_literalMutationString23870__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeFAttributeValueEmptyString */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeFAttributeValueEmptyString_literalMutationString23870 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeFAttributeValueEmptyString_literalMutationString23870_literalMutationString23886 */
    @Test(timeout = 10000)
    public void escapeFAttributeValueEmptyString_literalMutationString23870_literalMutationString23886_literalMutationString23953() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeFAttributeValueEmptyString_literalMutationString23870__1 = XMLUtils.escapeAttributeValue("Failed to serialize node to XML String< [{}]");
        // AssertGenerator add assertion
        Assert.assertEquals("Failed to serialize node to XML String&#60; [&#123;}]", o_escapeFAttributeValueEmptyString_literalMutationString23870__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii */
    @Test(timeout = 10000)
    public void escapeNonAscii_literalMutationString24158() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeNonAscii_literalMutationString24158__1 = XMLUtils.escape("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeNonAscii_literalMutationString24158__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii_literalMutationString24158 */
    @Test(timeout = 10000)
    public void escapeNonAscii_literalMutationString24158_literalMutationString24175() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeNonAscii_literalMutationString24158__1 = XMLUtils.escape("Failed to serialize node to XML String: [{}]");
        // AssertGenerator add assertion
        Assert.assertEquals("Failed to serialize node to XML String: [&#123;}]", o_escapeNonAscii_literalMutationString24158__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii_literalMutationString24158 */
    @Test(timeout = 10000)
    public void escapeNonAscii_literalMutationString24158_literalMutationString24174() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeNonAscii_literalMutationString24158__1 = XMLUtils.escape("&#x26;&#x27;t&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;t&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeNonAscii_literalMutationString24158__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii_literalMutationString24158 */
    @Test(timeout = 10000)
    public void escapeNonAscii_literalMutationString24158_literalMutationString24173() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeNonAscii_literalMutationString24158__1 = XMLUtils.escape("kPs:n9ztS5tfJjVJ!X8L)S_$mqx6-rr@y#wdH<tygv");
        // AssertGenerator add assertion
        Assert.assertEquals("kPs:n9ztS5tfJjVJ!X8L)S_$mqx6-rr@y#wdH&#60;tygv", o_escapeNonAscii_literalMutationString24158__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii_literalMutationString24159 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii_literalMutationString24159_literalMutationString24179 */
    @Test(timeout = 10000)
    public void escapeNonAscii_literalMutationString24159_literalMutationString24179_literalMutationString24266() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeNonAscii_literalMutationString24159__1 = XMLUtils.escape("4#%MHb&XPNKCOou<UWEX:(k [Yn!UkA&Y.DvD0g9nw");
        // AssertGenerator add assertion
        Assert.assertEquals("4#%MHb&#38;XPNKCOou&#60;UWEX:(k [Yn!UkA&#38;Y.DvD0g9nw", o_escapeNonAscii_literalMutationString24159__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii_literalMutationString24158 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii_literalMutationString24158_literalMutationString24174 */
    @Test(timeout = 10000)
    public void escapeNonAscii_literalMutationString24158_literalMutationString24174_literalMutationString24239() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeNonAscii_literalMutationString24158__1 = XMLUtils.escape("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;");
        // AssertGenerator add assertion
        Assert.assertEquals("&#38;#x26;&#38;#x27;&#38;#x22;&#38;#x3c;&#38;#x3C;&#38;#x3e;&#38;#x3E;", o_escapeNonAscii_literalMutationString24158__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii_literalMutationString24158 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii_literalMutationString24158_literalMutationString24176 */
    @Test(timeout = 10000)
    public void escapeNonAscii_literalMutationString24158_literalMutationString24176_literalMutationString24251() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeNonAscii_literalMutationString24158__1 = XMLUtils.escape("2)H3/W>)]2nehKtSjBCkzxCn/:4ICG8Wt[h_,z`%p");
        // AssertGenerator add assertion
        Assert.assertEquals("2)H3/W&#62;)]2nehKtSjBCkzxCn/:4ICG8Wt[h_,z`%p", o_escapeNonAscii_literalMutationString24158__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii_literalMutationString24158 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeNonAscii_literalMutationString24158_literalMutationString24175 */
    @Test(timeout = 10000)
    public void escapeNonAscii_literalMutationString24158_literalMutationString24175_literalMutationString24248() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeNonAscii_literalMutationString24158__1 = XMLUtils.escape("Failed to serialize kode to XML String: [{}]");
        // AssertGenerator add assertion
        Assert.assertEquals("Failed to serialize kode to XML String: [&#123;}]", o_escapeNonAscii_literalMutationString24158__1);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeXMLComment */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeXMLComment_literalMutationString24453 */
    @Test(timeout = 10000)
    public void escapeXMLComment_literalMutationString24453_literalMutationString24663() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeXMLComment_literalMutationString24453__1 = XMLUtils.escapeXMLComment("-- ");
        // AssertGenerator add assertion
        Assert.assertEquals("-\\- ", o_escapeXMLComment_literalMutationString24453__1);
        // AssertGenerator create local variable with return value of invocation
        String o_escapeXMLComment_literalMutationString24453__2 = XMLUtils.escapeXMLComment("Failed to serialize node to XML String: [{}]");
        // AssertGenerator create local variable with return value of invocation
        String o_escapeXMLComment_literalMutationString24453__3 = XMLUtils.escapeXMLComment("---");
        // AssertGenerator add assertion
        Assert.assertEquals("-\\-\\-\\", o_escapeXMLComment_literalMutationString24453__3);
        // AssertGenerator create local variable with return value of invocation
        String o_escapeXMLComment_literalMutationString24453__4 = XMLUtils.escapeXMLComment("- ");
        // AssertGenerator add assertion
        Assert.assertEquals("- ", o_escapeXMLComment_literalMutationString24453__4);
        // AssertGenerator add assertion
        Assert.assertEquals("Failed to serialize node to XML String: [{}]", o_escapeXMLComment_literalMutationString24453__2);
        // AssertGenerator add assertion
        Assert.assertEquals("-\\- ", o_escapeXMLComment_literalMutationString24453__1);
        // AssertGenerator add assertion
        Assert.assertEquals("-\\-\\-\\", o_escapeXMLComment_literalMutationString24453__3);
    }

    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeXMLComment */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeXMLComment_literalMutationString24453 */
    /* amplification of org.xwiki.xml.XMLUtilsTest#escapeXMLComment_literalMutationString24453_literalMutationString24663 */
    @Test(timeout = 10000)
    public void escapeXMLComment_literalMutationString24453_literalMutationString24663_literalMutationString27409() {
        // AssertGenerator create local variable with return value of invocation
        String o_escapeXMLComment_literalMutationString24453__1 = XMLUtils.escapeXMLComment("-- ");
        // AssertGenerator create local variable with return value of invocation
        String o_escapeXMLComment_literalMutationString24453__2 = XMLUtils.escapeXMLComment("Failed to serialize node to XML String: [{}]");
        // AssertGenerator add assertion
        Assert.assertEquals("Failed to serialize node to XML String: [{}]", o_escapeXMLComment_literalMutationString24453__2);
        // AssertGenerator create local variable with return value of invocation
        String o_escapeXMLComment_literalMutationString24453__3 = XMLUtils.escapeXMLComment("-2--");
        // AssertGenerator add assertion
        Assert.assertEquals("-2-\\-\\", o_escapeXMLComment_literalMutationString24453__3);
        // AssertGenerator create local variable with return value of invocation
        String o_escapeXMLComment_literalMutationString24453__4 = XMLUtils.escapeXMLComment("- ");
        // AssertGenerator add assertion
        Assert.assertEquals("- ", o_escapeXMLComment_literalMutationString24453__4);
        // AssertGenerator add assertion
        Assert.assertEquals("-\\- ", o_escapeXMLComment_literalMutationString24453__1);
        // AssertGenerator add assertion
        Assert.assertEquals("-2-\\-\\", o_escapeXMLComment_literalMutationString24453__3);
        // AssertGenerator add assertion
        Assert.assertEquals("Failed to serialize node to XML String: [{}]", o_escapeXMLComment_literalMutationString24453__2);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripHTMLEnvelope */
    @Test(timeout = 10000)
    public void testStripHTMLEnvelope_literalMutationString6() throws Exception {
        Document document = this.cleaner.clean(new StringReader("<html><head><body><p>test1</p><pM>test2</p></body></html>"));
        HTMLUtils.stripHTMLEnvelope(document);
        String String_7 = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><p>test1</p><p>test2</p></html>\n";
        // AssertGenerator create local variable with return value of invocation
        String o_testStripHTMLEnvelope_literalMutationString6__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><p>test1</p><p>test2</p></html>\n", o_testStripHTMLEnvelope_literalMutationString6__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><p>test1</p><p>test2</p></html>\n", String_7);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripHTMLEnvelope */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripHTMLEnvelope_literalMutationString4 */
    @Test(timeout = 10000)
    public void testStripHTMLEnvelope_literalMutationString4_literalMutationString60() throws Exception {
        Document document = this.cleaner.clean(new StringReader("<html><head><!body><p>test1</p><p>test2</p></b0dy></html>"));
        HTMLUtils.stripHTMLEnvelope(document);
        String String_5 = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><p>test1</p><p>test2</p></html>\n";
        // AssertGenerator create local variable with return value of invocation
        String o_testStripHTMLEnvelope_literalMutationString4__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><p>test1</p><p>test2</p></html>\n", o_testStripHTMLEnvelope_literalMutationString4__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><p>test1</p><p>test2</p></html>\n", String_5);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripHTMLEnvelope */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripHTMLEnvelope_literalMutationString4 */
    @Test(timeout = 10000)
    public void testStripHTMLEnvelope_literalMutationString4_literalMutationString63() throws Exception {
        Document document = this.cleaner.clean(new StringReader("<html><had><body><p>test1</p><p>test2</p></b0dy></html>"));
        HTMLUtils.stripHTMLEnvelope(document);
        String String_5 = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><p>test1</p><p>test2</p></html>\n";
        // AssertGenerator create local variable with return value of invocation
        String o_testStripHTMLEnvelope_literalMutationString4__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><p>test1</p><p>test2</p></html>\n", o_testStripHTMLEnvelope_literalMutationString4__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><p>test1</p><p>test2</p></html>\n", String_5);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripHTMLEnvelope */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripHTMLEnvelope_literalMutationString6 */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripHTMLEnvelope_literalMutationString6_literalMutationString93 */
    @Test(timeout = 10000)
    public void testStripHTMLEnvelope_literalMutationString6_literalMutationString93_literalMutationString1062() throws Exception {
        Document document = this.cleaner.clean(new StringReader("<html><head><body><p>test1</p><pMtest2</p></body></html>"));
        HTMLUtils.stripHTMLEnvelope(document);
        String String_7 = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><p>test1</p><p>test2</p></Jtml>\n";
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><p>test1</p><p>test2</p></Jtml>\n", String_7);
        // AssertGenerator create local variable with return value of invocation
        String o_testStripHTMLEnvelope_literalMutationString6__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><p>test1</p></html>\n", o_testStripHTMLEnvelope_literalMutationString6__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><p>test1</p><p>test2</p></Jtml>\n", String_7);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripHTMLEnvelope */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripHTMLEnvelope_literalMutationString4 */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripHTMLEnvelope_literalMutationString4_literalMutationString60 */
    @Test(timeout = 10000)
    public void testStripHTMLEnvelope_literalMutationString4_literalMutationString60_literalMutationString685() throws Exception {
        Document document = this.cleaner.clean(new StringReader("<html><head><!body><p>test1</p><p>test2</p></b0dy></html>"));
        HTMLUtils.stripHTMLEnvelope(document);
        String String_5 = (AmplDefaultHTMLCleanerTest.HEADER) + "";
        // AssertGenerator create local variable with return value of invocation
        String o_testStripHTMLEnvelope_literalMutationString4__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><p>test1</p><p>test2</p></html>\n", o_testStripHTMLEnvelope_literalMutationString4__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n", String_5);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph */
    @Test(timeout = 10000)
    public void testStripTopLevelParagraph_literalMutationString1685() throws Exception {
        Document document = this.cleaner.clean(new StringReader("<html><head /><body><p>test</p></body></html>"));
        HTMLUtils.stripFirstElementInside(document, "body", "<a href=\"http://xwiki.org\" target=\"_blank\">label</a>");
        String String_21 = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><body>test</body></html>\n";
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body>test</body></html>\n", String_21);
        // AssertGenerator create local variable with return value of invocation
        String o_testStripTopLevelParagraph_literalMutationString1685__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p>test</p></body></html>\n", o_testStripTopLevelParagraph_literalMutationString1685__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body>test</body></html>\n", String_21);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph */
    @Test(timeout = 10000)
    public void testStripTopLevelParagraph_literalMutationString1678() throws Exception {
        Document document = this.cleaner.clean(new StringReader("<html><head /><body><p>test</p></body></html>"));
        HTMLUtils.stripFirstElementInside(document, "", "p");
        String String_14 = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><body>test</body></html>\n";
        // AssertGenerator create local variable with return value of invocation
        String o_testStripTopLevelParagraph_literalMutationString1678__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p>test</p></body></html>\n", o_testStripTopLevelParagraph_literalMutationString1678__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body>test</body></html>\n", String_14);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph_literalMutationString1682 */
    @Test(timeout = 10000)
    public void testStripTopLevelParagraph_literalMutationString1682_literalMutationString1917() throws Exception {
        Document document = this.cleaner.clean(new StringReader("<html><head /><body><pItest</p></body></html>"));
        HTMLUtils.stripFirstElementInside(document, "boy", "p");
        String String_18 = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><body>test</body></html>\n";
        // AssertGenerator create local variable with return value of invocation
        String o_testStripTopLevelParagraph_literalMutationString1682__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body></body></html>\n", o_testStripTopLevelParagraph_literalMutationString1682__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body>test</body></html>\n", String_18);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph_literalMutationString1684 */
    @Test(timeout = 10000)
    public void testStripTopLevelParagraph_literalMutationString1684_literalMutationString1963() throws Exception {
        Document document = this.cleaner.clean(new StringReader("<html><head /><boy><p>test</p></body></html>"));
        HTMLUtils.stripFirstElementInside(document, "body", "");
        String String_20 = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><body>test</body></html>\n";
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body>test</body></html>\n", String_20);
        // AssertGenerator create local variable with return value of invocation
        String o_testStripTopLevelParagraph_literalMutationString1684__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p>test</p></body></html>\n", o_testStripTopLevelParagraph_literalMutationString1684__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body>test</body></html>\n", String_20);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph_literalMutationString1674 */
    @Test(timeout = 10000)
    public void testStripTopLevelParagraph_literalMutationString1674_literalMutationString1755() throws Exception {
        Document document = this.cleaner.clean(new StringReader("<a href=\"htt@://xwiki.org\" target=\"_blank\">label</a>"));
        HTMLUtils.stripFirstElementInside(document, "body", "p");
        String String_10 = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><body>test</body></html>\n";
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body>test</body></html>\n", String_10);
        // AssertGenerator create local variable with return value of invocation
        String o_testStripTopLevelParagraph_literalMutationString1674__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><a href=\"htt@://xwiki.org\" target=\"_blank\">label</a></body></html>\n", o_testStripTopLevelParagraph_literalMutationString1674__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body>test</body></html>\n", String_10);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph_literalMutationString1680 */
    @Test(timeout = 10000)
    public void testStripTopLevelParagraph_literalMutationString1680_literalMutationString1877() throws Exception {
        Document document = this.cleaner.clean(new StringReader(""));
        HTMLUtils.stripFirstElementInside(document, "zody", "p");
        String String_16 = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><body>test</body></html>\n";
        // AssertGenerator create local variable with return value of invocation
        String o_testStripTopLevelParagraph_literalMutationString1680__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body></body></html>\n", o_testStripTopLevelParagraph_literalMutationString1680__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body>test</body></html>\n", String_16);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph_literalMutationString1681 */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph_literalMutationString1681_literalMutationString1897 */
    @Test(timeout = 10000)
    public void testStripTopLevelParagraph_literalMutationString1681_literalMutationString1897_literalMutationString3962() throws Exception {
        Document document = this.cleaner.clean(new StringReader("<html><head /><body><p>test</p></bod_y></html>"));
        HTMLUtils.stripFirstElementInside(document, "", "p");
        String String_17 = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><body>test</body></html>\n";
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body>test</body></html>\n", String_17);
        // AssertGenerator create local variable with return value of invocation
        String o_testStripTopLevelParagraph_literalMutationString1681__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p>test</p></body></html>\n", o_testStripTopLevelParagraph_literalMutationString1681__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body>test</body></html>\n", String_17);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph_literalMutationString1685 */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph_literalMutationString1685_literalMutationString1985 */
    @Test(timeout = 10000)
    public void testStripTopLevelParagraph_literalMutationString1685_literalMutationString1985_literalMutationString4907() throws Exception {
        Document document = this.cleaner.clean(new StringReader("<tml><head /><body><p>test</p></body></html>"));
        HTMLUtils.stripFirstElementInside(document, "body", "<a href=\"http://xwiki.org\" target=\"_blank\">label</a>");
        String String_21 = (AmplDefaultHTMLCleanerTest.HEADER) + "";
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n", String_21);
        // AssertGenerator create local variable with return value of invocation
        String o_testStripTopLevelParagraph_literalMutationString1685__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p>test</p></body></html>\n", o_testStripTopLevelParagraph_literalMutationString1685__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n", String_21);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph_literalMutationString1684 */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph_literalMutationString1684_literalMutationString1969 */
    @Test(timeout = 10000)
    public void testStripTopLevelParagraph_literalMutationString1684_literalMutationString1969_literalMutationString3159() throws Exception {
        Document document = this.cleaner.clean(new StringReader("<html><shead /><body><p>test</p></body></html>"));
        HTMLUtils.stripFirstElementInside(document, "boFdy", "");
        String String_20 = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><body>test</body></html>\n";
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body>test</body></html>\n", String_20);
        // AssertGenerator create local variable with return value of invocation
        String o_testStripTopLevelParagraph_literalMutationString1684__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p>test</p></body></html>\n", o_testStripTopLevelParagraph_literalMutationString1684__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body>test</body></html>\n", String_20);
    }

    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph_literalMutationString1674 */
    /* amplification of org.xwiki.xml.html.HTMLUtilsTest#testStripTopLevelParagraph_literalMutationString1674_literalMutationString1755 */
    @Test(timeout = 10000)
    public void testStripTopLevelParagraph_literalMutationString1674_literalMutationString1755_literalMutationString4160() throws Exception {
        Document document = this.cleaner.clean(new StringReader("<a href=\"htt@://xwiki.org\" target=\"_blank\">label</a>"));
        HTMLUtils.stripFirstElementInside(document, "body", "p");
        String String_10 = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><bodyHtest</body></html>\n";
        // AssertGenerator create local variable with return value of invocation
        String o_testStripTopLevelParagraph_literalMutationString1674__6 = HTMLUtils.toString(document);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><a href=\"htt@://xwiki.org\" target=\"_blank\">label</a></body></html>\n", o_testStripTopLevelParagraph_literalMutationString1674__6);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><bodyHtest</body></html>\n", String_10);
    }

    /**
     * Test {@link UniqueIdFilter}.
     */
    /* amplification of org.xwiki.xml.internal.html.DefaultHTMLCleanerTest#duplicateIds */
    @Test(timeout = 10000)
    public void duplicateIds_literalMutationString126869() throws Exception {
        String actual = "Il&6TW|:*8uRH0ycq4Z5e<J&]T_4[=jRw?7/&P K#wo9*w";
        // AssertGenerator add assertion
        Assert.assertEquals("Il&6TW|:*8uRH0ycq4Z5e<J&]T_4[=jRw?7/&P K#wo9*w", actual);
        String expected = "<p id=\"x\">1</p><p id=\"xy\">2</p><p id=\"x0\">3</p>";
        // AssertGenerator add assertion
        Assert.assertEquals("<p id=\"x\">1</p><p id=\"xy\">2</p><p id=\"x0\">3</p>", expected);
        HTMLCleanerConfiguration config = this.mocker.getComponentUnderTest().getDefaultConfiguration();
        List<HTMLFilter> filters = new ArrayList<HTMLFilter>(config.getFilters());
        // AssertGenerator create local variable with return value of invocation
        boolean o_duplicateIds_literalMutationString126869__9 = filters.add(this.mocker.<HTMLFilter>getInstance(HTMLFilter.class, "uniqueId"));
        // AssertGenerator add assertion
        Assert.assertTrue(o_duplicateIds_literalMutationString126869__9);
        config.setFilters(filters);
        String String_6 = ((AmplDefaultHTMLCleanerTest.HEADER_FULL) + expected) + (AmplDefaultHTMLCleanerTest.FOOTER);
        // AssertGenerator create local variable with return value of invocation
        String o_duplicateIds_literalMutationString126869__13 = HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader(actual), config));
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p>Il&amp;6TW|:*8uRH0ycq4Z5e</p></body></html>\n", o_duplicateIds_literalMutationString126869__13);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p id=\"x\">1</p><p id=\"xy\">2</p><p id=\"x0\">3</p></body></html>\n", String_6);
        // AssertGenerator add assertion
        Assert.assertTrue(o_duplicateIds_literalMutationString126869__9);
        // AssertGenerator add assertion
        Assert.assertEquals("<p id=\"x\">1</p><p id=\"xy\">2</p><p id=\"x0\">3</p>", expected);
        // AssertGenerator add assertion
        Assert.assertEquals("Il&6TW|:*8uRH0ycq4Z5e<J&]T_4[=jRw?7/&P K#wo9*w", actual);
    }

    /**
     * Test {@link UniqueIdFilter}.
     */
    /* amplification of org.xwiki.xml.internal.html.DefaultHTMLCleanerTest#duplicateIds */
    /* amplification of org.xwiki.xml.internal.html.DefaultHTMLCleanerTest#duplicateIds_literalMutationString126877 */
    @Test(timeout = 10000)
    public void duplicateIds_literalMutationString126877_literalMutationString127107() throws Exception {
        String actual = "<p id=\"x\">1</p><pn id=\"xy\">2</p><p id=\"x\">3</p>";
        String expected = "<p id=\"x\">1</><p id=\"xy\">2</p><p id=\"x0\">3</p>";
        // AssertGenerator add assertion
        Assert.assertEquals("<p id=\"x\">1</><p id=\"xy\">2</p><p id=\"x0\">3</p>", expected);
        HTMLCleanerConfiguration config = this.mocker.getComponentUnderTest().getDefaultConfiguration();
        List<HTMLFilter> filters = new ArrayList<HTMLFilter>(config.getFilters());
        // AssertGenerator create local variable with return value of invocation
        boolean o_duplicateIds_literalMutationString126877__9 = filters.add(this.mocker.<HTMLFilter>getInstance(HTMLFilter.class, "uniqueId"));
        config.setFilters(filters);
        String String_14 = ((AmplDefaultHTMLCleanerTest.HEADER_FULL) + expected) + (AmplDefaultHTMLCleanerTest.FOOTER);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p id=\"x\">1</><p id=\"xy\">2</p><p id=\"x0\">3</p></body></html>\n", String_14);
        // AssertGenerator create local variable with return value of invocation
        String o_duplicateIds_literalMutationString126877__13 = HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader(actual), config));
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p id=\"x\">1</p><p>2</p><p id=\"x0\">3</p></body></html>\n", o_duplicateIds_literalMutationString126877__13);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p id=\"x\">1</><p id=\"xy\">2</p><p id=\"x0\">3</p></body></html>\n", String_14);
        // AssertGenerator add assertion
        Assert.assertEquals("<p id=\"x\">1</><p id=\"xy\">2</p><p id=\"x0\">3</p>", expected);
        // AssertGenerator add assertion
        Assert.assertEquals("<p id=\"x\">1</p><pn id=\"xy\">2</p><p id=\"x\">3</p>", actual);
    }

    /**
     * Verify that we can control what filters are used for cleaning.
     */
    /* amplification of org.xwiki.xml.internal.html.DefaultHTMLCleanerTest#explicitFilterList */
    /* amplification of org.xwiki.xml.internal.html.DefaultHTMLCleanerTest#explicitFilterList_literalMutationString147359 */
    /* amplification of org.xwiki.xml.internal.html.DefaultHTMLCleanerTest#explicitFilterList_literalMutationString147359_literalMutationString147431 */
    @Test(timeout = 10000)
    public void explicitFilterList_literalMutationString147359_literalMutationString147431_literalMutationString148233() throws ComponentLookupException {
        HTMLCleanerConfiguration configuration = this.mocker.getComponentUnderTest().getDefaultConfiguration();
        configuration.setFilters(Collections.<HTMLFilter>emptyList());
        String result = HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader("gqn(<PC,Q"), configuration));
        String String_26 = ((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "<a href=\"http://xwiki.org\" target=\"_blank\">label</a>") + (AmplDefaultHTMLCleanerTest.FOOTER);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><a href=\"http://xwiki.org\" target=\"_blank\">label</a></body></html>\n", String_26);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body>gqn(</body></html>\n", result);
    }

    /**
     * Verify that the restricted parameter works.
     */
    /* amplification of org.xwiki.xml.internal.html.DefaultHTMLCleanerTest#restrictedHtml */
    @Test(timeout = 10000)
    public void restrictedHtml_literalMutationString151445() throws ComponentLookupException {
        HTMLCleanerConfiguration configuration = this.mocker.getComponentUnderTest().getDefaultConfiguration();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.putAll(configuration.getParameters());
        // AssertGenerator create local variable with return value of invocation
        String o_restrictedHtml_literalMutationString151445__8 = parameters.put("restricted", "true");
        // AssertGenerator add assertion
        Assert.assertNull(o_restrictedHtml_literalMutationString151445__8);
        configuration.setParameters(parameters);
        String result = HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader("<script>alert(\"foo\")</script>"), configuration));
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>alert(\"foo\")</pre></body></html>\n", result);
        String String_66 = ((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "<pre>alert(\"foo\")</pre>") + (AmplDefaultHTMLCleanerTest.FOOTER);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>alert(\"foo\")</pre></body></html>\n", String_66);
        result = HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader("<tyle>p {color:white;}</style>"), configuration));
        String String_67 = ((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "<pre>p {color:white;}</pre>") + (AmplDefaultHTMLCleanerTest.FOOTER);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>p {color:white;}</pre></body></html>\n", String_67);
        // AssertGenerator add assertion
        Assert.assertNull(o_restrictedHtml_literalMutationString151445__8);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p>p {color:white;}</p></body></html>\n", result);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>alert(\"foo\")</pre></body></html>\n", String_66);
        // AssertGenerator add assertion
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p>p {color:white;}</p></body></html>\n", result);
    }

    /**
     * Verify that the restricted parameter works.
     */
    /* amplification of org.xwiki.xml.internal.html.DefaultHTMLCleanerTest#restrictedHtml */
    /* amplification of org.xwiki.xml.internal.html.DefaultHTMLCleanerTest#restrictedHtml_sd151457 */
    @Test(timeout = 10000)
    public void restrictedHtml_sd151457_failAssert5_literalMutationString152801() throws ComponentLookupException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            String __DSPOT_arg0_7482 = "qt@)Vb_b;){[dp8s5`:C";
            HTMLCleanerConfiguration configuration = this.mocker.getComponentUnderTest().getDefaultConfiguration();
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.putAll(configuration.getParameters());
            // StatementAdd: generate variable from return value
            String __DSPOT_invoc_8 = parameters.put("restricted", "true");
            configuration.setParameters(parameters);
            String result = HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader("<scrip>alert(\"foo\")</script>"), configuration));
            // AssertGenerator add assertion
            Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p>alert(\"foo\")</p></body></html>\n", result);
            String String_88 = ((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "<pre>alert(\"foo\")</pre>") + (AmplDefaultHTMLCleanerTest.FOOTER);
            // AssertGenerator add assertion
            Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>alert(\"foo\")</pre></body></html>\n", String_88);
            result = HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader("<style>p {color:white;}</style>"), configuration));
            // AssertGenerator add assertion
            Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>p {color:white;}</pre></body></html>\n", result);
            String String_89 = ((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "<pre>p {color:white;}</pre>") + (AmplDefaultHTMLCleanerTest.FOOTER);
            // AssertGenerator add assertion
            Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>p {color:white;}</pre></body></html>\n", String_89);
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_8.compareToIgnoreCase(__DSPOT_arg0_7482);
            org.junit.Assert.fail("restrictedHtml_sd151457 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /**
     * Verify that the restricted parameter works.
     */
    /* amplification of org.xwiki.xml.internal.html.DefaultHTMLCleanerTest#restrictedHtml */
    /* amplification of org.xwiki.xml.internal.html.DefaultHTMLCleanerTest#restrictedHtml_sd151475 */
    @Test(timeout = 10000)
    public void restrictedHtml_sd151475_failAssert22_literalMutationString153936() throws ComponentLookupException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg1_7505 = 45659417;
            int __DSPOT_arg0_7504 = -1012766684;
            HTMLCleanerConfiguration configuration = this.mocker.getComponentUnderTest().getDefaultConfiguration();
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.putAll(configuration.getParameters());
            // StatementAdd: generate variable from return value
            String __DSPOT_invoc_8 = parameters.put("restricted", "true");
            configuration.setParameters(parameters);
            String result = HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader("<script>alert(\"foo\")</script>"), configuration));
            // AssertGenerator add assertion
            Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>alert(\"foo\")</pre></body></html>\n", result);
            String String_124 = ((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "<pre>alert(\"foo\")</pre>") + (AmplDefaultHTMLCleanerTest.FOOTER);
            // AssertGenerator add assertion
            Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>alert(\"foo\")</pre></body></html>\n", String_124);
            result = HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader("<s/tyle>p {color:white;}</style>"), configuration));
            // AssertGenerator add assertion
            Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><del>p {color:white;}</del></body></html>\n", result);
            String String_125 = ((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "<pre>p {color:white;}</pre>") + (AmplDefaultHTMLCleanerTest.FOOTER);
            // AssertGenerator add assertion
            Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>p {color:white;}</pre></body></html>\n", String_125);
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_8.lastIndexOf(__DSPOT_arg0_7504, __DSPOT_arg1_7505);
            org.junit.Assert.fail("restrictedHtml_sd151475 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }

    /**
     * Verify that the restricted parameter works.
     */
    /* amplification of org.xwiki.xml.internal.html.DefaultHTMLCleanerTest#restrictedHtml */
    /* amplification of org.xwiki.xml.internal.html.DefaultHTMLCleanerTest#restrictedHtml_sd151492 */
    @Test(timeout = 10000)
    public void restrictedHtml_sd151492_failAssert39_literalMutationString155560_literalMutationString164222() throws ComponentLookupException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg1_7537 = 291281351;
            int __DSPOT_arg0_7536 = 826883364;
            HTMLCleanerConfiguration configuration = this.mocker.getComponentUnderTest().getDefaultConfiguration();
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.putAll(configuration.getParameters());
            // StatementAdd: generate variable from return value
            String __DSPOT_invoc_8 = parameters.put("restricted", "true");
            configuration.setParameters(parameters);
            String result = HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader("<vscript>alert(\"foo\")</script>"), configuration));
            // AssertGenerator add assertion
            Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><p>alert(\"foo\")</p></body></html>\n", result);
            String String_158 = ((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "<pre>alert(\"foo\")</pre>") + (AmplDefaultHTMLCleanerTest.FOOTER);
            // AssertGenerator add assertion
            Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>alert(\"foo\")</pre></body></html>\n", String_158);
            result = HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader("<style>p {color:white;}</style>"), configuration));
            // AssertGenerator add assertion
            Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body><pre>p {color:white;}</pre></body></html>\n", result);
            String String_159 = ((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "") + (AmplDefaultHTMLCleanerTest.FOOTER);
            // AssertGenerator add assertion
            Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n<html><head></head><body></body></html>\n", String_159);
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_8.substring(__DSPOT_arg0_7536, __DSPOT_arg1_7537);
            org.junit.Assert.fail("restrictedHtml_sd151492 should have thrown NullPointerException");
        } catch (NullPointerException eee) {
        }
    }
}

