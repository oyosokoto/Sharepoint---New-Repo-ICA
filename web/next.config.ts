import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: "export",
  images: {
    unoptimized: false,
  },
  // Disable server-side features since we're building a static SPA
  trailingSlash: true,
};

export default nextConfig;
