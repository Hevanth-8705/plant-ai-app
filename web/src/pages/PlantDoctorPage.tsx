import React, { useState } from 'react';
import { Activity, Sparkles, Upload, Check, AlertTriangle, ShieldCheck, RefreshCw } from 'lucide-react';

export const PlantDoctorPage: React.FC = () => {
  const [plantName, setPlantName] = useState('Monstera Deliciosa');
  const [selectedSymptoms, setSelectedSymptoms] = useState<string[]>([
    'Yellowing lower leaves'
  ]);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [report, setReport] = useState<any>(null);

  const symptomOptions = [
    'Yellowing lower leaves',
    'Brown crispy leaf tips',
    'White powdery spots',
    'Fine webbing on underside',
    'Dark spots with yellow halos',
    'Drooping or wilting stems',
    'Holes or notched leaf margins',
    'Soft mushy base'
  ];

  const toggleSymptom = (sym: string) => {
    if (selectedSymptoms.includes(sym)) {
      setSelectedSymptoms(selectedSymptoms.filter((s) => s !== sym));
    } else {
      setSelectedSymptoms([...selectedSymptoms, sym]);
    }
  };

  const handleDiagnose = () => {
    setIsAnalyzing(true);
    setTimeout(() => {
      setIsAnalyzing(false);
      setReport({
        diagnosis: 'Root Moisture Inconsistency & Environmental Stress',
        severity: 'Moderate',
        causes: [
          'Compacted soil retaining excess moisture around root system',
          'Watering on fixed intervals rather than moisture testing'
        ],
        environmentalDrivers:
          'Relative indoor humidity below 45% with stagnant air flow around dense lower foliage.',
        remedies: [
          'Allow the top 2 inches of soil to completely dry before adding water',
          'Ensure container drainage holes are clear of mineral salt blockage',
          'Prune dead or yellowed leaves with sterilized shears to redirect energy'
        ],
        prevention: [
          'Perform finger moisture probe check twice weekly',
          'Provide bright indirect sunlight (avoid direct midday scorch)'
        ],
        needsExpert: false
      });
    }, 1200);
  };

  return (
    <div className="max-w-4xl mx-auto px-4 lg:px-8 py-8 space-y-6">
      <div className="text-center max-w-xl mx-auto space-y-2">
        <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-rose-50 border border-rose-200 text-rose-700 text-xs font-bold">
          <Activity className="w-3.5 h-3.5" />
          <span>Multimodal Botanical AI</span>
        </div>
        <h1 className="text-3xl font-extrabold text-forest-900">AI Plant Doctor</h1>
        <p className="text-xs text-sage-700">
          Analyze sick leaves, identify fungal and pest symptoms, and receive safe organic treatments.
        </p>
      </div>

      {!report ? (
        <div className="p-6 sm:p-8 rounded-3xl bg-white border border-sage-300 shadow-sm space-y-6">
          <div>
            <label className="block text-xs font-bold text-forest-900 mb-2">1. Select Affected Plant</label>
            <input
              type="text"
              value={plantName}
              onChange={(e) => setPlantName(e.target.value)}
              className="w-full px-4 py-2.5 rounded-xl border border-sage-300 bg-white text-sm text-forest-900 focus:outline-none focus:ring-2 focus:ring-forest-700"
              placeholder="e.g. Monstera, Sweet Basil, Roma Tomato..."
            />
          </div>

          <div>
            <label className="block text-xs font-bold text-forest-900 mb-2">2. Upload Leaf Photograph</label>
            <div className="border-2 border-dashed border-sage-300 hover:border-forest-700 rounded-2xl p-6 text-center cursor-pointer bg-forest-50/40 transition-colors">
              <Upload className="w-8 h-8 text-forest-700 mx-auto mb-2" />
              <p className="text-xs font-bold text-forest-900">Drag & drop or tap to select image</p>
              <p className="text-[11px] text-sage-600 mt-1">Clear close-up of leaf or stem (PNG, JPG)</p>
            </div>
          </div>

          <div>
            <label className="block text-xs font-bold text-forest-900 mb-2">
              3. Check Observed Symptoms
            </label>
            <div className="flex flex-wrap gap-2">
              {symptomOptions.map((sym) => {
                const isSelected = selectedSymptoms.includes(sym);
                return (
                  <button
                    key={sym}
                    type="button"
                    onClick={() => toggleSymptom(sym)}
                    className={`px-3 py-1.5 rounded-xl text-xs font-bold transition-all flex items-center gap-1.5 ${
                      isSelected
                        ? 'bg-forest-700 text-white shadow-sm'
                        : 'bg-sage-100 text-sage-700 hover:bg-sage-200'
                    }`}
                  >
                    {isSelected && <Check className="w-3 h-3" />}
                    <span>{sym}</span>
                  </button>
                );
              })}
            </div>
          </div>

          <button
            onClick={handleDiagnose}
            disabled={isAnalyzing}
            className="w-full py-3.5 rounded-xl bg-forest-700 hover:bg-forest-900 text-white font-bold text-sm shadow-md transition-all flex items-center justify-center gap-2"
          >
            {isAnalyzing ? (
              <>
                <RefreshCw className="w-4 h-4 animate-spin" />
                <span>Screening Foliage...</span>
              </>
            ) : (
              <>
                <Sparkles className="w-4 h-4 text-limeAccent" />
                <span>Run AI Health Diagnosis</span>
              </>
            )}
          </button>
        </div>
      ) : (
        <div className="p-6 sm:p-8 rounded-3xl bg-white border border-sage-300 shadow-sm space-y-6">
          <div className="flex items-start justify-between border-b border-sage-200 pb-4">
            <div>
              <span className="text-[11px] font-bold text-sage-600 uppercase tracking-wider">Diagnosis</span>
              <h2 className="text-xl font-extrabold text-forest-900 mt-0.5">{report.diagnosis}</h2>
            </div>
            <span className="px-3 py-1 rounded-full bg-amber-100 text-amber-800 text-xs font-extrabold">
              Severity: {report.severity}
            </span>
          </div>

          <div className="p-4 rounded-2xl bg-sage-50 border border-sage-200/60 text-xs text-sage-800">
            <span className="font-bold text-forest-900 block mb-1">Environmental Drivers:</span>
            {report.environmentalDrivers}
          </div>

          <div>
            <h3 className="font-bold text-sm text-forest-900 mb-2">🌿 Recommended Organic Remedies</h3>
            <ul className="space-y-1.5 text-xs text-sage-800">
              {report.remedies.map((rem: string, i: number) => (
                <li key={i} className="flex items-start gap-2">
                  <span className="text-forest-700 font-bold">•</span>
                  <span>{rem}</span>
                </li>
              ))}
            </ul>
          </div>

          <div>
            <h3 className="font-bold text-sm text-forest-900 mb-2">🛡️ Long-Term Prevention Tips</h3>
            <ul className="space-y-1.5 text-xs text-sage-800">
              {report.prevention.map((tip: string, i: number) => (
                <li key={i} className="flex items-start gap-2">
                  <span className="text-forest-700 font-bold">•</span>
                  <span>{tip}</span>
                </li>
              ))}
            </ul>
          </div>

          <button
            onClick={() => setReport(null)}
            className="w-full py-3 rounded-xl bg-forest-700 hover:bg-forest-900 text-white font-bold text-xs shadow-md transition-all"
          >
            Start Another Scan
          </button>
        </div>
      )}
    </div>
  );
};
