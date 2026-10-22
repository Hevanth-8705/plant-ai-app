import React from 'react';
import { Sprout, ShieldCheck, Sparkles, User, SunMedium } from 'lucide-react';

interface NavbarProps {
  currentTab: string;
  onSelectTab: (tab: string) => void;
  isLoggedIn: boolean;
}

export const Navbar: React.FC<NavbarProps> = ({
  currentTab,
  onSelectTab,
  isLoggedIn
}) => {
  const navItems = [
    { id: 'dashboard', label: 'Dashboard' },
    { id: 'plants', label: 'My Plants' },
    { id: 'scan', label: 'Scan Plant' },
    { id: 'doctor', label: 'AI Plant Doctor' },
    { id: 'designer', label: 'Garden Designer' },
    { id: 'sensors', label: 'Smart IoT' },
    { id: 'community', label: 'Community' },
    { id: 'budget', label: 'Budget' },
    { id: 'vacation', label: 'Vacation Care' }
  ];

  return (
    <nav className="sticky top-0 z-50 bg-[#fbfbf6]/90 backdrop-blur-md border-b border-sage-300/40 px-4 lg:px-8 py-3.5 transition-all">
      <div className="max-w-7xl mx-auto flex items-center justify-between">
        {/* Brand */}
        <div
          onClick={() => onSelectTab('landing')}
          className="flex items-center gap-2.5 cursor-pointer group"
        >
          <div className="w-10 h-10 rounded-xl bg-forest-700 flex items-center justify-center text-white shadow-md shadow-forest-900/10 group-hover:bg-forest-900 transition-colors">
            <Sprout className="w-5 h-5" />
          </div>
          <div>
            <span className="font-bold text-lg text-forest-900 tracking-tight">PlantPal</span>
            <span className="hidden sm:block text-[10px] text-sage-500 font-medium -mt-1 tracking-wider uppercase">
              AI Gardening Ecosystem
            </span>
          </div>
        </div>

        {/* Navigation Tabs */}
        <div className="hidden md:flex items-center gap-1 bg-sage-100/60 p-1 rounded-xl border border-sage-300/30">
          {navItems.map((item) => (
            <button
              key={item.id}
              onClick={() => onSelectTab(item.id)}
              className={`px-3 py-1.5 rounded-lg text-xs font-semibold transition-all ${
                currentTab === item.id
                  ? 'bg-forest-700 text-white shadow-sm'
                  : 'text-sage-700 hover:text-forest-900 hover:bg-white/60'
              }`}
            >
              {item.label}
            </button>
          ))}
        </div>

        {/* User / XP Badge */}
        <div className="flex items-center gap-3">
          <div className="hidden sm:flex items-center gap-1.5 px-3 py-1 bg-amber-50 border border-amber-200/80 rounded-full text-amber-800 text-xs font-bold">
            <Sparkles className="w-3.5 h-3.5 text-amber-600" />
            <span>Lvl 2 • 120 XP</span>
          </div>

          <button
            onClick={() => onSelectTab('auth')}
            className="flex items-center gap-2 px-3.5 py-1.5 rounded-xl bg-forest-700 hover:bg-forest-900 text-white text-xs font-bold shadow-sm transition-all"
          >
            <User className="w-3.5 h-3.5" />
            <span>{isLoggedIn ? 'Account' : 'Sign In'}</span>
          </button>
        </div>
      </div>
    </nav>
  );
};
