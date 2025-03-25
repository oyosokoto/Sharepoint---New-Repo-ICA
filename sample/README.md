# SharePoint Admin Portal

This is the admin portal for the SharePoint Group Bill Payment App. It allows businesses to create and manage payment pods for group bill splitting.

## Features

- **Authentication**: Secure login for business administrators
- **Dashboard**: Overview of payment pods and statistics
- **Create Payment Pods**: Create new payment pods with item details, pricing, and number of podders
- **Manage Pods**: View, activate/deactivate, and generate QR codes for payment pods
- **QR Code Generation**: Generate QR codes for customers to join payment pods

## Getting Started

### Prerequisites

- Node.js 18.0 or later
- Firebase account with Firestore and Authentication enabled

### Setup

1. Clone the repository:

```bash
git clone <repository-url>
cd sharepoint/web
```

2. Install dependencies:

```bash
npm install
```

3. Configure Firebase:

Create a `.env.local` file in the root directory with your Firebase configuration:

```
NEXT_PUBLIC_FIREBASE_API_KEY=your-api-key
NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=your-auth-domain
NEXT_PUBLIC_FIREBASE_PROJECT_ID=your-project-id
NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET=your-storage-bucket
NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=your-messaging-sender-id
NEXT_PUBLIC_FIREBASE_APP_ID=your-app-id
```

4. Run the development server:

```bash
npm run dev
```

5. Open [http://localhost:3000](http://localhost:3000) with your browser to see the result.

## Demo Credentials

For testing purposes, you can use the following credentials:

- **Email**: admin@sharepoint.com
- **Password**: Admin123!

## Usage

1. **Login**: Use the demo credentials to log in to the admin portal
2. **Create a Pod**: Navigate to "Create Pod" and fill in the details for a new payment pod
3. **View Pods**: See all created pods in the "Payment Pods" section
4. **Share Pod Code**: Generate and share QR codes with customers to join the payment pod

## Integration with Mobile App

This admin portal works in conjunction with the SharePoint mobile app. The mobile app allows users to:

1. Join pods using the pod code or by scanning the QR code
2. Split bills automatically among pod members
3. Track payment status
4. Process payments

## Technologies Used

- Next.js 15
- React 19
- Firebase (Authentication & Firestore)
- Tailwind CSS
- TypeScript
