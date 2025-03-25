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
 * Calculates the amount per podder based on total amount and number of podders
 */
export const calculateAmountPerPodder = (totalAmount: number, podderCount: number): number => {
  if (podderCount <= 0) return 0;
  return parseFloat((totalAmount / podderCount).toFixed(2));
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
