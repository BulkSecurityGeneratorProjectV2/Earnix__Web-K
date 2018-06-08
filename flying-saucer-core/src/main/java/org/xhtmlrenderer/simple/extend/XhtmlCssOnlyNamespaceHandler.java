/*
 * Copyright (c) 2005 Torbjoern Gannholm
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 *
 */
package org.xhtmlrenderer.simple.extend;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.CDataNode;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.xhtmlrenderer.css.extend.StylesheetFactory;
import org.xhtmlrenderer.css.sheet.Stylesheet;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;
import org.xhtmlrenderer.simple.NoNamespaceHandler;
import org.xhtmlrenderer.util.Configuration;
import org.xhtmlrenderer.util.XRLog;

/**
 * Handles xhtml but only css styling is honored,
 * no presentational html attributes (see css 2.1 spec, 6.4.4)
 */
public class XhtmlCssOnlyNamespaceHandler extends NoNamespaceHandler {

    /**
     * Description of the Field
     */
    final static String _namespace = "http://www.w3.org/1999/xhtml";

    private static StylesheetInfo _defaultStylesheet;
    private static boolean _defaultStylesheetError = false;

    private final Map _metadata = null;

    /**
     * Gets the namespace attribute of the XhtmlNamespaceHandler object
     *
     * @return The namespace value
     */
    public String getNamespace() {
        return _namespace;
    }

    /**
     * Gets the class attribute of the XhtmlNamespaceHandler object
     *
     * @param e PARAM
     * @return The class value
     */
    public String getClass(org.w3c.dom.Element e) {
        return e.getAttribute("class");
    }

    /**
     * Gets the iD attribute of the XhtmlNamespaceHandler object
     *
     * @param e PARAM
     * @return The iD value
     */
    public String getID(org.w3c.dom.Element e) {
        String result = e.getAttribute("id").trim();
        return result.length() == 0 ? null : result;
    }

    protected String convertToLength(String value) {
        if (isInteger(value)) {
            return value + "px";
        } else {
            return value;
        }
    }

    protected boolean isInteger(String value) {
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (! (c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }

    protected String getAttribute(Element e, String attrName) {
        String result = e.attr(attrName);
        result = result.trim();
        return result.length() == 0 ? null : result;
    }

    /**
     * Gets the elementStyling attribute of the XhtmlNamespaceHandler object
     *
     * @param e PARAM
     * @return The elementStyling value
     */
    public String getElementStyling(Element e) {
        StringBuffer style = new StringBuffer();
        if (e.nodeName().equals("td") || e.nodeName().equals("th")) {
            String s;
            s = getAttribute(e, "colspan");
            if (s != null) {
                style.append("-fs-table-cell-colspan: ");
                style.append(s);
                style.append(";");
            }
            s = getAttribute(e, "rowspan");
            if (s != null) {
                style.append("-fs-table-cell-rowspan: ");
                style.append(s);
                style.append(";");
            }
        } else if (e.nodeName().equals("img")) {
            String s;
            s = getAttribute(e, "width");
            if (s != null) {
                style.append("width: ");
                style.append(convertToLength(s));
                style.append(";");
            }
            s = getAttribute(e, "height");
            if (s != null) {
                style.append("height: ");
                style.append(convertToLength(s));
                style.append(";");
            }
        } else if (e.nodeName().equals("canvas")) {
            String s;
            s = getAttribute(e, "width");
            if (s != null) {
                style.append("width: ");
                style.append(convertToLength(s));
                style.append(";");
            }
            s = getAttribute(e, "height");
            if (s != null) {
                style.append("height: ");
                style.append(convertToLength(s));
                style.append(";");
            }
        } else if (e.nodeName().equals("colgroup") || e.nodeName().equals("col")) {
            String s;
            s = getAttribute(e, "span");
            if (s != null) {
                style.append("-fs-table-cell-colspan: ");
                style.append(s);
                style.append(";");
            }
            s = getAttribute(e, "width");
            if (s != null) {
                style.append("width: ");
                style.append(convertToLength(s));
                style.append(";");
            }
        }
        style.append(e.attr("style"));
        return style.toString();
    }

    /**
     * Gets the linkUri attribute of the XhtmlNamespaceHandler object
     *
     * @param e PARAM
     * @return The linkUri value
     */
    public String getLinkUri(Element e) {
        String href = null;
        if (e.nodeName().equalsIgnoreCase("a") && e.hasAttr("href")) {
            href = e.attr("href");
        }
        return href;
    }

    public String getAnchorName(Element e) {
        if (e != null && e.nodeName().equalsIgnoreCase("a") &&
                e.hasAttr("name")) {
            return e.attr("name");
        }
        return null;
    }

    private static String readTextContent(org.jsoup.nodes.Element element) {
        StringBuffer result = new StringBuffer();
        org.jsoup.nodes.Node current = element.childNode(0);
        while (current != null) {
//            short nodeType = current.getNodeType();
            if (current instanceof TextNode) {
                TextNode t = (TextNode)current;
                result.append(t.getWholeText());
            }
            current = current.nextSibling();
        }
        return result.toString();
    }

    private static String collapseWhiteSpace(String text) {
        StringBuffer result = new StringBuffer();
        int l = text.length();
        for (int i = 0; i < l; i++) {
            char c = text.charAt(i);
            if (Character.isWhitespace(c)) {
                result.append(' ');
                while (++i < l) {
                    c = text.charAt(i);
                    if (! Character.isWhitespace(c)) {
                        i--;
                        break;
                    }
                }
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Returns the title of the document as located in the contents of /html/head/title, or "" if none could be found.
     *
     * @param doc the document to search for a title
     * @return The document's title, or "" if none found
     */
    public String getDocumentTitle(org.jsoup.nodes.Document doc) {
        String title = "";

        org.jsoup.nodes.Element html = doc;
        org.jsoup.nodes.Element head = findFirstChild(html, "head");
        if (head != null) {
            org.jsoup.nodes.Element titleElem = findFirstChild(head, "title");
            if (titleElem != null) {
                title = collapseWhiteSpace(readTextContent(titleElem).trim());
            }
        }

        return title;
    }

    private org.jsoup.nodes.Element findFirstChild(org.jsoup.nodes.Element parent, String targetName) {
        List<org.jsoup.nodes.Node> children = parent.childNodes();
        for (int i = 0; i < children.size(); i++) {
            org.jsoup.nodes.Node n = children.get(i);
            if (n instanceof org.jsoup.nodes.Element && n.nodeName().equals(targetName)) {
                return (org.jsoup.nodes.Element)n;
            }
        }

        return null;
    }

    protected StylesheetInfo readStyleElement(org.jsoup.nodes.Element style) {
        String media = style.attr("media");
        if ("".equals(media)) {
            media = "all";
        }//default for HTML is "screen", but that is silly and firefox seems to assume "all"
        StylesheetInfo info = new StylesheetInfo();
        info.setMedia(media);
        info.setType(style.attr("type"));
        info.setTitle(style.attr("title"));
        info.setOrigin(StylesheetInfo.AUTHOR);

        StringBuffer buf = new StringBuffer();
        org.jsoup.nodes.Node current = style.children().size() > 0 ? style.child(0) : null;
        while (current != null) {
            if (current instanceof DataNode) {
                buf.append(((DataNode)current).getWholeData());
            }
            current = current.nextSibling();
        }

        String css = buf.toString().trim();
        if (css.length() > 0) {
            info.setContent(css.toString());

            return info;
        } else {
            return null;
        }
    }

    protected StylesheetInfo readLinkElement(org.jsoup.nodes.Element link) {
        String rel = link.attr("rel").toLowerCase();
        if (rel.indexOf("alternate") != -1) {
            return null;
        }//DON'T get alternate stylesheets
        if (rel.indexOf("stylesheet") == -1) {
            return null;
        }

        String type = link.attr("type");
        if (! (type.equals("") || type.equals("text/css"))) {
            return null;
        }

        StylesheetInfo info = new StylesheetInfo();

        if (type.equals("")) {
            type = "text/css";
        } // HACK is not entirely correct because default may be set by META tag or HTTP headers
        info.setType(type);

        info.setOrigin(StylesheetInfo.AUTHOR);

        info.setUri(link.attr("href"));
        String media = link.attr("media");
        if ("".equals(media)) {
            media = "all";
        }
        info.setMedia(media);

        String title = link.attr("title");
        info.setTitle(title);

        return info;
    }

    /**
     * Gets the stylesheetLinks attribute of the XhtmlNamespaceHandler object
     *
     * @param doc PARAM
     * @return The stylesheetLinks value
     */
    public StylesheetInfo[] getStylesheets(org.jsoup.nodes.Document doc) {
        List result = new ArrayList();
        //get the processing-instructions (actually for XmlDocuments)
        result.addAll(Arrays.asList(super.getStylesheets(doc)));

        //get the link elements
        org.jsoup.nodes.Element html = doc;
        org.jsoup.nodes.Element head = findFirstChild(html, "head");
        if (head != null) {
            org.jsoup.nodes.Node current = head.childNode(0);
            while (current != null) {
                if (current instanceof org.jsoup.nodes.Element) {
                    org.jsoup.nodes.Element elem = (org.jsoup.nodes.Element)current;
                    StylesheetInfo info = null;
                    String elemName = elem.nodeName();
                    if (elemName == null)
                    {
                        elemName = elem.tagName();
                    }
                    if (elemName.equals("link")) {
                        info = readLinkElement(elem);
                    } else if (elemName.equals("style")) {
                        info = readStyleElement(elem);
                    }
                    if (info != null) {
                        result.add(info);
                    }
                }
                current = current.nextSibling();
            }
        }

        return (StylesheetInfo[])result.toArray(new StylesheetInfo[result.size()]);
    }

    public StylesheetInfo getDefaultStylesheet(StylesheetFactory factory) {
        synchronized (XhtmlCssOnlyNamespaceHandler.class) {
            if (_defaultStylesheet != null) {
                return _defaultStylesheet;
            }

            if (_defaultStylesheetError) {
                return null;
            }

            StylesheetInfo info = new StylesheetInfo();
            info.setUri(getNamespace());
            info.setOrigin(StylesheetInfo.USER_AGENT);
            info.setMedia("all");
            info.setType("text/css");

            InputStream is = null;
            try {
                is = getDefaultStylesheetStream();

                if (_defaultStylesheetError) {
                    return null;
                }

                Stylesheet sheet = factory.parse(new InputStreamReader(is), info);
                info.setStylesheet(sheet);

                is.close();
                is = null;
            } catch (Exception e) {
                _defaultStylesheetError = true;
                XRLog.exception("Could not parse default stylesheet", e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        //  ignore
                    }
                }
            }

            _defaultStylesheet = info;

            return _defaultStylesheet;
        }
    }

    private InputStream getDefaultStylesheetStream() {
        InputStream stream = null;
        String defaultStyleSheet = Configuration.valueFor("xr.css.user-agent-default-css") + "XhtmlNamespaceHandler.css";
        stream = this.getClass().getResourceAsStream(defaultStyleSheet);
        if (stream == null) {
            XRLog.exception("Can't load default CSS from " + defaultStyleSheet + "." +
                    "This file must be on your CLASSPATH. Please check before continuing.");
            _defaultStylesheetError = true;
        }

        return stream;
    }

    private Map getMetaInfo(org.jsoup.nodes.Document doc) {
        if(this._metadata != null) {
            return this._metadata;
        }

        Map metadata = new HashMap();

        org.jsoup.nodes.Element html = doc;//();
        org.jsoup.nodes.Element head = findFirstChild(html, "head");
        if (head != null) {
            org.jsoup.nodes.Node current = head.children().size() > 0 ? head.children().get(0): null;
            while (current != null) {
                if (current instanceof org.jsoup.nodes.Element) {
                    Element elem = (Element)current;
                    String elemName;// = elem.getLocalName();
//                    if (elemName == null)
//                    {
                        elemName = elem.tagName();
//                    }
                    if (elemName.equals("meta")) {
                        String http_equiv = elem.attr("http-equiv");
                        String content = elem.attr("content");

                        if(!http_equiv.equals("") && !content.equals("")) {
                            metadata.put(http_equiv, content);
                        }
                    }
                }
                current = current.nextSibling();
            }
        }

        return metadata;
    }

    public String getLang(org.jsoup.nodes.Element e) {
        if(e == null) {
            return "";
        }
        String lang = e.attr("lang");
        if("".equals(lang)) {
            lang = (String) this.getMetaInfo(e.ownerDocument()).get("Content-Language");
            if(lang == null) {
                lang = "";
            }
        }
        return lang;
    }
}
