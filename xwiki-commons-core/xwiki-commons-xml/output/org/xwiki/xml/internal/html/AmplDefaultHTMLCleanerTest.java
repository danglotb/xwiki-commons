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
package org.xwiki.xml.internal.html;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.mockito.MockitoComponentMockingRule;
import org.xwiki.xml.XMLUtils;
import org.xwiki.xml.html.AmplHTMLUtilsTest;
import org.xwiki.xml.html.HTMLCleaner;
import org.xwiki.xml.html.HTMLCleanerConfiguration;
import org.xwiki.xml.html.HTMLUtils;
import org.xwiki.xml.html.filter.HTMLFilter;
import org.xwiki.xml.internal.html.filter.AttributeFilter;
import org.xwiki.xml.internal.html.filter.BodyFilter;
import org.xwiki.xml.internal.html.filter.FontFilter;
import org.xwiki.xml.internal.html.filter.LinkFilter;
import org.xwiki.xml.internal.html.filter.ListFilter;
import org.xwiki.xml.internal.html.filter.ListItemFilter;
import org.xwiki.xml.internal.html.filter.UniqueIdFilter;


/**
 * Unit tests for {@link org.xwiki.xml.internal.html.DefaultHTMLCleaner}.
 *
 * @version $Id$
 * @since 1.6M1
 */
@ComponentList({ ListFilter.class, ListItemFilter.class, FontFilter.class, BodyFilter.class, AttributeFilter.class, UniqueIdFilter.class, DefaultHTMLCleaner.class, LinkFilter.class })
public class AmplDefaultHTMLCleanerTest {
    public static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + ("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" " + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n");

    private static final String HEADER_FULL = (AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><body>";

    private static final String FOOTER = "</body></html>\n";

    @Rule
    public final MockitoComponentMockingRule<HTMLCleaner> mocker = new MockitoComponentMockingRule<HTMLCleaner>(DefaultHTMLCleaner.class);

    @Test
    public void elementExpansion() throws ComponentLookupException {
        assertHTML("<p><textarea></textarea></p>", "<textarea/>");
        // Verify exceptions (by default elements are expanded).
        assertHTML("<p><br /></p>", "<p><br></p>");
        assertHTML("<hr />", "<hr>");
    }

    @Test
    public void specialCharacters() throws ComponentLookupException {
        // TODO: We still have a problem I think in that if there are characters such as "&" or quote in the source
        // text they are not escaped. This is because we have use "false" in DefaultHTMLCleaner here:
        // Document document = new JDomSerializer(this.cleanerProperties, false).createJDom(cleanedNode);
        // See the problem described here: http://sourceforge.net/forum/forum.php?thread_id=2243880&forum_id=637246
        assertHTML("<p>&quot;&amp;**notbold**&lt;notag&gt;&nbsp;</p>", "<p>&quot;&amp;**notbold**&lt;notag&gt;&nbsp;</p>");
        assertHTML("<p>\"&amp;</p>", "<p>\"&</p>");
        assertHTML("<p><img src=\"http://host.com/a.gif?a=foo&amp;b=bar\" /></p>", "<img src=\"http://host.com/a.gif?a=foo&b=bar\" />");
        assertHTML("<p>&#xA;</p>", "<p>&#xA;</p>");
        // Verify that double quotes are escaped in attribute values
        assertHTML("<p value=\"script:&quot;&quot;\"></p>", "<p value=\'script:\"\"\'");
    }

    @Test
    public void closeUnbalancedTags() throws ComponentLookupException {
        assertHTML("<hr /><p>hello</p>", "<hr><p>hello");
    }

    @Test
    public void conversionsFromHTML() throws ComponentLookupException {
        assertHTML("<p>this <strong>is</strong> bold</p>", "this <b>is</b> bold");
        assertHTML("<p><em>italic</em></p>", "<i>italic</i>");
        assertHTML("<del>strike</del>", "<strike>strike</strike>");
        assertHTML("<del>strike</del>", "<s>strike</s>");
        assertHTML("<ins>strike</ins>", "<u>strike</u>");
        assertHTML("<p style=\"text-align:center\">center</p>", "<center>center</center>");
        assertHTML("<p><span style=\"color:red;font-family:Arial;font-size:1.0em;\">This is some text!</span></p>", "<font face=\"Arial\" size=\"3\" color=\"red\">This is some text!</font>");
        assertHTML("<p><span style=\"font-size:1.6em;\">This is some text!</span></p>", "<font size=\"+3\">This is some text!</font>");
        assertHTML(("<table><tbody><tr><td style=\"text-align:right;background-color:red;vertical-align:top\">" + "x</td></tr></tbody></table>"), "<table><tr><td align=right valign=top bgcolor=red>x</td></tr></table>");
    }

    @Test
    public void convertImageAlignment() throws ComponentLookupException {
        assertHTML("<p><img style=\"float:left\" /></p>", "<img align=\"left\"/>");
        assertHTML("<p><img style=\"float:right\" /></p>", "<img align=\"right\"/>");
        assertHTML("<p><img style=\"vertical-align:top\" /></p>", "<img align=\"top\"/>");
        assertHTML("<p><img style=\"vertical-align:middle\" /></p>", "<img align=\"middle\"/>");
        assertHTML("<p><img style=\"vertical-align:bottom\" /></p>", "<img align=\"bottom\"/>");
    }

    @Test
    public void convertImplicitParagraphs() throws ComponentLookupException {
        assertHTML("<p>word1</p><p>word2</p><p>word3</p><hr /><p>word4</p>", "word1<p>word2</p>word3<hr />word4");
        // Don't convert when there are only spaces or new lines
        assertHTML("<p>word1</p>  \n  <p>word2</p>", "<p>word1</p>  \n  <p>word2</p>");
        // Ensure that whitespaces at the end works.
        assertHTML("\n ", "\n ");
        // Ensure that comments are not wrapped
        assertHTML("<!-- comment1 -->\n<p>hello</p>\n<!-- comment2 -->", "<!-- comment1 -->\n<p>hello</p>\n<!-- comment2 -->");
        // Ensure that comments don't prevent other elements to be wrapped with paragraphs.
        assertHTML("<!-- comment --><p><span>hello</span><!-- comment --></p><p>world</p>", "<!-- comment --><span>hello</span><!-- comment --><p>world</p>");
    }

    @Test
    public void cleanNonXHTMLLists() throws ComponentLookupException {
        // Fixing invalid list item.
        assertHTML("<ul><li>item</li></ul>", "<li>item</li>");
        assertHTML("<ul><li>item1<ul><li>item2</li></ul></li></ul>", "<ul><li>item1</li><ul><li>item2</li></ul></ul>");
        assertHTML("<ul><li>item1<ul><li>item2<ul><li>item3</li></ul></li></ul></li></ul>", "<ul><li>item1</li><ul><li>item2</li><ul><li>item3</li></ul></ul></ul>");
        assertHTML("<ul><li style=\"list-style-type: none\"><ul><li>item</li></ul></li></ul>", "<ul><ul><li>item</li></ul></ul>");
        assertHTML("<ul> <li style=\"list-style-type: none\"><ul><li>item</li></ul></li></ul>", "<ul> <ul><li>item</li></ul></ul>");
        assertHTML("<ul><li>item1<ol><li>item2</li></ol></li></ul>", "<ul><li>item1</li><ol><li>item2</li></ol></ul>");
        assertHTML("<ol><li>item1<ol><li>item2<ol><li>item3</li></ol></li></ol></li></ol>", "<ol><li>item1</li><ol><li>item2</li><ol><li>item3</li></ol></ol></ol>");
        assertHTML("<ol><li style=\"list-style-type: none\"><ol><li>item</li></ol></li></ol>", "<ol><ol><li>item</li></ol></ol>");
        assertHTML(("<ul><li>item1<ul><li style=\"list-style-type: none\"><ul><li>item2</li></ul></li>" + "<li>item3</li></ul></li></ul>"), "<ul><li>item1</li><ul><ul><li>item2</li></ul><li>item3</li></ul></ul>");
        assertHTML("<ul>\n\n<li style=\"list-style-type: none\"><p>text</p></li></ul>", "<ul>\n\n<p>text</p></ul>");
        assertHTML("<ul><li>item<p>text</p></li><!--x-->  </ul>", "<ul><li>item</li><!--x-->  <p>text</p></ul>");
        assertHTML("<ul> \n<li style=\"list-style-type: none\"><em>1</em>2<ins>3</ins></li><!--x--><li>item</li></ul>", "<ul> \n<em>1</em><!--x-->2<ins>3</ins><li>item</li></ul>");
    }

    /**
     * Verify that scripts are not cleaned and that we can have a CDATA section inside. Also verify CDATA behaviors.
     */
    @Test
    public void scriptAndCData() throws ComponentLookupException {
        assertHTML("<script type=\"text/javascript\">//<![CDATA[\n\nalert(\"Hello World\")\n\n//]]></script>", "<script type=\"text/javascript\"><![CDATA[\nalert(\"Hello World\")\n]]></script>");
        assertHTML("<script type=\"text/javascript\">//<![CDATA[\n//\nalert(\"Hello World\")\n\n//]]></script>", "<script type=\"text/javascript\">//<![CDATA[\nalert(\"Hello World\")\n//]]></script>");
        assertHTML("<script type=\"text/javascript\">//<![CDATA[\n\nalert(\"Hello World\")\n\n//]]></script>", "<script type=\"text/javascript\">/*<![CDATA[*/\nalert(\"Hello World\")\n/*]]>*/</script>");
        assertHTML(("<script type=\"text/javascript\">//<![CDATA[\n\n\n" + (((("function escapeForXML(origtext) {\n" + "   return origtext.replace(/\\&/g,\'&\'+\'amp;\').replace(/</g,\'&\'+\'lt;\')\n") + "       .replace(/>/g,\'&\'+\'gt;\').replace(/\'/g,\'&\'+\'apos;\').replace(/\"/g,\'&\'+\'quot;\');") + "}\n\n\n//]]>") + "</script>")), ("<script type=\"text/javascript\">\n" + (((((("/*<![CDATA[*/\n" + "function escapeForXML(origtext) {\n") + "   return origtext.replace(/\\&/g,\'&\'+\'amp;\').replace(/</g,\'&\'+\'lt;\')\n") + "       .replace(/>/g,\'&\'+\'gt;\').replace(/\'/g,\'&\'+\'apos;\').replace(/\"/g,\'&\'+\'quot;\');") + "}\n") + "/*]]>*/\n") + "</script>")));
        assertHTML("<script>//<![CDATA[\n<>\n//]]></script>", "<script>&lt;&gt;</script>");
        assertHTML("<script>//<![CDATA[\n<>\n//]]></script>", "<script><></script>");
        // Verify that CDATA not inside SCRIPT or STYLE elements are considered comments in HTML and thus stripped
        // when cleaned.
        assertHTML("<p></p>", "<p><![CDATA[&]]></p>");
        assertHTML("<p>&amp;&amp;</p>", "<p>&<![CDATA[&]]>&</p>");
    }

    /**
     * Verify that inline style elements are not cleaned and that we can have a CDATA section inside.
     */
    @Test
    public void styleAndCData() throws ComponentLookupException {
        assertHTMLWithHeadContent("<style type=\"text/css\">/*<![CDATA[*/\na { color: red; }\n/*]]>*/</style>", "<style type=\"text/css\"><![CDATA[\na { color: red; }\n]]></style>");
        assertHTMLWithHeadContent("<style type=\"text/css\">/*<![CDATA[*/\na { color: red; }\n/*]]>*/</style>", "<style type=\"text/css\">/*<![CDATA[*/\na { color: red; }\n/*]]>*/</style>");
        assertHTMLWithHeadContent("<style type=\"text/css\">/*<![CDATA[*/a>span { color: blue;}\n/*]]>*/</style>", "<style type=\"text/css\">a&gt;span { color: blue;}</style>");
        assertHTMLWithHeadContent("<style>/*<![CDATA[*/<>\n/*]]>*/</style>", "<style>&lt;&gt;</style>");
        assertHTMLWithHeadContent("<style>/*<![CDATA[*/<>\n/*]]>*/</style>", "<style><></style>");
    }

    /**
     * Verify that we can control what filters are used for cleaning.
     */
    @Test
    public void explicitFilterList() throws ComponentLookupException {
        HTMLCleanerConfiguration configuration = this.mocker.getComponentUnderTest().getDefaultConfiguration();
        configuration.setFilters(Collections.<HTMLFilter>emptyList());
        String result = HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader("something"), configuration));
        // Note that if the default Body filter had been executed the result would have been:
        // <p>something</p>.
        Assert.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "something") + (AmplDefaultHTMLCleanerTest.FOOTER)), result);
    }

    /**
     * Verify that the restricted parameter works.
     */
    @Test
    public void restrictedHtml() throws ComponentLookupException {
        HTMLCleanerConfiguration configuration = this.mocker.getComponentUnderTest().getDefaultConfiguration();
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.putAll(configuration.getParameters());
        parameters.put("restricted", "true");
        configuration.setParameters(parameters);
        String result = HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader("<script>alert(\"foo\")</script>"), configuration));
        Assert.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "<pre>alert(\"foo\")</pre>") + (AmplDefaultHTMLCleanerTest.FOOTER)), result);
        result = HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader("<style>p {color:white;}</style>"), configuration));
        Assert.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "<pre>p {color:white;}</pre>") + (AmplDefaultHTMLCleanerTest.FOOTER)), result);
    }

    /**
     * Verify that passing a fully-formed XHTML header works fine.
     */
    @Test
    public void fullXHTMLHeader() throws ComponentLookupException {
        assertHTML("<p>test</p>", (((AmplDefaultHTMLCleanerTest.HEADER_FULL) + "<p>test</p>") + (AmplDefaultHTMLCleanerTest.FOOTER)));
    }

    /**
     * Test {@link UniqueIdFilter}.
     */
    @Test
    public void duplicateIds() throws Exception {
        String actual = "<p id=\"x\">1</p><p id=\"xy\">2</p><p id=\"x\">3</p>";
        String expected = "<p id=\"x\">1</p><p id=\"xy\">2</p><p id=\"x0\">3</p>";
        HTMLCleanerConfiguration config = this.mocker.getComponentUnderTest().getDefaultConfiguration();
        List<HTMLFilter> filters = new ArrayList<HTMLFilter>(config.getFilters());
        filters.add(this.mocker.<HTMLFilter>getInstance(HTMLFilter.class, "uniqueId"));
        config.setFilters(filters);
        Assert.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER_FULL) + expected) + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader(actual), config)));
    }

    /**
     * Test that tags with a namespace are not considered as unknown tags by HTMLCleaner (see also <a
     * href="https://jira.xwiki.org/browse/XWIKI-9753">XWIKI-9753</a>).
     */
    @Test
    public void cleanSVGTags() throws Exception {
        String input = "<p>before</p>\n" + ((("<p><svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\">\n" + "<circle cx=\"100\" cy=\"50\" fill=\"red\" r=\"40\" stroke=\"black\" stroke-width=\"2\"></circle>\n") + "</svg></p>\n") + "<p>after</p>\n");
        assertHTML(input, (((AmplDefaultHTMLCleanerTest.HEADER_FULL) + input) + (AmplDefaultHTMLCleanerTest.FOOTER)));
    }

    /**
     * Test that cleaning works when there's a TITLE element in the body (but with a namespace). The issue was that
     * HTMLCleaner would consider it a duplicate of the TITLE element in the HEAD even though it's namespaced. (see also
     * <a href="https://jira.xwiki.org/browse/XWIKI-9753">XWIKI-9753</a>).
     */
    @Test
    @Ignore("See https://jira.xwiki.org/browse/XWIKI-9753")
    public void cleanTitleWithNamespace() throws Exception {
        // Test with TITLE in HEAD
        String input = "<html xmlns=\"http://www.w3.org/1999/xhtml\" lang=\"en\" xml:lang=\"en\">\n" + (((((((((((("  <head>\n" + "    <title>Title test</title>\n") + "  </head>\n") + "  <body>\n") + "    <p>before</p>\n") + "    <svg xmlns=\"http://www.w3.org/2000/svg\" height=\"300\" width=\"500\">\n") + "      <g>\n") + "        <title>SVG Title Demo example</title>\n") + "        <rect height=\"50\" style=\"fill:none; stroke:blue; stroke-width:1px\" width=\"200\" x=\"10\" ") + "y=\"10\"></rect>\n") + "      </g>\n") + "    </svg>\n") + "    <p>after</p>\n");
        Assert.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER) + input) + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader(input))));
    }

    /**
     * Verify that a xmlns namespace set on the HTML element is not removed by default and it's removed if
     * {@link HTMLCleanerConfiguration#NAMESPACES_AWARE} is set to false.
     */
    @Test
    @Ignore("See https://sourceforge.net/p/htmlcleaner/bugs/168/")
    public void cleanHTMLTagWithNamespace() throws Exception {
        String input = "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head></head><body>";
        // Default
        Assert.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER) + input) + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader(input))));
        // Configured for namespace awareness being false
        HTMLCleanerConfiguration config = this.mocker.getComponentUnderTest().getDefaultConfiguration();
        config.setParameters(Collections.singletonMap(HTMLCleanerConfiguration.NAMESPACES_AWARE, "false"));
        Assert.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER) + "<html><head></head><body>") + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader(input), config)));
    }

    /**
     * Test that cleaning an empty DIV works (it used to fail, see <a
     * href="https://jira.xwiki.org/browse/XWIKI-4007">XWIKI-4007</a>).
     */
    @Test
    public void cleanEmptyDIV() throws Exception {
        String input = "<div id=\"y\"></div><div id=\"z\">something</div>";
        assertHTML(input, (((AmplDefaultHTMLCleanerTest.HEADER_FULL) + input) + (AmplDefaultHTMLCleanerTest.FOOTER)));
    }

    @Test
    public void verifyLegendTagNotStripped() throws Exception {
        String input = "<fieldset><legend>test</legend><div>content</div></fieldset>";
        assertHTML(input, (((AmplDefaultHTMLCleanerTest.HEADER_FULL) + input) + (AmplDefaultHTMLCleanerTest.FOOTER)));
    }

    @Test
    public void verifySpanIsExpanded() throws Exception {
        assertHTML("<p><span class=\"fa fa-icon\"></span></p>", "<span class=\"fa fa-icon\" />");
    }

    @Test
    public void verifyExternalLinksAreSecure() throws Exception {
        assertHTML("<p><a href=\"relativeLink\" target=\"_blank\">label</a></p>", "<a href=\"relativeLink\" target=\"_blank\">label</a>");
        assertHTML("<p><a href=\"http://xwiki.org\" rel=\" noopener noreferrer\" target=\"_blank\">label</a></p>", "<a href=\"http://xwiki.org\" target=\"_blank\">label</a>");
        assertHTML("<p><a href=\"http://xwiki.org\" rel=\" noopener noreferrer\" target=\"someframe\">label</a></p>", "<a href=\"http://xwiki.org\" target=\"someframe\">label</a>");
        assertHTML("<p><a href=\"http://xwiki.org\" target=\"_top\">label</a></p>", "<a href=\"http://xwiki.org\" target=\"_top\">label</a>");
        assertHTML("<p><a href=\"http://xwiki.org\" target=\"_parent\">label</a></p>", "<a href=\"http://xwiki.org\" target=\"_parent\">label</a>");
        assertHTML("<p><a href=\"http://xwiki.org\" target=\"_self\">label</a></p>", "<a href=\"http://xwiki.org\" target=\"_self\">label</a>");
        assertHTML("<p><a href=\"http://xwiki.org\" rel=\"noopener noreferrer\" target=\"_blank\">label</a></p>", "<a href=\"http://xwiki.org\" target=\"_blank\" rel=\"noopener\">label</a>");
        assertHTML("<p><a href=\"http://xwiki.org\" rel=\"noreferrer noopener\" target=\"_blank\">label</a></p>", "<a href=\"http://xwiki.org\" target=\"_blank\" rel=\"noreferrer\">label</a>");
        assertHTML("<p><a href=\"http://xwiki.org\" rel=\"hello noopener noreferrer\" target=\"_blank\">label</a></p>", "<a href=\"http://xwiki.org\" target=\"_blank\" rel=\"hello\">label</a>");
    }

    @Test
    public void verifyEntitiesAreNotBroken() throws Exception {
        assertHTML("<p>&Eacute;</p>", "&Eacute;");
    }

    /**
     *
     *
     * @see <a href="https://jira.xwiki.org/browse/XCOMMONS-1293">XCOMMONS-1293</a>
     */
    @Test
    public void verifyIFRAMECleaning() throws Exception {
        // TODO: these 2 lines need to be changed to the following when https://jira.xwiki.org/browse/XCOMMONS-1292 is
        // fixed:
        // assertHTML("<iframe src=\"whatever\"></iframe>", "<iframe src=\"whatever\"/>");
        // assertHTML("<iframe src=\"whatever\"></iframe>\r\n", "<iframe src=\"whatever\"/>\r\n");
        assertHTML("<p><iframe src=\"whatever\"></iframe></p>", "<iframe src=\"whatever\"/>");
        assertHTML("<p><iframe src=\"whatever\"></iframe>\r\n</p>", "<iframe src=\"whatever\"/>\r\n");
        assertHTML("<p>\r\n<iframe src=\"whatever\"></iframe></p>", "\r\n<iframe src=\"whatever\"/>");
        assertHTML("<p>\r\n<iframe src=\"whatever\"></iframe>\r\n</p>", "\r\n<iframe src=\"whatever\"/>\r\n");
        assertHTML("<p><iframe src=\"whatever\"></iframe><iframe src=\"whatever\"></iframe></p>", "<iframe src=\"whatever\"/><iframe src=\"whatever\"/>");
        assertHTML("<p><iframe src=\"whatever\"></iframe>\r\n<iframe src=\"whatever\"></iframe></p>", "<iframe src=\"whatever\"/>\r\n<iframe src=\"whatever\"/>");
        assertHTML("<p>\r\n<iframe src=\"whatever\"></iframe>\r\n<iframe src=\"whatever\"></iframe>\r\n</p>", "\r\n<iframe src=\"whatever\"/>\r\n<iframe src=\"whatever\"/>\r\n");
    }

    private void assertHTML(String expected, String actual) throws ComponentLookupException {
        Assert.assertEquals((((AmplDefaultHTMLCleanerTest.HEADER_FULL) + expected) + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader(actual))));
    }

    private void assertHTMLWithHeadContent(String expected, String actual) throws ComponentLookupException {
        Assert.assertEquals((((((AmplDefaultHTMLCleanerTest.HEADER) + "<html><head>") + expected) + "</head><body>") + (AmplDefaultHTMLCleanerTest.FOOTER)), HTMLUtils.toString(this.mocker.getComponentUnderTest().clean(new StringReader(actual))));
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

