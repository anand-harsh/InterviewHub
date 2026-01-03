import './globals.css';
import { AuthProvider } from './context/AuthContext';
import Navbar from './components/Navbar';

export const metadata = {
  title: 'Mock Interview Platform',
  description: 'Connect with peers for interview practice',
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body>
        <AuthProvider>
          <Navbar />
          {children}
        </AuthProvider>
      </body>
    </html>
  );
}