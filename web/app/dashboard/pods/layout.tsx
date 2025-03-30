import type { Metadata } from 'next';
import { APP_CONFIG } from '../../../constants/app';

export const metadata: Metadata = {
    title: 'Payment Pods Dashboard',
    description: 'Manage your payment pods and track group expenses in real-time.',
    openGraph: {
        title: `Payment Pods Dashboard | ${APP_CONFIG.name}`,
        description: 'Manage your payment pods and track group expenses in real-time.',
        url: `${APP_CONFIG.url}/dashboard/pods`,
    }
};

export default function PodsLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return <>{children}</>;
}
