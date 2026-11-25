/*
 * Copyright (c) 2002-2026, City of Paris
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
package fr.paris.lutece.plugins.html2pdf.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.sun.net.httpserver.HttpServer;

import fr.paris.lutece.portal.service.util.AppPathService;

/**
 * Resources of the HTML rendered to PDF: relative URLs resolve to the files of the webapp; only data: URLs, files of the
 * webapp and the site's own host are read (decision 25/11: base = webapp directory + whitelist).
 */
public class OpenHtmlToPdfBuilderResourcesTest
{
    @TempDir
    static Path _root;
    private static Path _webapp;
    private static Path _outside;
    private static HttpServer _server;

    @BeforeAll
    static void setUp( ) throws IOException
    {
        _webapp = Files.createDirectories( _root.resolve( "webapp" ) );
        Files.createDirectories( _webapp.resolve( "images" ) );
        writePng( _webapp.resolve( "images/logo.png" ) );
        _outside = Files.createDirectories( _root.resolve( "outside" ) );
        writePng( _outside.resolve( "secret.png" ) );
        AppPathService.init( _webapp.toString( ) );

        byte [ ] png = Files.readAllBytes( _webapp.resolve( "images/logo.png" ) );
        _server = HttpServer.create( new InetSocketAddress( "127.0.0.1", 0 ), 0 );
        _server.createContext( "/probe.png", exchange -> {
            exchange.getResponseHeaders( ).add( "Content-Type", "image/png" );
            exchange.sendResponseHeaders( 200, png.length );
            try ( OutputStream out = exchange.getResponseBody( ) )
            {
                out.write( png );
            }
        } );
        _server.start( );
    }

    @AfterAll
    static void tearDown( )
    {
        _server.stop( 0 );
    }

    private static void writePng( Path path ) throws IOException
    {
        ImageIO.write( new BufferedImage( 30, 20, BufferedImage.TYPE_INT_RGB ), "png", path.toFile( ) );
    }

    private static int images( String strBody ) throws Exception
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream( );
        new OpenHtmlToPdfBuilder( ).reset( ).withHtmlContent( "<html><body><p>text</p>" + strBody + "</body></html>" ).render( out );
        int nImages = 0;
        try ( PDDocument doc = PDDocument.load( out.toByteArray( ) ) )
        {
            for ( PDPage page : doc.getPages( ) )
            {
                PDResources resources = page.getResources( );
                for ( COSName name : resources.getXObjectNames( ) )
                {
                    if ( resources.getXObject( name ) instanceof PDImageXObject )
                    {
                        nImages++;
                    }
                }
            }
        }
        return nImages;
    }

    @Test
    public void testRelativeImageResolvedInTheWebapp( ) throws Exception
    {
        assertEquals( 1, images( "<img src=\"images/logo.png\" alt=\"logo\"/>" ), "a relative image must be read from the webapp" );
    }

    @Test
    public void testWebappFileImageRendered( ) throws Exception
    {
        assertEquals( 1, images( "<img src=\"" + _webapp.resolve( "images/logo.png" ).toUri( ) + "\" alt=\"logo\"/>" ) );
    }

    @Test
    public void testFileOutsideTheWebappRefused( ) throws Exception
    {
        assertEquals( 0, images( "<img src=\"" + _outside.resolve( "secret.png" ).toUri( ) + "\" alt=\"secret\"/>" ),
                "a file outside the webapp must not be read" );
        assertEquals( 0, images( "<img src=\"images/../../outside/secret.png\" alt=\"secret\"/>" ), "../ must not leave the webapp" );
    }

    @Test
    public void testSymbolicLinkLeavingTheWebappRefused( ) throws Exception
    {
        Path link = _webapp.resolve( "images/link.png" );
        Files.deleteIfExists( link );
        Files.createSymbolicLink( link, _outside.resolve( "secret.png" ) );
        assertEquals( 0, images( "<img src=\"images/link.png\" alt=\"link\"/>" ), "a symbolic link must not lead out of the webapp" );
    }

    @Test
    public void testForeignHttpHostRefused( ) throws Exception
    {
        assertEquals( 0, images( "<img src=\"http://127.0.0.1:" + _server.getAddress( ).getPort( ) + "/probe.png\" alt=\"probe\"/>" ),
                "a host other than the site must not be fetched" );
    }

    @Test
    public void testDataImageRendered( ) throws Exception
    {
        String strData = java.util.Base64.getEncoder( ).encodeToString( Files.readAllBytes( _webapp.resolve( "images/logo.png" ) ) );
        assertEquals( 1, images( "<img src=\"data:image/png;base64," + strData + "\" alt=\"data\"/>" ) );
    }
}
