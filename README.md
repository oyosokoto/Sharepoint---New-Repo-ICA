# ~~SharePoint~~ **SplitIOU** - Group Bill Payment App

## 📌 Project Overview

**~~SharePoint~~ SplitIOU** is a mobile application designed to facilitate group payments for shared expenses. Whether dining out, traveling, or covering household bills, users can seamlessly split costs and make collective payments. This app aims to simplify the process of managing shared expenses while ensuring transparency and ease of use.

## 🎯 Purpose

This project is developed as part of my **Mobile App Development** coursework for the **ICA submission** at **Teesside University**. The app showcases key Android development concepts, including Firebase integration, payment handling, and user authentication.

## 🔥 Features

- 📲 **User Authentication**: Sign up and log in with Firebase Authentication.
- 💰 **Group Payments**: Users can join or create a payment group.
- 🧾 **Bill Splitting**: Automatically divides bills among group members.
- 📊 **Transaction Tracking**: Keeps records of who has paid and who hasn't.
- 🔔 **Notifications**: Reminders for pending payments.
- 🔗 **Payment Integration**: Secure payment gateway for transactions.
- 🖥️ **Admin Web Portal**: Business interface for creating and managing payment pods.

## 🛠️ Tech Stack

### Mobile App

- **Package Name**: `com.tees.mad.e4089074.sharepoint`
- **Language**: Kotlin
- **Framework**: Android Jetpack Components with Jetpack Compose
- **Backend**: Firebase Firestore & Firebase Authentication
- **Payment Handling**: Stripe
- **UI Design**: Material Design principles with Jetpack Compose

### Admin Web Portal

- **Framework**: Next.js 15 with React 19
- **Language**: TypeScript
- **Backend**: Firebase Firestore & Firebase Authentication
- **Styling**: Tailwind CSS
- **QR Code Generation**: For pod sharing
- **Admin Dashboard Link**: [https://splitiou.vercel.app](https://splitiou.vercel.app)

## 📂 Project Structure

```
SharePoint/
│── app/                           # Android mobile app
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/tees/mad/e4089074/sharepoint/
│   │   │   ├── res/
│   │   │   ├── AndroidManifest.xml
│── web/                           # Next.js admin portal
│   ├── app/                       # Next.js app router
│   ├── components/                # React components
│   ├── contexts/                  # Context providers
│   ├── lib/                       # Firebase configuration
│   ├── types/                     # TypeScript types
│   ├── utils/                     # Utility functions
│── .gitignore
│── build.gradle.kts
│── README.md
```

## 🚀 Getting Started

### Prerequisites

- Android Studio (Latest Version)
- Node.js 18.0 or later
- Firebase Account
- API Keys for Stripe/PayPal (if implementing payments)

### Mobile App Setup

1. **Clone the repository:**
   ```sh
   git clone https://github.com/oyosokoto/Sharepoint---New-Repo-ICA.git
   cd Sharepoint---New-Repo-ICA
   ```
2. **Open in Android Studio**
3. **Configure Firebase:**
   - Add `google-services.json` to `app/`
   - Enable Firestore and Authentication in Firebase Console
4. **Run the App**:
   - Connect an emulator or Android device
   - Click ▶️ Run in Android Studio

### Admin Portal Setup

1. **Navigate to the web directory:**
   ```sh
   cd web
   ```
2. **Install dependencies:**
   ```sh
   npm install
   ```
3. **Configure Firebase:**
   Create a `.env.local` file with your Firebase configuration:
   ```
   NEXT_PUBLIC_FIREBASE_API_KEY=your-api-key
   NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN=your-auth-domain
   NEXT_PUBLIC_FIREBASE_PROJECT_ID=your-project-id
   NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET=your-storage-bucket
   NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID=your-messaging-sender-id
   NEXT_PUBLIC_FIREBASE_APP_ID=your-app-id
   ```
4. **Run the development server:**
   ```sh
   npm run dev
   ```
5. **Access the admin portal:**

   1. Open the deployed instance [https://splitiou.vercel.app](https://splitiou.vercel.app) in your browser
   2. Open the local instance[http://localhost:3000](http://localhost:3000) in your browser after runing `npm run dev`

   **Demo Credentials:**

   - Email: admin@SplitIOU.com
   - Password: Admin123!

## 📜 License

This project is developed for academic purposes under Teesside University. It is not intended for commercial use.

## 📢 Asset Disclaimer

Some assets used in this project are not owned by me. If any of your assets are included, please reach out so I can give proper credit or remove them if necessary.

## 🙌 Acknowledgments

- **Teesside University** - For providing the platform for learning mobile app development.
- **Firebase & Stripe** - For enabling seamless authentication and payments.

---

🚀 **Developed by Samuel Andrew as part of the ICA Mobile App Development class at Teesside University.**
