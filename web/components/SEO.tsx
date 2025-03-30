import { APP_CONFIG } from '../constants/app';
import { Metadata } from 'next';

interface SEOProps {
    title?: string;
    description?: string;
    path?: string;
    image?: string;
}

export function generateMetadata({
    title,
    description,
    path,
    image
}: SEOProps): Metadata {
    const displayTitle = title
        ? `${title} | ${APP_CONFIG.name}`
        : APP_CONFIG.title;

    const url = path ? `${APP_CONFIG.url}${path}` : APP_CONFIG.url;

    return {
        title: displayTitle,
        description: description || APP_CONFIG.description,
        metadataBase: new URL(APP_CONFIG.url),
        alternates: {
            canonical: url,
        },
        openGraph: {
            title: displayTitle,
            description: description || APP_CONFIG.description,
            url,
            siteName: APP_CONFIG.name,
            images: [
                {
                    url: image || APP_CONFIG.ogImage,
                    width: 1200,
                    height: 630,
                },
            ],
            locale: 'en_US',
            type: 'website',
        },
        twitter: {
            card: 'summary_large_image',
            title: displayTitle,
            description: description || APP_CONFIG.description,
            images: [image || APP_CONFIG.twitterImage],
        },
        themeColor: APP_CONFIG.themeColor,
    };
}
