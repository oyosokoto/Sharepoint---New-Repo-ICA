'use client';

import { useEffect, useState } from 'react';
import { collection, getDocs, query, orderBy, limit } from 'firebase/firestore';
import { db } from '../../lib/firebase';
import { useAuth } from '../../contexts/AuthContext';
import { PaymentPod } from '../../types';
import { formatCurrency } from '../../utils/podUtils';
import Link from 'next/link';

export default function Dashboard() {
  const { user } = useAuth();
  const [recentPods, setRecentPods] = useState<PaymentPod[]>([]);
  const [loading, setLoading] = useState(true);
  const [stats, setStats] = useState({
    totalPods: 0,
    activePods: 0,
    totalAmount: 0,
  });

  useEffect(() => {
    const fetchData = async () => {
      if (!user) return;

      try {
        // Get recent pods
        const podsQuery = query(
          collection(db, 'pods'),
          orderBy('createdAt', 'desc'),
          limit(5)
        );

        const podsSnapshot = await getDocs(podsQuery);
        const podsData = podsSnapshot.docs.map((doc) => ({
          id: doc.id,
          ...doc.data(),
        })) as PaymentPod[];

        setRecentPods(podsData);

        // Calculate stats
        const allPodsQuery = query(collection(db, 'pods'));
        const allPodsSnapshot = await getDocs(allPodsQuery);
        const allPods = allPodsSnapshot.docs.map((doc) => doc.data()) as PaymentPod[];

        const activePods = allPods.filter(pod => pod.active).length;
        const totalAmount = allPods.reduce((sum, pod) => sum + pod.totalAmount, 0);

        setStats({
          totalPods: allPods.length,
          activePods,
          totalAmount,
        });

      } catch (error) {
        console.error('Error fetching data:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [user]);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-purple-600"></div>
      </div>
    );
  }

  return (
    <div className="px-4 py-8">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-purple-600 mb-2">Business Dashboard</h1>
          <p className="text-gray-600 dark:text-gray-300">Manage your payment pods and track transactions</p>
        </div>
        <div className="mt-4 md:mt-0">
          <Link
            href="/dashboard/create-pod"
            className="inline-flex items-center justify-center px-5 py-2.5 border border-transparent text-sm font-medium rounded-md text-white bg-purple-600 shadow-md hover:bg-purple-700 transition-colors duration-200"
          >
            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clipRule="evenodd" />
            </svg>
            Create New Pod
          </Link>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
        <div className="bg-white dark:bg-dark-surface rounded-xl shadow-card dark:shadow-card-dark p-6 border-l-4 border-purple-600 transform transition-transform hover:scale-105 duration-300">
          <div className="flex items-center mb-4">
            <div className="p-3 rounded-full bg-purple-50 dark:bg-purple-900/30 mr-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-purple-600 dark:text-purple-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10" />
              </svg>
            </div>
            <h2 className="text-lg font-semibold text-gray-700 dark:text-gray-200">Total Payment Pods</h2>
          </div>
          <p className="text-4xl font-bold text-purple-600 dark:text-purple-400">{stats.totalPods}</p>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">All time created pods</p>
        </div>

        <div className="bg-white dark:bg-dark-surface rounded-xl shadow-card dark:shadow-card-dark p-6 border-l-4 border-success transform transition-transform hover:scale-105 duration-300">
          <div className="flex items-center mb-4">
            <div className="p-3 rounded-full bg-green-50 dark:bg-green-900/30 mr-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-success" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h2 className="text-lg font-semibold text-gray-700 dark:text-gray-200">Active Pods</h2>
          </div>
          <p className="text-4xl font-bold text-success">{stats.activePods}</p>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">Currently active payment pods</p>
        </div>

        <div className="bg-white dark:bg-dark-surface rounded-xl shadow-card dark:shadow-card-dark p-6 border-l-4 border-accent-light transform transition-transform hover:scale-105 duration-300">
          <div className="flex items-center mb-4">
            <div className="p-3 rounded-full bg-pink-50 dark:bg-pink-900/30 mr-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-accent" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h2 className="text-lg font-semibold text-gray-700 dark:text-gray-200">Total Amount</h2>
          </div>
          <p className="text-4xl font-bold text-accent">{formatCurrency(stats.totalAmount)}</p>
          <p className="text-sm text-gray-500 dark:text-gray-400 mt-2">Total value of all pods</p>
        </div>
      </div>

      {/* Recent Pods */}
      <div className="bg-white dark:bg-dark-surface rounded-xl shadow-card dark:shadow-card-dark overflow-hidden mb-10">
        <div className="flex justify-between items-center p-6 border-b border-gray-200 dark:border-dark-border">
          <div className="flex items-center">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-purple-600 dark:text-purple-400 mr-3" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
            <h2 className="text-xl font-semibold text-gray-800 dark:text-white">Recent Payment Pods</h2>
          </div>
          <Link
            href="/dashboard/pods"
            className="text-sm text-purple-600 hover:text-purple-700 dark:text-purple-400 dark:hover:text-purple-300 font-medium flex items-center"
          >
            View All
            <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 ml-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
            </svg>
          </Link>
        </div>

        {recentPods.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200 dark:divide-dark-border">
              <thead className="bg-purple-50 dark:bg-purple-900/20">
                <tr>
                  <th scope="col" className="px-6 py-4 text-left text-xs font-medium text-gray-600 dark:text-gray-300 uppercase tracking-wider">
                    Business
                  </th>
                  <th scope="col" className="px-6 py-4 text-left text-xs font-medium text-gray-600 dark:text-gray-300 uppercase tracking-wider">
                    Item
                  </th>
                  <th scope="col" className="px-6 py-4 text-left text-xs font-medium text-gray-600 dark:text-gray-300 uppercase tracking-wider">
                    Total
                  </th>
                  <th scope="col" className="px-6 py-4 text-left text-xs font-medium text-gray-600 dark:text-gray-300 uppercase tracking-wider">
                    Podders
                  </th>
                  <th scope="col" className="px-6 py-4 text-left text-xs font-medium text-gray-600 dark:text-gray-300 uppercase tracking-wider">
                    Pod Code
                  </th>
                  <th scope="col" className="px-6 py-4 text-left text-xs font-medium text-gray-600 dark:text-gray-300 uppercase tracking-wider">
                    Status
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white dark:bg-dark-surface divide-y divide-gray-200 dark:divide-dark-border">
                {recentPods.map((pod) => (
                  <tr key={pod.id} className="hover:bg-purple-50 dark:hover:bg-purple-900/10 transition-colors duration-150">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
                      {pod.businessName}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600 dark:text-gray-300">
                      {pod.itemName} <span className="text-xs text-gray-500 dark:text-gray-400">(x{pod.quantity})</span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 dark:text-white">
                      {formatCurrency(pod.totalAmount)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 text-purple-600 dark:text-purple-400 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                        </svg>
                        <span className="text-sm text-gray-600 dark:text-gray-300">{pod.podderCount}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="px-3 py-1 inline-flex text-sm font-medium rounded-md bg-purple-50 text-purple-700 dark:bg-purple-900/30 dark:text-purple-300">
                        {pod.podCode}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-3 py-1 inline-flex text-xs font-semibold rounded-full ${pod.active
                        ? 'bg-green-100 text-green-800 dark:bg-green-900/30 dark:text-green-300'
                        : 'bg-red-100 text-red-800 dark:bg-red-900/30 dark:text-red-300'
                        }`}>
                        {pod.active ? 'Active' : 'Closed'}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="text-center py-12">
            <div className="inline-flex items-center justify-center w-16 h-16 rounded-full bg-purple-50 dark:bg-purple-900/30 mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-8 w-8 text-purple-600 dark:text-purple-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 9v3m0 0v3m0-3h3m-3 0H9m12 0a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h3 className="text-xl font-medium text-gray-900 dark:text-white mb-2">No payment pods created yet</h3>
            <p className="text-gray-500 dark:text-gray-400 mb-6">Create your first payment pod to get started</p>
            <Link
              href="/dashboard/create-pod"
              className="inline-flex items-center px-5 py-2.5 border border-transparent text-sm font-medium rounded-md text-white bg-purple-600 shadow-md hover:bg-purple-700 transition-colors duration-200"
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clipRule="evenodd" />
              </svg>
              Create Your First Pod
            </Link>
          </div>
        )}
      </div>

      {/* Quick Actions */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white dark:bg-dark-surface rounded-xl shadow-card dark:shadow-card-dark p-6 transform transition-all hover:shadow-lg duration-300 flex flex-col items-center text-center">
          <div className="w-14 h-14 rounded-full bg-purple-50 dark:bg-purple-900/30 flex items-center justify-center mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-7 w-7 text-purple-600 dark:text-purple-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
            </svg>
          </div>
          <h3 className="text-lg font-semibold text-gray-800 dark:text-white mb-2">Create Pod</h3>
          <p className="text-gray-500 dark:text-gray-400 mb-4">Create a new payment pod for your customers</p>
          <Link
            href="/dashboard/create-pod"
            className="mt-auto inline-flex items-center justify-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-purple-600 hover:bg-purple-700 w-full transition-colors duration-200"
          >
            Create New Pod
          </Link>
        </div>

        <div className="bg-white dark:bg-dark-surface rounded-xl shadow-card dark:shadow-card-dark p-6 transform transition-all hover:shadow-lg duration-300 flex flex-col items-center text-center">
          <div className="w-14 h-14 rounded-full bg-purple-50 dark:bg-purple-900/30 flex items-center justify-center mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-7 w-7 text-purple-600 dark:text-purple-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
            </svg>
          </div>
          <h3 className="text-lg font-semibold text-gray-800 dark:text-white mb-2">Manage Pods</h3>
          <p className="text-gray-500 dark:text-gray-400 mb-4">View and manage all your existing payment pods</p>
          <Link
            href="/dashboard/pods"
            className="mt-auto inline-flex items-center justify-center px-4 py-2 border border-purple-600 text-sm font-medium rounded-md text-purple-600 bg-white hover:bg-purple-50 dark:bg-dark-surface dark:hover:bg-purple-900/10 dark:text-purple-400 w-full transition-colors duration-200"
          >
            View All Pods
          </Link>
        </div>

        <div className="bg-white dark:bg-dark-surface rounded-xl shadow-card dark:shadow-card-dark p-6 transform transition-all hover:shadow-lg duration-300 flex flex-col items-center text-center">
          <div className="w-14 h-14 rounded-full bg-purple-50 dark:bg-purple-900/30 flex items-center justify-center mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" className="h-7 w-7 text-purple-600 dark:text-purple-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
          </div>
          <h3 className="text-lg font-semibold text-gray-800 dark:text-white mb-2">Analytics</h3>
          <p className="text-gray-500 dark:text-gray-400 mb-4">View detailed analytics and reports of your pods</p>
          <button
            className="mt-auto inline-flex items-center justify-center px-4 py-2 border border-gray-300 dark:border-gray-600 text-sm font-medium rounded-md text-gray-700 dark:text-gray-300 bg-white dark:bg-dark-surface hover:bg-gray-50 dark:hover:bg-gray-800 w-full transition-colors duration-200"
            disabled
          >
            Coming Soon
          </button>
        </div>
      </div>
    </div>
  );
}
