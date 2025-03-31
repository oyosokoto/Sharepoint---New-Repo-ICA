'use client';

import React, { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { collection, addDoc, serverTimestamp } from 'firebase/firestore';
import { db } from '@/lib/firebase';
import { useAuth } from '@/contexts/AuthContext';
import {
  generatePodCode,
  calculateAmountPerPodder,
  formatCurrency,
  SplitType,
  calculateRandomSplit,
  initializeCustomSplit
} from '@/utils/podUtils';
import QRCode from 'react-qr-code';
import { PodItem } from '@/types';

export default function CreatePod() {
  const router = useRouter();
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const [podCode, setPodCode] = useState('');
  const [businessName, setBusinessName] = useState('');
  const [podderCount, setPodderCount] = useState('1');

  // State for managing multiple items
  const [items, setItems] = useState<{
    id: string;
    name: string;
    price: string;
    quantity: string;
  }[]>([
    {
      id: Date.now().toString(),
      name: '',
      price: '',
      quantity: '1'
    }
  ]);

  // State for split type and amounts
  const [splitType, setSplitType] = useState<SplitType>(SplitType.EQUAL);
  const [splitAmounts, setSplitAmounts] = useState<number[]>([]);

  const [calculatedValues, setCalculatedValues] = useState({
    totalAmount: 0,
    amountPerPodder: 0,
  });

  // Function to update split amounts based on split type
  const updateSplitAmounts = React.useCallback(() => {
    const podderCountNum = parseInt(podderCount) || 1;
    const { totalAmount } = calculatedValues;

    if (totalAmount <= 0) {
      setSplitAmounts([]);
      return;
    }

    switch (splitType) {
      case SplitType.EQUAL:
        // Equal split - everyone pays the same
        const equalAmount = calculateAmountPerPodder(totalAmount, podderCountNum);
        setSplitAmounts(Array(podderCountNum).fill(equalAmount));
        break;

      case SplitType.RANDOM:
        // Random split - random distribution that sums to total
        setSplitAmounts(calculateRandomSplit(totalAmount, podderCountNum));
        break;

      case SplitType.CUSTOM:
        // Custom split - initialize with equal values, will be editable by users later
        setSplitAmounts(initializeCustomSplit(totalAmount, podderCountNum));
        break;
    }
  }, [calculatedValues, podderCount, splitType]);

  // Update split amounts when total amount or podder count changes
  useEffect(() => {
    updateSplitAmounts();
  }, [updateSplitAmounts]);

  // Handle split type change
  const handleSplitTypeChange = (newSplitType: SplitType) => {
    setSplitType(newSplitType);
  };

  const handleBusinessNameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setBusinessName(e.target.value);
  };

  const handlePodderCountChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPodderCount(e.target.value);
    calculateTotals(items, e.target.value);
  };

  const handleItemChange = (id: string, field: 'name' | 'price' | 'quantity', value: string) => {
    const updatedItems = items.map(item =>
      item.id === id ? { ...item, [field]: value } : item
    );
    setItems(updatedItems);
    calculateTotals(updatedItems, podderCount);
  };

  const addItem = () => {
    setItems([
      ...items,
      {
        id: Date.now().toString(),
        name: '',
        price: '',
        quantity: '1'
      }
    ]);
  };

  const removeItem = (id: string) => {
    if (items.length > 1) {
      const updatedItems = items.filter(item => item.id !== id);
      setItems(updatedItems);
      calculateTotals(updatedItems, podderCount);
    }
  };

  const calculateTotals = (itemsList: typeof items, podderCountStr: string) => {
    const totalAmount = itemsList.reduce((sum, item) => {
      const price = parseFloat(item.price) || 0;
      const quantity = parseInt(item.quantity) || 0;
      return sum + (price * quantity);
    }, 0);

    const podderCountNum = parseInt(podderCountStr) || 1;
    const amountPerPodder = calculateAmountPerPodder(totalAmount, podderCountNum);

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

      // Transform items for storage
      const podItems: PodItem[] = items.map(item => ({
        id: item.id,
        name: item.name,
        price: parseFloat(parseInt(item.price).toFixed(2)) || 0,
        quantity: parseInt(item.quantity) || 0,
        subtotal: (parseFloat(parseInt(item.price).toFixed(2)) || 0) * (parseInt(item.quantity) || 0)
      }));

      const podData = {
        businessName,
        items: podItems,
        totalAmount: calculatedValues.totalAmount,
        podderCount: parseInt(podderCount) || 1,
        amountPerPodder: calculatedValues.amountPerPodder,
        splitType: splitType,
        splitAmounts: splitAmounts,
        podCode: newPodCode,
        createdAt: serverTimestamp(),
        createdBy: user.uid,
        active: true,
      };

      await addDoc(collection(db!, 'pods'), podData);
      setSuccess(true);

    } catch (error) {
      console.error('Error creating pod:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleReset = () => {
    setBusinessName('');
    setPodderCount('1');
    setItems([
      {
        id: Date.now().toString(),
        name: '',
        price: '',
        quantity: '1'
      }
    ]);
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
                <p className="font-medium">{businessName}</p>
              </div>
              <div className="col-span-1 md:col-span-2">
                <p className="text-sm text-gray">Items</p>
                <div className="mt-2 space-y-2">
                  {items.map(item => (
                    <div key={item.id} className="flex items-center space-x-2 text-sm">
                      <span className="font-medium">{item.name}</span>
                      <span>x{item.quantity}</span>
                      <span>@ {formatCurrency(parseFloat(item.price) || 0)}</span>
                      <span className="text-gray-500">= {formatCurrency((parseFloat(item.price) || 0) * (parseInt(item.quantity) || 0))}</span>
                    </div>
                  ))}
                </div>
              </div>
              <div>
                <p className="text-sm text-gray">Total Amount</p>
                <p className="font-medium">{formatCurrency(calculatedValues.totalAmount)}</p>
              </div>
              <div>
                <p className="text-sm text-gray">Number of Podders</p>
                <p className="font-medium">{podderCount}</p>
              </div>
              <div>
                <p className="text-sm text-gray">Split Method</p>
                <p className="font-medium">
                  {splitType === SplitType.EQUAL ? 'Equal Split' :
                    splitType === SplitType.RANDOM ? 'Random Split' : 'Custom Split'}
                </p>
              </div>

              {splitAmounts.length > 0 && (
                <div className="col-span-1 md:col-span-2 mt-2">
                  <p className="text-sm text-gray mb-2">Payment Distribution</p>
                  <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-2">
                    {splitAmounts.map((amount, index) => (
                      <div key={index} className="bg-purple-0 p-2 rounded">
                        <p className="text-xs text-gray-500">Podder {index + 1}</p>
                        <p className="font-medium">{formatCurrency(amount)}</p>
                      </div>
                    ))}
                  </div>
                </div>
              )}
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
            <div className="space-y-6">
              <div>
                <label htmlFor="businessName" className="block text-sm font-medium text-gray-700">
                  Business Name
                </label>
                <input
                  type="text"
                  id="businessName"
                  name="businessName"
                  value={businessName}
                  onChange={handleBusinessNameChange}
                  required
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-purple-deep focus:border-purple-deep"
                />
              </div>

              <div>
                <div className="flex justify-between items-center mb-2">
                  <h3 className="text-md font-medium text-gray-700">Items</h3>
                  <button
                    type="button"
                    onClick={addItem}
                    className="inline-flex items-center px-3 py-1 border border-transparent text-sm leading-4 font-medium rounded-md text-white bg-purple-deep hover:bg-purple-royal focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-purple-deep"
                  >
                    <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4 mr-1" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                    </svg>
                    Add Item
                  </button>
                </div>

                {items.map((item, index) => (
                  <div key={item.id} className="bg-purple-0 p-4 rounded-md mb-4">
                    <div className="flex justify-between items-center mb-3">
                      <h4 className="text-sm font-medium text-gray-700">Item {index + 1}</h4>
                      {items.length > 1 && (
                        <button
                          type="button"
                          onClick={() => removeItem(item.id)}
                          className="text-red-500 hover:text-red-700"
                        >
                          <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                          </svg>
                        </button>
                      )}
                    </div>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                      <div>
                        <label htmlFor={`item-name-${item.id}`} className="block text-sm font-medium text-gray-700">
                          Item Name
                        </label>
                        <input
                          type="text"
                          id={`item-name-${item.id}`}
                          value={item.name}
                          onChange={(e) => handleItemChange(item.id, 'name', e.target.value)}
                          required
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-purple-deep focus:border-purple-deep"
                          placeholder="e.g. Coca Cola"
                        />
                      </div>
                      <div>
                        <label htmlFor={`item-price-${item.id}`} className="block text-sm font-medium text-gray-700">
                          Price (£)
                        </label>
                        <input
                          type="number"
                          id={`item-price-${item.id}`}
                          value={item.price}
                          onChange={(e) => handleItemChange(item.id, 'price', e.target.value)}
                          required
                          min="0.01"
                          step="0.01"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-purple-deep focus:border-purple-deep"
                          placeholder="2.50"
                        />
                      </div>
                      <div>
                        <label htmlFor={`item-quantity-${item.id}`} className="block text-sm font-medium text-gray-700">
                          Quantity
                        </label>
                        <input
                          type="number"
                          id={`item-quantity-${item.id}`}
                          value={item.quantity}
                          onChange={(e) => handleItemChange(item.id, 'quantity', e.target.value)}
                          required
                          min="1"
                          className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-purple-deep focus:border-purple-deep"
                        />
                      </div>
                    </div>
                    {parseFloat(item.price) > 0 && parseInt(item.quantity) > 0 && (
                      <div className="mt-2 text-right text-sm text-gray-500">
                        Subtotal: {formatCurrency((parseFloat(item.price) || 0) * (parseInt(item.quantity) || 0))}
                      </div>
                    )}
                  </div>
                ))}
              </div>

              <div>
                <label htmlFor="podderCount" className="block text-sm font-medium text-gray-700">
                  Number of Podders
                </label>
                <input
                  type="number"
                  id="podderCount"
                  name="podderCount"
                  value={podderCount}
                  onChange={handlePodderCountChange}
                  required
                  min="1"
                  className="mt-1 block w-full border border-gray-300 rounded-md shadow-sm py-2 px-3 focus:outline-none focus:ring-purple-deep focus:border-purple-deep"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-2">
                  Payment Split Method
                </label>
                <div className="space-y-2">
                  <div className="flex items-center">
                    <input
                      type="radio"
                      id="split-equal"
                      name="splitType"
                      checked={splitType === SplitType.EQUAL}
                      onChange={() => handleSplitTypeChange(SplitType.EQUAL)}
                      className="h-4 w-4 text-purple-deep focus:ring-purple-deep border-gray-300"
                    />
                    <label htmlFor="split-equal" className="ml-2 block text-sm text-gray-700">
                      Equal Split (everyone pays the same)
                    </label>
                  </div>

                  <div className="flex items-center">
                    <input
                      type="radio"
                      id="split-random"
                      name="splitType"
                      checked={splitType === SplitType.RANDOM}
                      onChange={() => handleSplitTypeChange(SplitType.RANDOM)}
                      className="h-4 w-4 text-purple-deep focus:ring-purple-deep border-gray-300"
                    />
                    <label htmlFor="split-random" className="ml-2 block text-sm text-gray-700">
                      Random Split (random distribution)
                    </label>
                  </div>

                  <div className="flex items-center">
                    <input
                      type="radio"
                      id="split-custom"
                      name="splitType"
                      checked={splitType === SplitType.CUSTOM}
                      onChange={() => handleSplitTypeChange(SplitType.CUSTOM)}
                      className="h-4 w-4 text-purple-deep focus:ring-purple-deep border-gray-300"
                    />
                    <label htmlFor="split-custom" className="ml-2 block text-sm text-gray-700">
                      Custom Split (customers choose amount)
                    </label>
                  </div>
                </div>
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
                  <p className="text-sm text-gray-500">Split Method:</p>
                  <p className="text-lg font-semibold text-purple-deep">
                    {splitType === SplitType.EQUAL ? 'Equal Split' :
                      splitType === SplitType.RANDOM ? 'Random Split' : 'Custom Split'}
                  </p>
                </div>
              </div>

              {/* Show split amounts if we have them and total amount > 0 */}
              {splitAmounts.length > 0 && calculatedValues.totalAmount > 0 && (
                <div className="mt-4 border-t border-gray-200 pt-4">
                  <p className="text-sm text-gray-500 mb-2">Payment Distribution:</p>
                  <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-2">
                    {splitAmounts.map((amount, index) => (
                      <div key={index} className="bg-white p-2 rounded border border-gray-200">
                        <p className="text-xs text-gray-500">Podder {index + 1}</p>
                        <p className="font-medium">{formatCurrency(amount)}</p>
                      </div>
                    ))}
                  </div>
                </div>
              )}
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
