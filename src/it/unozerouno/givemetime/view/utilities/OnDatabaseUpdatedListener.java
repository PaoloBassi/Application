package it.unozerouno.givemetime.view.utilities;

public abstract class OnDatabaseUpdatedListener {
	protected abstract void onUpdateFinished(Object updatedItem);
	public void updateFinished(Object updatedItem){
		onUpdateFinished(updatedItem);
	}
}
