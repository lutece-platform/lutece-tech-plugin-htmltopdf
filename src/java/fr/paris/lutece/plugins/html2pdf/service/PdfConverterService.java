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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.inject.Inject;

@ApplicationScoped
public class PdfConverterService
{

    private Map<String, IPdfBuilder> _pdfBuilders = new HashMap<>( );
    private IPdfBuilder _currentPdfBuilder;
    
    @Inject
    private Instance<IPdfBuilder> _pdfBuilder;

    /**
     * init
     */
    @PostConstruct
    private void initPdfConverterService( )
    {
    	List<IPdfBuilder> pdfConverterServiceProviderList = _pdfBuilder.stream( ).toList( );
    	if ( !pdfConverterServiceProviderList.isEmpty( ) )
        {
            for ( IPdfBuilder b : pdfConverterServiceProviderList )
            {
                _pdfBuilders.put( b.getName( ), b );
            }
        }
        _currentPdfBuilder = getDefaultBuilder( );
    }

    /**
     * Returns the unique instance of the {@link PdfConverterService} service.
     * 
     * <p>This method is deprecated and is provided for backward compatibility only. 
     * For new code, use dependency injection with {@code @Inject} to obtain the 
     * {@link PdfConverterService} instance instead.</p>
     * 
     * @return The unique instance of {@link PdfConverterService}.
     * 
     * @deprecated Use {@code @Inject} to obtain the {@link PdfConverterService} 
     * instance. This method will be removed in future versions.
     */
    @Deprecated( since = "8.0", forRemoval = true )
    public static PdfConverterService getInstance( )
    {
        return CDI.current( ).select( PdfConverterService.class ).get( );
    }

    /**
     * get the current FileStoreService provider
     * 
     * @return the current FileStoreService provider
     */
    public IPdfBuilder getPdfBuilder( )
    {
        return _currentPdfBuilder;
    }

    /**
     * get default File Store Service Provider
     * 
     * @return the provider
     */
    private IPdfBuilder getDefaultBuilder( )
    {
        return _pdfBuilders.get( (String) _pdfBuilders.keySet( ).toArray( ) [0] );
    }

}
