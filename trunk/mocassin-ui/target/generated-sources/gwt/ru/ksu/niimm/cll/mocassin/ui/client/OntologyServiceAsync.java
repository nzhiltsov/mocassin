package ru.ksu.niimm.cll.mocassin.ui.client;

import java.util.List;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface OntologyServiceAsync
{

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see ru.ksu.niimm.cll.mocassin.ui.client.OntologyService
     */
    void getConceptList( AsyncCallback<java.util.List<ru.ksu.niimm.cll.mocassin.ui.client.OntConcept>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see ru.ksu.niimm.cll.mocassin.ui.client.OntologyService
     */
    void getRelationList( ru.ksu.niimm.cll.mocassin.ui.client.OntConcept concept, AsyncCallback<java.util.List<ru.ksu.niimm.cll.mocassin.ui.client.OntRelation>> callback );


    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see ru.ksu.niimm.cll.mocassin.ui.client.OntologyService
     */
    void getRelationRangeConceptList( ru.ksu.niimm.cll.mocassin.ui.client.OntRelation relation, AsyncCallback<java.util.List<ru.ksu.niimm.cll.mocassin.ui.client.OntElement>> callback );


    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static OntologyServiceAsync instance;

        public static final OntologyServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (OntologyServiceAsync) GWT.create( OntologyService.class );
                ServiceDefTarget target = (ServiceDefTarget) instance;
                target.setServiceEntryPoint( GWT.getModuleBaseURL() + "GWT.rpc" );
            }
            return instance;
        }

        private Util()
        {
            // Utility class should not be instanciated
        }
    }
}
