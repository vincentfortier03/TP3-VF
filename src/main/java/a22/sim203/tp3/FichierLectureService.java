package a22.sim203.tp3;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.FileReader;

public class FichierLectureService extends Service<String> {

	public FichierLectureService(String contenuString) {
		super();
	}

	@Override
	protected Task<String> createTask() {
		return new FichierReaderStringTask();
	}

	public class FichierReaderStringTask extends Task<String> {

		@Override
		protected String call() throws Exception {

			String retVal = "";

			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader("text.txt"));
				retVal = br.readLine();
			} finally {
				if (br != null) {
					br.close();
				}
			}
			updateProgress(1, 1);

			return retVal; //
		}
	}

}
