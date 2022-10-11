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
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Entities.EscapeMode;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterServiceException;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterService;
import fr.paris.lutece.test.LuteceTestCase;

/**
 * This is the business class test for the object HtmltoPDFService
 */
public class HtmltoPDFServiceBusinessTest extends LuteceTestCase
{

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

}
