# SharePoint - Simplified Group Payment Solution

SharePoint is a modern web application designed to simplify the process of splitting bills and managing shared expenses. Whether you're a restaurant owner, a group of friends dining out, or roommates sharing household expenses, SharePoint makes it easy to create, manage, and settle shared payments.

![SharePoint Logo](public/sharepoint-logo.png)

## 🌟 Features

### For Businesses
- **Create Payment Pods**: Generate payment pods with multiple items and quantities
- **Flexible Payment Splitting**: Choose between equal split, random split, or let customers choose their amounts
- **QR Code Generation**: Easily share payment pods with customers via QR codes
- **Pod Management**: Track active and closed payment pods
- **Multi-Item Support**: Add multiple items with different prices and quantities to a single pod

### For Customers
- **Join Payment Pods**: Easily join a payment pod using a unique code or QR scan
- **View Itemized Bills**: See exactly what items are included in the shared expense
- **Fair Split Calculation**: Automatically calculate each person's share of the bill
- **Payment Tracking**: Keep track of who has paid their share

## 🚀 Technology Stack

- **Frontend**: Next.js, React, TailwindCSS
- **Backend**: Firebase (Authentication, Firestore)
- **Deployment**: Vercel

## 📋 Getting Started

### Prerequisites
- Node.js (v14 or later)
- npm or yarn
- Firebase account

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/sharepoint.git
   cd sharepoint
   ```

2. Install dependencies:
   ```bash
   npm install
   # or
   yarn install
   ```

3. Set up environment variables:
   Create a `.env.local` file in the root directory with the following variables:
   ```
   NEXT_PUBLIC_FIREBASE_API_KEY=your_api_key
   NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=your_auth_domain
   NEXT_PUBLIC_FIREBASE_PROJECT_ID=your_project_id
   NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET=your_storage_bucket
   NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=your_messaging_sender_id
   NEXT_PUBLIC_FIREBASE_APP_ID=your_app_id
   ```

4. Run the development server:
   ```bash
   npm run dev
   # or
   yarn dev
   ```

5. Open [http://localhost:3000](http://localhost:3000) with your browser to see the application.

## 📱 Usage

### For Businesses

1. **Sign up/Login**: Create an account or log in to your existing account
2. **Create a Payment Pod**: 
   - Enter your business name
   - Add multiple items with their prices and quantities
   - Specify the number of people sharing the bill
3. **Share the Pod**: Share the generated QR code or pod code with your customers
4. **Manage Pods**: View all your pods, check their status, and close them when complete

### For Customers

1. **Join a Pod**: Enter the pod code or scan the QR code provided by the business
2. **View Details**: See all items in the pod and your share of the bill
3. **Mark as Paid**: Indicate when you've paid your share

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgements

- Developed as an ICA project for Teesside University
- Created by etherofgodd
