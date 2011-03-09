package org.sopera.monitoring.queue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.sopera.monitoring.event.Event;
import org.sopera.monitoring.event.MonitoringException;

public class PersistentQueue<E extends Event> implements Queue<E> {
	private static Logger logger = Logger.getLogger(PersistentQueue.class
			.getName());

	private ArrayList<com.gaborcselle.persistent.PersistentQueue<E>> queues = new ArrayList<com.gaborcselle.persistent.PersistentQueue<E>>();
	private int addIndex = 0;
	private int removeIndex = 0;

	private void setUp(String path, String filename, int defragInterval,
			int numberOfQueues) {
		try {

			String tmpPathFile = path + filename;

			logger.info("Setting temporary persitent queue to: " + tmpPathFile);
			for (int i = 0; i < numberOfQueues; i++) {
				com.gaborcselle.persistent.PersistentQueue<E> queue = new com.gaborcselle.persistent.PersistentQueue<E>(
						tmpPathFile + i, defragInterval);
				queues.add(queue);
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Could not initiate queue", e);
		}
	}

	public PersistentQueue(String path, String filename, int defragInterval,
			int numberOfQueues) {
		setUp(path, filename, defragInterval, numberOfQueues);
	}

	public PersistentQueue(String filename, int defragInterval,
			int numberOfQueues) {
		String tmpPath = System.getProperty("java.io.tmpdir");
		File file = new File(tmpPath);
		String tmpPathFile = file.getPath() + File.separator;

		setUp(tmpPathFile, filename, defragInterval, numberOfQueues);
	}

	public void add(E object) throws MonitoringException {
		try {
			getAddQueue().add(object);
		} catch (IOException e) {
			throw new MonitoringException("1100",
					"Could not add event to persitent queue.", e, object);
		}
	}

	private synchronized com.gaborcselle.persistent.PersistentQueue<E> getAddQueue() {
		addIndex++;
		if (addIndex >= queues.size())
			addIndex = 0;
		return queues.get(addIndex);
	}

	private synchronized com.gaborcselle.persistent.PersistentQueue<E> getRemoveQueue() {
		removeIndex++;
		if (removeIndex >= queues.size())
			removeIndex = 0;
		return queues.get(removeIndex);
	}

	public boolean isEmpty() {
		for (com.gaborcselle.persistent.PersistentQueue<E> queue : queues) {
			if (queue.isEmpty() == false)
				return false;
		}
		return true;
	}

	public E remove() {
		try {
			com.gaborcselle.persistent.PersistentQueue<E> queue = getRemoveQueue();
			E result = queue.remove();
			if (result == null) {
				for (int i = 0; i < queues.size(); i++) {
					result = queues.get(i).remove();
					if (result != null)
						return result;
				}
				return null;
			} else
				return result;
		} catch (IOException e) {
			throw new MonitoringException("1101",
					"Could not receive event from persitent queue.", e);
		}
	}

}
