import Link from 'next/link';
import Image from 'next/image';

export default function Home() {
  const currentYear = new Date().getFullYear();

  return (
    <div className="min-h-screen bg-gradient-to-b from-purple-0 to-white text-black">
      {/* Modern Header with Gradient */}
      <header className="bg-white shadow-lg sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex justify-between items-center">
          <div className="flex items-center">
            <h1 className="text-2xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-purple-deep to-purple-royal">
              SharePoint
            </h1>
          </div>
          <div className="flex items-center space-x-4">
            <a
              href="#problem"
              className="text-gray-600 hover:text-purple-deep transition-colors"
            >
              Why SharePoint?
            </a>
            <a href="#features" className="text-gray-600 hover:text-purple-deep transition-colors">Features</a>
            <Link
              href="/login"
              className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-full text-white bg-gradient-to-r from-purple-deep to-purple-royal hover:from-purple-royal hover:to-purple-deep transition-all shadow-md hover:shadow-lg"
            >
              <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5 mr-2" viewBox="0 0 20 20" fill="currentColor">
                <path fillRule="evenodd" d="M3 3a1 1 0 011-1h12a1 1 0 011 1v12a1 1 0 01-1 1H4a1 1 0 01-1-1V3zm1 0v12h12V3H4z" clipRule="evenodd" />
                <path d="M7 7a1 1 0 011-1h4a1 1 0 110 2H8a1 1 0 01-1-1z" />
                <path d="M7 11a1 1 0 011-1h4a1 1 0 110 2H8a1 1 0 01-1-1z" />
              </svg>
              Dashboard
            </Link>
          </div>
        </div>
      </header>

      {/* Hero Section with Animation */}
      <section className="py-20 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          <div className="order-2 lg:order-1">
            <h1 className="text-4xl sm:text-5xl md:text-6xl font-extrabold text-purple-deep mb-6 leading-tight">
              Split Bills <span className="bg-clip-text text-transparent bg-gradient-to-r from-purple-deep to-purple-royal">Effortlessly</span>
            </h1>
            <p className="text-xl text-gray-600 mb-10">
              SharePoint makes group payments simple, transparent, and hassle-free. Whether you're dining out, traveling, or sharing household expenses.
            </p>
            <div className="flex flex-col sm:flex-row gap-4">
              <Link
                href="/login"
                className="inline-flex items-center justify-center px-6 py-3 border border-transparent text-base font-medium rounded-full text-white bg-gradient-to-r from-purple-deep to-purple-royal hover:from-purple-royal hover:to-purple-deep transition-all shadow-md hover:shadow-lg"
              >
                Business Login
              </Link>
              <a
                href="#problem"
                className="inline-flex items-center justify-center px-6 py-3 border border-purple-deep text-base font-medium rounded-full text-purple-deep bg-white hover:bg-purple-0 transition-all shadow-sm hover:shadow-md"
              >
                Learn More
              </a>
            </div>
          </div>
          <div className="order-1 lg:order-2 flex justify-center">
            <div className="relative w-full max-w-md h-80 lg:h-96">
              <div className="absolute top-0 right-0 w-64 h-64 bg-purple-0 rounded-full filter blur-3xl opacity-50 animate-pulse"></div>
              <div className="absolute bottom-0 left-0 w-64 h-64 bg-purple-royal rounded-full filter blur-3xl opacity-30 animate-pulse" style={{ animationDelay: '1s' }}></div>
              <div className="relative z-10 w-full h-full flex items-center justify-center">
                <div className="bg-white p-6 rounded-xl shadow-xl transform rotate-3 transition-transform hover:rotate-0">
                  <div className="text-center mb-4">
                    <h3 className="text-lg font-medium text-purple-deep">Payment Pod</h3>
                    <p className="text-sm text-gray-500">Share with your friends</p>
                  </div>
                  <div className="space-y-3 text-black">
                    <div className="flex justify-between text-sm">
                      <span>Coca Cola</span>
                      <span>x2 @ £2.50</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span>Sprite</span>
                      <span>x3 @ £2.50</span>
                    </div>
                    <div className="flex justify-between text-sm">
                      <span>Chilli Cheese Burger</span>
                      <span>x1 @ £8.99</span>
                    </div>
                    <div className="border-t pt-2 flex justify-between font-medium">
                      <span>Total:</span>
                      <span>£21.49</span>
                    </div>
                    <div className="flex justify-between text-sm text-purple-deep">
                      <span>Your share (3 people):</span>
                      <span>£7.16</span>
                    </div>
                    <div className="text-xs text-gray-500 mt-2 pt-2 border-t border-gray-100">
                      <div className="flex justify-between mb-1">
                        <span>Split method:</span>
                        <span className="font-medium">Equal Split</span>
                      </div>
                      <div className="flex space-x-1 text-center">
                        <span className="bg-purple-0 px-2 py-1 rounded-md flex-1">Equal</span>
                        <span className="bg-gray-100 px-2 py-1 rounded-md flex-1">Random</span>
                        <span className="bg-gray-100 px-2 py-1 rounded-md flex-1">Custom</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Problem Statement Section */}
      <section id="problem" className="py-16 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto bg-white rounded-3xl shadow-lg my-12">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center">
          <div className="order-2 lg:order-1">
            <h2 className="text-3xl font-bold text-purple-deep mb-6">The Problem We're Solving</h2>
            <div className="space-y-4">
              <p className="text-gray-600">
                <strong className="text-purple-deep">Splitting bills is complicated.</strong> Whether you're at a restaurant, sharing household expenses, or planning a group trip, calculating who owes what can be a headache.
              </p>
              <p className="text-gray-600">
                <strong className="text-purple-deep">For businesses</strong>, managing group payments often means dealing with multiple transactions, confusion over items ordered, and time wasted on calculations.
              </p>
              <p className="text-gray-600">
                <strong className="text-purple-deep">For customers</strong>, it means awkward conversations about money, uneven splits, and the hassle of figuring out exact amounts.
              </p>
            </div>
          </div>
          <div className="order-1 lg:order-2">
            <div className="bg-purple-0 p-6 rounded-xl">
              <h3 className="text-xl font-semibold text-purple-deep mb-4">Common Challenges:</h3>
              <ul className="space-y-3 text-black">
                <li className="flex items-start">
                  <svg className="h-6 w-6 text-red-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <span>Confusion over who ordered what items</span>
                </li>
                <li className="flex items-start">
                  <svg className="h-6 w-6 text-red-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <span>Time wasted calculating individual shares</span>
                </li>
                <li className="flex items-start">
                  <svg className="h-6 w-6 text-red-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <span>Tracking who has paid and who hasn't</span>
                </li>
                <li className="flex items-start">
                  <svg className="h-6 w-6 text-red-500 mr-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10 14l2-2m0 0l2-2m-2 2l-2-2m2 2l2 2m7-2a9 9 0 11-18 0 9 9 0 0118 0z" />
                  </svg>
                  <span>Awkward money conversations among friends</span>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <div className="mt-12 text-center">
          <h3 className="text-2xl font-bold text-purple-deep mb-4">Our Solution</h3>
          <p className="text-xl text-gray-600 max-w-3xl mx-auto">
            SharePoint transforms this experience with a simple platform that lets businesses create detailed payment pods with multiple items, and allows customers to easily join and settle their fair share.
          </p>
        </div>
      </section>

      {/* Features Section */}
      <section id="features" className="py-16 px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
        <h2 className="text-3xl font-bold text-purple-deep text-center mb-12">Key Features</h2>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
          <div className="bg-white p-6 rounded-lg shadow-md">
            <div className="w-12 h-12 bg-purple-0 rounded-full flex items-center justify-center mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-purple-deep" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2m2 4h10a2 2 0 002-2v-6a2 2 0 00-2-2H9a2 2 0 00-2 2v6a2 2 0 002 2zm7-5a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
            <h3 className="text-xl font-semibold text-purple-deep mb-2">Group Payments</h3>
            <p className="text-gray">Create or join payment pods to split bills among friends, family, or colleagues.</p>
          </div>

          <div className="bg-white p-6 rounded-lg shadow-md">
            <div className="w-12 h-12 bg-purple-0 rounded-full flex items-center justify-center mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-purple-deep" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 7h6m0 10v-3m-3 3h.01M9 17h.01M9 14h.01M12 14h.01M15 11h.01M12 11h.01M9 11h.01M7 21h10a2 2 0 002-2V5a2 2 0 00-2-2H7a2 2 0 00-2 2v14a2 2 0 002 2z" />
              </svg>
            </div>
            <h3 className="text-xl font-semibold text-purple-deep mb-2">Bill Splitting</h3>
            <p className="text-gray">Automatically calculate each person's share of the bill with precision and fairness.</p>
          </div>

          <div className="bg-white p-6 rounded-lg shadow-md">
            <div className="w-12 h-12 bg-purple-0 rounded-full flex items-center justify-center mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-purple-deep" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
            </div>
            <h3 className="text-xl font-semibold text-purple-deep mb-2">Transaction Tracking</h3>
            <p className="text-gray">Keep records of all shared expenses and payments in one convenient place.</p>
          </div>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-8">
          <div className="bg-white p-6 rounded-lg shadow-md">
            <div className="w-12 h-12 bg-purple-0 rounded-full flex items-center justify-center mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-purple-deep" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
              </svg>
            </div>
            <h3 className="text-xl font-semibold text-purple-deep mb-2">Flexible Splitting</h3>
            <p className="text-gray">Choose between equal split, random distribution, or let customers decide their contribution amounts.</p>
          </div>
          <div className="bg-white p-6 rounded-lg shadow-md">
            <div className="w-12 h-12 bg-purple-0 rounded-full flex items-center justify-center mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-purple-deep" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
              </svg>
            </div>
            <h3 className="text-xl font-semibold text-purple-deep mb-2">Payment Reminders</h3>
            <p className="text-gray">Get notifications for pending payments and never forget to settle your share.</p>
          </div>

          <div className="bg-white p-6 rounded-lg shadow-md">
            <div className="w-12 h-12 bg-purple-0 rounded-full flex items-center justify-center mb-4">
              <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6 text-purple-deep" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 11c0 3.517-1.009 6.799-2.753 9.571m-3.44-2.04l.054-.09A13.916 13.916 0 008 11a4 4 0 118 0c0 1.017-.07 2.019-.203 3m-2.118 6.844A21.88 21.88 0 0015.171 17m3.839 1.132c.645-2.266.99-4.659.99-7.132A8 8 0 008 4.07M3 15.364c.64-1.319 1-2.8 1-4.364 0-1.457.39-2.823 1.07-4" />
              </svg>
            </div>
            <h3 className="text-xl font-semibold text-purple-deep mb-2">Secure Payments</h3>
            <p className="text-gray">Process payments securely through integrated payment gateways.</p>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-white py-8 px-4 sm:px-6 lg:px-8">
        <div className="max-w-7xl mx-auto text-center">
          <p className="text-gray mb-2">
            &copy; {currentYear} etherofgodd. All rights reserved.
          </p>
          <p className="text-sm text-gray">
            Developed as an ICA project for Teesside University.
          </p>
        </div>
      </footer>
    </div>
  );
}
