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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

/**
 * Concurrent renderings with the shared builder bean: each request gets its own document
 */
public class OpenHtmlToPdfBuilderTest
{
    private static final int RENDERINGS = 24;

    @Test
    public void testConcurrentRenderings( ) throws Exception
    {
        OpenHtmlToPdfBuilder builder = new OpenHtmlToPdfBuilder( );
        ExecutorService executor = Executors.newFixedThreadPool( 8 );
        try
        {
            List<Future<String>> results = new ArrayList<>( );
            for ( int i = 0; i < RENDERINGS; i++ )
            {
                String strText = "document" + i;
                results.add( executor.submit( ( ) -> {
                    ByteArrayOutputStream out = new ByteArrayOutputStream( );
                    builder.reset( ).withHtmlContent( "<html><body><p>" + strText + "</p></body></html>" ).render( out );
                    try ( PDDocument doc = PDDocument.load( out.toByteArray( ) ) )
                    {
                        return new PDFTextStripper( ).getText( doc ).trim( );
                    }
                } ) );
            }
            for ( int i = 0; i < RENDERINGS; i++ )
            {
                assertEquals( "document" + i, results.get( i ).get( ) );
            }
        }
        finally
        {
            executor.shutdown( );
        }
    }

    @Test
    public void testRenderingsAreIndependent( )
    {
        OpenHtmlToPdfBuilder builder = new OpenHtmlToPdfBuilder( );
        assertTrue( builder.reset( ) != builder.reset( ) );
        assertTrue( builder.withHtmlContent( "a" ) != builder );
    }
}
