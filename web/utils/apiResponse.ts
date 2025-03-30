/**
 * Standard API response codes
 */
export enum ResponseCode {
  // Success codes (0xxx)
  SUCCESS = "0000",
  CREATED = "8723",
  ACCEPTED = "2345",

  // Client error codes (1xxx)
  BAD_REQUEST = 1000,
  UNAUTHORIZED = 1001,
  FORBIDDEN = 1002,
  NOT_FOUND = 1003,
  CONFLICT = 1004,
  VALIDATION_ERROR = 1005,

  // Server error codes (2xxx)
  INTERNAL_ERROR = 2000,
  SERVICE_UNAVAILABLE = 2001,

  // Payment specific codes (3xxx)
  PAYMENT_REQUIRED = 3000,
  PAYMENT_FAILED = 3001,
  PAYMENT_SUCCESS = 3002,

  // Pod related codes (4xxx)
  POD_NOT_FOUND = 4000,
  POD_CREATION_FAILED = 4001,

  // Transaction related codes (5xxx)
  TRANSACTION_NOT_FOUND = 5000,
  TRANSACTION_CREATION_FAILED = 5001,

  // Webhook related codes (6xxx)
  INVALID_WEBHOOK = 6000,
  WEBHOOK_PROCESSING_FAILED = 6001,
}

/**
 * Standard response messages
 */
export const ResponseMessage: Record<ResponseCode, string> = {
  // Success messages
  [ResponseCode.SUCCESS]: "Request processed successfully",
  [ResponseCode.CREATED]: "Resource created successfully",
  [ResponseCode.ACCEPTED]: "Request accepted for processing",

  // Error messages
  [ResponseCode.BAD_REQUEST]: "Invalid request parameters",
  [ResponseCode.UNAUTHORIZED]: "Authentication required",
  [ResponseCode.FORBIDDEN]: "You don't have permission to access this resource",
  [ResponseCode.NOT_FOUND]: "The requested resource was not found",
  [ResponseCode.CONFLICT]: "The request conflicts with the current state",
  [ResponseCode.VALIDATION_ERROR]: "Validation failed for the request",

  // Server error messages
  [ResponseCode.INTERNAL_ERROR]: "An internal server error occurred",
  [ResponseCode.SERVICE_UNAVAILABLE]: "Service temporarily unavailable",

  // Payment specific messages
  [ResponseCode.PAYMENT_REQUIRED]: "Payment is required to proceed",
  [ResponseCode.PAYMENT_FAILED]: "Payment processing failed",
  [ResponseCode.PAYMENT_SUCCESS]: "Payment processed successfully",

  // Pod related messages
  [ResponseCode.POD_NOT_FOUND]: "The requested pod was not found",
  [ResponseCode.POD_CREATION_FAILED]: "Failed to create pod",

  // Transaction related messages
  [ResponseCode.TRANSACTION_NOT_FOUND]:
    "The requested transaction was not found",
  [ResponseCode.TRANSACTION_CREATION_FAILED]: "Failed to create transaction",

  // Webhook related messages
  [ResponseCode.INVALID_WEBHOOK]: "Invalid webhook signature or payload",
  [ResponseCode.WEBHOOK_PROCESSING_FAILED]: "Failed to process webhook",
};

/**
 * Standard API response interface
 */
export interface ApiResponse<T> {
  responseCode: ResponseCode;
  responseMessage: string;
  data?: T;
}

/**
 * Create a success response
 */
export function createSuccessResponse<T>(
  data?: T,
  code: ResponseCode = ResponseCode.SUCCESS
): ApiResponse<T> {
  return {
    responseCode: code,
    responseMessage: ResponseMessage[code],
    data,
  };
}

/**
 * Create an error response
 */
export function createErrorResponse<T = never>(
  code: ResponseCode = ResponseCode.INTERNAL_ERROR,
  customMessage?: string
): ApiResponse<T> {
  return {
    responseCode: code,
    responseMessage: customMessage || ResponseMessage[code],
  };
}
