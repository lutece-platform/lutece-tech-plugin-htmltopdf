package fr.paris.lutece.plugins.html2pdf.service;

import java.io.OutputStream;
import java.util.Map;

// public interface IPdfConverterServiceProvider {
	
	public interface  IPdfBuilder {
		
		/**
		 * get builder name
		 * @return the name
		 */
		String getName();
		
		/**
		 * reset all options
		 * @return the builder
		 */
		public IPdfBuilder reset( );
		
		/**
		 * add the html content to render
		 * @param strHtml
		 * @return the builder
		 */
		public IPdfBuilder withHtmlContent( String strHtml );
		
		/**
		 * set not editable
		 * @return the builder
		 */
		public IPdfBuilder notEditable( );
		
		/** 
		 * add specific options
		 * 
		 * @param mapOptions
		 * @return  the builder
		 */
		public IPdfBuilder withOptions( Map<String, String> mapOptions ) ;

		
		/**
		 * Build : 
		 * Render the html content as PDF in outpustream
		 * 
		 * @param out
		 * @throws PdfConverterServiceException
		 */
		public void render( OutputStream out) throws PdfConverterServiceException ;
	}

//}