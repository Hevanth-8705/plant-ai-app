import React, { useState, useEffect } from 'react';
import {
  Droplets,
  Sprout,
  Sparkles,
  AlertCircle,
  Plus,
  CheckCircle2,
  Activity,
  Cpu,
  Sun,
  Palette,
  Check,
  RefreshCw,
  Calendar,
  Key,
  Lightbulb,
  Leaf,
  ShieldCheck,
  ChevronDown,
  ChevronUp
} from 'lucide-react';
import { GrowthTrendsChart } from '../components/GrowthTrendsChart';
import { SensorTrendsChart } from '../components/SensorTrendsChart';
import { ThreeGardenScene } from '../components/ThreeGardenScene';

interface DashboardPageProps {
  onOpenAddPlant: () => void;
  onNavigateTab: (tab: string) => void;
}

interface DailyGardenTip {
  title: string;
  category: string;
  season: string;
  tip: string;
  actionItem: string;
  funFact: string;
  dateStr: string;
  source: 'gemini' | 'botanical-engine';
}

export const DashboardPage: React.FC<DashboardPageProps> = ({
  onOpenAddPlant,
  onNavigateTab
}) => {
  const [tasks, setTasks] = useState([
    {
      id: 't1',
      plant: 'Monstera Deliciosa',
      type: 'Watering',
      due: 'Overdue by 1d',
      details: 'Check if top 2 inches feel dry before hydrating',
      urgent: true,
      completed: false
    },
    {
      id: 't2',
      plant: 'Sweet Genovese Basil',
      type: 'Watering',
      due: 'Due Today',
      details: 'Maintain evenly moist organic potting soil',
      urgent: false,
      completed: false
    },
    {
      id: 't3',
      plant: 'Roma Tomato',
      type: 'Fertilizing',
      due: 'Due Today',
      details: 'Feed with balanced organic nutrients for fruit set',
      urgent: false,
      completed: false
    }
  ]);

  const [missions, setMissions] = useState([
    { id: 'm1', text: 'Conduct finger soil test on Snake Plant', xp: 15, done: true },
    { id: 'm2', text: 'Rotate windowsill pots 90° for even light', xp: 20, done: false },
    { id: 'm3', text: 'Scan a leaf for health diagnostics', xp: 30, done: false }
  ]);

  const [xp, setXp] = useState(120);

  // Gemini AI Daily Tip & Seasonal Advice State
  const [dailyTip, setDailyTip] = useState<DailyGardenTip | null>(null);
  const [isLoadingTip, setIsLoadingTip] = useState(false);
  const [tipActionCompleted, setTipActionCompleted] = useState(false);
  const [apiKey, setApiKey] = useState(() => {
    return localStorage.getItem('plantpal_gemini_key') || '';
  });
  const [showKeyInput, setShowKeyInput] = useState(false);
  const [tempApiKey, setTempApiKey] = useState('');
  const [apiError, setApiError] = useState<string | null>(null);

  // Determine current formatted date and season
  const now = new Date();
  const dateFormatted = now.toLocaleDateString('en-US', {
    weekday: 'long',
    month: 'long',
    day: 'numeric',
    year: 'numeric'
  });

  const getSeasonalContext = (d: Date) => {
    const month = d.getMonth(); // 0-indexed (9 = Oct)
    if (month >= 2 && month <= 4) {
      return {
        season: 'Early Spring Awakening',
        icon: '🌸',
        summary: 'Photoperiod expanding; vegetative growth surge begins.'
      };
    } else if (month >= 5 && month <= 7) {
      return {
        season: 'Peak Summer Hydration',
        icon: '☀️',
        summary: 'High evaporation rates; focus on early morning deep roots.'
      };
    } else if (month >= 8 && month <= 10) {
      return {
        season: 'Mid-Autumn Sunlight & Moisture Transition',
        icon: '🍂',
        summary: 'Shortening daylight hours; pull back fertilizers and extend watering intervals.'
      };
    } else {
      return {
        season: 'Winter Dormancy & Protection',
        icon: '❄️',
        summary: 'Radiator dry air mitigation and minimal root disturbance.'
      };
    }
  };

  const seasonalContext = getSeasonalContext(now);

  // Offline Botanical Heuristic Seasonal Advice Generator
  const getBotanicalFallbackTip = (): DailyGardenTip => {
    const month = now.getMonth();
    if (month >= 8 && month <= 10) {
      // Autumn tips
      const autumnTips = [
        {
          title: 'Dial Back Watering as Sunlight Hours Wane',
          category: 'Indoor Foliage & Balcony',
          tip: `As we transition into ${seasonalContext.season}, plants enter slower metabolic phases. With lower solar radiation and cooler evenings, potting soil dries out significantly slower. Always perform a 2-inch knuckle moisture test before watering to prevent cold root rot.`,
          actionItem: 'Wipe dust off broad leaves (Monstera, Fiddle Leaf) with a damp microfiber cloth to maximize waning autumn light absorption.',
          funFact: 'Dust accumulation on tropical houseplant leaves can reduce photosynthetically active radiation (PAR) absorption by up to 35%!'
        },
        {
          title: 'Cease Heavy High-Nitrogen Fertilization',
          category: 'Soil & Nutrition',
          tip: 'Stimulating tender new vegetative growth in late autumn can stress houseplants when indoor heating systems dry out ambient air. Let roots naturally harden off by pausing synthetic feeds until early spring awakenings.',
          actionItem: 'Top-dress indoor pots with 0.5 inches of worm castings for gentle, slow-release micro-nutrients that will not burn roots.',
          funFact: 'Earthworm castings contain beneficial mycorrhizal fungi that help root systems withstand winter temperature fluctuations.'
        },
        {
          title: 'Prepare Tender Herbs for Early Frosts',
          category: 'Culinary Herbs & Vegetables',
          tip: 'Basil, peppers, and tender annuals cannot withstand night temperatures dipping below 50°F (10°C). Bring prized culinary pots indoors to a south-facing sunny windowsill before the first cold snap shocks their vascular tissue.',
          actionItem: 'Inspect undersides of outdoor plants for hitchhiking spider mites or aphids before introducing them to your indoor collection.',
          funFact: 'Cold-pressed neem oil acts both as an organic insecticide and an anti-transpirant leaf shine that helps retain leaf hydration.'
        }
      ];
      const pick = autumnTips[now.getDate() % autumnTips.length];
      return {
        ...pick,
        season: seasonalContext.season,
        dateStr: dateFormatted,
        source: 'botanical-engine'
      };
    } else if (month >= 2 && month <= 4) {
      // Spring
      return {
        title: 'Spring Awakening: Resume Balanced Organic Feeds',
        category: 'Growth & Awakening',
        season: seasonalContext.season,
        tip: 'Longer daylight triggers root cell division and new terminal leaf buds. Re-introduce diluted organic kelp or fish emulsion at half strength to nourish vigorous spring leaf flushes.',
        actionItem: 'Rotate all windowsill pots 90 degrees clockwise to encourage upright symmetrical stem development.',
        funFact: 'Phototropism causes plant auxin hormones to concentrate on the shaded side of stems, making them elongate faster toward light.',
        dateStr: dateFormatted,
        source: 'botanical-engine'
      };
    } else {
      // Winter / Summer generic high quality fallback
      return {
        title: 'Microclimate Humidity Preservation',
        category: 'Houseplants & Tropicals',
        season: seasonalContext.season,
        tip: 'Indoor heating or summer air conditioners strip relative humidity down to 25-30%, causing crispy leaf margins on tropicals. Grouping plants together creates a collective transpiration microclimate.',
        actionItem: 'Set up a shallow tray filled with river pebbles and water beneath your Calatheas and Ferns without letting pot bottoms touch water.',
        funFact: 'Up to 97% of the water absorbed by plant roots is released back into the immediate surrounding air through transpiration!',
        dateStr: dateFormatted,
        source: 'botanical-engine'
      };
    }
  };

  // Fetch or Generate Tip using Gemini API (with botanical fallback)
  const generateDailyTip = async (forcedApiKey?: string) => {
    setIsLoadingTip(true);
    setApiError(null);

    const activeKey = forcedApiKey !== undefined ? forcedApiKey : apiKey;

    if (!activeKey || activeKey.trim() === '') {
      // Use rich botanical rule engine fallback
      setTimeout(() => {
        setDailyTip(getBotanicalFallbackTip());
        setIsLoadingTip(false);
      }, 500);
      return;
    }

    try {
      const prompt = `You are PlantPal's master horticulturist and botanical expert.
Today's date is ${dateFormatted}. The current seasonal phase is ${seasonalContext.season}.
Provide a daily, highly actionable gardening tip and seasonal plant care advice tailored for today's exact date and season.
Cover practical indoor houseplant care, balcony vegetables, soil management, or temperature/light transitions.

Respond STRICTLY with a valid JSON object matching this schema (no markdown formatting, no code fences):
{
  "title": "A catchy, expert 4-7 word headline",
  "category": "One of: Houseplants, Balcony & Patio, Herbs & Edibles, Soil Health, or Microclimate",
  "tip": "2-3 sentences of clear, practical, expert advice relevant to this seasonal window.",
  "actionItem": "A specific 5-minute task the gardener can do today.",
  "funFact": "A 1-sentence botanical or plant biology science insight related to this advice."
}`;

      const response = await fetch(
        `https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=${activeKey.trim()}`,
        {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            contents: [
              {
                parts: [{ text: prompt }]
              }
            ],
            generationConfig: {
              temperature: 0.7,
              responseMimeType: 'application/json'
            }
          })
        }
      );

      if (!response.ok) {
        throw new Error(`Gemini API returned status ${response.status}`);
      }

      const data = await response.json();
      const rawText = data?.candidates?.[0]?.content?.parts?.[0]?.text;

      if (!rawText) {
        throw new Error('No candidate content received from Gemini');
      }

      // Clean markdown code blocks if any
      const cleaned = rawText.replace(/```json/g, '').replace(/```/g, '').trim();
      const parsed = JSON.parse(cleaned);

      setDailyTip({
        title: parsed.title || 'Seasonal Garden Care & Transition',
        category: parsed.category || 'General Gardening',
        season: seasonalContext.season,
        tip: parsed.tip || parsed.advice || 'Ensure proper seasonal watering and sunlight orientation.',
        actionItem: parsed.actionItem || 'Check soil moisture on top 2 inches of potting mix.',
        funFact: parsed.funFact || 'Plants adjust their stomata apertures to conserve moisture during seasonal changes.',
        dateStr: dateFormatted,
        source: 'gemini'
      });
    } catch (err: any) {
      console.warn('Gemini API fetch failed, falling back to Botanical Engine:', err);
      setApiError(err.message || 'Could not connect to Gemini API');
      setDailyTip(getBotanicalFallbackTip());
    } finally {
      setIsLoadingTip(false);
    }
  };

  useEffect(() => {
    generateDailyTip();
  }, []);

  const handleSaveApiKey = (e: React.FormEvent) => {
    e.preventDefault();
    const key = tempApiKey.trim();
    localStorage.setItem('plantpal_gemini_key', key);
    setApiKey(key);
    setShowKeyInput(false);
    generateDailyTip(key);
  };

  const completeTask = (id: string) => {
    setTasks(tasks.map(t => t.id === id ? { ...t, completed: true } : t));
    setXp(prev => prev + 25);
  };

  const toggleMission = (id: string) => {
    setMissions(missions.map(m => {
      if (m.id === id) {
        const nextDone = !m.done;
        setXp(prev => prev + (nextDone ? m.xp : -m.xp));
        return { ...m, done: nextDone };
      }
      return m;
    }));
  };

  const handleCompleteTipAction = () => {
    if (!tipActionCompleted) {
      setTipActionCompleted(true);
      setXp(prev => prev + 15);
    }
  };

  const activeTasks = tasks.filter(t => !t.completed);

  return (
    <div className="max-w-7xl mx-auto px-4 lg:px-8 py-8 space-y-8">
      {/* Welcome & Garden Health Banner */}
      <div className="p-6 sm:p-8 rounded-3xl bg-gradient-to-r from-forest-700 via-forest-500 to-sage-500 text-white shadow-xl flex flex-col md:flex-row items-start md:items-center justify-between gap-6">
        <div>
          <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/20 text-white text-xs font-semibold mb-2">
            <Sparkles className="w-3 h-3 text-limeAccent" />
            <span>Urban Garden Sanctuary</span>
          </div>
          <h1 className="text-2xl sm:text-3xl font-extrabold tracking-tight">
            Good morning, Master Gardener 🌱
          </h1>
          <p className="text-white/80 text-xs sm:text-sm mt-1 max-w-lg">
            Your plants are receiving gentle morning sunlight. You have {activeTasks.length} hydration tasks requiring your attention today.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={onOpenAddPlant}
            className="px-4 py-2.5 rounded-xl bg-white text-forest-900 font-bold text-xs shadow-md hover:bg-forest-50 transition-all flex items-center gap-1.5"
          >
            <Plus className="w-4 h-4 text-forest-700" />
            <span>Add Plant</span>
          </button>
          <button
            onClick={() => onNavigateTab('doctor')}
            className="px-4 py-2.5 rounded-xl bg-forest-900/40 hover:bg-forest-900/60 border border-white/20 text-white font-bold text-xs shadow-md transition-all flex items-center gap-1.5"
          >
            <Activity className="w-4 h-4 text-limeAccent" />
            <span>AI Doctor</span>
          </button>
        </div>
      </div>

      {/* Metric Cards Row */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <div className="p-5 rounded-2xl bg-white border border-sage-300/40 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-forest-100 flex items-center justify-center text-forest-700">
            <Sprout className="w-6 h-6" />
          </div>
          <div>
            <div className="text-2xl font-extrabold text-forest-900">5</div>
            <div className="text-xs text-sage-700 font-medium">Total Plants</div>
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-sage-300/40 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-rose-50 flex items-center justify-center text-rose-600">
            <Droplets className="w-6 h-6" />
          </div>
          <div>
            <div className="text-2xl font-extrabold text-rose-700">1</div>
            <div className="text-xs text-sage-700 font-medium">Water Overdue</div>
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-sage-300/40 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-amber-50 flex items-center justify-center text-amber-600">
            <Activity className="w-6 h-6" />
          </div>
          <div>
            <div className="text-2xl font-extrabold text-amber-700">2</div>
            <div className="text-xs text-sage-700 font-medium">Due Today</div>
          </div>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-sage-300/40 shadow-sm flex items-center gap-4">
          <div className="w-12 h-12 rounded-xl bg-emerald-50 flex items-center justify-center text-emerald-600">
            <CheckCircle2 className="w-6 h-6" />
          </div>
          <div>
            <div className="text-2xl font-extrabold text-emerald-700">100%</div>
            <div className="text-xs text-sage-700 font-medium">Garden Health</div>
          </div>
        </div>
      </div>

      {/* DAILY AI-GENERATED GARDENING TIP & SEASONAL ADVICE FEATURE */}
      <section className="relative overflow-hidden rounded-3xl bg-gradient-to-br from-white via-sage-50/60 to-forest-50/50 border border-sage-300/50 shadow-md p-6 sm:p-7 space-y-5">
        {/* Decorative foliage backdrop highlight */}
        <div className="absolute top-0 right-0 -mt-8 -mr-8 w-44 h-44 rounded-full bg-limeAccent/10 blur-2xl pointer-events-none" />

        {/* Feature Header Bar */}
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-sage-200/60 pb-4">
          <div className="space-y-1">
            <div className="flex flex-wrap items-center gap-2">
              <span className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-forest-700 text-white text-xs font-bold shadow-sm">
                <Sparkles className="w-3.5 h-3.5 text-limeAccent animate-pulse" />
                <span>Daily AI Seasonal Guidance</span>
              </span>

              <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full bg-amber-100/80 text-amber-900 text-xs font-bold border border-amber-200">
                <Calendar className="w-3 h-3 text-amber-700" />
                <span>{dateFormatted}</span>
              </span>

              <span className="inline-flex items-center gap-1 px-2.5 py-0.5 rounded-full bg-sage-200/60 text-forest-900 text-xs font-semibold">
                <span>{seasonalContext.icon}</span>
                <span>{seasonalContext.season}</span>
              </span>
            </div>
            <h2 className="text-lg sm:text-xl font-extrabold text-forest-900 pt-1">
              Today's Botanical Almanac & Care Insight
            </h2>
          </div>

          {/* Action buttons */}
          <div className="flex items-center gap-2">
            <button
              onClick={() => generateDailyTip()}
              disabled={isLoadingTip}
              className="px-3.5 py-2 rounded-xl bg-white hover:bg-forest-50 text-forest-800 border border-sage-300 text-xs font-bold flex items-center gap-1.5 shadow-sm transition-all disabled:opacity-60"
              title="Generate new seasonal advice for today"
            >
              <RefreshCw className={`w-3.5 h-3.5 text-forest-700 ${isLoadingTip ? 'animate-spin' : ''}`} />
              <span>{isLoadingTip ? 'Consulting Gemini...' : 'Regenerate'}</span>
            </button>

            <button
              onClick={() => {
                setTempApiKey(apiKey);
                setShowKeyInput(!showKeyInput);
              }}
              className={`px-3 py-2 rounded-xl text-xs font-bold border flex items-center gap-1.5 transition-all ${
                apiKey
                  ? 'bg-emerald-50 text-emerald-800 border-emerald-300'
                  : 'bg-white text-sage-700 border-sage-300 hover:bg-sage-50'
              }`}
              title="Configure Gemini API Key"
            >
              <Key className="w-3.5 h-3.5 text-sage-600" />
              <span>{apiKey ? 'API Key Active' : 'Set Gemini Key'}</span>
              {showKeyInput ? <ChevronUp className="w-3 h-3" /> : <ChevronDown className="w-3 h-3" />}
            </button>
          </div>
        </div>

        {/* Optional Collapsible Gemini API Key Input */}
        {showKeyInput && (
          <form
            onSubmit={handleSaveApiKey}
            className="p-4 rounded-2xl bg-white border border-sage-300 shadow-sm space-y-3 animate-in fade-in duration-200"
          >
            <div className="flex items-center justify-between">
              <span className="text-xs font-bold text-forest-900 flex items-center gap-1.5">
                <Key className="w-3.5 h-3.5 text-forest-700" />
                <span>Custom Gemini API Key (Direct Model Generation)</span>
              </span>
              <span className="text-[11px] text-sage-500 font-medium">
                Uses gemini-3.5-flash
              </span>
            </div>
            <div className="flex gap-2">
              <input
                type="password"
                placeholder="AIzaSy... (Paste Gemini API key or leave blank for offline botanical model)"
                value={tempApiKey}
                onChange={(e) => setTempApiKey(e.target.value)}
                className="flex-1 px-3 py-2 rounded-xl border border-sage-300 text-xs font-mono focus:outline-none focus:ring-2 focus:ring-forest-600 bg-white"
              />
              <button
                type="submit"
                className="px-4 py-2 rounded-xl bg-forest-700 hover:bg-forest-900 text-white text-xs font-bold transition-all shadow-sm"
              >
                Save & Apply
              </button>
            </div>
            <p className="text-[11px] text-sage-600">
              When no custom key is provided, PlantPal runs its internal Botanical Expert Seasonal Heuristic Engine for immediate, verified seasonal care advice.
            </p>
          </form>
        )}

        {/* Tip Body Content */}
        {isLoadingTip ? (
          <div className="p-8 rounded-2xl bg-white/70 border border-sage-200 flex flex-col items-center justify-center space-y-3 text-center">
            <div className="w-10 h-10 rounded-full border-4 border-forest-600 border-t-transparent animate-spin" />
            <div className="space-y-1">
              <p className="text-sm font-bold text-forest-900">
                Gemini AI is analyzing current solar angles & seasonal moisture...
              </p>
              <p className="text-xs text-sage-600">
                Synthesizing horticulturist advice for {dateFormatted}
              </p>
            </div>
          </div>
        ) : dailyTip ? (
          <div className="space-y-4">
            {/* Headline and Category */}
            <div className="space-y-1.5">
              <div className="flex items-center gap-2">
                <span className="text-[10px] font-extrabold uppercase px-2 py-0.5 rounded-md bg-forest-100 text-forest-800 tracking-wider">
                  {dailyTip.category}
                </span>
                <span className="text-xs text-sage-600 font-medium">
                  • Season Focus: {dailyTip.season}
                </span>
              </div>
              <h3 className="text-lg sm:text-xl font-black text-forest-900 tracking-tight leading-snug">
                {dailyTip.title}
              </h3>
              <p className="text-xs sm:text-sm text-sage-800 leading-relaxed max-w-4xl">
                {dailyTip.tip}
              </p>
            </div>

            {/* Dual Highlights: Today's Action Item & Botanical Fun Fact */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3.5 pt-1">
              {/* Action Item Pill */}
              <div className="p-4 rounded-2xl bg-emerald-50/80 border border-emerald-200/90 flex items-start justify-between gap-3 shadow-xs">
                <div className="space-y-1">
                  <div className="flex items-center gap-1.5 text-xs font-bold text-emerald-900">
                    <Leaf className="w-3.5 h-3.5 text-emerald-600 flex-shrink-0" />
                    <span>Today's 5-Minute Care Action</span>
                  </div>
                  <p className="text-xs text-emerald-950 font-medium leading-relaxed">
                    {dailyTip.actionItem}
                  </p>
                </div>
                <button
                  onClick={handleCompleteTipAction}
                  disabled={tipActionCompleted}
                  className={`px-3 py-1.5 rounded-xl text-xs font-bold flex items-center gap-1 shadow-xs transition-all flex-shrink-0 ${
                    tipActionCompleted
                      ? 'bg-emerald-600 text-white'
                      : 'bg-white hover:bg-emerald-100 text-emerald-900 border border-emerald-300'
                  }`}
                >
                  <Check className="w-3 h-3" />
                  <span>{tipActionCompleted ? 'Done! (+15 XP)' : 'Mark Done'}</span>
                </button>
              </div>

              {/* Botanical Fun Fact */}
              <div className="p-4 rounded-2xl bg-amber-50/80 border border-amber-200/90 flex items-start gap-3 shadow-xs">
                <Lightbulb className="w-4 h-4 text-amber-600 flex-shrink-0 mt-0.5" />
                <div className="space-y-0.5">
                  <span className="text-xs font-bold text-amber-900 block">
                    Botanical Science Fact
                  </span>
                  <p className="text-xs text-amber-950 leading-relaxed font-medium">
                    {dailyTip.funFact}
                  </p>
                </div>
              </div>
            </div>

            {/* Footer metadata */}
            <div className="flex flex-wrap items-center justify-between text-[11px] text-sage-600 pt-2 border-t border-sage-200/50">
              <div className="flex items-center gap-2">
                <span className="flex items-center gap-1 font-semibold text-forest-800">
                  <ShieldCheck className="w-3.5 h-3.5 text-forest-700" />
                  {dailyTip.source === 'gemini'
                    ? 'Generated via Google Gemini 3.5 Flash'
                    : 'Curated by PlantPal Botanical Knowledge Engine'}
                </span>
                {apiError && (
                  <span className="text-amber-700 italic">
                    (Note: {apiError}. Serving curated botanical almanac)
                  </span>
                )}
              </div>

              <span className="text-sage-500 font-medium">
                Advice updated daily for {dateFormatted}
              </span>
            </div>
          </div>
        ) : null}
      </section>

      {/* Main Two Column Layout */}
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* Left Column: Today's Care Checklist & Daily Missions */}
        <div className="lg:col-span-7 space-y-6">
          {/* Today's Plant Care Tasks */}
          <div className="p-6 rounded-3xl bg-white border border-sage-300/40 shadow-sm space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h2 className="text-lg font-bold text-forest-900">Today's Plant Care</h2>
                <p className="text-xs text-sage-700">Check soil moisture before hydrating.</p>
              </div>
              <span className="px-2.5 py-0.5 rounded-full bg-forest-100 text-forest-700 text-xs font-bold">
                {activeTasks.length} pending
              </span>
            </div>

            {activeTasks.length === 0 ? (
              <div className="p-6 text-center rounded-2xl bg-forest-50 border border-forest-500/20 text-forest-700 text-xs font-semibold">
                🎉 All plant care tasks for today are completed! Great job nurturing your oasis.
              </div>
            ) : (
              <div className="space-y-3">
                {activeTasks.map((t) => (
                  <div
                    key={t.id}
                    className={`p-4 rounded-2xl border transition-all flex items-center justify-between gap-4 ${
                      t.urgent
                        ? 'bg-rose-50/50 border-rose-200'
                        : 'bg-white border-sage-200 hover:border-forest-500/30'
                    }`}
                  >
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-bold text-sm text-forest-900">{t.plant}</span>
                        <span
                          className={`text-[10px] font-extrabold px-2 py-0.5 rounded-md uppercase ${
                            t.urgent ? 'bg-rose-600 text-white' : 'bg-amber-100 text-amber-800'
                          }`}
                        >
                          {t.due}
                        </span>
                      </div>
                      <p className="text-xs text-sage-700">{t.details}</p>
                    </div>

                    <button
                      onClick={() => completeTask(t.id)}
                      className="px-3.5 py-2 rounded-xl bg-forest-700 hover:bg-forest-900 text-white text-xs font-bold shadow-sm flex items-center gap-1.5 transition-all flex-shrink-0"
                    >
                      <Check className="w-3.5 h-3.5" />
                      <span>Done</span>
                    </button>
                  </div>
                ))}
              </div>
            )}
          </div>

            {/* Daily Gardening Missions (XP Gamification) */}
          <div className="p-6 rounded-3xl bg-white border border-sage-300/40 shadow-sm space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h2 className="text-lg font-bold text-forest-900">Daily Missions & XP</h2>
                <p className="text-xs text-sage-700">Earn botanical badges & ceramic pot styles.</p>
              </div>
              <div className="flex items-center gap-1 text-xs font-extrabold text-amber-700 bg-amber-50 px-3 py-1 rounded-full border border-amber-200">
                <Sparkles className="w-3.5 h-3.5" />
                <span>{xp} Total XP</span>
              </div>
            </div>

            <div className="space-y-2.5">
              {missions.map((m) => (
                <div
                  key={m.id}
                  onClick={() => toggleMission(m.id)}
                  className={`p-3.5 rounded-xl border flex items-center justify-between cursor-pointer transition-all ${
                    m.done
                      ? 'bg-forest-50 border-forest-500/30 line-through text-sage-500'
                      : 'bg-white border-sage-200 hover:border-forest-500'
                  }`}
                >
                  <div className="flex items-center gap-3">
                    <div
                      className={`w-5 h-5 rounded-md flex items-center justify-center border transition-colors ${
                        m.done ? 'bg-forest-700 border-forest-700 text-white' : 'border-sage-400'
                      }`}
                    >
                      {m.done && <Check className="w-3.5 h-3.5" />}
                    </div>
                    <span className="text-xs font-semibold">{m.text}</span>
                  </div>
                  <span className="text-[11px] font-extrabold text-amber-700">+{m.xp} XP</span>
                </div>
              ))}
            </div>
            
            {/* Achievements Section */}
            <div className="pt-4 border-t border-sage-200">
               <h3 className="text-sm font-bold text-forest-900 mb-2">Unlocked Badges</h3>
               <div className="flex gap-2">
                 <div className="flex flex-col items-center gap-1 p-2 bg-amber-100 rounded-xl">
                   <Droplets className="w-6 h-6 text-amber-700" />
                   <span className="text-[10px] font-bold text-amber-900">Hydration Hero</span>
                 </div>
               </div>
            </div>
          </div>
        </div>

        {/* Right Column: 3D Plant Companion Digital Twin & Quick Hub */}
        <div className="lg:col-span-5 space-y-6">
          {/* 3D Living Companion Card */}
          <div className="p-6 rounded-3xl bg-white border border-sage-300/40 shadow-sm space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="font-bold text-forest-900 text-base">3D Living Digital Twin</h3>
                <p className="text-xs text-sage-700">Monstera Deliciosa (Healthy)</p>
              </div>
              <button
                onClick={() => onNavigateTab('plants')}
                className="text-xs font-bold text-forest-700 hover:underline"
              >
                Full Profile →
              </button>
            </div>

            <div className="w-full h-64 rounded-2xl bg-forest-50/60 border border-sage-200 overflow-hidden relative">
              <ThreeGardenScene potColor="#C86D51" leafScale={1.1} interactive={true} />
            </div>

            <div className="grid grid-cols-2 gap-2 text-center text-xs">
              <div className="p-2.5 rounded-xl bg-sage-100/60 font-semibold text-forest-900">
                <span className="text-[10px] text-sage-700 block">Height</span>
                <span>46.5 cm</span>
              </div>
              <div className="p-2.5 rounded-xl bg-sage-100/60 font-semibold text-forest-900">
                <span className="text-[10px] text-sage-700 block">Foliage Count</span>
                <span>8 Leaves</span>
              </div>
            </div>
          </div>

          {/* Growth Trend Visualizations */}
          <div className="p-6 rounded-3xl bg-white border border-sage-300/40 shadow-sm space-y-4">
            <h3 className="font-bold text-forest-900 text-base">Growth Trends</h3>
            <GrowthTrendsChart 
              weeklyData={[
                { name: 'W1', growth: 40 },
                { name: 'W2', growth: 42 },
                { name: 'W3', growth: 44 },
                { name: 'W4', growth: 46 }
              ]}
              monthlyData={[
                { name: 'Aug', growth: 10 },
                { name: 'Sep', growth: 25 },
                { name: 'Oct', growth: 46 }
              ]}
            />
          </div>

          {/* IoT Sensor Trends */}
          <div className="p-6 rounded-3xl bg-white border border-sage-300/40 shadow-sm space-y-4">
            <h3 className="font-bold text-forest-900 text-base">Soil Conditions (Live)</h3>
            <div className="text-xs text-sage-600">Monstera Pot Device (ESP32-A1)</div>
            <SensorTrendsChart 
              data={[
                { timestamp: '2026-10-21T08:00:00', soilMoisture: 48, temperatureC: 22 },
                { timestamp: '2026-10-21T12:00:00', soilMoisture: 45, temperatureC: 24 },
                { timestamp: '2026-10-21T16:00:00', soilMoisture: 42, temperatureC: 23 },
                { timestamp: '2026-10-21T20:00:00', soilMoisture: 40, temperatureC: 22 }
              ]}
            />
          </div>

          {/* Smart Modules Quick Shortcuts */}
          <div className="p-6 rounded-3xl bg-white border border-sage-300/40 shadow-sm space-y-3">
            <h3 className="font-bold text-forest-900 text-base">Gardening Ecosystem</h3>
            <div className="grid grid-cols-2 gap-3 text-xs font-bold">
              <button
                onClick={() => onNavigateTab('sensors')}
                className="p-3 rounded-xl bg-emerald-50 hover:bg-emerald-100/80 text-emerald-900 flex items-center gap-2 transition-all text-left"
              >
                <Cpu className="w-4 h-4 text-emerald-700 flex-shrink-0" />
                <span>Smart IoT Hub</span>
              </button>

              <button
                onClick={() => onNavigateTab('designer')}
                className="p-3 rounded-xl bg-amber-50 hover:bg-amber-100/80 text-amber-900 flex items-center gap-2 transition-all text-left"
              >
                <Palette className="w-4 h-4 text-amber-700 flex-shrink-0" />
                <span>Garden Designer</span>
              </button>

              <button
                onClick={() => onNavigateTab('community')}
                className="p-3 rounded-xl bg-purple-50 hover:bg-purple-100/80 text-purple-900 flex items-center gap-2 transition-all text-left"
              >
                <Sprout className="w-4 h-4 text-purple-700 flex-shrink-0" />
                <span>Community Posts</span>
              </button>

              <button
                onClick={() => onNavigateTab('budget')}
                className="p-3 rounded-xl bg-blue-50 hover:bg-blue-100/80 text-blue-900 flex items-center gap-2 transition-all text-left"
              >
                <Sparkles className="w-4 h-4 text-blue-700 flex-shrink-0" />
                <span>Garden Budget</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
