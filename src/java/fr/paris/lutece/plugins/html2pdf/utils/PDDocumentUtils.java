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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;

import com.openhtmltopdf.pdfboxout.PdfBoxRenderer;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

public class PDDocumentUtils
{

    /**
     * Convert to PDDocument
     * 
     * @param builder
     * @return PDDocument
     * @throws IOException
     */
    public static PDDocument convertToPDDocument( PdfRendererBuilder builder ) throws IOException
    {
        PDDocument document = null;

        try ( PdfBoxRenderer renderer = builder.buildPdfRenderer( ) )
        {
            renderer.createPDFWithoutClosing( );

            document = renderer.getPdfDocument( );
        }

        return document;
    }

    /**
     * Convert PDDocument to not editable PDDocument
     * 
     * @param document
     * @throws IOException
     */
    public static void convertToNotEditablePdf( PDDocument document )
    {
        PDAcroForm acroForm = document.getDocumentCatalog( ).getAcroForm( );

        if ( acroForm == null )
            return;

        acroForm.setNeedAppearances( true );
        for ( PDField field : acroForm.getFields( ) )
        {
            field.setReadOnly( true );
        }
    }

    /**
     * Add watermark
     * 
     * @param document
     * @param watermark
     * @throws IOException
     */
    /*
     * public static void addWaterMark( PDDocument document , WaterMark watermark ) throws IOException { for(PDPage page: document.getPages()) {
     * PDPageContentStream cs = new PDPageContentStream(document, page, AppendMode.APPEND, true, true);
     * 
     * String ts = watermark.getText( ); PDFont font = watermark.getFont( ); float fontSize = watermark.getFontSize( ); PDExtendedGraphicsState r0 = new
     * PDExtendedGraphicsState(); r0.setNonStrokingAlphaConstant(0.5f); cs.setGraphicsStateParameters(r0); cs.beginText(); cs.setFont(font, fontSize);
     * cs.setTextMatrix(Matrix.getRotateInstance(watermark.getRotation( ), watermark.getX( ), watermark.getY( ))); cs.showText(ts); cs.endText(); cs.close(); }
     * }
     */

    /**
     * add barcode
     * 
     * @param document
     * @param barCode
     * @throws IOException
     * @throws WriterException
     */
    /*
     * public static void addBarCode( PDDocument document, BarCode barCode ) throws IOException, WriterException {
     * 
     * PDPage page = document.getPage( barCode.getPageIndex( ) ); PDPageContentStream contentStream = new PDPageContentStream(document, page,
     * PDPageContentStream.AppendMode.APPEND, true);
     * 
     * Map<EncodeHintType, Object> hintMap = new HashMap<>();
     * 
     * hintMap.put(EncodeHintType.MARGIN, 0); hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
     * 
     * BitMatrix matrix = new MultiFormatWriter().encode( new String( barCode.getText().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8),
     * barCode.getBarCodeFormat(), barCode.getWidth(), barCode.getHeight(), hintMap);
     * 
     * MatrixToImageConfig config = new MatrixToImageConfig(); BufferedImage bImage = MatrixToImageWriter.toBufferedImage(matrix, config); PDImageXObject image
     * = JPEGFactory.createFromImage(document, bImage); contentStream.drawImage(image, barCode.getX(), barCode.getY(), barCode.getWidth(), barCode.getHeight());
     * contentStream.close();
     * 
     * }
     */

}
