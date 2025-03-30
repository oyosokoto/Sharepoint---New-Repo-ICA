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
