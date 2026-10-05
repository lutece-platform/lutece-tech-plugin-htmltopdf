/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.html2pdf.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import fr.paris.lutece.plugins.html2pdf.service.IPdfBuilder;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterServiceException;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * This is the business class test for the object HtmltoPDFService
 */
public class HtmltoPDFServiceBusinessTest extends LuteceTestCase
{

    /** Directory of the fonts used by the accessible rendering tests */
    private static final String FONT_PATH = Paths.get( "src", "test", "java", "resources", "fonts" ).toFile( ).getAbsolutePath( ) + File.separator;

    /**
     * test HtmltoPDFService 1
     */
    public void testHtml2Pdf_1( )
    {

        Map<String, String> mapOptions = new HashMap<>( );
        mapOptions.put( "PathFont", "/" );

        String html = "<!DOCTYPE html PUBLIC \"-//OPENHTMLTOPDF//DOC XHTML Character Entities Only 1.0//EN\" \"\">\n"
                + "<html><head><title>Example</title></head><body>very simple test</body></html>";

        Path resourceDirectoryOutput = Paths.get( "src", "test", "java", "resources", "output", "out.pdf" );
        String absolutePathOutput = resourceDirectoryOutput.toFile( ).getAbsolutePath( );
        try ( OutputStream outputStream = new FileOutputStream( absolutePathOutput ) )
        {
            // new OpenHtmlToPdfConverterServiceProvider.PdfBuilder( html ).render( outputStream );
            PdfConverterService.getInstance( ).getPdfBuilder( ).reset( ).withHtmlContent( html ).render( outputStream );
        }
        catch( PdfConverterServiceException e )
        {
            fail( e.getMessage( ) );
        }
        catch( IOException e )
        {
            fail( e.getMessage( ) );
        }

    }

    /**
     * test HtmltoPDFService 2
     */

    public void testHtml2Pdf_2( )
    {

        String html = "<!DOCTYPE html PUBLIC \"-//OPENHTMLTOPDF//DOC XHTML Character Entities Only 1.0//EN\" \"\">\n"
                + "<html><head><title>Example</title></head><body>very simple test</body></html>";

        Map<String, String> mapOptions = new HashMap<>( );
        mapOptions.put( "PathFont", "/" );

        Path resourceDirectoryOutput = Paths.get( "src", "test", "java", "resources", "output", "out2.pdf" );
        String absolutePathOutput = resourceDirectoryOutput.toFile( ).getAbsolutePath( );
        try ( OutputStream outputStream = new FileOutputStream( absolutePathOutput ) )
        {
            PdfConverterService.getInstance( ).getPdfBuilder( ).reset( ).withHtmlContent( html ).withOptions( mapOptions ).notEditable( )
                    .render( outputStream );
        }
        catch( PdfConverterServiceException e )
        {
            fail( e.getMessage( ) );
        }
        catch( IOException e )
        {
            fail( e.getMessage( ) );
        }
    }

    /**
     * test HtmltoPDFService 3
     * 
     * @throws IOException
     */

    public void testHtml2Pdf_3( ) throws IOException
    {
        Path resourceDirectory = Paths.get( "src", "test", "java", "resources", "templates", "test-checkbox.html" );
        String absolutePath = resourceDirectory.toFile( ).getAbsolutePath( );
        File input = new File( absolutePath );
        Document doc = Jsoup.parse( input, "UTF-8" );
        doc.outputSettings( ).syntax( Document.OutputSettings.Syntax.xml );
        doc.outputSettings( ).escapeMode( EscapeMode.base.xhtml );
        doc.outputSettings( ).charset( "UTF-8" );

        String html = doc.html( );

        Map<String, String> mapOptions = new HashMap<>( );
        // mapOptions.put( "PathFont", "/home/norbert/DEV/liberation-fonts-ttf-2.1.5/");

        Path resourceDirectoryOutput = Paths.get( "src", "test", "java", "resources", "output", "outRadioBox.pdf" );
        String absolutePathOutput = resourceDirectoryOutput.toFile( ).getAbsolutePath( );
        try ( OutputStream outputStream = new FileOutputStream( absolutePathOutput ) )
        {
            PdfConverterService.getInstance( ).getPdfBuilder( ).reset( ).withHtmlContent( html ).withOptions( mapOptions ).notEditable( )
                    .render( outputStream );
        }
        catch( PdfConverterServiceException e )
        {
            fail( e.getMessage( ) );
        }
        catch( IOException e )
        {
            fail( e.getMessage( ) );
        }
    }

    /**
     * test HtmltoPDFService 2
     * 
     * @throws IOException
     */

    public void testHtml2Pdf_4( ) throws IOException
    {

        Path resourceDirectory = Paths.get( "src", "test", "java", "resources", "templates", "PM_cerfa.html" );
        String absolutePath = resourceDirectory.toFile( ).getAbsolutePath( );
        File input = new File( absolutePath );

        Document doc = Jsoup.parse( input, "UTF-8" );
        doc.outputSettings( ).syntax( Document.OutputSettings.Syntax.xml );
        doc.outputSettings( ).escapeMode( EscapeMode.base.xhtml );
        doc.outputSettings( ).charset( "UTF-8" );

        String html = doc.html( );

        Map<String, String> mapOptions = new HashMap<>( );
        mapOptions.put( "PathFont", "/home/norbert/DEV/liberation-fonts-ttf-2.1.5/" );

        Map<String, String> mapWatermarkOptions = new HashMap<>( );
        mapWatermarkOptions.put( "FontSize", "50f" );
        mapWatermarkOptions.put( "Rotation", "1f" );
        mapWatermarkOptions.put( "Text", "TESTWATERMARK" );
        mapWatermarkOptions.put( "X", "200f" );
        mapWatermarkOptions.put( "Y", "200f" );
        mapWatermarkOptions.put( "Font", "HELVETICA" );

        Map<String, String> mapQRcodeOptions = new HashMap<>( );
        mapQRcodeOptions.put( "barCodeFormat", "QR_CODE" );
        mapQRcodeOptions.put( "text", "testNLG" );
        mapQRcodeOptions.put( "x", "450f" );
        mapQRcodeOptions.put( "y", "700f" );
        mapQRcodeOptions.put( "width", "100" );
        mapQRcodeOptions.put( "height", "100" );
        mapQRcodeOptions.put( "pageIndex", "0" );

        Path resourceDirectoryOutput = Paths.get( "src", "test", "java", "resources", "output", "out4.pdf" );
        String absolutePathOutput = resourceDirectoryOutput.toFile( ).getAbsolutePath( );
        try ( OutputStream outputStream = new FileOutputStream( absolutePathOutput ) )
        {

            PdfConverterService.getInstance( ).getPdfBuilder( ).reset( ).withHtmlContent( html ).withOptions( mapOptions ).notEditable( )
                    .render( outputStream );
        }
        catch( PdfConverterServiceException e )
        {

        }
        catch( IOException e )
        {

        }
    }

    /**
     * test HtmltoPDFService : the accessible option produces a tagged document ( PDF/UA )
     *
     * \"throws IOException
     */
    public void testHtml2Pdf_accessible( ) throws IOException
    {
        File output = renderAccessible( "outAccessible.pdf", true, FONT_PATH );

        try ( PDDocument doc = PDDocument.load( output ) )
        {
            PDDocumentCatalog catalog = doc.getDocumentCatalog( );

            assertNotNull( "the document must carry a MarkInfo dictionary", catalog.getMarkInfo( ) );
            assertTrue( "the document must be marked as tagged", catalog.getMarkInfo( ).isMarked( ) );
            assertNotNull( "the document must carry a structure tree", catalog.getStructureTreeRoot( ) );
            assertEquals( "the language of the html element must be reported on the document", "fr", catalog.getLanguage( ) );
            assertEquals( "the title of the document must be set", "Example", doc.getDocumentInformation( ).getTitle( ) );
            assertEquals( "the description of the document must be set", "Accessible rendering", doc.getDocumentInformation( ).getSubject( ) );

            Set<String> setTypes = getStructureTypes( catalog );
            assertTrue( "the heading must be tagged, found " + setTypes, setTypes.contains( "H1" ) );
            assertTrue( "the paragraph must be tagged, found " + setTypes, setTypes.contains( "P" ) );
            assertTrue( "the table must be tagged, found " + setTypes, setTypes.contains( "Table" ) );
            assertTrue( "the header cells of the table must be tagged, found " + setTypes, setTypes.contains( "TH" ) );
        }
    }

    /**
     * test HtmltoPDFService : without the accessible option the rendering is left unchanged
     *
     * \"throws IOException
     */
    public void testHtml2Pdf_notAccessibleByDefault( ) throws IOException
    {
        File output = renderAccessible( "outNotAccessible.pdf", false, FONT_PATH );

        try ( PDDocument doc = PDDocument.load( output ) )
        {
            PDDocumentCatalog catalog = doc.getDocumentCatalog( );

            assertFalse( "the document must not be tagged when the option is not set",
                    catalog.getMarkInfo( ) != null && catalog.getMarkInfo( ).isMarked( ) );
            assertNull( "the document must not carry a structure tree when the option is not set", catalog.getStructureTreeRoot( ) );
        }
    }

    /**
     * test HtmltoPDFService : the accessible option must not leak from one rendering to the next,
     * the builder being shared by all the applications of the site
     *
     * \"throws IOException
     */
    public void testHtml2Pdf_accessibleIsClearedByReset( ) throws IOException
    {
        renderAccessible( "outAccessibleBeforeReset.pdf", true, FONT_PATH );

        File output = renderAccessible( "outAfterReset.pdf", false, null );

        try ( PDDocument doc = PDDocument.load( output ) )
        {
            PDDocumentCatalog catalog = doc.getDocumentCatalog( );

            assertFalse( "the accessible option of the previous rendering must have been cleared by reset",
                    catalog.getMarkInfo( ) != null && catalog.getMarkInfo( ).isMarked( ) );
            assertNull( "the structure tree of the previous rendering must not be kept", catalog.getStructureTreeRoot( ) );
        }
    }

    /**
     * test HtmltoPDFService : every variant of the font family is embedded, the PDF/UA conformance
     * forbidding the non embedded fonts
     *
     * \"throws IOException
     */
    public void testHtml2Pdf_accessibleFontsAreEmbedded( ) throws IOException
    {
        File output = renderAccessible( "outAccessibleFonts.pdf", true, FONT_PATH );

        try ( PDDocument doc = PDDocument.load( output ) )
        {
            Set<String> setFonts = new TreeSet<>( );

            for ( PDPage page : doc.getPages( ) )
            {
                for ( COSName name : page.getResources( ).getFontNames( ) )
                {
                    PDFont font = page.getResources( ).getFont( name );
                    assertTrue( "the font " + font.getName( ) + " must be embedded", font.isEmbedded( ) );
                    setFonts.add( font.getName( ) );
                }
            }

            // the regular, the bold and the italic runs must each use their own variant of the
            // family : a single font would mean that the bold and the italic fell back on it
            assertEquals( "the three variants of the family must be used, found " + setFonts, 3, setFonts.size( ) );
        }
    }

    /**
     * Render the test page used by the accessibility tests
     *
     * \"param strFileName
     *            the name of the produced file
     * \"param bAccessible
     *            whether the accessible option is set
     * \"param strFontPath
     *            the directory of the fonts, null to register none
     * \"return the produced file
     * \"throws IOException
     */
    private File renderAccessible( String strFileName, boolean bAccessible, String strFontPath ) throws IOException
    {
        String html = "<!DOCTYPE html PUBLIC \"-//OPENHTMLTOPDF//DOC XHTML Character Entities Only 1.0//EN\" \"\">\n"
                + "<html lang=\"fr\" xml:lang=\"fr\"><head><title>Example</title>"
                + "<meta name=\"subject\" content=\"Accessible rendering\"/>"
                + "<style>body{font-family:'Arial',sans-serif}table{border-collapse:collapse}th,td{border:1px solid #000}</style>"
                + "</head><body><h1>Title of the document</h1>"
                + "<p>A paragraph with a <strong>bold</strong> run and an <em>italic</em> run.</p>"
                + "<table><tr><th scope=\"col\">Service</th><th scope=\"col\">Price</th></tr>"
                + "<tr><td>Simple test</td><td>48,00</td></tr></table></body></html>";

        Path resourceDirectoryOutput = Paths.get( "src", "test", "java", "resources", "output", strFileName );
        File output = resourceDirectoryOutput.toFile( );

        IPdfBuilder builder = PdfConverterService.getInstance( ).getPdfBuilder( ).reset( );

        if ( bAccessible )
        {
            builder = builder.accessible( );
        }

        if ( strFontPath != null )
        {
            Map<String, String> mapOptions = new HashMap<>( );
            mapOptions.put( "PathFont", strFontPath );
            builder = builder.withOptions( mapOptions );
        }

        try ( OutputStream outputStream = new FileOutputStream( output ) )
        {
            builder.withHtmlContent( html ).render( outputStream );
        }
        catch( PdfConverterServiceException e )
        {
            fail( e.getMessage( ) );
        }

        return output;
    }

    /**
     * Collect the types of the elements of the structure tree of a document
     *
     * \"param catalog
     *            the catalog of the document
     * \"return the types found
     */
    private Set<String> getStructureTypes( PDDocumentCatalog catalog )
    {
        Set<String> setTypes = new LinkedHashSet<>( );

        if ( catalog.getStructureTreeRoot( ) != null )
        {
            collectStructureTypes( catalog.getStructureTreeRoot( ).getKids( ), setTypes );
        }

        return setTypes;
    }

    /**
     * Collect recursively the types of the elements of a structure tree
     *
     * \"param kids
     *            the children to walk through
     * \"param setTypes
     *            the types found so far
     */
    private void collectStructureTypes( Object kids, Set<String> setTypes )
    {
        if ( kids instanceof List )
        {
            for ( Object kid : (List<?>) kids )
            {
                collectStructureTypes( kid, setTypes );
            }
        }
        else
            if ( kids instanceof PDStructureElement )
            {
                PDStructureElement element = (PDStructureElement) kids;
                setTypes.add( element.getStandardStructureType( ) );
                collectStructureTypes( element.getKids( ), setTypes );
            }
    }

}
