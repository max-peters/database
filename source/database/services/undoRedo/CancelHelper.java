package database.services.undoRedo;

import java.sql.SQLException;
import javax.swing.text.BadLocationException;

public class CancelHelper {
	private boolean lastUndo;

	public CancelHelper() {
		lastUndo = false;
	}

	public void cancel(IUndoRedoService undoRedo) throws InterruptedException, SQLException, BadLocationException {
		if (lastUndo) {
			undoRedo.redoCommand();
			lastUndo = false;
		}
		else {
			undoRedo.undoCommand();
			lastUndo = true;
		}
	}
}
