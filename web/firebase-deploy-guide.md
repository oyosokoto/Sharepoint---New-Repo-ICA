# Deploying Your Static SPA to Firebase Hosting

This guide will walk you through the process of deploying your Next.js static export to Firebase Hosting.

## Prerequisites

1. Make sure you have the Firebase CLI installed. If not, install it globally:

```bash
npm install -g firebase-tools
```

2. Make sure you have built your Next.js app with static export enabled (as configured in `next.config.ts`).

## Deployment Steps

### 1. Login to Firebase

```bash
firebase login
```

This will open a browser window to authenticate with your Google account.

### 2. Initialize Firebase in your project

```bash
firebase init
```

During the initialization process:

- Select "Hosting: Configure files for Firebase Hosting"
- Select your Firebase project or create a new one
- When asked about the public directory, enter: `out` (this is where Next.js outputs the static export)
- Configure as a single-page app: `Yes`
- Set up automatic builds and deploys with GitHub: `No` (unless you want to set up CI/CD)
- Overwrite existing files: `No` (to keep your custom configurations)

### 3. Build your Next.js app

```bash
npm run build
```

This will create the static export in the `out` directory.

### 4. Deploy to Firebase Hosting

```bash
firebase deploy --only hosting
```

After deployment completes, you'll receive a URL where your app is hosted.

## Additional Configuration

The `firebase.json` file in your project contains the Firebase Hosting configuration. It includes:

- The public directory (`out`)
- URL rewrite rules for client-side routing
- Cache headers for static assets

You can modify this file to customize your hosting configuration.

## Automating Deployment

You can create a script to automate the build and deploy process:

```bash
#!/bin/bash
# Build the Next.js app with static export
npm run build

# Deploy to Firebase Hosting
firebase deploy --only hosting
```

Save this as `deploy.sh`, make it executable with `chmod +x deploy.sh`, and run it with `./deploy.sh`.

## Troubleshooting

- If you encounter routing issues, make sure the rewrite rule in `firebase.json` is correctly configured to route all requests to `index.html`.
- For 404 errors, ensure your app handles client-side routing correctly.
- If assets are not loading, check the paths in your Next.js configuration and make sure they're compatible with Firebase Hosting.
