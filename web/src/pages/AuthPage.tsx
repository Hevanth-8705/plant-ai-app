import React, { useState } from 'react';
import { ThreeGardenScene } from '../components/ThreeGardenScene';
import { Lock, Mail, User, AlertCircle } from 'lucide-react';
import { auth } from '../lib/firebase';
import { 
  signInWithEmailAndPassword, 
  createUserWithEmailAndPassword, 
  GoogleAuthProvider, 
  signInWithPopup, 
  updateProfile 
} from 'firebase/auth';

interface AuthPageProps {
  onLoginSuccess: () => void;
  onNavigateTab: (tab: string) => void;
}

export const AuthPage: React.FC<AuthPageProps> = ({ onLoginSuccess, onNavigateTab }) => {
  const [isRegister, setIsRegister] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState('');

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg('');
    setIsLoading(true);

    try {
      if (isRegister) {
        const userCredential = await createUserWithEmailAndPassword(auth, email, password);
        await updateProfile(userCredential.user, { displayName: name });
      } else {
        await signInWithEmailAndPassword(auth, email, password);
      }
      onLoginSuccess();
      onNavigateTab('dashboard');
    } catch (err: any) {
      setErrorMsg(err.message || 'Authentication failed');
    } finally {
      setIsLoading(false);
    }
  };

  const handleGoogleSignIn = async () => {
    try {
      const provider = new GoogleAuthProvider();
      await signInWithPopup(auth, provider);
      onLoginSuccess();
      onNavigateTab('dashboard');
    } catch (err: any) {
      setErrorMsg(err.message || 'Google sign-in failed');
    }
  };

  return (
    <div className="relative min-h-screen flex items-center justify-center p-6 bg-[#fbfbf6]">
      <div className="absolute inset-0 z-0 opacity-40">
        <ThreeGardenScene potColor="#C86D51" leafScale={1.2} interactive={true} />
      </div>

      <div className="relative z-10 w-full max-w-md bg-white/80 backdrop-blur-md p-8 rounded-3xl shadow-xl border border-white/50">
        <h2 className="text-3xl font-extrabold text-forest-900 mb-2">
          {isRegister ? 'Create Account' : 'Welcome'}
        </h2>
        
        {errorMsg && (
          <div className="mb-6 p-4 rounded-xl bg-rose-50 border border-rose-200 text-rose-700 text-sm flex items-center gap-2">
            <AlertCircle className="w-5 h-5" />
            <span>{errorMsg}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          {isRegister && (
            <div>
              <label className="block text-xs font-bold text-forest-900 mb-1">Full Name</label>
              <input type="text" required value={name} onChange={(e) => setName(e.target.value)} className="w-full px-4 py-3 rounded-xl border border-sage-300" />
            </div>
          )}
          <div>
            <label className="block text-xs font-bold text-forest-900 mb-1">Email</label>
            <input type="email" required value={email} onChange={(e) => setEmail(e.target.value)} className="w-full px-4 py-3 rounded-xl border border-sage-300" />
          </div>
          <div>
            <label className="block text-xs font-bold text-forest-900 mb-1">Password</label>
            <input type="password" required value={password} onChange={(e) => setPassword(e.target.value)} className="w-full px-4 py-3 rounded-xl border border-sage-300" />
          </div>

          <button type="submit" disabled={isLoading} className="w-full py-4 rounded-xl bg-forest-700 text-white font-bold hover:bg-forest-900">
            {isLoading ? 'Processing...' : (isRegister ? 'Register' : 'Login')}
          </button>
        </form>

        <button onClick={handleGoogleSignIn} className="mt-4 w-full py-3 rounded-xl border border-forest-700 text-forest-700 font-bold">
            Sign in with Google
        </button>

        <button onClick={() => setIsRegister(!isRegister)} className="mt-6 w-full text-sm text-forest-700 hover:underline">
          {isRegister ? 'Already have an account? Login' : 'Need an account? Register'}
        </button>
      </div>
    </div>
  );
};
