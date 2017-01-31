package database.services.settings;

import database.services.writerReader.IWriteRead;

public interface ISettingsProvider extends IWriteRead {
	public InternalParameters getInternalParameters();
}
