// Define a function to safely access environment variables in both server and client contexts
const getEnvVar = (key: string, defaultValue: string): string => {
  // For static builds, ensure we're only using NEXT_PUBLIC_ prefixed variables
  // which are embedded at build time
  return (
    (typeof process !== "undefined" && process.env[`NEXT_PUBLIC_${key}`]) ||
    defaultValue
  );
};

// App name with fallback
const APP_NAME = getEnvVar("APP_NAME", "SplitIOU");

export const APP_CONFIG = {
  name: APP_NAME,
  title: `${APP_NAME} - Split Payments Made Easy`,
  description: "Split and manage group payments effortlessly with SplitIOU",
  url: getEnvVar("SITE_URL", ""),
  logo: "/logo.png",
  ogImage: "/og-image.png",
  twitterImage: "/twitter-image.png",
  themeColor: "#7C3AED",
  keywords: [
    "payment splitting",
    "group payments",
    "split bills",
    "payment pods",
    APP_NAME,
  ],
  contact: {
    email: "hello@SplitIOU.com",
    twitter: "@SplitIOU",
  },
  verification: {
    google: getEnvVar("GOOGLE_SITE_VERIFICATION", ""),
  },
} as const;

export const ROUTES = {
  home: "/",
  dashboard: "/dashboard",
  pods: "/dashboard/pods",
  createPod: "/dashboard/create-pod",
} as const;
