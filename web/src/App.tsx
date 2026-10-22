import React, { useState } from 'react';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { Navbar } from './components/Navbar';
import { LandingPage } from './pages/LandingPage';
import { DashboardPage } from './pages/DashboardPage';
import { MyPlantsPage } from './pages/MyPlantsPage';
import { PlantDoctorPage } from './pages/PlantDoctorPage';
import { GardenDesignerPage } from './pages/GardenDesignerPage';
import { IoTSensorsPage } from './pages/IoTSensorsPage';
import { CommunityPage } from './pages/CommunityPage';
import { BudgetPage } from './pages/BudgetPage';
import { VacationPage } from './pages/VacationPage';
import { AuthPage } from './pages/AuthPage';
import { ScanScreen } from './pages/ScanScreen';
import { Sprout, Smartphone } from 'lucide-react';

const MainContent: React.FC = () => {
  const [currentTab, setCurrentTab] = useState<string>('landing');
  const { user } = useAuth();
  const isLoggedIn = !!user;

  const renderActiveScreen = () => {
    switch (currentTab) {
      case 'landing':
        return (
          <LandingPage
            onGetStarted={() => setCurrentTab('dashboard')}
            onExploreFeature={(tab) => setCurrentTab(tab)}
          />
        );
      case 'dashboard':
        return (
          <DashboardPage
            onOpenAddPlant={() => setCurrentTab('plants')}
            onNavigateTab={(tab) => setCurrentTab(tab)}
          />
        );
      case 'plants':
        return <MyPlantsPage onOpenAddPlant={() => {}} />;
      case 'scan':
        return <ScanScreen />;
      case 'doctor':
        return <PlantDoctorPage />;
      case 'designer':
        return <GardenDesignerPage />;
      case 'sensors':
        return <IoTSensorsPage />;
      case 'community':
        return <CommunityPage />;
      case 'budget':
        return <BudgetPage />;
      case 'vacation':
        return <VacationPage />;
      case 'auth':
        return (
          <AuthPage
            onLoginSuccess={() => setCurrentTab('dashboard')}
            onNavigateTab={(tab) => setCurrentTab(tab)}
          />
        );
      default:
        return (
          <DashboardPage
            onOpenAddPlant={() => setCurrentTab('plants')}
            onNavigateTab={(tab) => setCurrentTab(tab)}
          />
        );
    }
  };

  return (
    <div className="min-h-screen flex flex-col bg-[#fbfbf6] text-[#181d18] selection:bg-forest-100 selection:text-forest-900">
      <Navbar
        currentTab={currentTab}
        onSelectTab={(tab) => setCurrentTab(tab)}
        isLoggedIn={isLoggedIn}
      />

      <main className="flex-1 transition-all">{renderActiveScreen()}</main>

      <footer className="bg-forest-950 text-cream border-t border-forest-900 mt-20">
        <div className="max-w-7xl mx-auto px-4 lg:px-8 py-12">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-8">
            <div className="space-y-3">
              <div className="flex items-center gap-2.5">
                <div className="w-8 h-8 rounded-lg bg-forest-700 flex items-center justify-center text-white">
                  <Sprout className="w-4 h-4" />
                </div>
                <span className="font-extrabold text-lg text-white">PlantPal</span>
              </div>
              <p className="text-xs text-sage-300 leading-relaxed">
                The complete AI & 3D plant care companion.
              </p>
            </div>
          </div>
          <div className="mt-12 pt-6 border-t border-forest-900/60 flex flex-col sm:flex-row items-center justify-between text-xs text-sage-400 gap-3">
            <span>© {new Date().getFullYear()} PlantPal Ecosystem. All rights reserved.</span>
          </div>
        </div>
      </footer>
    </div>
  );
};

export const App: React.FC = () => (
  <AuthProvider>
    <MainContent />
  </AuthProvider>
);
export default App;
