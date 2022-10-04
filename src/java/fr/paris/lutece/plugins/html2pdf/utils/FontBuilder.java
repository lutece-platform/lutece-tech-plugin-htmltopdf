package fr.paris.lutece.plugins.html2pdf.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

/**
 * @author omar.ben.haddi
 *
 */
public class FontBuilder
{
    /**
     * @param pathFont 
     * @return Map< String, String >
     */
    public static Map< String, String > mapFonts(String pathFont)
    {
        Map< String, String > mapFonts = new HashMap<>();
        File repertoire = new File( pathFont );
        String liste[] = repertoire.list();      
 
        if (liste != null) {         
            for (int i = 0; i < liste.length; i++) {
                mapFonts.put( pathFont + liste[i] , liste[i].replaceAll( ".ttf", "" ) );
            }
        }        
        return mapFonts;
    }
    
    /** Add font to parameter builder
     * @param builder
     * @param pathFont 
     */
    public static void addFontToRender(PdfRendererBuilder builder, String pathFont)
    {
        Map< String, String > mapFonts = mapFonts( pathFont );
        if(!mapFonts.isEmpty( ))
        {
            for(Map.Entry<String, String> ttf : mapFonts.entrySet( ))
            {
                File file = new File (ttf.getKey( ));
                builder.useFont( file, ttf.getValue( ) );
            }
        }       
    }
}
