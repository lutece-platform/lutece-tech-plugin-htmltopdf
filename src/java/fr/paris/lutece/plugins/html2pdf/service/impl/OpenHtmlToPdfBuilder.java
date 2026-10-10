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
package fr.paris.lutece.plugins.html2pdf.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import fr.paris.lutece.plugins.html2pdf.service.IPdfBuilder;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterServiceException;
import fr.paris.lutece.plugins.html2pdf.utils.FontBuilder;
import fr.paris.lutece.plugins.html2pdf.utils.PDDocumentUtils;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * PDF builder based on OpenHtmlToPdf. The bean is shared by every request: it keeps no state, each rendering (started by
 * {@link #reset()} or by any other method) has its own content, options and renderer
 */
@ApplicationScoped
public class OpenHtmlToPdfBuilder implements IPdfBuilder
{

    public static final String KEY_FONT_PATH = "PathFont";
    public static final String KEY_PRODUCER = "Producer";
    public static final String BASE_DOCUMENT_URI = "/";

    @Override
    public IPdfBuilder withHtmlContent( String strHtml )
    {
        return reset( ).withHtmlContent( strHtml );
    }

    @Override
    public IPdfBuilder notEditable( )
    {
        return reset( ).notEditable( );
    }

    @Override
    public IPdfBuilder withOptions( Map<String, String> mapOptions )
    {
        return reset( ).withOptions( mapOptions );
    }

    @Override
    public void render( OutputStream out ) throws PdfConverterServiceException
    {
        reset( ).render( out );
    }

    @Override
    public String getName( )
    {
        return "OpenHtml2Pdf Builder";
    }

    /**
     * Starts a new rendering
     *
     * @return the rendering
     */
    @Override
    public IPdfBuilder reset( )
    {
        return new Rendering( getName( ) );
    }

    /**
     * One rendering: used by one request only
     */
    private static final class Rendering implements IPdfBuilder
    {
        private final String _strName;
        private final PdfRendererBuilder _pdfRendererBuilder = new PdfRendererBuilder( );
        private boolean _bEditable = true;
        private String _strHtml;

        private Rendering( String strName )
        {
            _strName = strName;
            _pdfRendererBuilder.useFastMode( );
        }

        @Override
        public IPdfBuilder withHtmlContent( String strHtml )
        {
            _strHtml = strHtml;
            return this;
        }

        @Override
        public IPdfBuilder notEditable( )
        {
            _bEditable = false;
            return this;
        }

        @Override
        public IPdfBuilder withOptions( Map<String, String> mapOptions )
        {
            if ( mapOptions.containsKey( KEY_FONT_PATH ) )
            {
                FontBuilder.addFontToRender( _pdfRendererBuilder, mapOptions.get( KEY_FONT_PATH ) );
            }

            // TODO add map options tests and defaults ...
            return this;
        }

        @Override
        public void render( OutputStream out ) throws PdfConverterServiceException
        {
            _pdfRendererBuilder.withHtmlContent( _strHtml, BASE_DOCUMENT_URI );

            try ( PDDocument doc = PDDocumentUtils.convertToPDDocument( _pdfRendererBuilder ) )
            {
                if ( !_bEditable )
                {
                    PDDocumentUtils.convertToNotEditablePdf( doc );
                }

                doc.save( out );
            }
            catch( IOException e )
            {
                throw new PdfConverterServiceException( e.getMessage( ), e );
            }
        }

        @Override
        public String getName( )
        {
            return _strName;
        }

        @Override
        public IPdfBuilder reset( )
        {
            return new Rendering( _strName );
        }
    }
}
