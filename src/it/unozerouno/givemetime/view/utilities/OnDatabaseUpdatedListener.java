package it.unozerouno.givemetime.view.utilities;

public abstract class OnDatabaseUpdatedListener<ExpectedResult> {
	protected abstract void onUpdateFinished(ExpectedResult updatedItem);
	public void updateFinished(ExpectedResult updatedItem){
		onUpdateFinished(updatedItem);
	}
}
