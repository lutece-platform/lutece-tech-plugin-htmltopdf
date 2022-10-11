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
package fr.paris.lutece.plugins.html2pdf.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

/**
 * @author omar.ben.haddi
 *
 */
public class FontBuilder
{
    /**
     * @param pathFont
     * @return Map< String, String >
     */
    public static Map<String, String> mapFonts( String pathFont )
    {
        Map<String, String> mapFonts = new HashMap<>( );
        File repertoire = new File( pathFont );
        String liste[] = repertoire.list( );

        if ( liste != null )
        {
            for ( int i = 0; i < liste.length; i++ )
            {
                mapFonts.put( pathFont + liste [i], liste [i].replaceAll( ".ttf", "" ) );
            }
        }
        return mapFonts;
    }

    /**
     * Add font to parameter builder
     * 
     * @param builder
     * @param pathFont
     */
    public static void addFontToRender( PdfRendererBuilder builder, String pathFont )
    {
        Map<String, String> mapFonts = mapFonts( pathFont );
        if ( !mapFonts.isEmpty( ) )
        {
            for ( Map.Entry<String, String> ttf : mapFonts.entrySet( ) )
            {
                File file = new File( ttf.getKey( ) );
                builder.useFont( file, ttf.getValue( ) );
            }
        }
    }
}
