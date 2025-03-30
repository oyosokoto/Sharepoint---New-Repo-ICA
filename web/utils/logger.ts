/**
 * Logger utility for consistent logging with icons
 */

// Log levels
export enum LogLevel {
  INFO = "INFO",
  SUCCESS = "SUCCESS",
  WARNING = "WARNING",
  ERROR = "ERROR",
  DEBUG = "DEBUG",
}

// Icons for different log levels
const LogIcons = {
  [LogLevel.INFO]: "ℹ️",
  [LogLevel.SUCCESS]: "✅",
  [LogLevel.WARNING]: "⚠️",
  [LogLevel.ERROR]: "❌",
  [LogLevel.DEBUG]: "🔍",
};

// Colors for different log levels (for console output)
const LogColors = {
  [LogLevel.INFO]: "\x1b[36m", // Cyan
  [LogLevel.SUCCESS]: "\x1b[32m", // Green
  [LogLevel.WARNING]: "\x1b[33m", // Yellow
  [LogLevel.ERROR]: "\x1b[31m", // Red
  [LogLevel.DEBUG]: "\x1b[35m", // Magenta
  RESET: "\x1b[0m", // Reset
};

/**
 * Format a log message with timestamp, level, and icon
 */
type LogData = unknown;

const formatLogMessage = (
  level: LogLevel,
  message: string,
  data?: LogData
): string => {
  const timestamp = new Date().toISOString();
  const icon = LogIcons[level];
  const color = LogColors[level];
  const reset = LogColors.RESET;

  let formattedMessage = `${timestamp} ${color}${icon} [${level}]${reset} ${message}`;

  if (data) {
    try {
      const dataString =
        typeof data === "object" ? JSON.stringify(data, null, 2) : String(data);
      formattedMessage += `\n${dataString}`;
    } catch (error) {
      formattedMessage += `\n[Unable to stringify data: ${error}]`;
    }
  }

  return formattedMessage;
};

/**
 * Log an info message
 */
export const logInfo = (message: string, data?: LogData): void => {
  console.log(formatLogMessage(LogLevel.INFO, message, data));
};

/**
 * Log a success message
 */
export const logSuccess = (message: string, data?: LogData): void => {
  console.log(formatLogMessage(LogLevel.SUCCESS, message, data));
};

/**
 * Log a warning message
 */
export const logWarning = (message: string, data?: LogData): void => {
  console.warn(formatLogMessage(LogLevel.WARNING, message, data));
};

/**
 * Log an error message
 */
export const logError = (message: string, error?: LogData): void => {
  console.error(formatLogMessage(LogLevel.ERROR, message, error));
};

/**
 * Log a debug message (only in development)
 */
export const logDebug = (message: string, data?: LogData): void => {
  if (process.env.NODE_ENV === "development") {
    console.debug(formatLogMessage(LogLevel.DEBUG, message, data));
  }
};

/**
 * Default logger object with all methods
 */
export const logger = {
  info: logInfo,
  success: logSuccess,
  warning: logWarning,
  error: logError,
  debug: logDebug,
};

export default logger;
