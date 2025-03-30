import { initializeApp, getApps, cert } from "firebase-admin/app";
import { getFirestore } from "firebase-admin/firestore";
import { getAuth } from "firebase-admin/auth";

// Initialize Firebase Admin SDK for server-side operations
const initializeFirebaseAdmin = () => {
  // Check if Firebase Admin is already initialized
  if (getApps().length === 0) {
    // Initialize with service account if provided
    if (process.env.FIREBASE_SERVICE_ACCOUNT_KEY) {
      try {
        const serviceAccount = JSON.parse(
          process.env.FIREBASE_SERVICE_ACCOUNT_KEY
        );

        // Fix the private key format by replacing literal \n with actual newlines
        if (serviceAccount.private_key) {
          serviceAccount.private_key = serviceAccount.private_key.replace(
            /\\n/g,
            "\n"
          );
        }

        initializeApp({
          credential: cert(serviceAccount),
          projectId: process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID,
        });
      } catch (error) {
        console.error(
          "Error initializing Firebase Admin with service account:",
          error
        );

        // Fallback to application default credentials
        initializeApp({
          projectId: process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID,
        });
      }
    } else {
      // Initialize with application default credentials
      initializeApp({
        projectId: process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID,
      });
    }
  }

  return {
    db: getFirestore(),
    auth: getAuth(),
  };
};

// Export the initialized Firebase Admin SDK
export const { db, auth } = initializeFirebaseAdmin();
