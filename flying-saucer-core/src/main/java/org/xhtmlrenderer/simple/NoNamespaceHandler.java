/*
 *
 * XhtmlDocument.java
 * Copyright (c) 2004, 2005 Torbjoern Gannholm
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

package org.xhtmlrenderer.simple;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import org.xhtmlrenderer.css.extend.StylesheetFactory;
import org.xhtmlrenderer.css.extend.TreeResolver;
import org.xhtmlrenderer.css.sheet.StylesheetInfo;
import org.xhtmlrenderer.extend.NamespaceHandler;

/**
 * Handles a general XML document
 *
 * @author Torbjoern Gannholm
 */
public class NoNamespaceHandler implements NamespaceHandler {

    static final String _namespace = "http://www.w3.org/XML/1998/namespace";

    public String getNamespace() {
        return _namespace;
    }

    public String getAttributeValue(org.jsoup.nodes.Element e, String attrName) {
        return e.attr(attrName);
    }
    
    public String getAttributeValue(org.jsoup.nodes.Element e, String namespaceURI, String attrName) {
        return e.attr(attrName);
    }

    public String getClass(org.jsoup.nodes.Element e) {
        return null;
    }

    public String getID(org.jsoup.nodes.Element e) {
        return null;
    }

    public String getLang(org.jsoup.nodes.Element e) {
        if(e == null) {
            return "";
        }
        return e.attr("lang");
    }

    public String getElementStyling(org.jsoup.nodes.Element e) {
        return null;
    }

    public String getNonCssStyling(org.jsoup.nodes.Element e) {
        return null;
    }

    public String getLinkUri(org.jsoup.nodes.Element e) {
        return null;
    }

    public String getDocumentTitle(org.jsoup.nodes.Document doc) {
        return null;
    }
    
    public String getAnchorName(org.jsoup.nodes.Element e) {
        return null;
    }

    public boolean isImageElement(org.jsoup.nodes.Element e) {
        return false;
    }

    public String getImageSourceURI(org.jsoup.nodes.Element e) {
        return null;
    }
    
    public boolean isFormElement(org.jsoup.nodes.Element e) {
        return false;
    }

    private Pattern _typePattern = Pattern.compile("type\\s?=\\s?");
    private Pattern _hrefPattern = Pattern.compile("href\\s?=\\s?");
    private Pattern _titlePattern = Pattern.compile("title\\s?=\\s?");
    private Pattern _alternatePattern = Pattern.compile("alternate\\s?=\\s?");
    private Pattern _mediaPattern = Pattern.compile("media\\s?=\\s?");

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    public StylesheetInfo[] getStylesheets(org.jsoup.nodes.Document doc) {
        List<StylesheetInfo> list = new ArrayList<>();
        //get the processing-instructions (actually for XmlDocuments)
        //type and href are required to be set
        val styleElements = new ArrayList<Element>();
        styleElements.addAll(doc.getElementsByTag("style"));
        styleElements.addAll(doc.select("link[rel=stylesheet]"));
        
        styleElements.forEach(e -> {
            if(e.tagName().equals("link")) {
                val href = e.attr("href");
                if(StringUtils.isNotBlank(href)){
                    val info = new StylesheetInfo();
                    info.setUri(href);
                    info.setType("stylesheet");
                    list.add(info);
                }
            } else if(e.tagName().equals("style")){
                val info = new StylesheetInfo();
                info.setType("text/css");
                list.add(info);
            }
        });
        
//        for (int i = 0, len = nl.size(); i < len; i++) {
//            val element = nl.get(i);
//            org.jsoup.nodes.Node node = element.childNodeSize() > 0 ? element.childNode(0) : element;
//            if (!(node instanceof DataNode) )
//                continue;
////            ProcessingInstruction piNode = (ProcessingInstruction) node;
////            if (!piNode.getTarget().equals("xml-stylesheet")) continue;
//            StylesheetInfo info;
//            info = new StylesheetInfo();
//            info.setOrigin(StylesheetInfo.AUTHOR);
//            String pi = ((org.jsoup.nodes.DataNode) node).getWholeData();
//            Matcher m = _alternatePattern.matcher(pi);
//            if (m.matches()) {
//                int start = m.end();
//                String alternate = pi.substring(start + 1, pi.indexOf(pi.charAt(start), start + 1));
////                TODO: handle alternate stylesheets
//                if (alternate.equals("yes")) continue;//DON'T get alternate stylesheets for now
//            }
//            
////            if(!node.parent().nodeName().equals("style")){
////                continue;
////            }
////            m = _typePattern.matcher(pi);
////            if (m.find()) {
////                int start = m.end();
////                String type = pi.substring(start + 1, pi.indexOf(pi.charAt(start), start + 1));
//////                TODO: handle other stylesheet types
////                if (!type.equals("text/css")) continue;//for now
////                info.setType(type);
////            }
//            m = _hrefPattern.matcher(pi);
//            if (m.find()) {
//                int start = m.end();
//                String href = pi.substring(start + 1, pi.indexOf(pi.charAt(start), start + 1));
//                info.setUri(href);
//            }
//            
//            info.setContent(((DataNode) node).getWholeData());
////            m = _titlePattern.matcher(pi);
////            if (m.find()) {
////                int start = m.end();
////                String title = pi.substring(start + 1, pi.indexOf(pi.charAt(start), start + 1));
////                info.setTitle(title);
////            }
////            m = _mediaPattern.matcher(pi);
////            if (m.find()) {
////                int start = m.end();
////                String media = pi.substring(start + 1, pi.indexOf(pi.charAt(start), start + 1));
////                info.setMedia(media);
////            } else {
////                info.addMedium("screen");
////            }
//            list.add(info);
//        }

        return list.toArray(new StylesheetInfo[0]);
    }

    public StylesheetInfo getDefaultStylesheet(StylesheetFactory factory) {
        return null;
    }

    @Override
    public boolean isCanvasElement(org.jsoup.nodes.Element e) {
        return (e != null && e.nodeName().equalsIgnoreCase("canvas"));
    }
}
