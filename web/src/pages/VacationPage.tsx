import React, { useState } from 'react';
import {
  Luggage,
  Calendar,
  CheckCircle2,
  AlertCircle,
  FileText,
  Share2,
  Droplets,
  Sun,
  Shield,
  HeartHandshake,
  Download,
  Info
} from 'lucide-react';

export const VacationPage: React.FC = () => {
  const [departureDate, setDepartureDate] = useState('2026-10-25');
  const [returnDate, setReturnDate] = useState('2026-11-02');
  const [caretakerName, setCaretakerName] = useState('Alex Morgan (Neighbor)');
  const [vacationModeActive, setVacationModeActive] = useState(false);
  const [checklist, setChecklist] = useState([
    { id: 'c1', task: 'Deep soak Monstera and Basil 24 hours prior to departure', done: true },
    { id: 'c2', task: 'Move windowsill plants 3 feet back from direct afternoon glare', done: true },
    { id: 'c3', task: 'Group tropical plants together to create a micro-humidity island', done: false },
    { id: 'c4', task: 'Fill cotton wick reservoir for balcony Roma Tomato', done: false },
    { id: 'c5', task: 'Leave key and printed plant care instructions on kitchen counter', done: false }
  ]);

  const daysAway = Math.max(
    1,
    Math.round(
      (new Date(returnDate).getTime() - new Date(departureDate).getTime()) /
        (1000 * 60 * 60 * 24)
    )
  );

  const toggleCheck = (id: string) => {
    setChecklist(prev =>
      prev.map(item => (item.id === id ? { ...item, done: !item.done } : item))
    );
  };

  return (
    <div className="max-w-6xl mx-auto px-4 lg:px-8 py-8 space-y-8">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white p-6 rounded-3xl border border-sage-300/40 shadow-sm">
        <div>
          <div className="inline-flex items-center gap-2 px-3 py-1 bg-sage-100 rounded-full text-forest-700 text-xs font-bold mb-2">
            <Luggage className="w-3.5 h-3.5 text-forest-700" />
            <span>Travel & Absence Protection</span>
          </div>
          <h1 className="text-2xl lg:text-3xl font-extrabold text-forest-900">
            Vacation Care & Plant Sitter
          </h1>
          <p className="text-sm text-sage-700 mt-1">
            Generate simple caretaker instructions and automatic wick-watering plans for carefree travels.
          </p>
        </div>

        <button
          onClick={() => setVacationModeActive(!vacationModeActive)}
          className={`px-5 py-2.5 rounded-2xl text-xs font-bold flex items-center gap-2 shadow-sm transition-all ${
            vacationModeActive
              ? 'bg-amber-600 hover:bg-amber-700 text-white'
              : 'bg-forest-700 hover:bg-forest-900 text-white'
          }`}
        >
          <Shield className="w-4 h-4" />
          <span>{vacationModeActive ? 'Vacation Mode: Active' : 'Arm Vacation Mode'}</span>
        </button>
      </div>

      {/* Vacation Date & Caretaker Banner */}
      <div className="bg-gradient-to-br from-forest-800 to-forest-950 text-cream p-6 lg:p-8 rounded-3xl shadow-md space-y-6">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div className="space-y-1">
            <span className="text-xs font-bold text-limeAccent tracking-wider uppercase">
              Travel Duration
            </span>
            <h2 className="text-2xl font-black text-white">
              {daysAway} Days Away ({departureDate} to {returnDate})
            </h2>
            <p className="text-xs text-sage-300">
              Assigned Sitter: <strong className="text-white">{caretakerName}</strong>
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <div className="bg-white/10 backdrop-blur-md px-4 py-2.5 rounded-2xl border border-white/10 text-xs">
              <span className="text-sage-300 block text-[10px]">Dep. Date</span>
              <input
                type="date"
                value={departureDate}
                onChange={(e) => setDepartureDate(e.target.value)}
                className="bg-transparent text-white font-bold focus:outline-none"
              />
            </div>

            <div className="bg-white/10 backdrop-blur-md px-4 py-2.5 rounded-2xl border border-white/10 text-xs">
              <span className="text-sage-300 block text-[10px]">Ret. Date</span>
              <input
                type="date"
                value={returnDate}
                onChange={(e) => setReturnDate(e.target.value)}
                className="bg-transparent text-white font-bold focus:outline-none"
              />
            </div>
          </div>
        </div>

        {/* Risk Assessment Badges */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-3 pt-2">
          <div className="bg-black/20 p-3.5 rounded-2xl border border-white/10">
            <span className="text-[11px] text-sage-300 font-bold block">Resilient Plants</span>
            <span className="text-base font-black text-emerald-300">
              Snake Plant & ZZ Plant
            </span>
            <p className="text-[10px] text-sage-300 mt-0.5">Zero watering required for {daysAway} days</p>
          </div>

          <div className="bg-black/20 p-3.5 rounded-2xl border border-white/10">
            <span className="text-[11px] text-sage-300 font-bold block">Moderate Risk</span>
            <span className="text-base font-black text-amber-300">
              Monstera Deliciosa
            </span>
            <p className="text-[10px] text-sage-300 mt-0.5">Needs 1 gentle hydration on Day 5</p>
          </div>

          <div className="bg-black/20 p-3.5 rounded-2xl border border-white/10">
            <span className="text-[11px] text-sage-300 font-bold block">High Need</span>
            <span className="text-base font-black text-rose-300">
              Sweet Basil & Roma Tomato
            </span>
            <p className="text-[10px] text-sage-300 mt-0.5">Requires wick reservoir or Day 3 visit</p>
          </div>
        </div>
      </div>

      {/* Split layout: Pre-Departure Checklist & Caretaker Cheat Sheet */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Pre-Departure Checklist */}
        <div className="bg-white p-6 lg:p-7 rounded-3xl border border-sage-300/40 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-base font-bold text-forest-900">
              Pre-Departure Readiness Checklist
            </h2>
            <span className="text-xs font-bold text-sage-500">
              {checklist.filter(c => c.done).length}/{checklist.length} Completed
            </span>
          </div>

          <div className="space-y-3">
            {checklist.map((item) => (
              <div
                key={item.id}
                onClick={() => toggleCheck(item.id)}
                className={`p-3.5 rounded-2xl border transition-all cursor-pointer flex items-start gap-3 ${
                  item.done
                    ? 'bg-emerald-50/50 border-emerald-200 text-forest-900'
                    : 'bg-white border-sage-200 hover:border-sage-300 text-sage-800'
                }`}
              >
                <div
                  className={`mt-0.5 w-5 h-5 rounded-lg flex items-center justify-center border transition-colors ${
                    item.done
                      ? 'bg-emerald-600 border-emerald-600 text-white'
                      : 'border-sage-400 bg-white'
                  }`}
                >
                  {item.done && <CheckCircle2 className="w-4 h-4" />}
                </div>
                <span className={`text-xs font-semibold ${item.done ? 'line-through text-sage-500' : ''}`}>
                  {item.task}
                </span>
              </div>
            ))}
          </div>

          {/* Low-tech DIY Hacks */}
          <div className="p-4 bg-sage-50 rounded-2xl border border-sage-200/70 text-xs space-y-2">
            <div className="flex items-center gap-2 font-bold text-forest-900">
              <Droplets className="w-4 h-4 text-forest-700" />
              <span>DIY Capillary Wick Irrigation</span>
            </div>
            <p className="text-sage-700 leading-relaxed">
              Place a 1-gallon water pitcher slightly elevated next to pots. Submerge one end of a thick cotton cord in the water, and bury the other end 2 inches into the soil. Capillary tension will deliver steady passive moisture for up to 10 days!
            </p>
          </div>
        </div>

        {/* Printable Caretaker Cheat Sheet */}
        <div className="bg-white p-6 lg:p-7 rounded-3xl border border-sage-300/40 shadow-sm space-y-4">
          <div className="flex items-center justify-between border-b border-sage-200 pb-3">
            <div>
              <h2 className="text-base font-bold text-forest-900">
                Plant Sitter Handover Guide
              </h2>
              <p className="text-xs text-sage-600">
                Simple, stress-free rules for your neighbor or caretaker.
              </p>
            </div>
            <button
              onClick={() => window.print()}
              className="flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-sage-100 hover:bg-sage-200 text-forest-800 text-xs font-bold transition-all"
            >
              <Download className="w-3.5 h-3.5" />
              <span>Print Sheet</span>
            </button>
          </div>

          <div className="space-y-3">
            <div className="p-3.5 bg-rose-50/70 rounded-2xl border border-rose-200/70 space-y-1">
              <span className="text-[10px] uppercase font-bold text-rose-800 tracking-wider">
                Rule #1: The Golden Rule
              </span>
              <p className="text-xs font-bold text-rose-950">
                When in doubt, DO NOT water. Underwatering is easily rescued; overwatering rots roots.
              </p>
            </div>

            <div className="space-y-2 text-xs">
              <div className="p-3 bg-white rounded-xl border border-sage-200 space-y-1">
                <span className="font-bold text-forest-900">🪴 Monstera (Living Room)</span>
                <p className="text-sage-700">
                  Stick finger 2 inches into soil on Wednesday. If dry: pour 1 measuring cup of water. If damp: leave untouched.
                </p>
              </div>

              <div className="p-3 bg-white rounded-xl border border-sage-200 space-y-1">
                <span className="font-bold text-forest-900">🌱 Snake Plant (Desk)</span>
                <p className="text-sage-700">
                  Strictly DO NOT WATER under any circumstance during this trip. It thrives in desert conditions.
                </p>
              </div>

              <div className="p-3 bg-white rounded-xl border border-sage-200 space-y-1">
                <span className="font-bold text-forest-900">🍅 Balcony Herbs & Tomato</span>
                <p className="text-sage-700">
                  Top off the water jar connected to the capillary wick if water level drops below the half mark.
                </p>
              </div>
            </div>

            <div className="p-3.5 bg-amber-50 rounded-2xl border border-amber-200/70 flex items-start gap-2 text-xs text-amber-900">
              <Info className="w-4 h-4 text-amber-700 shrink-0 mt-0.5" />
              <span>
                Emergency Contact: Call or text my cell anytime if a plant looks sad!
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
