package me.xiangchen.technique.posturesense;

import android.util.Log;
import me.xiangchen.app.duetos.LauncherExtension;
import me.xiangchen.app.duetos.LauncherManager;
import me.xiangchen.lib.xacAccelerometer;
import me.xiangchen.network.xacUDPTask;

public class xacPostureSenseFeatureMaker {

	public final static int WATCH = 0;
	public final static int NOWATCH = 1;
	public final static int NONE = 2;

	public final static int POSTURETIMEOUT = 700; // ms
	// public final static int TOUCHONSETTIME = 350; // ms
	final static int NUMROWSHANDEDNESS = LauncherManager.PHONEACCELFPSGAME
			* POSTURETIMEOUT / 1000;

	static final int MAXNUMROW = 256;
	static final int NUMSOURCES = 2;

	static String[] featureNames = null;
	static double[][] featureTablePhone = null;
	static double[][] featureTableWatch = null;
	static int pntrEntryPhone = 0;
	static int pntrEntryWatch = 0;
	static int numFeatures = 0;
	static String LOGTAG = "DuetOS";
	static int label = -1;

	static xacAccelerometer accelWatch;
	static xacAccelerometer accelPhone;
	static xacAccelerometer[] accels;

	/**
	 * create a table of features, including the first row (the names of the
	 * attributes)
	 */
	public static void createFeatureTable() {
		numFeatures = xacAccelerometer.NUMACCELAXES;
		featureTablePhone = new double[MAXNUMROW][numFeatures + 1];
		featureTableWatch = new double[MAXNUMROW][numFeatures + 1];
		pntrEntryPhone = 0;
		pntrEntryWatch = 0;

		accelWatch = new xacAccelerometer();
		accelPhone = new xacAccelerometer();
		accels = new xacAccelerometer[] { accelWatch, accelPhone };
	}

	/**
	 * add a row in the feature table
	 * 
	 * @param features
	 * @param val
	 */
	public static void addPhoneFeatureEntry() {
		if (pntrEntryPhone >= MAXNUMROW) {
			pntrEntryPhone = 0;
		}

		int idxFeat = 0;
		// for (int i = 0; i < NUMSOURCES; i++) {
		featureTablePhone[pntrEntryPhone][idxFeat++] = accelPhone.getX();
		featureTablePhone[pntrEntryPhone][idxFeat++] = accelPhone.getY();
		featureTablePhone[pntrEntryPhone][idxFeat++] = accelPhone.getZ();
		// }

		// featureTable[pntrEntry][numFeatures] = label;

		// Log.d(tag, "The " + (pntrEntry+1) + "th entry added");
		pntrEntryPhone++;
	}

	public static void addWatchFeatureEntry() {
		if (pntrEntryWatch >= MAXNUMROW) {
			pntrEntryWatch = 0;
		}

		int idxFeat = 0;
		// for (int i = 0; i < NUMSOURCES; i++) {
		featureTableWatch[pntrEntryWatch][idxFeat++] = accelWatch.getX();
		featureTableWatch[pntrEntryWatch][idxFeat++] = accelWatch.getY();
		featureTableWatch[pntrEntryWatch][idxFeat++] = accelWatch.getZ();
		// }

		// featureTable[pntrEntry][numFeatures] = label;

		// Log.d(tag, "The " + (pntrEntry+1) + "th entry added");
		pntrEntryWatch++;
	}

	public static void setLabel(int lb) {
		label = lb;
	}

	public static void updateWatchAccel(float[] values) {
		if (accelWatch == null)
			return;
		accelWatch.update(values[0], values[1], values[2]);
	}

	public static void updatePhoneAccel(float[] values) {
		if (accelPhone == null)
			return;
		accelPhone.update(values[0], values[1], values[2]);
	}

	public static void sendOffData(int numToSend, String[] classLabels) {
		int lockedPntrEntryPhone = pntrEntryPhone;
		int lockedPntrEntryWatch = pntrEntryWatch;
		int numToSendPhone = numToSend;
		int numToSendWatch = numToSendPhone * LauncherExtension.WATCHACCELFPS
				/ LauncherManager.PHONEACCELFPSGAME;

		if (label < 0 || numToSendPhone > lockedPntrEntryPhone
				|| numToSendWatch > lockedPntrEntryWatch)
			return;

		String strFeatureRow = "";

		// 1. the phone's
		for (int i = lockedPntrEntryPhone - numToSendPhone; i < lockedPntrEntryPhone; i++) {
			for (int j = 0; j < numFeatures; j++) {
				strFeatureRow += String.format("%.2f", featureTablePhone[i][j])
						+ ",";
			}
		}

		// 2. the watch's
		for (int i = lockedPntrEntryWatch - numToSendWatch; i < lockedPntrEntryWatch; i++) {
			for (int j = 0; j < numFeatures; j++) {
				strFeatureRow += String.format("%.2f", featureTableWatch[i][j])
						+ ",";
			}
		}

		strFeatureRow += classLabels[label] + '\0';

		new xacUDPTask().execute(strFeatureRow);
	}

	public static Object[] getFlattenedData(int numToSend) {
		int lockedPntrEntryPhone = pntrEntryPhone;
		int lockedPntrEntryWatch = pntrEntryWatch;
		int numToSendPhone = numToSend;
		int numToSendWatch = numToSendPhone * LauncherExtension.WATCHACCELFPS
				/ LauncherManager.PHONEACCELFPSGAME;

		if (pntrEntryWatch <= 0) {
			Log.d(LOGTAG, "watch accelerometer not working");
		}

		if (numToSendPhone > lockedPntrEntryPhone
				|| numToSendWatch > lockedPntrEntryWatch)
			return null;

		Object[] flattened = new Object[(numToSendPhone + numToSendWatch)
				* numFeatures];
		int idxFeature = 0;

		// 1. the phone's
		for (int i = lockedPntrEntryPhone - numToSendPhone; i < lockedPntrEntryPhone; i++) {
			for (int j = 0; j < numFeatures; j++) {
				flattened[idxFeature++] = featureTablePhone[i][j];
			}
		}

		// 2. the watch's
		for (int i = lockedPntrEntryWatch - numToSendWatch; i < lockedPntrEntryWatch; i++) {
			for (int j = 0; j < numFeatures; j++) {
				flattened[idxFeature++] = featureTableWatch[i][j];
			}
		}

		return flattened;
	}

	public static void clearData() {
		featureTablePhone = new double[MAXNUMROW][numFeatures + 1];
		featureTableWatch = new double[MAXNUMROW][numFeatures + 1];
		pntrEntryPhone = 0;
		pntrEntryWatch = 0;
	}

	public static int calculatePosture() {
		int labelNew = NONE;

		Object[] features = xacPostureSenseFeatureMaker
				.getFlattenedData(NUMROWSHANDEDNESS);
		int idxClass = -1;
		
		if(features != null) {
			try {
				idxClass = (int) PostureSenseClassifier.classify(features);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.d(LOGTAG, "cannot get features");
			}
		}

		switch (idxClass) {
		case 1:
			labelNew = WATCH;
//			if (labelNew != label) {
				Log.d(LOGTAG, "watch");
//			}
			break;
		case 0:
			labelNew = NOWATCH;
//			if (labelNew != label) {
				Log.d(LOGTAG, "no watch");
//			}
			break;
		case 2:
			labelNew = NONE;
//			if (labelNew != label) {
				Log.d(LOGTAG, "none");
//			}
			break;
		default:
			labelNew = label;
			break;
		}

		label = labelNew;
		
		clearData();
		return label;
	}

}
