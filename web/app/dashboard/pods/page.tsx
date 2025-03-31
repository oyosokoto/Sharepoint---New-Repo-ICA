'use client';

import { useEffect, useState } from 'react';
import { collection, getDocs, query, orderBy, doc, updateDoc } from 'firebase/firestore';
import { db } from '../../../lib/firebase';
import { useAuth } from '../../../contexts/AuthContext';
import { PaymentPod } from '../../../types';
import { formatCurrency } from '../../../utils/podUtils';
import Link from 'next/link';
import QRCode from 'react-qr-code';

export default function Pods() {
  const { user } = useAuth();
  const [pods, setPods] = useState<PaymentPod[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedPod, setSelectedPod] = useState<PaymentPod | null>(null);
  const [showQRModal, setShowQRModal] = useState(false);
  const [updatingPod, setUpdatingPod] = useState('');
  const [showPodDetails, setShowPodDetails] = useState(false);
  const [selectedPodForDetails, setSelectedPodForDetails] = useState<PaymentPod | null>(null);

  useEffect(() => {
    const fetchPods = async () => {
      if (!user) return;

      try {
        const podsQuery = query(
          collection(db!, 'pods'),
          orderBy('createdAt', 'desc')
        );

        const podsSnapshot = await getDocs(podsQuery);
        const podsData = podsSnapshot.docs.map((doc) => ({
          id: doc.id,
          ...doc.data(),
        })) as PaymentPod[];

        setPods(podsData);
      } catch (error) {
        console.error('Error fetching pods:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchPods();
  }, [user]);

  const togglePodStatus = async (podId: string, currentStatus: boolean) => {
    if (!podId) return;

    setUpdatingPod(podId);

    try {
      const podRef = doc(db!, 'pods', podId);
      await updateDoc(podRef, {
        active: !currentStatus
      });

      // Update local state
      setPods(pods.map(pod =>
        pod.id === podId ? { ...pod, active: !currentStatus } : pod
      ));

    } catch (error) {
      console.error('Error updating pod status:', error);
    } finally {
      setUpdatingPod('');
    }
  };

  const showQRCode = (pod: PaymentPod) => {
    setSelectedPod(pod);
    setShowQRModal(true);
  };

  const handlePodClick = (pod: PaymentPod) => {
    setSelectedPodForDetails(pod);
    setShowPodDetails(true);
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-purple-deep"></div>
      </div>
    );
  }

  return (
    <div className="px-4 py-6">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-semibold text-purple-deep">Payment Pods</h1>
        <Link
          href="/dashboard/create-pod"
          className="px-4 py-2 bg-purple-deep text-white rounded-md hover:bg-purple-royal"
        >
          Create New Pod
        </Link>
      </div>

      {pods.length > 0 ? (
        <div className="bg-white rounded-lg shadow-md overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-purple-0">
                <tr>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Business
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Items
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Total
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Podders
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Pod Code
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th scope="col" className="px-6 py-3 text-right text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {pods.map((pod) => (
                  <tr
                    key={pod.id}
                    className="hover:bg-purple-0"
                  >
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {pod.businessName}
                    </td>
                    <td className="px-6 py-4 text-sm text-gray-500">
                      <div className="max-h-20 overflow-y-auto">
                        {pod.items && pod.items.length > 0 ? (
                          <ul className="space-y-1">
                            {pod.items.map((item, idx) => (
                              <li key={idx} className="whitespace-nowrap">
                                {item.name} <span className="text-xs text-gray-500 dark:text-gray-400">(x{item.quantity})</span> - {formatCurrency(item.price)}
                              </li>
                            ))}
                          </ul>
                        ) : (
                          <span className="text-gray-400">No items</span>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {formatCurrency(pod.totalAmount)}
                    </td>
                    <td
                      className="px-6 py-4 whitespace-nowrap text-sm text-gray-500"
                      onClick={() => handlePodClick(pod)}
                    >
                      {pod.podderCount}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-purple-deep">
                      {pod.podCode}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${pod.active
                        ? 'bg-green-100 text-green-800'
                        : 'bg-red-100 text-red-800'
                        }`}>
                        {pod.active ? 'Active' : 'Closed'}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <button
                        onClick={() => showQRCode(pod)}
                        className="text-purple-deep hover:text-purple-royal mr-4 cursor-pointer"
                      >
                        Show QR
                      </button>
                      <button
                        onClick={() => togglePodStatus(pod.id!, pod.active)}
                        disabled={updatingPod === pod.id}
                        className={`${pod.active
                          ? 'text-red-600 hover:text-red-800'
                          : 'text-green-600 hover:text-green-800'
                          } ${updatingPod === pod.id ? 'opacity-50 cursor-not-allowed' : ''} cursor-pointer`}
                      >
                        {updatingPod === pod.id
                          ? 'Updating...'
                          : pod.active ? 'Close Pod' : 'Reactivate Pod'}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow-md p-8 text-center">
          <div className="inline-flex items-center justify-center w-12 h-12 rounded-full bg-purple-0 text-purple-deep mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v3m0 0v3m0-3h3m-3 0H9m12 0a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <h2 className="text-xl font-medium text-purple-deep mb-2">No Payment Pods Yet</h2>
          <p className="text-gray mb-6">Create your first payment pod to get started.</p>
          <Link
            href="/dashboard/create-pod"
            className="px-4 py-2 bg-purple-deep text-white rounded-md hover:bg-purple-royal"
          >
            Create New Pod
          </Link>
        </div>
      )}

      {/* Pod Details Modal */}
      {showPodDetails && selectedPodForDetails && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl p-6 max-w-md w-full">
            <div className="flex justify-between items-center mb-4">
              <h3 className="text-lg font-medium text-purple-deep">Pod Details</h3>
              <button
                onClick={() => setShowPodDetails(false)}
                className="text-gray-500 hover:text-gray-700"
              >
                ×
              </button>
            </div>

            <div className="space-y-4">
              <div>
                <p className="text-sm text-gray-500">Split Type</p>
                <p className="font-medium">
                  {selectedPodForDetails.splitType === 'equal' ? 'Equal Split' :
                    selectedPodForDetails.splitType === 'random' ? 'Random Split' :
                      selectedPodForDetails.splitType === 'custom' ? 'Custom Split' : 'Equal Split'}
                </p>
              </div>

              {selectedPodForDetails.splitAmounts && selectedPodForDetails.splitAmounts.length > 0 && (
                <div>
                  <p className="text-sm text-gray-500 mb-2">Split Amounts</p>
                  <div className="bg-purple-0 p-3 rounded-lg">
                    {selectedPodForDetails.splitAmounts.map((amount, index) => (
                      <div key={index} className="flex justify-between py-1">
                        <span>Podder {index + 1}</span>
                        <span className="font-medium">{formatCurrency(amount)}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>

            <div className="mt-4 flex justify-end">
              <button
                onClick={() => setShowPodDetails(false)}
                className="px-4 py-2 bg-purple-deep text-white rounded-md hover:bg-purple-royal"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {/* QR Code Modal */}
      {showQRModal && selectedPod && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg shadow-xl p-6 max-w-md w-full">
            <div className="text-center mb-4">
              <h3 className="text-lg font-medium text-purple-deep">Pod QR Code</h3>
              <p className="text-sm text-gray">Share this with your customers</p>
            </div>

            <div className="flex flex-col items-center justify-center gap-4 mb-6">
              <div className="text-center">
                <p className="text-sm text-gray mb-1">Pod Code</p>
                <div className="text-2xl font-bold text-purple-deep tracking-wider">{selectedPod.podCode}</div>
              </div>

              <div className="bg-white p-4 rounded-lg shadow">
                <QRCode
                  value={selectedPod.podCode}
                  size={200}
                  style={{ height: "auto", maxWidth: "100%", width: "100%" }}
                />
              </div>
            </div>

            <div className="border-t border-gray-200 pt-4">
              <div className="mb-3">
                <p className="text-sm text-gray mb-2">Items:</p>
                {selectedPod.items && selectedPod.items.length > 0 ? (
                  <ul className="space-y-1 max-h-32 overflow-y-auto">
                    {selectedPod.items.map((item, idx) => (
                      <li key={idx} className="flex justify-between text-sm">
                        <span>
                          {item.name} <span className="text-xs text-gray-500 dark:text-gray-400">(x{item.quantity})</span>
                        </span>
                        <span className="font-medium">{formatCurrency(item.subtotal)}</span>
                      </li>
                    ))}
                  </ul>
                ) : (
                  <p className="text-sm text-gray-400">No items</p>
                )}
              </div>
              <div className="flex justify-between text-sm mt-3 font-medium">
                <span className="text-gray">Total Amount:</span>
                <span>{formatCurrency(selectedPod.totalAmount)}</span>
              </div>
              <div className="flex justify-between text-sm mt-2">
                <span className="text-gray">Split Method:</span>
                <span className="font-medium">
                  {selectedPod.splitType === 'equal' ? 'Equal Split' :
                    selectedPod.splitType === 'random' ? 'Random Split' :
                      selectedPod.splitType === 'custom' ? 'Custom Split' : 'Equal Split'}
                </span>
              </div>

              {selectedPod.splitAmounts && selectedPod.splitAmounts.length > 0 ? (
                <div className="mt-3">
                  <p className="text-sm text-gray mb-2">Payment Distribution:</p>
                  <div className="grid grid-cols-2 gap-2 max-h-32 overflow-y-auto">
                    {selectedPod.splitAmounts.map((amount, index) => (
                      <div key={index} className="flex justify-between text-sm bg-purple-0 p-2 rounded">
                        <span className="text-xs text-gray-500">Podder {index + 1}</span>
                        <span className="font-medium">{formatCurrency(amount)}</span>
                      </div>
                    ))}
                  </div>
                </div>
              ) : (
                <div className="flex justify-between text-sm mt-2">
                  <span className="text-gray">Amount per Podder:</span>
                  <span className="font-medium">{formatCurrency(selectedPod.amountPerPodder)}</span>
                </div>
              )}
            </div>

            <div className="mt-6 flex justify-end">
              <button
                onClick={() => setShowQRModal(false)}
                className="px-4 py-2 bg-purple-deep text-white rounded-md hover:bg-purple-royal"
              >
                Close
              </button>

            </div>
          </div>
        </div>
      )}
    </div>
  );
}
