'use client';

import { useEffect } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';

export default function CalendarCallbackPage() {
  const router = useRouter();
  const searchParams = useSearchParams();

  useEffect(() => {
    const status = searchParams.get('status');

    if (status === 'success') {
      // ✅ Redirect wherever you want
      router.replace('/profile'); // or '/dashboard'
    } else {
      router.replace('/settings?error=calendar');
    }
  }, []);

  return null; // or loading spinner
}