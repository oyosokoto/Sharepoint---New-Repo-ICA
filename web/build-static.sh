#!/bin/bash

# Build the Next.js app with static export
echo "Building static export..."
npm run build

# Check if the build was successful
if [ $? -eq 0 ]; then
  echo "Build successful! Static files are in the 'out' directory."
  echo "You can serve these files with any static file server."
  echo "For example, with 'npx serve out' or by uploading to a static hosting service."
else
  echo "Build failed. Please check the error messages above."
fi
