package ru.ksu.niimm.cll.mocassin.ui.client;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public interface QueryServiceAsync
{

    /**
     * GWT-RPC service  asynchronous (client-side) interface
     * @see ru.ksu.niimm.cll.mocassin.ui.client.QueryService
     */
    void query( ru.ksu.niimm.cll.mocassin.ui.client.OntQueryStatement statement, ru.ksu.niimm.cll.mocassin.ui.client.PagingLoadConfig pagingLoadConfig, AsyncCallback<ru.ksu.niimm.cll.mocassin.ui.client.PagingLoadInfo<ru.ksu.niimm.cll.mocassin.ui.client.ResultDescription>> callback );


    /**
     * Utility class to get the RPC Async interface from client-side code
     */
    public static final class Util 
    { 
        private static QueryServiceAsync instance;

        public static final QueryServiceAsync getInstance()
        {
            if ( instance == null )
            {
                instance = (QueryServiceAsync) GWT.create( QueryService.class );
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
