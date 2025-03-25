/**
 * Generates a random alphanumeric code of specified length
 */
export const generatePodCode = (length: number = 6): string => {
  const characters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
  let result = '';
  
  for (let i = 0; i < length; i++) {
    const randomIndex = Math.floor(Math.random() * characters.length);
    result += characters.charAt(randomIndex);
  }
  
  return result;
};

/**
 * Split types for payment pods
 */
export enum SplitType {
  EQUAL = 'equal',
  RANDOM = 'random',
  CUSTOM = 'custom'
}

/**
 * Calculates the amount per podder based on total amount and number of podders
 * for equal split
 */
export const calculateAmountPerPodder = (totalAmount: number, podderCount: number): number => {
  if (podderCount <= 0) return 0;
  return parseFloat((totalAmount / podderCount).toFixed(2));
};

/**
 * Generates random amounts for each podder that sum to the total amount
 */
export const calculateRandomSplit = (totalAmount: number, podderCount: number): number[] => {
  if (podderCount <= 0) return [];
  if (podderCount === 1) return [totalAmount];
  
  // Generate random values
  const randomValues: number[] = [];
  let sum = 0;
  
  for (let i = 0; i < podderCount; i++) {
    // Generate a random value between 1 and 100
    const randomValue = Math.random() * 99 + 1;
    randomValues.push(randomValue);
    sum += randomValue;
  }
  
  // Normalize the values to sum to totalAmount
  const normalizedValues = randomValues.map(value => {
    // Calculate the proportion and convert to 2 decimal places
    return parseFloat(((value / sum) * totalAmount).toFixed(2));
  });
  
  // Due to rounding, the sum might be slightly off, adjust the last value
  const currentSum = normalizedValues.reduce((acc, val) => acc + val, 0);
  const difference = parseFloat((totalAmount - currentSum).toFixed(2));
  normalizedValues[normalizedValues.length - 1] += difference;
  
  return normalizedValues;
};

/**
 * Placeholder for custom split - will be determined by user input
 * Returns an array of equal values as default
 */
export const initializeCustomSplit = (totalAmount: number, podderCount: number): number[] => {
  const equalAmount = calculateAmountPerPodder(totalAmount, podderCount);
  return Array(podderCount).fill(equalAmount);
};

/**
 * Formats currency for display
 */
export const formatCurrency = (amount: number): string => {
  return new Intl.NumberFormat('en-GB', {
    style: 'currency',
    currency: 'GBP',
  }).format(amount);
};
