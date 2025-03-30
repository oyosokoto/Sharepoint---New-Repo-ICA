import { db } from "../lib/firebase-admin";
import { Transaction, TransactionStatus } from "../types";
import { Timestamp } from "firebase-admin/firestore";
import logger from "./logger";

// Collection references
const TRANSACTIONS_COLLECTION = "transactions";
const PODS_COLLECTION = "pods";
const PODDER_JOINS_COLLECTION = "podders";

/**
 * Creates a new transaction in Firestore
 */
export const createTransaction = async (
  userId: string,
  podId: string,
  amount: number,
  stripeSessionId?: string
): Promise<string> => {
  try {
    const transaction: Omit<Transaction, "id"> = {
      userId,
      podId,
      amount,
      status: TransactionStatus.PENDING,
      stripeSessionId,
      createdAt: Timestamp.now(),
      updatedAt: Timestamp.now(),
    };

    const docRef = await db
      .collection(TRANSACTIONS_COLLECTION)
      .add(transaction);

    logger.info(`Created transaction with ID: ${docRef.id}`, {
      userId,
      podId,
      amount,
      status: TransactionStatus.PENDING,
    });

    return docRef.id;
  } catch (error) {
    logger.error("Error creating transaction:", error);
    throw error;
  }
};

/**
 * Updates a transaction with Stripe payment intent ID and status
 */
export const updateTransactionWithPaymentIntent = async (
  transactionId: string,
  paymentIntentId: string,
  status: TransactionStatus
): Promise<void> => {
  try {
    await db.collection(TRANSACTIONS_COLLECTION).doc(transactionId).update({
      stripePaymentIntentId: paymentIntentId,
      status,
      updatedAt: Timestamp.now(),
    });

    logger.success(`Updated transaction ${transactionId} with payment intent`, {
      transactionId,
      paymentIntentId,
      status,
    });
  } catch (error) {
    logger.error("Error updating transaction:", error);
    throw error;
  }
};

/**
 * Updates a transaction status
 */
export const updateTransactionStatus = async (
  transactionId: string,
  status: TransactionStatus
): Promise<void> => {
  try {
    await db.collection(TRANSACTIONS_COLLECTION).doc(transactionId).update({
      status,
      updatedAt: Timestamp.now(),
    });

    logger.info(`Updated transaction ${transactionId} status to ${status}`, {
      transactionId,
      status,
    });
  } catch (error) {
    logger.error("Error updating transaction status:", error);
    throw error;
  }
};

/**
 * Gets a transaction by ID
 */
export const getTransactionById = async (
  transactionId: string
): Promise<Transaction | null> => {
  try {
    logger.debug(`Fetching transaction by ID: ${transactionId}`);

    const transactionDoc = await db
      .collection(TRANSACTIONS_COLLECTION)
      .doc(transactionId)
      .get();

    if (transactionDoc.exists) {
      logger.debug(`Found transaction with ID: ${transactionId}`);
      return { id: transactionDoc.id, ...transactionDoc.data() } as Transaction;
    }

    logger.warning(`Transaction not found with ID: ${transactionId}`);
    return null;
  } catch (error) {
    logger.error("Error getting transaction:", error);
    throw error;
  }
};

/**
 * Gets a transaction by Stripe session ID
 */
export const getTransactionBySessionId = async (
  sessionId: string
): Promise<Transaction | null> => {
  try {
    logger.debug(`Fetching transaction by session ID: ${sessionId}`);

    const querySnapshot = await db
      .collection(TRANSACTIONS_COLLECTION)
      .where("stripeSessionId", "==", sessionId)
      .limit(1)
      .get();

    if (!querySnapshot.empty) {
      const doc = querySnapshot.docs[0];
      logger.debug(`Found transaction with session ID: ${sessionId}`, {
        transactionId: doc.id,
      });
      return { id: doc.id, ...doc.data() } as Transaction;
    }

    logger.warning(`Transaction not found with session ID: ${sessionId}`);
    return null;
  } catch (error) {
    logger.error("Error getting transaction by session ID:", error);
    throw error;
  }
};

/**
 * Updates the PodderJoin record to mark payment as complete
 * and checks if all podders have paid the full amount
 */
export const updatePodderPaymentStatus = async (
  podId: string,
  userId: string
): Promise<void> => {
  try {
    const querySnapshot = await db
      .collection(PODS_COLLECTION)
      .doc(podId)
      .collection(PODDER_JOINS_COLLECTION)
      .where("userId", "==", userId)
      .limit(1)
      .get();

    if (!querySnapshot.empty) {
      const podderDoc = querySnapshot.docs[0];
      await podderDoc.ref.update({
        hasPaid: true,
        updatedAt: Timestamp.now(),
      });

      logger.success(`Updated podder payment status to paid`, {
        podId,
        userId,
        podderJoinId: podderDoc.id,
      });

      // After updating the podder's payment status, check if all podders have paid
      await checkAndEndPaymentPod(podId);
    } else {
      logger.warning(`No podder join record found to update payment status`, {
        podId,
        userId,
      });
    }
  } catch (error) {
    logger.error("Error updating podder payment status:", error);
    throw error;
  }
};

/**
 * Checks if the total amount paid by all podders equals the pod's total amount
 * If it does, sets the pod's active status to false
 */
export const checkAndEndPaymentPod = async (podId: string): Promise<void> => {
  try {
    // Get the pod document
    const podDoc = await db.collection(PODS_COLLECTION).doc(podId).get();

    if (!podDoc.exists) {
      logger.warning(`Pod not found when checking total payments: ${podId}`);
      return;
    }

    const pod = podDoc.data();
    if (!pod) return;

    // Get all completed transactions for this pod
    const transactionsSnapshot = await db
      .collection(TRANSACTIONS_COLLECTION)
      .where("podId", "==", podId)
      .where("status", "==", TransactionStatus.COMPLETED)
      .get();

    // Calculate the total amount paid
    let totalPaid = 0;
    transactionsSnapshot.forEach((doc) => {
      const transaction = doc.data();
      totalPaid += transaction.amount;
    });

    // Get all podders to check if everyone has paid
    const poddersSnapshot = await db
      .collection(PODS_COLLECTION)
      .doc(podId)
      .collection(PODDER_JOINS_COLLECTION)
      .get();

    const totalPodders = poddersSnapshot.size;
    let paidPodders = 0;

    poddersSnapshot.forEach((doc) => {
      const podder = doc.data();
      if (podder.hasPaid) {
        paidPodders++;
      }
    });

    logger.info(
      `Payment pod status check: ${paidPodders}/${totalPodders} podders paid, total amount: ${totalPaid}/${pod.amount}`,
      {
        podId,
        paidPodders,
        totalPodders,
        totalPaid,
        podAmount: pod.amount,
      }
    );

    // If all podders have paid and the total amount matches the pod amount
    if (paidPodders === totalPodders && totalPaid === pod.amount) {
      // Set the pod's active status to false
      await db.collection(PODS_COLLECTION).doc(podId).update({
        active: false,
        updatedAt: Timestamp.now(),
      });

      logger.success(`Payment pod completed and marked as inactive: ${podId}`, {
        podId,
        totalPaid,
        podAmount: pod.amount,
      });
    }
  } catch (error) {
    logger.error(`Error checking and ending payment pod: ${error}`, {
      podId,
    });
  }
};
