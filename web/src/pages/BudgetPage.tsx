import React, { useState } from 'react';
import {
  DollarSign,
  TrendingUp,
  PieChart,
  Plus,
  ShoppingBag,
  Sparkles,
  Calendar,
  Layers,
  ArrowUpRight,
  Lightbulb,
  CheckCircle2
} from 'lucide-react';

interface ExpenseItem {
  id: string;
  item: string;
  category: 'Plants' | 'Pots & Soil' | 'Fertilizers' | 'Tools & IoT';
  cost: number;
  date: string;
  store: string;
}

export const BudgetPage: React.FC = () => {
  const [budgetLimit, setBudgetLimit] = useState(250);
  const [expenses, setExpenses] = useState<ExpenseItem[]>([
    {
      id: 'e1',
      item: 'Variegated Monstera Albo Cutting',
      category: 'Plants',
      cost: 45.0,
      date: '2026-10-18',
      store: 'Local Botanical Nursery'
    },
    {
      id: 'e2',
      item: 'Terracotta Pots (Set of 3) + Organic Coir',
      category: 'Pots & Soil',
      cost: 28.5,
      date: '2026-10-12',
      store: 'Garden Craft Mart'
    },
    {
      id: 'e3',
      item: 'Cold-Pressed Seaweed & Humic Acid Feed',
      category: 'Fertilizers',
      cost: 19.99,
      date: '2026-10-05',
      store: 'EcoFlora Organics'
    },
    {
      id: 'e4',
      item: 'ESP32 Soil Probe + Solar Charger Pack',
      category: 'Tools & IoT',
      cost: 24.0,
      date: '2026-09-28',
      store: 'Maker IoT Supply'
    }
  ]);

  const [showAddModal, setShowAddModal] = useState(false);
  const [newItemName, setNewItemName] = useState('');
  const [newCategory, setNewCategory] = useState<'Plants' | 'Pots & Soil' | 'Fertilizers' | 'Tools & IoT'>('Plants');
  const [newCost, setNewCost] = useState('');
  const [newStore, setNewStore] = useState('');

  const totalSpent = expenses.reduce((acc, curr) => acc + curr.cost, 0);
  const percentSpent = Math.min(100, Math.round((totalSpent / budgetLimit) * 100));

  // Category totals
  const categoryTotals = expenses.reduce((acc, curr) => {
    acc[curr.category] = (acc[curr.category] || 0) + curr.cost;
    return acc;
  }, {} as Record<string, number>);

  const handleAddExpense = (e: React.FormEvent) => {
    e.preventDefault();
    const costNum = parseFloat(newCost);
    if (!newItemName.trim() || isNaN(costNum) || costNum <= 0) return;

    const newExp: ExpenseItem = {
      id: `e-${Date.now()}`,
      item: newItemName.trim(),
      category: newCategory,
      cost: costNum,
      date: new Date().toISOString().split('T')[0],
      store: newStore.trim() || 'Botanical Store'
    };

    setExpenses([newExp, ...expenses]);
    setShowAddModal(false);
    setNewItemName('');
    setNewCost('');
    setNewStore('');
  };

  return (
    <div className="max-w-6xl mx-auto px-4 lg:px-8 py-8 space-y-8">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white p-6 rounded-3xl border border-sage-300/40 shadow-sm">
        <div>
          <div className="inline-flex items-center gap-2 px-3 py-1 bg-sage-100 rounded-full text-forest-700 text-xs font-bold mb-2">
            <DollarSign className="w-3.5 h-3.5 text-forest-700" />
            <span>Green Investments & Cost Tracker</span>
          </div>
          <h1 className="text-2xl lg:text-3xl font-extrabold text-forest-900">
            Garden Budget & Purchases
          </h1>
          <p className="text-sm text-sage-700 mt-1">
            Keep plant hobbies rewarding and budget-conscious with category allocations.
          </p>
        </div>

        <button
          onClick={() => setShowAddModal(true)}
          className="px-5 py-2.5 rounded-2xl text-xs font-bold bg-forest-700 hover:bg-forest-900 text-white flex items-center gap-2 shadow-sm transition-all"
        >
          <Plus className="w-4 h-4" />
          <span>Log Purchase</span>
        </button>
      </div>

      {/* Budget Summary Metrics Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-5">
        <div className="bg-white p-6 rounded-3xl border border-sage-300/40 shadow-sm space-y-2">
          <span className="text-xs font-bold text-sage-500 uppercase tracking-wider">
            Total Season Spend
          </span>
          <div className="flex items-baseline gap-2">
            <span className="text-3xl font-black text-forest-900">
              ${totalSpent.toFixed(2)}
            </span>
            <span className="text-xs font-bold text-forest-700">
              of ${budgetLimit} target
            </span>
          </div>
          <div className="w-full bg-sage-100 h-2.5 rounded-full overflow-hidden mt-2">
            <div
              className={`h-full rounded-full transition-all duration-500 ${
                percentSpent > 90 ? 'bg-amber-500' : 'bg-forest-700'
              }`}
              style={{ width: `${percentSpent}%` }}
            />
          </div>
          <span className="text-[11px] text-sage-600 font-semibold block">
            {percentSpent}% budget consumed ({budgetLimit - totalSpent > 0 ? `$${(budgetLimit - totalSpent).toFixed(2)} remaining` : 'Limit reached'})
          </span>
        </div>

        <div className="bg-white p-6 rounded-3xl border border-sage-300/40 shadow-sm space-y-2">
          <span className="text-xs font-bold text-sage-500 uppercase tracking-wider">
            Top Category
          </span>
          <div className="flex items-baseline gap-2">
            <span className="text-3xl font-black text-forest-900">
              Plants & Cuttings
            </span>
          </div>
          <p className="text-xs text-sage-600 font-medium">
            Accounted for ${(categoryTotals['Plants'] || 0).toFixed(2)} of total expenditures
          </p>
        </div>

        <div className="bg-emerald-50/70 p-6 rounded-3xl border border-emerald-200/80 shadow-sm space-y-2">
          <div className="flex items-center gap-2 text-emerald-800 font-bold text-xs uppercase tracking-wider">
            <Lightbulb className="w-4 h-4 text-emerald-600" />
            <span>Propagation ROI</span>
          </div>
          <span className="text-3xl font-black text-emerald-950">+$120 Saved</span>
          <p className="text-xs text-emerald-800 font-medium leading-relaxed">
            Propagating 4 cuttings at home saved an estimated $120 compared to nursery purchase prices!
          </p>
        </div>
      </div>

      {/* Breakdown and Expense Table */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Category Breakdown */}
        <div className="bg-white p-6 rounded-3xl border border-sage-300/40 shadow-sm space-y-4">
          <h2 className="text-base font-bold text-forest-900">Spend by Category</h2>
          <div className="space-y-3">
            {[
              { cat: 'Plants', label: 'Plants & Cuttings', color: 'bg-emerald-600' },
              { cat: 'Pots & Soil', label: 'Soil, Coir & Ceramic Pots', color: 'bg-amber-600' },
              { cat: 'Fertilizers', label: 'Organic Nutrients & Feeds', color: 'bg-limeAccent' },
              { cat: 'Tools & IoT', label: 'IoT Sensors & Pruners', color: 'bg-teal-600' }
            ].map(({ cat, label, color }) => {
              const amount = categoryTotals[cat] || 0;
              const ratio = totalSpent > 0 ? (amount / totalSpent) * 100 : 0;
              return (
                <div key={cat} className="space-y-1.5">
                  <div className="flex items-center justify-between text-xs font-bold">
                    <span className="text-forest-900">{label}</span>
                    <span className="text-sage-700">${amount.toFixed(2)}</span>
                  </div>
                  <div className="w-full bg-sage-100 h-2 rounded-full overflow-hidden">
                    <div
                      className={`${color} h-full rounded-full transition-all`}
                      style={{ width: `${ratio}%` }}
                    />
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* Expenses List */}
        <div className="lg:col-span-2 bg-white p-6 rounded-3xl border border-sage-300/40 shadow-sm space-y-4">
          <div className="flex items-center justify-between">
            <h2 className="text-base font-bold text-forest-900">
              Recent Transactions ({expenses.length})
            </h2>
            <span className="text-xs text-sage-500 font-medium">Sorted by latest</span>
          </div>

          <div className="space-y-3">
            {expenses.map((item) => (
              <div
                key={item.id}
                className="flex items-center justify-between p-4 rounded-2xl border border-sage-200/60 hover:bg-sage-50/50 transition-colors"
              >
                <div className="flex items-center gap-3">
                  <div className="w-10 h-10 rounded-xl bg-forest-100 flex items-center justify-center text-forest-700 font-bold">
                    <ShoppingBag className="w-5 h-5" />
                  </div>
                  <div>
                    <h3 className="text-sm font-bold text-forest-900">{item.item}</h3>
                    <div className="flex items-center gap-2 text-xs text-sage-600 mt-0.5">
                      <span className="font-semibold text-forest-800">{item.category}</span>
                      <span>•</span>
                      <span>{item.store}</span>
                      <span>•</span>
                      <span>{item.date}</span>
                    </div>
                  </div>
                </div>

                <div className="text-right">
                  <span className="text-base font-black text-forest-900">
                    -${item.cost.toFixed(2)}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Add Modal */}
      {showAddModal && (
        <div className="fixed inset-0 z-50 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-md w-full p-6 border border-sage-300 shadow-2xl space-y-5">
            <div className="flex items-center justify-between border-b border-sage-200 pb-3">
              <div className="flex items-center gap-2">
                <DollarSign className="w-5 h-5 text-forest-700" />
                <h3 className="text-lg font-bold text-forest-900">Log Garden Expense</h3>
              </div>
              <button
                onClick={() => setShowAddModal(false)}
                className="text-sage-500 hover:text-forest-900 text-sm font-bold"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handleAddExpense} className="space-y-4">
              <div>
                <label className="text-xs font-bold text-forest-900 block mb-1">
                  Item Description
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. 5-Gallon Fabric Grow Bags (5-Pack)"
                  value={newItemName}
                  onChange={(e) => setNewItemName(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-sage-300 text-sm focus:outline-none focus:ring-2 focus:ring-forest-600"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-bold text-forest-900 block mb-1">
                    Cost ($ USD)
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    required
                    placeholder="18.50"
                    value={newCost}
                    onChange={(e) => setNewCost(e.target.value)}
                    className="w-full px-3.5 py-2.5 rounded-xl border border-sage-300 text-sm focus:outline-none focus:ring-2 focus:ring-forest-600"
                  />
                </div>

                <div>
                  <label className="text-xs font-bold text-forest-900 block mb-1">
                    Category
                  </label>
                  <select
                    value={newCategory}
                    onChange={(e) => setNewCategory(e.target.value as any)}
                    className="w-full px-3.5 py-2.5 rounded-xl border border-sage-300 text-sm focus:outline-none focus:ring-2 focus:ring-forest-600 bg-white"
                  >
                    <option value="Plants">Plants & Cuttings</option>
                    <option value="Pots & Soil">Pots & Soil</option>
                    <option value="Fertilizers">Fertilizers</option>
                    <option value="Tools & IoT">Tools & IoT</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="text-xs font-bold text-forest-900 block mb-1">
                  Vendor / Nursery
                </label>
                <input
                  type="text"
                  placeholder="e.g. Local Nursery, Home Depot, Amazon"
                  value={newStore}
                  onChange={(e) => setNewStore(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-sage-300 text-sm focus:outline-none focus:ring-2 focus:ring-forest-600"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowAddModal(false)}
                  className="px-4 py-2 rounded-xl text-xs font-bold text-sage-700 hover:bg-sage-100"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl text-xs font-bold bg-forest-700 hover:bg-forest-900 text-white shadow-md transition-all"
                >
                  Save Expense
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
