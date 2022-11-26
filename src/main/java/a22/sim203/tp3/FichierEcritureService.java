package a22.sim203.tp3;

import a22.sim203.tp3.simulation.Simulation;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.*;

public class FichierEcritureService extends Service<Void> {

	private Simulation simulation;

	public FichierEcritureService(Simulation sim) {
		super();
		this.simulation = sim;
	}

	@Override
	protected Task<Void> createTask() {
		return new FichierWriterVoidTask();
	}

	public class FichierWriterVoidTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {


			ObjectOutputStream bw = null;
			try {
				bw = new ObjectOutputStream(new FileOutputStream("simulation.sim"));
				bw.writeObject(simulation);
			} finally {
				if (bw != null) {
					bw.close();
				}
			}

			return null; // mendataire avec Void
		}
	}

	public void setContenuString(Simulation sim) {
		this.simulation = sim;
	}

}
