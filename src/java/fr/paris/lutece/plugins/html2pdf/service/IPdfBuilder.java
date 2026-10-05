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
package fr.paris.lutece.plugins.html2pdf.service;

import java.io.OutputStream;
import java.util.Map;

// public interface IPdfConverterServiceProvider {

public interface IPdfBuilder
{

    /**
     * get builder name
     * 
     * @return the name
     */
    String getName( );

    /**
     * reset all options
     * 
     * @return the builder
     */
    public IPdfBuilder reset( );

    /**
     * add the html content to render
     * 
     * @param strHtml
     * @return the builder
     */
    public IPdfBuilder withHtmlContent( String strHtml );

    /**
     * set not editable
     * 
     * @return the builder
     */
    public IPdfBuilder notEditable( );

    /**
     * enable the accessibility tagging (PDF/UA) of the generated document.
     *
     * The renderer produces a tagged PDF : headings, paragraphs, tables, lists and alternative texts
     * of the images are exposed to the assistive technologies, and the document title is displayed
     * instead of the file name. Without it the document is read as a single image.
     *
     * The html content must provide the elements required by the PDF/UA conformance : a lang
     * attribute on the html element, a title element, an alt attribute on every image, th elements
     * for the table headers. The fonts must also be embedded (see the PathFont option), the
     * conformance forbidding the use of the non embedded standard fonts.
     *
     * @return the builder
     */
    default IPdfBuilder accessible( )
    {
        return this;
    }

    /**
     * add specific options
     * 
     * @param mapOptions
     * @return the builder
     */
    public IPdfBuilder withOptions( Map<String, String> mapOptions );

    /**
     * Build : Render the html content as PDF in outpustream
     * 
     * @param out
     * @throws PdfConverterServiceException
     */
    public void render( OutputStream out ) throws PdfConverterServiceException;
}

// }
