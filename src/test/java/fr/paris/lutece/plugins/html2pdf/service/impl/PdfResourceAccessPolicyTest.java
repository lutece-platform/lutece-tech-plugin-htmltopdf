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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Whitelist of the resources read while rendering a PDF (decision 25/11: base = webapp directory + whitelist)
 */
public class PdfResourceAccessPolicyTest
{
    @TempDir
    Path _root;

    private PdfResourceAccessPolicy policy( ) throws Exception
    {
        Path webapp = Files.createDirectories( _root.resolve( "webapp/images" ) ).getParent( );
        return new PdfResourceAccessPolicy( webapp.toString( ), List.of( "https://www.example.org/site/", "http://bo.example.org:8080/site/" ) );
    }

    @Test
    public void testBaseUriIsTheWebappDirectory( ) throws Exception
    {
        PdfResourceAccessPolicy policy = policy( );
        String strExpected = _root.resolve( "webapp" ).toRealPath( ).toUri( ).toString( );
        assertEquals( strExpected.endsWith( "/" ) ? strExpected : strExpected + "/", policy.getBaseUri( ) );
        assertTrue( policy.getBaseUri( ).endsWith( "/" ) );
    }

    @Test
    public void testAllowed( ) throws Exception
    {
        PdfResourceAccessPolicy policy = policy( );
        String strWebapp = policy.getBaseUri( );
        assertTrue( policy.isAllowed( "data:image/png;base64,AAAA" ) );
        assertTrue( policy.isAllowed( strWebapp + "images/logo.png" ) );
        assertTrue( policy.isAllowed( "https://www.example.org/site/image?resource_type=image_resource&id=1" ) );
        assertTrue( policy.isAllowed( "https://WWW.example.org:443/site/document?id=2" ) );
        assertTrue( policy.isAllowed( "http://bo.example.org:8080/site/images/x.png" ) );
    }

    @Test
    public void testRefused( ) throws Exception
    {
        PdfResourceAccessPolicy policy = policy( );
        String strWebapp = policy.getBaseUri( );
        assertFalse( policy.isAllowed( strWebapp + "../outside.png" ) );
        assertFalse( policy.isAllowed( "file:///etc/passwd" ) );
        assertFalse( policy.isAllowed( "http://169.254.169.254/latest/meta-data/" ) );
        assertFalse( policy.isAllowed( "http://www.example.org/site/x.png" ), "other scheme/port than the site" );
        assertFalse( policy.isAllowed( "https://www.example.org:8443/x.png" ) );
        assertFalse( policy.isAllowed( "https://user@www.example.org/x.png" ) );
        assertFalse( policy.isAllowed( "jar:file:/tmp/a.jar!/x.png" ) );
        assertFalse( policy.isAllowed( "ftp://www.example.org/x.png" ) );
        assertFalse( policy.isAllowed( "/images/x.png" ) );
        assertFalse( policy.isAllowed( "" ) );
    }
}
