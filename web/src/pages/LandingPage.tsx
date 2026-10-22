import React from 'react';
import { ThreeGardenScene } from '../components/ThreeGardenScene';
import {
  Sprout,
  ShieldCheck,
  Sparkles,
  Droplets,
  Activity,
  Cpu,
  Palette,
  Sun,
  Plane,
  ChevronRight,
  CheckCircle2
} from 'lucide-react';

interface LandingPageProps {
  onGetStarted: () => void;
  onExploreFeature: (featureTab: string) => void;
}

export const LandingPage: React.FC<LandingPageProps> = ({
  onGetStarted,
  onExploreFeature
}) => {
  return (
    <div className="w-full min-h-screen">
      {/* Hero Section with Interactive 3D Garden Canvas */}
      <section className="relative overflow-hidden pt-8 pb-16 lg:pt-14 lg:pb-24 border-b border-sage-300/30">
        <div className="max-w-7xl mx-auto px-4 lg:px-8 grid grid-cols-1 lg:grid-cols-12 gap-8 items-center">
          {/* Left Text Column */}
          <div className="lg:col-span-7 space-y-6 z-10">
            <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-forest-100 border border-forest-500/20 text-forest-700 text-xs font-bold tracking-wide">
              <Sparkles className="w-3.5 h-3.5 text-limeAccent" />
              <span>Next-Gen AI & 3D Botanical Companion</span>
            </div>

            <h1 className="text-4xl sm:text-5xl lg:text-6xl font-extrabold text-forest-900 tracking-tight leading-[1.12]">
              Your Plants. Your Garden.{' '}
              <span className="text-transparent bg-clip-text bg-gradient-to-r from-forest-700 via-forest-500 to-limeAccent">
                Your Growing Journey.
              </span>
            </h1>

            <p className="text-base sm:text-lg text-sage-700 max-w-xl leading-relaxed">
              Grow smarter, care better, and live greener. PlantPal combines precision moisture-aware schedules, 3D digital twins, IoT soil telemetry, and multimodal AI diagnostics into one harmonious ecosystem.
            </p>

            <div className="flex flex-wrap items-center gap-4 pt-2">
              <button
                onClick={onGetStarted}
                className="px-6 py-3.5 rounded-xl bg-forest-700 hover:bg-forest-900 text-white font-bold text-sm shadow-lg shadow-forest-900/15 flex items-center gap-2 transition-all hover:scale-[1.02]"
              >
                <span>Start Growing Free</span>
                <ChevronRight className="w-4 h-4" />
              </button>

              <button
                onClick={() => onExploreFeature('doctor')}
                className="px-6 py-3.5 rounded-xl bg-white hover:bg-sage-100/60 border border-sage-300 text-forest-900 font-bold text-sm shadow-sm transition-all"
              >
                Scan Plant Leaf
              </button>
            </div>

            <div className="grid grid-cols-3 gap-4 pt-6 border-t border-sage-300/40 text-xs font-semibold text-sage-700">
              <div className="flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-forest-700" />
                <span>100% Offline-First</span>
              </div>
              <div className="flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-forest-700" />
                <span>3D Living Twins</span>
              </div>
              <div className="flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-forest-700" />
                <span>ESP32 Sensor Ready</span>
              </div>
            </div>
          </div>

          {/* Right 3D Interactive Canvas Column */}
          <div className="lg:col-span-5 relative w-full h-[380px] sm:h-[460px] lg:h-[500px] flex items-center justify-center">
            <div className="absolute inset-0 bg-gradient-to-b from-forest-100/40 via-transparent to-sage-100/50 rounded-3xl blur-2xl -z-10" />
            <div className="w-full h-full rounded-3xl border border-sage-300/40 bg-white/40 shadow-xl backdrop-blur-sm p-2 flex flex-col">
              <div className="flex items-center justify-between px-3 py-1.5 border-b border-sage-300/20 text-[11px] text-sage-700 font-medium">
                <span className="flex items-center gap-1.5 font-bold text-forest-900">
                  <span className="w-2 h-2 rounded-full bg-limeAccent animate-pulse" />
                  3D Living Companion: Monstera Deliciosa
                </span>
                <span>Drag to Rotate</span>
              </div>
              <div className="flex-1 w-full h-full relative">
                <ThreeGardenScene potColor="#C86D51" leafScale={1.1} interactive={true} />
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Feature Showcase Grid */}
      <section className="max-w-7xl mx-auto px-4 lg:px-8 py-16">
        <div className="text-center max-w-2xl mx-auto mb-12">
          <h2 className="text-2xl sm:text-3xl font-extrabold text-forest-900">
            A Complete Intelligent Garden Suite
          </h2>
          <p className="text-sm text-sage-700 mt-2">
            Every feature designed by horticulturists, software engineers, and plant lovers to keep your garden vibrant.
          </p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          <FeatureCard
            icon={<Droplets className="w-5 h-5 text-sky-600" />}
            title="Smart Watering & Feeding"
            desc="Schedules that account for soil dryness, container size, and seasons rather than rigid calendar guesswork."
            badge="Moisture Aware"
            onClick={() => onExploreFeature('dashboard')}
          />
          <FeatureCard
            icon={<Activity className="w-5 h-5 text-rose-600" />}
            title="AI Plant Doctor"
            desc="Multimodal leaf symptom scanning detects chlorosis, powdery mildew, spider mites, and prescribes organic remedies."
            badge="Gemini Powered"
            onClick={() => onExploreFeature('doctor')}
          />
          <FeatureCard
            icon={<Cpu className="w-5 h-5 text-emerald-600" />}
            title="Smart IoT Garden Sensors"
            desc="Real-time telemetry from ESP32 moisture probes, ambient temperature, humidity, and lux illuminance."
            badge="IoT Hardware"
            onClick={() => onExploreFeature('sensors')}
          />
          <FeatureCard
            icon={<Palette className="w-5 h-5 text-amber-600" />}
            title="AI Garden Designer"
            desc="Upload your balcony, terrace, or backyard to generate tailored plant layouts, container picks, and checklists."
            badge="Architectural AI"
            onClick={() => onExploreFeature('designer')}
          />
          <FeatureCard
            icon={<Plane className="w-5 h-5 text-indigo-600" />}
            title="Vacation Care Planning"
            desc="Departure and return itineraries with step-by-step care handover notes for friends and plant sitters."
            badge="Peace of Mind"
            onClick={() => onExploreFeature('vacation')}
          />
          <FeatureCard
            icon={<Sprout className="w-5 h-5 text-limeAccent" />}
            title="Garden XP & Community"
            desc="Earn badges for consistent care, unlock ceramic pot styles, and share flourishing garden milestones."
            badge="Gamification"
            onClick={() => onExploreFeature('community')}
          />
        </div>
      </section>
    </div>
  );
};

const FeatureCard: React.FC<{
  icon: React.ReactNode;
  title: string;
  desc: string;
  badge: string;
  onClick: () => void;
}> = ({ icon, title, desc, badge, onClick }) => {
  return (
    <div
      onClick={onClick}
      className="p-6 rounded-2xl bg-white border border-sage-300/40 hover:border-forest-500/40 shadow-sm hover:shadow-md transition-all cursor-pointer group flex flex-col justify-between"
    >
      <div>
        <div className="flex items-center justify-between mb-4">
          <div className="w-10 h-10 rounded-xl bg-sage-100 flex items-center justify-center">
            {icon}
          </div>
          <span className="text-[11px] font-bold px-2.5 py-0.5 rounded-full bg-forest-50 text-forest-700 border border-forest-500/10">
            {badge}
          </span>
        </div>
        <h3 className="font-bold text-base text-forest-900 group-hover:text-forest-700 transition-colors">
          {title}
        </h3>
        <p className="text-xs text-sage-700 mt-2 leading-relaxed">{desc}</p>
      </div>

      <div className="mt-5 pt-3 border-t border-sage-100 flex items-center text-xs font-bold text-forest-700 group-hover:translate-x-1 transition-transform">
        <span>Open Module</span>
        <ChevronRight className="w-3.5 h-3.5 ml-1" />
      </div>
    </div>
  );
};
