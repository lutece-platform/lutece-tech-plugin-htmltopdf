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

public class OpenHtmlToPdfBuilder implements IPdfBuilder {

	public static final String KEY_FONT_PATH = "PathFont";
	public static final String KEY_PRODUCER = "Producer";
	public static final String BASE_DOCUMENT_URI = "/";

	protected boolean _isEditable = true;
	protected String _strHtml;
	private PDDocument _doc = null;

	private static PdfRendererBuilder _pdfRendererbuilder = new PdfRendererBuilder();

	@Override
	public IPdfBuilder withHtmlContent(String strHtml) {
		this._strHtml = strHtml;
		return this;
	}

	@Override
	public OpenHtmlToPdfBuilder notEditable() {
		this._isEditable = false;
		return this;
	}

	@Override
	public OpenHtmlToPdfBuilder withOptions(Map<String, String> mapOptions) {
		
		if (mapOptions.containsKey(KEY_FONT_PATH)) {
			FontBuilder.addFontToRender(_pdfRendererbuilder, mapOptions.get(KEY_FONT_PATH));
		}

		// TODO add map options tests and defaults ...
		_pdfRendererbuilder.useFastMode();

		return this;
	}

	@Override
	public void render(OutputStream out) throws PdfConverterServiceException {

		try {
			// create builder
			_pdfRendererbuilder.withHtmlContent(_strHtml, BASE_DOCUMENT_URI);

			// create PDDocument
			_doc = PDDocumentUtils.convertToPDDocument(_pdfRendererbuilder);
			
			// is editable
			if (!_isEditable) {
				PDDocumentUtils.convertToNotEditablePdf(_doc);
			}

			// render
			_doc.save(out);
			_doc.close();
		} catch (IOException e) {
			throw new PdfConverterServiceException(e.getMessage(), e);
		} 

	}

	@Override
	public String getName() {
		return "OpenHtml2Pdf Builder";
	}

	@Override
	public IPdfBuilder reset() {
		 this._isEditable = true;
		 this._strHtml = null;
		return this;
	}

}
