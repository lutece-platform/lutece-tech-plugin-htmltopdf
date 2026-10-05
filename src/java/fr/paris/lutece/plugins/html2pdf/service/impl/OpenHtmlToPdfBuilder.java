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
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

import fr.paris.lutece.plugins.html2pdf.service.IPdfBuilder;
import fr.paris.lutece.plugins.html2pdf.service.PdfConverterServiceException;
import fr.paris.lutece.plugins.html2pdf.utils.FontBuilder;
import fr.paris.lutece.plugins.html2pdf.utils.PDDocumentUtils;

public class OpenHtmlToPdfBuilder implements IPdfBuilder
{

    public static final String KEY_FONT_PATH = "PathFont";
    public static final String KEY_PRODUCER = "Producer";
    public static final String BASE_DOCUMENT_URI = "/";

    protected boolean _isEditable = true;
    protected boolean _isAccessible = false;
    protected String _strHtml;
    protected String _strFontPath;

    @Override
    public IPdfBuilder withHtmlContent( String strHtml )
    {
        this._strHtml = strHtml;
        return this;
    }

    @Override
    public OpenHtmlToPdfBuilder notEditable( )
    {
        this._isEditable = false;
        return this;
    }

    @Override
    public OpenHtmlToPdfBuilder accessible( )
    {
        this._isAccessible = true;
        return this;
    }

    @Override
    public OpenHtmlToPdfBuilder withOptions( Map<String, String> mapOptions )
    {

        if ( mapOptions.containsKey( KEY_FONT_PATH ) )
        {
            this._strFontPath = mapOptions.get( KEY_FONT_PATH );
        }

        // TODO add map options tests and defaults ...

        return this;
    }

    @Override
    public void render( OutputStream out ) throws PdfConverterServiceException
    {

        try
        {
            // create builder : a new one for each rendering, the builder being stateful and this
            // service being shared by all the applications of the site
            PdfRendererBuilder pdfRendererbuilder = new PdfRendererBuilder( );

            // fonts : mandatory for the PDF/UA conformance, which forbids the non embedded fonts
            if ( _strFontPath != null )
            {
                FontBuilder.addFontToRender( pdfRendererbuilder, _strFontPath );
            }

            // fast mode : the default rendering mode, the only one handling the accessibility tagging
            pdfRendererbuilder.useFastMode( );

            // is accessible
            if ( _isAccessible )
            {
                pdfRendererbuilder.usePdfUaAccessbility( true );
            }

            pdfRendererbuilder.withHtmlContent( _strHtml, BASE_DOCUMENT_URI );

            // create PDDocument
            PDDocument doc = PDDocumentUtils.convertToPDDocument( pdfRendererbuilder );

            // is editable
            if ( !_isEditable )
            {
                PDDocumentUtils.convertToNotEditablePdf( doc );
            }

            // render
            doc.save( out );
            doc.close( );
        }
        catch( IOException e )
        {
            throw new PdfConverterServiceException( e.getMessage( ), e );
        }

    }

    @Override
    public String getName( )
    {
        return "OpenHtml2Pdf Builder";
    }

    @Override
    public IPdfBuilder reset( )
    {
        this._isEditable = true;
        this._isAccessible = false;
        this._strHtml = null;
        this._strFontPath = null;
        return this;
    }

}
