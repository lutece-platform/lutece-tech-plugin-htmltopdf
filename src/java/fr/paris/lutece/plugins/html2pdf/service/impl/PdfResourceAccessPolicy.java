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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.BiPredicate;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.openhtmltopdf.extend.FSStream;
import com.openhtmltopdf.extend.FSStreamFactory;
import com.openhtmltopdf.outputdevice.helper.ExternalResourceType;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPathService;

/**
 * Resources an HTML document rendered to PDF may read (images, style sheets, fonts): the rendering runs on the server, so
 * the HTML must not make it read local files or call internal services. Allowed: {@code data:} URLs, {@code file:} URLs
 * of files inside the webapp directory (after normalisation and resolution of symbolic links), and {@code http(s):} URLs of
 * the site's own host (resources served by servlets, e.g. {@code image?resource_type=...}). Everything else is refused: the
 * resource is skipped (warning in the log) and the PDF is still produced.
 */
public final class PdfResourceAccessPolicy implements BiPredicate<String, ExternalResourceType>, FSStreamFactory
{
    /** Protocols whose streams are opened through the policy (images are loaded without the access control) */
    static final String [ ] PROTOCOLS = {
            "file", "jar", "ftp", "http", "https", "netdoc", "mailto", "gopher"
    };

    private static final Logger LOGGER = LogManager.getLogger( "lutece.application" );
    private static final String SCHEME_DATA = "data";
    private static final String SCHEME_FILE = "file";
    private static final String SCHEME_HTTP = "http";
    private static final String SCHEME_HTTPS = "https";

    private final Path _webappRoot;
    private final Set<String> _setSiteAuthorities;

    /**
     * Constructor
     *
     * @param strWebappPath
     *            the webapp directory
     * @param collectionSiteUrls
     *            the URLs of the site (base and production URLs): their host and port may be read through http(s)
     */
    public PdfResourceAccessPolicy( String strWebappPath, Collection<String> collectionSiteUrls )
    {
        _webappRoot = realPath( strWebappPath );
        _setSiteAuthorities = new HashSet<>( );
        for ( String strUrl : collectionSiteUrls )
        {
            String strAuthority = authority( strUrl );
            if ( strAuthority != null )
            {
                _setSiteAuthorities.add( strAuthority );
            }
        }
    }

    /**
     * The policy of the running site: its webapp directory, its base URL and its production URL
     *
     * @return the policy
     */
    public static PdfResourceAccessPolicy ofSite( )
    {
        Set<String> setUrls = new HashSet<>( );
        try
        {
            String strBaseUrl = AppPathService.getBaseUrl( );
            setUrls.add( strBaseUrl );
            setUrls.add( AppPathService.getProdUrl( strBaseUrl ) );
        }
        catch( RuntimeException e )
        {
            // no request nor properties (batch, unit test): the site has no known http host
            AppLogService.debug( "PDF rendering: site URL unknown", e );
        }
        return new PdfResourceAccessPolicy( AppPathService.getWebAppPath( ), setUrls );
    }

    /**
     * The base URI of the rendered document: the webapp directory, so that relative URLs designate its files
     *
     * @return the base URI, or null if the webapp directory is not known
     */
    public String getBaseUri( )
    {
        if ( _webappRoot == null )
        {
            return null;
        }
        String strUri = _webappRoot.toUri( ).toString( );
        return strUri.endsWith( "/" ) ? strUri : strUri + "/";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean test( String strUri, ExternalResourceType type )
    {
        boolean bAllowed = isAllowed( strUri );
        if ( !bAllowed )
        {
            LOGGER.warn( "PDF rendering: resource refused ({}): {}", type, StringUtils.abbreviate( strUri, 200 ) );
        }
        return bAllowed;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Opens the resource if the policy allows it; a refused resource gives no stream (the renderer skips it).
     * </p>
     */
    @Override
    public FSStream getUrl( String strUri )
    {
        if ( !test( strUri, null ) )
        {
            return new UrlStream( null );
        }
        return new UrlStream( strUri );
    }

    /**
     * Stream of an allowed URL, opened on demand
     */
    private static final class UrlStream implements FSStream
    {
        private final String _strUri;

        private UrlStream( String strUri )
        {
            _strUri = strUri;
        }

        @Override
        public InputStream getStream( )
        {
            if ( _strUri == null )
            {
                return null;
            }
            try
            {
                return new URL( _strUri ).openStream( );
            }
            catch( IOException e )
            {
                LOGGER.warn( "PDF rendering: resource not readable: {} ({})", StringUtils.abbreviate( _strUri, 200 ), e.getMessage( ) );
                return null;
            }
        }

        @Override
        public Reader getReader( )
        {
            InputStream stream = getStream( );
            return stream == null ? null : new InputStreamReader( stream, StandardCharsets.UTF_8 );
        }
    }

    /**
     * Tells whether a resolved URI may be read
     *
     * @param strUri
     *            the URI
     * @return true if allowed
     */
    boolean isAllowed( String strUri )
    {
        if ( StringUtils.isBlank( strUri ) )
        {
            return false;
        }
        if ( StringUtils.startsWithIgnoreCase( strUri, SCHEME_DATA + ":" ) )
        {
            return true;
        }
        URI uri;
        try
        {
            uri = new URI( strUri );
        }
        catch( URISyntaxException e )
        {
            return false;
        }
        String strScheme = StringUtils.lowerCase( uri.getScheme( ), Locale.ROOT );
        if ( SCHEME_FILE.equals( strScheme ) )
        {
            return isWebappFile( uri );
        }
        if ( SCHEME_HTTP.equals( strScheme ) || SCHEME_HTTPS.equals( strScheme ) )
        {
            String strAuthority = authority( strUri );
            return strAuthority != null && _setSiteAuthorities.contains( strAuthority );
        }
        return false;
    }

    private boolean isWebappFile( URI uri )
    {
        if ( _webappRoot == null )
        {
            return false;
        }
        try
        {
            Path path = Paths.get( uri ).normalize( );
            if ( !path.startsWith( _webappRoot ) )
            {
                return false;
            }
            // a symbolic link must not lead out of the webapp; a missing file is not read anyway
            return !Files.exists( path ) || path.toRealPath( ).startsWith( _webappRoot );
        }
        catch( IllegalArgumentException | IOException | SecurityException e )
        {
            return false;
        }
    }

    private static Path realPath( String strPath )
    {
        if ( StringUtils.isBlank( strPath ) )
        {
            return null;
        }
        Path path = Paths.get( strPath ).toAbsolutePath( ).normalize( );
        try
        {
            return path.toRealPath( );
        }
        catch( IOException e )
        {
            return path;
        }
    }

    /**
     * Scheme, host and port of an http(s) URL, lower case, default port made explicit
     *
     * @param strUrl
     *            the URL
     * @return the authority, or null if the URL is not an absolute http(s) URL
     */
    private static String authority( String strUrl )
    {
        if ( StringUtils.isBlank( strUrl ) )
        {
            return null;
        }
        try
        {
            URI uri = new URI( strUrl.trim( ) );
            String strScheme = StringUtils.lowerCase( uri.getScheme( ), Locale.ROOT );
            String strHost = StringUtils.lowerCase( uri.getHost( ), Locale.ROOT );
            if ( strHost == null || !( SCHEME_HTTP.equals( strScheme ) || SCHEME_HTTPS.equals( strScheme ) ) || uri.getUserInfo( ) != null )
            {
                return null;
            }
            int nPort = uri.getPort( ) != -1 ? uri.getPort( ) : ( SCHEME_HTTPS.equals( strScheme ) ? 443 : 80 );
            return strScheme + "://" + strHost + ":" + nPort;
        }
        catch( URISyntaxException e )
        {
            return null;
        }
    }
}
