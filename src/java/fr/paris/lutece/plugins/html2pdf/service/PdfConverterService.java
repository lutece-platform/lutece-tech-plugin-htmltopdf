package fr.paris.lutece.plugins.html2pdf.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.paris.lutece.portal.service.spring.SpringContextService;

public class PdfConverterService  {
    
    private Map<String, IPdfBuilder> _pdfBuilders = new HashMap<>( );
    private IPdfBuilder _currentPdfBuilder;
    
    private static PdfConverterService _instance ;
	
    
    /**
     * init
     */
    private PdfConverterService( )
    {
    	List<IPdfBuilder> pdfConverterServiceProviderList = SpringContextService.getBeansOfType( IPdfBuilder.class );
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
     * getter
     * 
     * @return the instance
     */
    public static PdfConverterService getInstance( )
    {
    	if ( _instance == null )
    	{
    		_instance = new PdfConverterService( );
    	}
    	
        return _instance;
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
