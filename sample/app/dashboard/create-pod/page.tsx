'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { collection, addDoc, serverTimestamp } from 'firebase/firestore';
import { db } from '../../../lib/firebase';
import { useAuth } from '../../../contexts/AuthContext';
import { generatePodCode, calculateAmountPerPodder, formatCurrency } from '../../../utils/podUtils';
import QRCode from 'react-qr-code';

export default function CreatePod() {
  const router = useRouter();
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [podCode, setPodCode] = useState('');
  const [formData, setFormData] = useState({
    businessName: '',
    itemName: '',
    itemPrice: '',
    quantity: '1',
    podderCount: '1',
  });
  
  const [calculatedValues, setCalculatedValues] = useState({
    totalAmount: 0,
    amountPerPodder: 0,
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    
    // Calculate values when relevant fields change
    if (['itemPrice', 'quantity', 'podderCount'].includes(name)) {
      calculateValues({
        ...formData,
        [name]: value,
      });
    }
  };

  const calculateValues = (data: typeof formData) => {
    const itemPrice = parseFloat(data.itemPrice) || 0;
    const quantity = parseInt(data.quantity) || 1;
    const podderCount = parseInt(data.podderCount) || 1;
    
    const totalAmount = itemPrice * quantity;
    const amountPerPodder = calculateAmountPerPodder(totalAmount, podderCount);
    
    setCalculatedValues({
      totalAmount,
      amountPerPodder,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!user) return;
    
    setLoading(true);
    
    try {
      const newPodCode = generatePodCode();
      setPodCode(newPodCode);
      
      const podData = {
        businessName: formData.businessName,
        itemName: formData.itemName,
        itemPrice: parseFloat(formData.itemPrice),
        quantity: parseInt(formData.quantity),
        totalAmount: calculatedValues.totalAmount,
        podderCount: parseInt(formData.podderCount),
        amountPerPodder: calculatedValues.amountPerPodder,
        podCode: newPodCode,
        createdAt: serverTimestamp(),
        createdBy: user.uid,
        active: true,
      };
      
      await addDoc(collection(db, 'pods'), podData);
      setSuccess(true);
      
    } catch (error) {
      console.error('Error creating pod:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setFormData({
      businessName: '',
      itemName: '',
      itemPrice: '',
      quantity: '1',
      podderCount: '1',
    });
    setCalculatedValues({
      totalAmount: 0,
      amountPerPodder: 0,
    });
    setSuccess(false);
    setPodCode('');
  };

  return (
    <div className="px-4 py-6">
      <h1 className="text-2xl font-semibold text-purple-deep mb-6">Create Payment Pod</h1>
      
      {success ? (
        <div className="bg-white rounded-lg shadow-md p-6">
          <div className="text-center mb-6">
            <div className="inline-flex items-center justify-center w-12 h-12 rounded-full bg-green-100 text-green-500 mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h2 className="text-xl font-medium text-purple-deep">Payment Pod Created!</h2>
            <p className="text-gray mt-2">Share this code with your customers to join the pod.</p>
          </div>
          
          <div className="flex flex-col md:flex-row items-center justify-center gap-8 mb-8">
            <div className="text-center">
              <p className="text-sm text-gray mb-2">Pod Code</p>
              <div className="text-3xl font-bold text-purple-deep tracking-wider">{podCode}</div>
            </div>
            
            <div className="bg-white p-4 rounded-lg shadow">
              <QRCode 
                value={podCode} 
                size={150}
                style={{ height: "auto", maxWidth: "100%", width: "100%" }}
              />
            </div>
          </div>
          
          <div className="border-t border-gray-200 pt-6 mt-6">
            <h3 className="text-lg font-medium text-purple-deep mb-4">Pod Details</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-gray">Business Name</p>
                <p className="font-medium">{formData.businessName}</p>
              </div>
              <div>
                <p className="text-sm text-gray">Item</p>
                <p className="font-medium">{formData.itemName}</p>
              </div>
              <div>
                <p className="text-sm text-gray">Quantity</p>
                <p className="font-medium">{formData.quantity}</p>
              </div>
              <div>
                <p className="text-sm text-gray">Price per Item</p>
                <p className="font-medium">{formatCurrency(parseFloat(formData.itemPrice))}</p>
              </div>
              <div>
                <p className="text-sm text-gray">Total Amount</p>
                <p className="font-medium">{formatCurrency(calculatedValues.totalAmount)}</p>
              </div>
              <div>
                <p className="text-sm text-gray">Number of Podders</p>
                <p className="font-medium">{formData.podderCount}</p>
              </div>
              <div>
                <p className="text-sm text-gray">Amount per Podder</p>
                <p className="font-medium">{formatCurrency(calculatedValues.amountPerPodder)}</p>
              </div>
            </div>
          </div>
          
          <div className="flex justify-center mt-8 space-x-4">
            <button
              onClick={handleReset}
              className="px-4 py-2 border border-purple-deep text-purple-deep rounded-md hover:bg-purple-0"
            >
              Create Another Pod
            </button>
            <button
              onClick={() => router.push('/dashboard/pods')}
              className="px-4 py-2 bg-purple-deep text-white rounded-md hover:bg-purple-royal"
            >
              View All Pods
            </button>
          </div>
        </div>
      ) : (
        <div className="bg-white rounded-lg shadow-md p-6">
          <form onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="col-span-1 md:col-span-2">
                <label htmlFor="businessName" className="block text-sm font-medium text-gray-700">
                  Business Name
                </label>
                <input
                  type="text"
                  id="businessName"
                  name="businessName"
                  value={formData.businessName}
                  onChange={handleChange}
                  required
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-purple-deep focus:border-purple-deep"
                />
              </div>
              
              <div>
                <label htmlFor="itemName" className="block text-sm font-medium text-gray-700">
                  Item Name
                </label>
                <input
                  type="text"
                  id="itemName"
                  name="itemName"
                  value={formData.itemName}
                  onChange={handleChange}
                  required
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-purple-deep focus:border-purple-deep"
                  placeholder="e.g. English Breakfast"
                />
              </div>
              
              <div>
                <label htmlFor="itemPrice" className="block text-sm font-medium text-gray-700">
                  Price per Item (£)
                </label>
                <input
                  type="number"
                  id="itemPrice"
                  name="itemPrice"
                  value={formData.itemPrice}
                  onChange={handleChange}
                  required
                  min="0.01"
                  step="0.01"
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-purple-deep focus:border-purple-deep"
                  placeholder="10.00"
                />
              </div>
              
              <div>
                <label htmlFor="quantity" className="block text-sm font-medium text-gray-700">
                  Quantity
                </label>
                <input
                  type="number"
                  id="quantity"
                  name="quantity"
                  value={formData.quantity}
                  onChange={handleChange}
                  required
                  min="1"
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-purple-deep focus:border-purple-deep"
                />
              </div>
              
              <div>
                <label htmlFor="podderCount" className="block text-sm font-medium text-gray-700">
                  Number of Podders
                </label>
                <input
                  type="number"
                  id="podderCount"
                  name="podderCount"
                  value={formData.podderCount}
                  onChange={handleChange}
                  required
                  min="1"
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-purple-deep focus:border-purple-deep"
                />
              </div>
            </div>
            
            {/* Calculated Values */}
            <div className="mt-8 p-4 bg-purple-0 rounded-md">
              <h3 className="text-sm font-medium text-gray-700 mb-4">Summary</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                  <p className="text-sm text-gray-500">Total Amount:</p>
                  <p className="text-lg font-semibold text-purple-deep">
                    {formatCurrency(calculatedValues.totalAmount)}
                  </p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Amount per Podder:</p>
                  <p className="text-lg font-semibold text-purple-deep">
                    {formatCurrency(calculatedValues.amountPerPodder)}
                  </p>
                </div>
              </div>
            </div>
            
            <div className="mt-8 flex justify-end">
              <button
                type="button"
                onClick={() => router.push('/dashboard')}
                className="mr-4 px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={loading}
                className="px-4 py-2 bg-purple-deep text-white rounded-md hover:bg-purple-royal disabled:opacity-50"
              >
                {loading ? 'Creating...' : 'Create Payment Pod'}
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}
