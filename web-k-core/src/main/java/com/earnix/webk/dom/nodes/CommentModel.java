package com.earnix.webk.dom.nodes;

import com.earnix.webk.dom.Jsoup;
import com.earnix.webk.dom.helper.Validate;
import com.earnix.webk.dom.parser.Parser;

import java.io.IOException;

/**
 * A comment node.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class CommentModel extends LeafNodeModel {
    private static final String COMMENT_KEY = "comment";

    /**
     * Create a new comment node.
     *
     * @param data The contents of the comment
     */
    public CommentModel(String data) {
        value = data;
    }

    /**
     * Create a new comment node.
     *
     * @param data    The contents of the comment
     * @param baseUri base URI not used. This is a leaf node.
     * @deprecated
     */
    public CommentModel(String data, String baseUri) {
        this(data);
    }

    public String nodeName() {
        return "#comment";
    }

    /**
     * Get the contents of the comment.
     *
     * @return comment content
     */
    public String getData() {
        return coreValue();
    }
    
    public void setData(String data) {
        Validate.notNull(data);
        coreValue(data);
    }

    void outerHtmlHead(Appendable accum, int depth, DocumentModel.OutputSettings out) throws IOException {
        if (out.prettyPrint())
            indent(accum, depth, out);
        accum
                .append("<!--")
                .append(getData())
                .append("-->");
    }

    void outerHtmlTail(Appendable accum, int depth, DocumentModel.OutputSettings out) {
    }

    @Override
    public String toString() {
        return outerHtml();
    }

    /**
     * Check if this comment looks like an XML Declaration.
     *
     * @return true if it looks like, maybe, it's an XML Declaration.
     */
    public boolean isXmlDeclaration() {
        String data = getData();
        return (data.length() > 1 && (data.startsWith("!") || data.startsWith("?")));
    }

    /**
     * Attempt to cast this comment to an XML Declaration note.
     *
     * @return an XML declaration if it could be parsed as one, null otherwise.
     */
    public XmlDeclarationModel asXmlDeclaration() {
        String data = getData();
        DocumentModel doc = Jsoup.parse("<" + data.substring(1, data.length() - 1) + ">", baseUri(), Parser.xmlParser());
        XmlDeclarationModel decl = null;
        if (doc.childNodeSize() > 0) {
            ElementModel el = doc.child(0);
            decl = new XmlDeclarationModel(NodeModelUtils.parser(doc).settings().normalizeTag(el.tagName()), data.startsWith("!"));
            decl.attributes().addAll(el.attributes());
        }
        return decl;
    }
}
