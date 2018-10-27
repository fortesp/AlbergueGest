package reporting;

import com.itextpdf.text.pdf.BaseFont;
import com.lowagie.text.DocumentException;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class PDFConverter {


    private String fontDir;

    public PDFConverter() {
    }

    public String getFontDir() {
        return fontDir;
    }

    public void setFontDir(String fontDir) {
        this.fontDir = fontDir;
    }


    public void XmlToPDF(String xslfilename, String xmlfilename, String outputfilename) throws TransformerException, IOException, DocumentException {

        TransformerFactory tFactory = TransformerFactory.newInstance();
        final Transformer transformer = tFactory.newTransformer(new StreamSource(getClass().getResourceAsStream(xslfilename)));
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        StreamResult sr = new StreamResult(new StringWriter());

        transformer.transform(new StreamSource(xmlfilename), sr);

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(sr.getWriter().toString());
        renderer.getFontResolver().addFont(fontDir + "arial.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.getFontResolver().addFont(fontDir + "times.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.getFontResolver().addFont(fontDir + "timesbd.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.getFontResolver().addFont(fontDir + "calibri.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.layout();

        try {
            renderer.createPDF(new FileOutputStream(outputfilename));
        } catch (FileNotFoundException e) { // Resource busy
            renderer.createPDF(new FileOutputStream(outputfilename + "_temp.pdf"));
        }

        renderer.finishPDF();
    }

    public void HtmlToPDF(String htmlfilename, String outputfilename) throws IOException, DocumentException {

        ITextRenderer renderer = new ITextRenderer();

        OutputStream os = new FileOutputStream(outputfilename);

        renderer.setDocument(new File(htmlfilename));
        renderer.layout();
        renderer.createPDF(os);
        renderer.finishPDF();

        os.close();
    }

    public void XmlToHtmlToPDF(String xslfilename, String xmlfilename, String outputfilename) throws TransformerException, IOException, DocumentException {

        TransformerFactory tFactory = TransformerFactory.newInstance();
        final Transformer transformer = tFactory.newTransformer(new StreamSource(xslfilename));
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.transform(new StreamSource(xmlfilename), new StreamResult(new FileOutputStream(xslfilename + ".html")));

        HtmlToPDF(xslfilename + ".html", outputfilename);
    }


}
