/*
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
package org.xwiki.diff.script;

import java.io.StringReader;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.xwiki.component.annotation.Component;
import org.xwiki.diff.DiffException;
import org.xwiki.diff.xml.XMLDiffMarker;
import org.xwiki.diff.xml.XMLDiffPruner;
import org.xwiki.script.service.ScriptService;
import org.xwiki.stability.Unstable;
import org.xwiki.xml.XMLUtils;
import org.xwiki.xml.html.HTMLCleaner;
import org.xwiki.xml.html.HTMLCleanerConfiguration;
import org.xwiki.xml.html.HTMLUtils;

/**
 * Provides script oriented APIs to compute and display the changes between HTML documents.
 * 
 * @version $Id$
 * @since 11.6RC1
 */
@Component
@Named("diff.html")
@Singleton
@Unstable
public class DiffHTMLScriptService implements ScriptService
{
    @Inject
    private Logger logger;

    @Inject
    private HTMLCleaner htmlCleaner;

    @Inject
    @Named("html")
    private XMLDiffMarker htmlDiffMarker;

    @Inject
    @Named("html")
    private XMLDiffPruner htmlDiffPruner;

    /**
     * Helper object for manipulating DOM Level 3 Load and Save APIs.
     **/
    private DOMImplementationLS lsImpl;

    /**
     * Default component constructor.
     */
    public DiffHTMLScriptService()
    {
        try {
            this.lsImpl = (DOMImplementationLS) DOMImplementationRegistry.newInstance().getDOMImplementation("LS 3.0");
        } catch (Exception exception) {
            this.logger.warn("Cannot initialize the HTML Diff Script Service", exception);
        }
    }

    /**
     * Computes the changes between the given HTML fragments and returns them in the unified format.
     * 
     * @param previousHTML the previous version of the HTML
     * @param nextHTML the next version of the HTML
     * @return the changes between the given HTML fragments in unified format
     */
    public String unified(String previousHTML, String nextHTML)
    {
        Document previousDocument = parseHTML(previousHTML);
        try {
            if (!this.htmlDiffMarker.markDiff(previousDocument, parseHTML(nextHTML))) {
                // No changes detected.
                return "";
            }
        } catch (DiffException e) {
            // Failed to compute the changes.
            return null;
        }
        this.htmlDiffPruner.prune(previousDocument);
        return unwrap(HTMLUtils.toString(previousDocument, false, false).trim());
    }

    private Document parseHTML(String html)
    {
        // We need to clean the HTML because it may have been generated with the HTML macro using clean=false.
        return parseXML(cleanHTML(html));
    }

    private String cleanHTML(String html)
    {
        HTMLCleanerConfiguration config = this.htmlCleaner.getDefaultConfiguration();
        // We need to parse the clean HTML as XML later and we don't want to resolve the entity references from the DTD.
        config.setParameters(Collections.singletonMap(HTMLCleanerConfiguration.USE_CHARACTER_REFERENCES, "true"));
        Document htmlDoc = this.htmlCleaner.clean(new StringReader(wrap(html)), config);
        // We serialize and parse again the HTML as XML because the HTML Cleaner doesn't handle entity and character
        // references very well: they all end up as plain text (they are included in the value returned by
        // Node#getNodeValue()).
        return HTMLUtils.toString(htmlDoc);
    }

    private Document parseXML(String xml)
    {
        LSInput input = this.lsImpl.createLSInput();
        input.setStringData(xml);
        return XMLUtils.parse(input);
    }

    private String wrap(String fragment)
    {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE html>"
            + "<html xmlns=\"http://www.w3.org/1999/xhtml\"><head></head><body>" + fragment + "</body></html>";
    }

    private String unwrap(String html)
    {
        int start = html.indexOf("<body>") + 6;
        int end = html.indexOf("</body>");
        return html.substring(start, end);
    }
}
