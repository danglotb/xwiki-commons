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
package org.xwiki.xml;


import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link org.xwiki.xml.XMLUtils}.
 *
 * @version $Id$
 * @since 1.6M1
 */
public class AmplXMLUtilsTest {
    @Test
    public void escapeXMLComment() {
        Assert.assertEquals("-\\- ", XMLUtils.escapeXMLComment("-- "));
        Assert.assertEquals("-\\", XMLUtils.escapeXMLComment("-"));
        Assert.assertEquals("-\\-\\-\\", XMLUtils.escapeXMLComment("---"));
        Assert.assertEquals("- ", XMLUtils.escapeXMLComment("- "));
    }

    @Test
    public void unescapeXMLComment() {
        Assert.assertEquals("", XMLUtils.unescapeXMLComment("\\"));
        Assert.assertEquals("\\", XMLUtils.unescapeXMLComment("\\\\"));
        Assert.assertEquals("--", XMLUtils.unescapeXMLComment("\\-\\-"));
        Assert.assertEquals("--", XMLUtils.unescapeXMLComment("\\-\\-\\"));
    }

    @Test
    public void escape() {
        String escapedText = XMLUtils.escape("a < a\' && a\' < a\" => a < a\"");
        Assert.assertFalse("Failed to escape <", escapedText.contains("<"));
        Assert.assertFalse("Failed to escape >", escapedText.contains(">"));
        Assert.assertFalse("Failed to escape '", escapedText.contains("'"));
        Assert.assertFalse("Failed to escape \"", escapedText.contains("\""));
        Assert.assertFalse("Failed to escape &", escapedText.contains("&&"));
    }

    @Test
    public void escapeApos() {
        Assert.assertFalse("' wrongly escaped to non-HTML &apos;", XMLUtils.escape("'").equals("&apos;"));
    }

    @Test
    public void escapeEmptyString() {
        Assert.assertEquals("\"\" should be \"\"", "", XMLUtils.escape(""));
    }

    @Test
    public void escapeWithNull() {
        Assert.assertNull("null should be null", XMLUtils.escape(null));
    }

    @Test
    public void escapeNonAscii() {
        Assert.assertEquals("Non-ASCII characters were escaped", "\u0123", XMLUtils.escape("\u0123"));
    }

    @Test
    public void escapeAttributeValueApos() {
        Assert.assertFalse("' wrongly escaped to non-HTML &apos;", XMLUtils.escapeAttributeValue("'").equals("&apos;"));
    }

    @Test
    public void escapeFAttributeValueEmptyString() {
        Assert.assertEquals("\"\" should be \"\"", "", XMLUtils.escapeAttributeValue(""));
    }

    @Test
    public void escapeFAttributeValueWithNull() {
        Assert.assertNull("null should be null", XMLUtils.escapeAttributeValue(null));
    }

    @Test
    public void escapeAttributeValueNonAscii() {
        Assert.assertEquals("Non-ASCII characters were escaped", "\u0123", XMLUtils.escapeAttributeValue("\u0123"));
    }

    @Test
    public void escapeElementContentEmptyString() {
        Assert.assertEquals("\"\" should be \"\"", "", XMLUtils.escapeElementContent(""));
    }

    @Test
    public void escapeElementContentWithNull() {
        Assert.assertNull("null should be null", XMLUtils.escapeElementContent(null));
    }

    @Test
    public void escapeElementContentNonAscii() {
        Assert.assertEquals("Non-ASCII characters were escaped", "\u0123", XMLUtils.escapeElementContent("\u0123"));
    }

    @Test
    public void unescape() {
        Assert.assertEquals("Failed to unescaped named entities", "&\'\"<>", XMLUtils.unescape("&amp;&apos;&quot;&lt;&gt;"));
        Assert.assertEquals("Failed to unescaped decimal entities", "&\'\"<>", XMLUtils.unescape("&#38;&#39;&#34;&#60;&#62;"));
        Assert.assertEquals("Failed to unescaped decimal entities with leading zeros", "&\'\"<>", XMLUtils.unescape("&#038;&#0039;&#00034;&#000060;&#0000062;"));
        Assert.assertEquals("Failed to unescaped hexadecimal entities", "&\'\"<<>>", XMLUtils.unescape("&#x26;&#x27;&#x22;&#x3c;&#x3C;&#x3e;&#x3E;"));
        Assert.assertEquals("Failed to unescaped hexadecimal entities with leading zeros", "&\'\"<<>>", XMLUtils.unescape("&#x026;&#x0027;&#x00022;&#x00003c;&#x0003C;&#x003e;&#x03E;"));
    }

    @Test
    public void unescapeEmptyString() {
        Assert.assertEquals("\"\" should be \"\"", "", XMLUtils.unescape(""));
    }

    @Test
    public void unescapeWithNull() {
        Assert.assertNull("null should be null", XMLUtils.unescape(null));
    }

    @Test
    public void unescapeOtherEscapes() {
        Assert.assertEquals("Extra named entities were unescaped", "&deg;", XMLUtils.unescape("&deg;"));
        Assert.assertEquals("Extra decimal entities were unescaped", "&#65;", XMLUtils.unescape("&#65;"));
        Assert.assertEquals("Extra hexadecimal entities were unescaped", "&#x5;", XMLUtils.unescape("&#x5;"));
    }

    @Test
    public void createDomDocument() {
        // Nothing much that we can test here...
        Assert.assertNotNull(XMLUtils.createDOMDocument());
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
}

