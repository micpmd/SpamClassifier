package bayes.classifier.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author jspacco
 *         <p>
 *         Bayesian spam filters based on this formulation of Bayes Thm:
 *         <p>
 * 
 *         Pr(S|W) = &nbsp;&nbsp; Pr(W|S) * Pr(S) <br>
 *         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * 
 *         -------------- <br>
 *         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 *         &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Pr(W) <br>
 * 
 * 
 */
public class SpamClassifier {
	// Put some instance variables here
	private Map<String, Integer> spamMap = new HashMap<>();
	private Map<String, Integer> hamMap = new HashMap<>();
	private ArrayList<String> spamMessages = new ArrayList<String>();
	private ArrayList<String> hamMessages = new ArrayList<String>();
	private double Thres = 0;

	/**
	 * Add the spam file to be read from the given input stream to our dataset.
	 * 
	 * @param in
	 *            The inputstream from which to read the spam file.
	 */
	public void addSpamFile(InputStream in) throws IOException {
		Scanner scan = new Scanner(in);
		Map<String, Integer> seen = new HashMap<>();
		while (scan.hasNext()) {
			String s = scan.next();
			if (!seen.containsKey(s)) {
				if (spamMap.containsKey(s)) {
					int i = this.spamMap.get(s);
					this.spamMap.put(s, i + 1);
				} else {
					this.spamMap.put(s, 1);
				}
				seen.put(s, 1);
			}
		}
	}

	/**
	 * Add the ham file to be read from the given input stream to our dataset.
	 * 
	 * @param in
	 *            The inputstream from which to read the ham file.
	 */
	public void addHamFile(InputStream in) throws IOException {
		Scanner scan = new Scanner(in);
		Map<String, Integer> seen = new HashMap<>();
		while (scan.hasNext()) {
			String s = scan.next();
			if (!seen.containsKey(s)) {
				if (hamMap.containsKey(s)) {
					int i = this.hamMap.get(s);
					this.hamMap.put(s, i + 1);
				} else {
					this.hamMap.put(s, 1);
				}
				seen.put(s, 1);
			}
		}
	}

	/**
	 * Add all the files in the given directory to our dataset as spam files.
	 * 
	 * <b>NOTE</b> This method can (and really should) call
	 * {@link #addSpamFile(InputStream)}.
	 * 
	 * @param dir
	 *            The directory from which to read the files
	 */
	public void addAllSpamFilesInDirectory(File dir) throws IOException {
		for (File f : dir.listFiles()) {
			this.addSpamFile(new FileInputStream(f));
			this.spamMessages.add(f.getName());
		}
	}

	/**
	 * Add all the files in the given directory to our dataset as ham files.
	 * 
	 * <b>NOTE</b> This method can (and really should) call
	 * {@link #addHamFile(InputStream)}.
	 * 
	 * @param dir
	 *            The directory from which to read the files
	 */
	public void addAllHamFilesInDirectory(File dir) throws IOException {
		for (File f : dir.listFiles()) {
			this.addHamFile(new FileInputStream(f));
			this.hamMessages.add(f.getName());
		}
	}

	/**
	 * Get the number of spam messages in the data set.
	 * 
	 * @return The number of spam messages in the data set.
	 */
	public int getNumSpamMessages() {
		return this.spamMessages.size();
	}

	/**
	 * Get the number of ham messages in the data set.
	 * 
	 * @return The number of ham messages in the data set.
	 */
	public int getNumHamMessages() {
		return this.hamMessages.size();
	}

	/**
	 * Return a set of all the words that occur in at least one spam message.
	 * 
	 * @return
	 */
	public Set<String> getAllSpamWords() {
		return this.spamMap.keySet();
	}

	/**
	 * Return a set of all the words that occur in at least one ham message.
	 * 
	 * @return
	 */
	public Set<String> getAllHamWords() {
		return this.hamMap.keySet();
	}

	/**
	 * Return a set of all the unique words the classifier has seen so far.
	 * 
	 * @return A set of all of the unique words that the classifier has seen so
	 *         far.
	 */
	public Set<String> getAllWords() {
		Set<String> set1 = new HashSet<String>(spamMap.keySet());
		Set<String> set2 = new HashSet<String>(hamMap.keySet());
		//set1.retainAll(set2);
		set1.addAll(set2);
		return set1;
	}

	/**
	 * Get the number of times the given word occurs in the spam messages in the
	 * data set.
	 * 
	 * @param word
	 * @return The number of occurrences of the given word in the spam messages
	 *         in the data set.
	 */
	public int getNumSpamOccurrences(String word) {
		if (this.spamMap.containsKey(word)) {
			return this.spamMap.get(word);
		} else {
			return 0;
		}
	}

	/**
	 * Get the number of times the given word occurs in the ham messages in the
	 * data set.
	 * 
	 * @param word
	 * @return The number of occurrences of the given word in the ham messages
	 *         in the data set.
	 */
	public int getNumHamOccurrences(String word) {
		if (this.hamMap.containsKey(word)) {
			return this.hamMap.get(word);
		} else {
			return 0;
		}
	}

	/**
	 * Set the threshold at which we classify a message as spam.
	 * 
	 * @param t
	 *            the threshold
	 */
	public void setThreshold(double t) {
		this.Thres = t;
	}

	/**
	 * Get the threshold at which we classify a message as spam.
	 */
	public double getThreshold() {
		return this.Thres;
	}

	/**
	 * Return the overall probability that a word occurs in any message, either
	 * spam or ham.
	 * 
	 * @param word
	 * @return
	 */
	public double probWord(String word) {
		if (spamMap.containsKey(word) && hamMap.containsKey(word)) {
			return (double) (this.spamMap.get(word) + this.hamMap.get(word))
					/ (spamMessages.size() + hamMessages.size());
		} else if (spamMap.containsKey(word)) {
			return (double) this.spamMap.get(word)
					/ (spamMessages.size() + hamMessages.size());
		} else if (hamMap.containsKey(word)) {
			return (double) this.hamMap.get(word)
					/ (spamMessages.size() + hamMessages.size());
		}
		return 0;
	}

	/**
	 * Using the probabilities that we have trained into our dataset determine
	 * the score for the given message.
	 * 
	 * @param in
	 * @return The score for the given file.
	 */
	public double probSpamForMessage(InputStream in) {
		Scanner scan = new Scanner(in);
		double a = 1;
		double b = 1;
		Map<String, Integer> seen = new HashMap<>();

		while (scan.hasNext()) {
			String word = scan.next();

			if (seen.keySet().contains(word)) {
				continue;
			} else {
				seen.put(word, 1);
			}

			if (this.probSpamGivenWord(word) == null) {
				continue;
			}

			double p = this.probSpamGivenWord(word);

			if (p == 1) {
				a *= 0.95;
				b *= 0.05;
			} else if (p == 0) {
				a *= 0.05;
				b *= 0.95;
			} else {
				a = a * p;
				b = b * (1 - p);
			}
		}

		return a / (a + b);
	}

	/**
	 * Compute the probability that a message is spam given that it contains the
	 * given word, using the corpus of messages that has so far been used for
	 * training.
	 * 
	 * You can return null if the given has never occurred as either spam or ham
	 * in any message in the training data.
	 * 
	 * 
	 * @param word
	 *            The word to test.
	 * @return The probability (between 0.0 and 1.0) that a message is spam
	 *         given that it contains the given word, or null if the probability
	 *         for the given word cannot be computed.
	 */
	public Double probSpamGivenWord(String word) {
		if (spamMap.containsKey(word) && hamMap.containsKey(word)) {
			int i = spamMap.get(word);
			return (double) i / (i + hamMap.get(word));
		} else if (spamMap.containsKey(word)) {
			return 1.0;
		} else if (hamMap.containsKey(word)) {
			return 0.0;
		}
		return null;
	}

	/**
	 * Read a message from the given InputStream. Return true if the message is
	 * spam (i.e. its score is above the current threshold) and false otherwise.
	 * 
	 * @param in
	 * @return True if the given message is spam; false if it's ham.
	 */
	public boolean isSpam(InputStream in) {
		if (this.probSpamForMessage(in) > this.Thres) {
			return true;
		}
		return false;
	}

}
