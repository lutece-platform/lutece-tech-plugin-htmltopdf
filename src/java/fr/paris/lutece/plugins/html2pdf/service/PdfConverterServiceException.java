package fr.paris.lutece.plugins.html2pdf.service;

public class PdfConverterServiceException extends Exception {
	private static final long serialVersionUID = 2676611887614784376L;

    /**
     * Constructor 
     *
     * @param strMessage
     *            The error message
     * @param t
     *            The initial throwable
     */
    public PdfConverterServiceException( String strMessage, Throwable t )
    {
        super( strMessage, t );
    }
}
