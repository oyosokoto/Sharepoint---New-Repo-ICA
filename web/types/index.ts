export interface PodItem {
  id: string;
  name: string;
  price: number;
  quantity: number;
  subtotal: number;
}

export interface PaymentPod {
  id?: string;
  businessName: string;
  items: PodItem[];
  totalAmount: number;
  podderCount: number;
  amountPerPodder: number;
  splitType?: string;
  splitAmounts?: number[];
  podCode: string;
  createdAt: Date | string;
  updatedAt: Date | string;
  createdBy: string;
  active: boolean;
}

export interface PodderJoin {
  podId: string;
  userId: string;
  joinedAt: Date;
  hasPaid: boolean;
}

export enum TransactionStatus {
  PENDING = "pending",
  COMPLETED = "completed",
  FAILED = "failed",
  REFUNDED = "refunded",
}

// Import Timestamp from firebase-admin for server-side code
import type { Timestamp as FirebaseTimestamp } from "firebase-admin/firestore";

export interface Transaction {
  id?: string;
  userId: string;
  podId: string;
  amount: number;
  status: TransactionStatus;
  stripeSessionId?: string;
  stripePaymentIntentId?: string;
  createdAt: Date | string | FirebaseTimestamp;
  updatedAt: Date | string | FirebaseTimestamp;
  totalPodders: number;
  totalPodAmount: number;
  businessName: string;
}
