import React, { useState } from 'react';
import { Palette, Sparkles, Check, ArrowRight, Home, Sun, Compass } from 'lucide-react';

export const GardenDesignerPage: React.FC = () => {
  const [spaceType, setSpaceType] = useState('Balcony Terrace');
  const [sunlight, setSunlight] = useState('Bright Indirect (4-6 hrs)');
  const [style, setStyle] = useState('Modern Lush Oasis');
  const [budget, setBudget] = useState('$150');
  const [isGenerating, setIsGenerating] = useState(false);
  const [designResult, setDesignResult] = useState<any>(null);

  const handleGenerate = () => {
    setIsGenerating(true);
    setTimeout(() => {
      setIsGenerating(false);
      setDesignResult({
        title: `${style} for your ${spaceType}`,
        narrative:
          'A biophilic sanctuary combining architectural foliage, culinary herbs, and cascading vertical planters designed to maximize limited square footage.',
        plantPicks: [
          { name: 'Monstera Deliciosa', role: 'Architectural Corner Centerpiece', pot: '12" Terracotta' },
          { name: 'Sweet Genovese Basil', role: 'High-Sun Railing Planter Box', pot: 'Self-watering trough' },
          { name: 'Trailing English Ivy', role: 'Cascading Wall Trellis Accent', pot: 'Hanging macrame pot' }
        ],
        tips: [
          'Position larger pots in corners to anchor wind stability.',
          'Group herbs together on railing planters to catch maximum morning sunlight.',
          'Use perlite and orchid bark to ensure free drainage without heavy soil compaction.'
        ],
        materialsCost: budget
      });
    }, 1000);
  };

  return (
    <div className="max-w-4xl mx-auto px-4 lg:px-8 py-8 space-y-6">
      <div className="text-center max-w-xl mx-auto space-y-2">
        <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-amber-50 border border-amber-200 text-amber-800 text-xs font-bold">
          <Palette className="w-3.5 h-3.5" />
          <span>AI Garden Architecture</span>
        </div>
        <h1 className="text-3xl font-extrabold text-forest-900">AI Garden Designer</h1>
        <p className="text-xs text-sage-700">
          Transform your balcony, window sill, or backyard into a thriving botanical paradise.
        </p>
      </div>

      <div className="p-6 sm:p-8 rounded-3xl bg-white border border-sage-300 shadow-sm space-y-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <div>
            <label className="block text-xs font-bold text-forest-900 mb-2">Available Space</label>
            <select
              value={spaceType}
              onChange={(e) => setSpaceType(e.target.value)}
              className="w-full px-4 py-2.5 rounded-xl border border-sage-300 bg-white text-xs text-forest-900 focus:outline-none focus:ring-2 focus:ring-forest-700"
            >
              <option>Balcony Terrace (Small / Medium)</option>
              <option>Sunny Window Sill (Indoor)</option>
              <option>Backyard Raised Bed</option>
              <option>Rooftop Terrace</option>
              <option>Shaded Patio Corner</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold text-forest-900 mb-2">Sunlight Exposure</label>
            <select
              value={sunlight}
              onChange={(e) => setSunlight(e.target.value)}
              className="w-full px-4 py-2.5 rounded-xl border border-sage-300 bg-white text-xs text-forest-900 focus:outline-none focus:ring-2 focus:ring-forest-700"
            >
              <option>Bright Indirect (4-6 hrs filtered)</option>
              <option>Full Direct Sun (6+ hrs)</option>
              <option>Partial Shade (2-4 hrs gentle light)</option>
              <option>Low Light / North-Facing</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold text-forest-900 mb-2">Aesthetic Style</label>
            <select
              value={style}
              onChange={(e) => setStyle(e.target.value)}
              className="w-full px-4 py-2.5 rounded-xl border border-sage-300 bg-white text-xs text-forest-900 focus:outline-none focus:ring-2 focus:ring-forest-700"
            >
              <option>Modern Lush Oasis</option>
              <option>Culinary Kitchen Herb Garden</option>
              <option>Cottage Pollinator Blooms</option>
              <option>Minimalist Zen Succulent Corner</option>
            </select>
          </div>

          <div>
            <label className="block text-xs font-bold text-forest-900 mb-2">Estimated Budget</label>
            <input
              type="text"
              value={budget}
              onChange={(e) => setBudget(e.target.value)}
              placeholder="$100 - $200"
              className="w-full px-4 py-2.5 rounded-xl border border-sage-300 bg-white text-xs text-forest-900 focus:outline-none focus:ring-2 focus:ring-forest-700"
            />
          </div>
        </div>

        <button
          onClick={handleGenerate}
          disabled={isGenerating}
          className="w-full py-3.5 rounded-xl bg-forest-700 hover:bg-forest-900 text-white font-bold text-sm shadow-md transition-all flex items-center justify-center gap-2"
        >
          <Sparkles className="w-4 h-4 text-limeAccent" />
          <span>{isGenerating ? 'Synthesizing Garden Concept...' : 'Generate AI Garden Concept'}</span>
        </button>
      </div>

      {designResult && (
        <div className="p-6 sm:p-8 rounded-3xl bg-white border border-sage-300 shadow-sm space-y-6">
          <div className="border-b border-sage-200 pb-4">
            <span className="text-[11px] font-bold text-forest-700 uppercase tracking-wider">Concept Blueprint</span>
            <h2 className="text-xl font-extrabold text-forest-900 mt-1">{designResult.title}</h2>
            <p className="text-xs text-sage-700 mt-2 leading-relaxed">{designResult.narrative}</p>
          </div>

          <div>
            <h3 className="font-bold text-sm text-forest-900 mb-3">🌿 Recommended Companion Species</h3>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              {designResult.plantPicks.map((pick: any, idx: number) => (
                <div key={idx} className="p-4 rounded-2xl bg-sage-50 border border-sage-200/80 space-y-1">
                  <h4 className="font-bold text-xs text-forest-900">{pick.name}</h4>
                  <p className="text-[11px] text-sage-700">{pick.role}</p>
                  <span className="inline-block text-[10px] font-bold text-forest-700 bg-white px-2 py-0.5 rounded-md border border-sage-200">
                    Pot: {pick.pot}
                  </span>
                </div>
              ))}
            </div>
          </div>

          <div>
            <h3 className="font-bold text-sm text-forest-900 mb-2">📋 Arrangement & Layout Guidance</h3>
            <ul className="space-y-1.5 text-xs text-sage-800">
              {designResult.tips.map((tip: string, i: number) => (
                <li key={i} className="flex items-start gap-2">
                  <span className="text-forest-700 font-bold">•</span>
                  <span>{tip}</span>
                </li>
              ))}
            </ul>
          </div>
        </div>
      )}
    </div>
  );
};
