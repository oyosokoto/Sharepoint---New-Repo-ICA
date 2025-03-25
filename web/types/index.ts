export interface PaymentPod {
  id?: string;
  businessName: string;
  itemName: string;
  itemPrice: number;
  quantity: number;
  totalAmount: number;
  podderCount: number;
  amountPerPodder: number;
  podCode: string;
  createdAt: Date | string;
  createdBy: string;
  active: boolean;
}

export interface PodderJoin {
  podId: string;
  userId: string;
  joinedAt: Date;
  hasPaid: boolean;
}
