import React, { useState } from 'react';
import { Search, Plus, Droplets, Sparkles, Filter, Calendar, MapPin, Check } from 'lucide-react';
import { ThreeGardenScene } from '../components/ThreeGardenScene';

interface Plant {
  id: string;
  name: string;
  species: string;
  category: string;
  isIndoor: boolean;
  wateringInterval: number;
  daysUntilWater: number;
  potColor: string;
  notes: string;
}

export const MyPlantsPage: React.FC<{ onOpenAddPlant: () => void }> = ({ onOpenAddPlant }) => {
  const [search, setSearch] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('ALL');
  const [inspectingPlant, setInspectingPlant] = useState<Plant | null>(null);

  const [plants, setPlants] = useState<Plant[]>([
    {
      id: 'p1',
      name: 'Monstera Deliciosa',
      species: 'Monstera deliciosa (Swiss Cheese)',
      category: 'INDOOR',
      isIndoor: true,
      wateringInterval: 7,
      daysUntilWater: -1, // Overdue
      potColor: '#C86D51',
      notes: 'Loves bright indirect sun and moderate humidity. Wipe broad fenestrated leaves monthly.'
    },
    {
      id: 'p2',
      name: 'Snake Plant',
      species: 'Sansevieria trifasciata',
      category: 'SUCCULENT',
      isIndoor: true,
      wateringInterval: 14,
      daysUntilWater: 3,
      potColor: '#E8E8E0',
      notes: 'Extremely drought tolerant. Allow soil to dry completely between cycles.'
    },
    {
      id: 'p3',
      name: 'Sweet Genovese Basil',
      species: 'Ocimum basilicum',
      category: 'HERB',
      isIndoor: false,
      wateringInterval: 2,
      daysUntilWater: 0, // Due today
      potColor: '#4A6450',
      notes: 'Pinch top shoots for bushy growth. Needs 6+ hours of full sun.'
    },
    {
      id: 'p4',
      name: 'Roma Tomato',
      species: 'Solanum lycopersicum',
      category: 'VEGETABLE',
      isIndoor: false,
      wateringInterval: 2,
      daysUntilWater: 1,
      potColor: '#D99B26',
      notes: 'Water consistently at soil base to protect against blossom end rot.'
    },
    {
      id: 'p5',
      name: 'Peace Lily',
      species: 'Spathiphyllum wallisii',
      category: 'FLOWER',
      isIndoor: true,
      wateringInterval: 5,
      daysUntilWater: 2,
      potColor: '#1E4620',
      notes: 'Leaves gracefully droop to signal thirst. Thrives in low to partial light.'
    }
  ]);

  const categories = ['ALL', 'INDOOR', 'OUTDOOR', 'VEGETABLE', 'HERB', 'FLOWER', 'SUCCULENT'];

  const filtered = plants.filter((p) => {
    const matchesSearch =
      p.name.toLowerCase().includes(search.toLowerCase()) ||
      p.species.toLowerCase().includes(search.toLowerCase());
    const matchesCat = selectedCategory === 'ALL' || p.category === selectedCategory;
    return matchesSearch && matchesCat;
  });

  const handleWater = (id: string) => {
    setPlants(
      plants.map((p) =>
        p.id === id ? { ...p, daysUntilWater: p.wateringInterval } : p
      )
    );
  };

  return (
    <div className="max-w-7xl mx-auto px-4 lg:px-8 py-8 space-y-6">
      {/* Top Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-forest-900">
            Garden Library ({plants.length} Plants)
          </h1>
          <p className="text-xs text-sage-700 mt-1">
            Track individual species profiles, moisture countdowns, and 3D visual companions.
          </p>
        </div>

        <button
          onClick={onOpenAddPlant}
          className="px-4 py-2.5 rounded-xl bg-forest-700 hover:bg-forest-900 text-white font-bold text-xs shadow-md transition-all flex items-center gap-2"
        >
          <Plus className="w-4 h-4" />
          <span>Add New Plant</span>
        </button>
      </div>

      {/* Search & Filters */}
      <div className="flex flex-col sm:flex-row gap-3">
        <div className="relative flex-1">
          <Search className="absolute left-3.5 top-3 w-4 h-4 text-sage-500" />
          <input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search plants by name or botanical variety..."
            className="w-full pl-10 pr-4 py-2.5 rounded-xl border border-sage-300 bg-white text-xs sm:text-sm text-forest-900 focus:outline-none focus:ring-2 focus:ring-forest-700 shadow-sm"
          />
        </div>

        <div className="flex items-center gap-1.5 overflow-x-auto pb-1 sm:pb-0">
          {categories.map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-3 py-2 rounded-xl text-xs font-bold whitespace-nowrap transition-all ${
                selectedCategory === cat
                  ? 'bg-forest-700 text-white shadow-sm'
                  : 'bg-white border border-sage-300/60 text-sage-700 hover:bg-sage-50'
              }`}
            >
              {cat}
            </button>
          ))}
        </div>
      </div>

      {/* Plants Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {filtered.map((plant) => (
          <div
            key={plant.id}
            className="p-5 rounded-2xl bg-white border border-sage-300/50 shadow-sm hover:shadow-md transition-all flex flex-col justify-between"
          >
            <div>
              <div className="flex items-start justify-between mb-3">
                <div className="flex items-center gap-3">
                  <div
                    className="w-12 h-12 rounded-xl flex items-center justify-center text-white text-xl font-bold shadow-sm"
                    style={{ backgroundColor: plant.potColor }}
                  >
                    🌱
                  </div>
                  <div>
                    <h3 className="font-bold text-sm text-forest-900">{plant.name}</h3>
                    <p className="text-[11px] text-sage-600 italic">{plant.species}</p>
                  </div>
                </div>

                <span
                  className={`text-[10px] font-extrabold px-2 py-0.5 rounded-md uppercase ${
                    plant.daysUntilWater < 0
                      ? 'bg-rose-100 text-rose-800'
                      : plant.daysUntilWater === 0
                      ? 'bg-amber-100 text-amber-800'
                      : 'bg-sky-100 text-sky-800'
                  }`}
                >
                  {plant.daysUntilWater < 0
                    ? `Overdue by ${Math.abs(plant.daysUntilWater)}d`
                    : plant.daysUntilWater === 0
                    ? 'Due Today'
                    : `In ${plant.daysUntilWater}d`}
                </span>
              </div>

              <p className="text-xs text-sage-700 line-clamp-2 mt-2 leading-relaxed">
                {plant.notes}
              </p>
            </div>

            <div className="mt-5 pt-3 border-t border-sage-100 flex items-center justify-between">
              <button
                onClick={() => setInspectingPlant(plant)}
                className="text-xs font-bold text-forest-700 hover:text-forest-900 flex items-center gap-1"
              >
                <span>3D Companion</span>
                <span>→</span>
              </button>

              <button
                onClick={() => handleWater(plant.id)}
                className="px-3 py-1.5 rounded-lg bg-sky-50 hover:bg-sky-100 text-sky-700 text-xs font-bold flex items-center gap-1.5 transition-colors"
              >
                <Droplets className="w-3.5 h-3.5" />
                <span>Mark Watered</span>
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* 3D Plant Companion Modal */}
      {inspectingPlant && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
          <div className="w-full max-w-lg bg-[#fbfbf6] rounded-3xl p-6 shadow-2xl border border-sage-300 space-y-4">
            <div className="flex items-center justify-between border-b border-sage-200 pb-3">
              <div>
                <h3 className="font-extrabold text-lg text-forest-900">
                  {inspectingPlant.name}
                </h3>
                <p className="text-xs text-sage-600">{inspectingPlant.species}</p>
              </div>
              <button
                onClick={() => setInspectingPlant(null)}
                className="text-sage-500 hover:text-forest-900 font-bold"
              >
                ✕
              </button>
            </div>

            <div className="w-full h-72 rounded-2xl bg-white border border-sage-200 overflow-hidden relative">
              <ThreeGardenScene
                potColor={inspectingPlant.potColor}
                leafScale={1.1}
                interactive={true}
              />
            </div>

            <div className="text-xs text-sage-700 leading-relaxed bg-sage-100/50 p-3 rounded-xl">
              {inspectingPlant.notes}
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <button
                onClick={() => setInspectingPlant(null)}
                className="px-4 py-2 rounded-xl bg-forest-700 text-white font-bold text-xs"
              >
                Close 3D View
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
