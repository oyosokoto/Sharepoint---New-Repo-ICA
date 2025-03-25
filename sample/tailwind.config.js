/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./pages/**/*.{js,ts,jsx,tsx}",
    "./components/**/*.{js,ts,jsx,tsx}",
    "./app/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        purple: {
          0: '#F0F0F8',
          deep: '#6B21A8',
          royal: '#8B5CF6',
        },
        gray: {
          DEFAULT: '#6B7280',
        }
      },
      backgroundColor: {
        purple: {
          0: '#F0F0F8',
          deep: '#6B21A8',
          royal: '#8B5CF6',
        },
        gray: {
          DEFAULT: '#6B7280',
        }
      }
    },
  },
  plugins: [],
 };