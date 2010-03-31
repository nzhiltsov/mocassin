package ru.ksu.niimm.ose.ui.client;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AsyncCallbackWrapper<T> implements AsyncCallback<T>,
		IAsyncCallbackWrapper<T> {
	private static LoadingPanel loadingPanel;

	@Override
	public final void onFailure(Throwable caught) {
		GWT.log("error has occured:", caught);
		afterCall();
		handleFailure(caught);
	}

	@Override
	public final void onSuccess(T result) {
		handleSuccess(result);
		afterCall();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ru.ksu.niimm.ose.ui.client.IAsyncCallbackWrapper#handleFailure(java.lang
	 * .Throwable)
	 */
	public void handleFailure(Throwable caught) {
		Window.alert("Server error has occured");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ksu.niimm.ose.ui.client.IAsyncCallbackWrapper#handleSuccess(T)
	 */
	public abstract void handleSuccess(T result);

	/*
	 * (non-Javadoc)
	 * 
	 * @see ru.ksu.niimm.ose.ui.client.IAsyncCallbackWrapper#beforeCall()
	 */
	public void beforeCall() {
		getLoadingPanel().popupShow();
	}

	private void afterCall() {
		getLoadingPanel().popupHide();
	}

	private static LoadingPanel getLoadingPanel() {
		if (loadingPanel == null) {
			loadingPanel = new LoadingPanel();
		}
		return loadingPanel;
	}

}
