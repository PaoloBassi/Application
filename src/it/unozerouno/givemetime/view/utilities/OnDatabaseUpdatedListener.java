package it.unozerouno.givemetime.view.utilities;

public abstract class OnDatabaseUpdatedListener {
	protected abstract void onUpdateFinished();
	public void updateFinished(){
		onUpdateFinished();
	}
}
