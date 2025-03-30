#!/bin/bash

# Exit on error
set -e

echo "🚀 Starting deployment process..."

# Build the Next.js app with static export
echo "📦 Building Next.js static export..."
npm run build

# Check if Firebase CLI is installed
if ! command -v firebase &> /dev/null; then
    echo "❌ Firebase CLI is not installed. Installing..."
    npm install -g firebase-tools
fi

# Check if user is logged in to Firebase
echo "🔑 Checking Firebase login status..."
firebase_login_status=$(firebase login:list)
if [[ $firebase_login_status == *"No authorized accounts"* ]]; then
    echo "🔒 Please login to Firebase:"
    firebase login
fi

# Deploy to Firebase Hosting
echo "🚀 Deploying to Firebase Hosting..."
firebase deploy --only hosting

echo "✅ Deployment complete!"
echo "Visit your deployed app at the URL shown above."
