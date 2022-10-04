package fr.paris.lutece.plugins.html2pdf.business;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;

import fr.paris.lutece.plugins.html2pdf.service.PdfConverterServiceException;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterService;


/**
 * @author omar.ben.haddi
 *
 */
public class Exemple
{

    /**
     * @param args
     * @throws IOException
     */
    public static void main( String[] args ) throws IOException
    {
        
    	//File input = new File("/home/norbert/git/plugin-htmltopdf_seb/src/test/java/resources/templates/test-checkbox.html");
    	//File input = new File("/home/norbert/git/plugin-htmltopdf_seb/src/test/java/resources/templates/PM_cerfa.html");
    	//File input = new File("/home/norbert/git/plugin-htmltopdf_seb/src/test/java/resources/templates/forms.html");
    	File input = new File("/home/norbert/git/plugin-htmltopdf_seb/src/test/java/resources/templates/test-checkbox.html");
    	//String html = loadTemplate("/home/norbert/git/plugin-htmltopdf_seb/src/test/java/resources/templates/test-checkbox.html");
    	Document doc = Jsoup.parse(input, "UTF-8");
    	doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
    	doc.outputSettings().escapeMode(EscapeMode.base.xhtml);
        doc.outputSettings().charset("UTF-8");
    	
    	//String html = Jsoup.parse(input, "UTF-8").html( );
    	String html = doc.html();
        
    	
        Map<String, String> mapOptions = new HashMap<>();
        //mapOptions.put( "PathFont", "/home/norbert/git/plugin-htmltopdf_seb/src/test/java/resources/fonts/");
        mapOptions.put( "PathFont", "/home/norbert/dev/liberation-fonts-ttf-2.1.5/");
        
        Map<String, String> mapWatermarkOptions = new HashMap<>();
        mapWatermarkOptions.put("FontSize", "50f");
        mapWatermarkOptions.put("Rotation", "1f");
        mapWatermarkOptions.put("Text", "TESTWATERMARK");
        mapWatermarkOptions.put("X", "200f");
        mapWatermarkOptions.put("Y", "200f");
        mapWatermarkOptions.put("Font", "HELVETICA");
        
        Map<String, String> mapQRcodeOptions = new HashMap<>();
        mapQRcodeOptions.put("barCodeFormat", "QR_CODE");
        mapQRcodeOptions.put("text", "testNLG");
        mapQRcodeOptions.put("x", "450f");
        mapQRcodeOptions.put("y", "700f");
        mapQRcodeOptions.put("width", "100");
        mapQRcodeOptions.put("height", "100");
        mapQRcodeOptions.put("pageIndex", "0");
        
        //try ( OutputStream outputStream = new ByteArrayOutputStream(1024) )
        try ( OutputStream outputStream =  new FileOutputStream("/home/norbert/DEV/formCheckbox.pdf") )
        {
        	/*
        	new OpenHtmlToPdfConverterServiceProvider.PdfBuilder( html )
        			.withOptions(mapOptions)
        			.withWatermark(mapWatermarkOptions)
        			.withBarCode(mapQRcodeOptions)
        			.withNotEditable()
        			.render( outputStream );
        	*/
        	PdfConverterService.getInstance().getPdfBuilder().reset().withHtmlContent(html).render(outputStream);
        	
        	//PdfConverterService2.getInstance().getPdfServiceProvider();
        	//new HtmlToPdfConverterService.getInstance().getDefaultProvider().PdfBuilder( html ).render( outputStream );
        } 
        catch ( PdfConverterServiceException e )
        {
        	
        } 
        catch (IOException e) 
        {
        	
		}
    }

}
