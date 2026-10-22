import React, { useState, useEffect } from 'react';
import {
  Cpu,
  Wifi,
  Droplets,
  Thermometer,
  Sun,
  BatteryCharging,
  RefreshCw,
  Plus,
  AlertTriangle,
  CheckCircle,
  Sliders,
  Radio,
  Clock,
  Sparkles
} from 'lucide-react';

interface SensorNode {
  id: string;
  name: string;
  hardware: string;
  linkedPlant: string;
  status: 'online' | 'warning' | 'offline';
  battery: number;
  lastUpdated: string;
  readings: {
    moisture: number; // percentage
    temperature: number; // Celsius
    humidity: number; // percentage
    lightLux: number; // lux
  };
  thresholds: {
    moistureMin: number;
    moistureMax: number;
    tempMin: number;
    tempMax: number;
  };
}

export const IoTSensorsPage: React.FC = () => {
  const [sensors, setSensors] = useState<SensorNode[]>([
    {
      id: 'node-01',
      name: 'Living Room Node (ESP32-S3)',
      hardware: 'Capacitive Soil v2.0 + SHT30 + BH1750',
      linkedPlant: 'Monstera Deliciosa',
      status: 'online',
      battery: 88,
      lastUpdated: '12 seconds ago',
      readings: {
        moisture: 38,
        temperature: 22.4,
        humidity: 58,
        lightLux: 3200
      },
      thresholds: {
        moistureMin: 30,
        moistureMax: 70,
        tempMin: 18,
        tempMax: 28
      }
    },
    {
      id: 'node-02',
      name: 'Balcony Solar Node (ESP8266)',
      hardware: 'Resistive Probe + DS18B20 + LDR',
      linkedPlant: 'Roma Tomato & Basil',
      status: 'warning',
      battery: 64,
      lastUpdated: '45 seconds ago',
      readings: {
        moisture: 22, // Low!
        temperature: 28.1,
        humidity: 42,
        lightLux: 18500
      },
      thresholds: {
        moistureMin: 35,
        moistureMax: 80,
        tempMin: 15,
        tempMax: 32
      }
    },
    {
      id: 'node-03',
      name: 'Desk Mini Probe (Zigbee 3.0)',
      hardware: 'Tuya TS0601 Soil Sensor',
      linkedPlant: 'Snake Plant (Sansevieria)',
      status: 'online',
      battery: 95,
      lastUpdated: '2 minutes ago',
      readings: {
        moisture: 18, // Snake plant likes it dry
        temperature: 21.0,
        humidity: 50,
        lightLux: 1200
      },
      thresholds: {
        moistureMin: 10,
        moistureMax: 45,
        tempMin: 16,
        tempMax: 30
      }
    }
  ]);

  const [isLiveStreaming, setIsLiveStreaming] = useState(true);
  const [showPairModal, setShowPairModal] = useState(false);
  const [newDeviceName, setNewDeviceName] = useState('');
  const [newDevicePlant, setNewDevicePlant] = useState('Peace Lily');
  const [selectedSensorId, setSelectedSensorId] = useState<string>('node-01');

  // Simulate real-time sensor fluctuation
  useEffect(() => {
    if (!isLiveStreaming) return;
    const interval = setInterval(() => {
      setSensors(prev =>
        prev.map(s => {
          const deltaMoist = (Math.random() - 0.5) * 0.4;
          const deltaTemp = (Math.random() - 0.5) * 0.2;
          const deltaLux = Math.round((Math.random() - 0.5) * 50);
          return {
            ...s,
            lastUpdated: 'Just now',
            readings: {
              ...s.readings,
              moisture: Math.max(5, Math.min(95, Number((s.readings.moisture + deltaMoist).toFixed(1)))),
              temperature: Math.max(10, Math.min(40, Number((s.readings.temperature + deltaTemp).toFixed(1)))),
              lightLux: Math.max(100, s.readings.lightLux + deltaLux)
            }
          };
        })
      );
    }, 4000);
    return () => clearInterval(interval);
  }, [isLiveStreaming]);

  const handlePairDevice = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newDeviceName.trim()) return;
    const newNode: SensorNode = {
      id: `node-${Date.now().toString().slice(-4)}`,
      name: newDeviceName,
      hardware: 'ESP32 IoT Garden Node (WiFi 2.4GHz)',
      linkedPlant: newDevicePlant,
      status: 'online',
      battery: 100,
      lastUpdated: 'Just paired',
      readings: {
        moisture: 45,
        temperature: 23.0,
        humidity: 55,
        lightLux: 2500
      },
      thresholds: {
        moistureMin: 30,
        moistureMax: 70,
        tempMin: 18,
        tempMax: 28
      }
    };
    setSensors([newNode, ...sensors]);
    setSelectedSensorId(newNode.id);
    setShowPairModal(false);
    setNewDeviceName('');
  };

  const selectedSensor = sensors.find(s => s.id === selectedSensorId) || sensors[0];

  return (
    <div className="max-w-7xl mx-auto px-4 lg:px-8 py-8 space-y-8">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 bg-white p-6 rounded-3xl border border-sage-300/40 shadow-sm">
        <div>
          <div className="inline-flex items-center gap-2 px-3 py-1 bg-sage-100 rounded-full text-forest-700 text-xs font-bold mb-2">
            <Radio className="w-3.5 h-3.5 animate-pulse text-forest-700" />
            <span>ESP32 / MQTT / Zigbee Telemetry Hub</span>
          </div>
          <h1 className="text-2xl lg:text-3xl font-extrabold text-forest-900">
            Smart Garden IoT Probes
          </h1>
          <p className="text-sm text-sage-700 mt-1">
            Real-time capacitive soil moisture, temperature, humidity, and PAR light logging.
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={() => setIsLiveStreaming(!isLiveStreaming)}
            className={`px-4 py-2.5 rounded-2xl text-xs font-bold flex items-center gap-2 border transition-all ${
              isLiveStreaming
                ? 'bg-emerald-50 text-emerald-800 border-emerald-300'
                : 'bg-stone-100 text-stone-600 border-stone-300'
            }`}
          >
            <RefreshCw className={`w-3.5 h-3.5 ${isLiveStreaming ? 'animate-spin' : ''}`} />
            <span>{isLiveStreaming ? 'Live Stream Active' : 'Stream Paused'}</span>
          </button>

          <button
            onClick={() => setShowPairModal(true)}
            className="px-4 py-2.5 rounded-2xl text-xs font-bold bg-forest-700 hover:bg-forest-900 text-white flex items-center gap-2 shadow-sm transition-all"
          >
            <Plus className="w-4 h-4" />
            <span>Pair New Probe</span>
          </button>
        </div>
      </div>

      {/* Main Grid: Device List & Selected Device Telemetry */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Device Cards column */}
        <div className="space-y-4">
          <h2 className="text-sm font-bold text-forest-900 uppercase tracking-wider px-1">
            Connected Nodes ({sensors.length})
          </h2>
          {sensors.map((sensor) => {
            const isSelected = sensor.id === selectedSensorId;
            const isDry = sensor.readings.moisture < sensor.thresholds.moistureMin;
            return (
              <div
                key={sensor.id}
                onClick={() => setSelectedSensorId(sensor.id)}
                className={`p-5 rounded-3xl border transition-all cursor-pointer ${
                  isSelected
                    ? 'bg-forest-50/80 border-forest-500 shadow-md ring-2 ring-forest-500/20'
                    : 'bg-white border-sage-300/40 hover:border-sage-400'
                }`}
              >
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-2.5">
                    <div className="w-10 h-10 rounded-2xl bg-forest-100 flex items-center justify-center text-forest-800">
                      <Cpu className="w-5 h-5" />
                    </div>
                    <div>
                      <h3 className="text-sm font-bold text-forest-900">{sensor.name}</h3>
                      <p className="text-xs text-sage-600 font-medium">📍 {sensor.linkedPlant}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-1.5">
                    <span
                      className={`w-2.5 h-2.5 rounded-full ${
                        sensor.status === 'online'
                          ? isDry
                            ? 'bg-amber-500 animate-ping'
                            : 'bg-emerald-500'
                          : 'bg-rose-500'
                      }`}
                    />
                    <span className="text-[11px] font-bold text-sage-700 capitalize">
                      {isDry ? 'Low Moisture' : sensor.status}
                    </span>
                  </div>
                </div>

                <div className="grid grid-cols-3 gap-2 mt-4 pt-4 border-t border-sage-200/50 text-center">
                  <div className="bg-white/80 p-2 rounded-xl border border-sage-100">
                    <span className="text-[10px] text-sage-500 font-bold block">Moisture</span>
                    <span
                      className={`text-sm font-black ${
                        isDry ? 'text-amber-700' : 'text-forest-900'
                      }`}
                    >
                      {sensor.readings.moisture}%
                    </span>
                  </div>
                  <div className="bg-white/80 p-2 rounded-xl border border-sage-100">
                    <span className="text-[10px] text-sage-500 font-bold block">Temp</span>
                    <span className="text-sm font-black text-forest-900">
                      {sensor.readings.temperature}°C
                    </span>
                  </div>
                  <div className="bg-white/80 p-2 rounded-xl border border-sage-100">
                    <span className="text-[10px] text-sage-500 font-bold block">Light</span>
                    <span className="text-sm font-black text-forest-900">
                      {sensor.readings.lightLux > 1000
                        ? `${(sensor.readings.lightLux / 1000).toFixed(1)}k`
                        : sensor.readings.lightLux} lx
                    </span>
                  </div>
                </div>

                <div className="flex items-center justify-between text-[11px] text-sage-500 font-medium mt-3">
                  <span className="flex items-center gap-1">
                    <BatteryCharging className="w-3.5 h-3.5 text-sage-600" />
                    {sensor.battery}%
                  </span>
                  <span>{sensor.lastUpdated}</span>
                </div>
              </div>
            );
          })}
        </div>

        {/* Detailed Telemetry Pane */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-white p-6 lg:p-8 rounded-3xl border border-sage-300/40 shadow-sm space-y-6">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-3 border-b border-sage-200/60 pb-5">
              <div>
                <span className="text-xs font-bold text-sage-500 uppercase tracking-wider">
                  Real-time Sensor Telemetry
                </span>
                <h2 className="text-xl font-black text-forest-900">
                  {selectedSensor.name}
                </h2>
                <p className="text-xs text-sage-700 mt-0.5">
                  Assigned Plant: <strong className="text-forest-800">{selectedSensor.linkedPlant}</strong> • Hardware: {selectedSensor.hardware}
                </p>
              </div>
              <div className="flex items-center gap-2">
                <span className="px-3 py-1 bg-emerald-100 text-emerald-800 text-xs font-bold rounded-full flex items-center gap-1.5">
                  <Wifi className="w-3.5 h-3.5" />
                  Signal -62 dBm
                </span>
                <span className="px-3 py-1 bg-sage-100 text-sage-800 text-xs font-bold rounded-full">
                  MQTT v5.0
                </span>
              </div>
            </div>

            {/* Gauges Grid */}
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {/* Soil Moisture Gauge */}
              <div className="bg-gradient-to-br from-blue-50/70 to-indigo-50/40 p-5 rounded-2xl border border-blue-200/60 relative overflow-hidden">
                <div className="flex items-center justify-between mb-3">
                  <div className="flex items-center gap-2 text-blue-800 font-bold text-sm">
                    <Droplets className="w-5 h-5 text-blue-600" />
                    <span>Soil Moisture</span>
                  </div>
                  <span className="text-xs font-bold text-blue-700 bg-blue-100 px-2 py-0.5 rounded-lg">
                    Target: {selectedSensor.thresholds.moistureMin}% - {selectedSensor.thresholds.moistureMax}%
                  </span>
                </div>
                <div className="flex items-baseline gap-2">
                  <span className="text-4xl font-black text-blue-950">
                    {selectedSensor.readings.moisture}%
                  </span>
                  <span className="text-xs text-blue-700 font-semibold">
                    {selectedSensor.readings.moisture < selectedSensor.thresholds.moistureMin
                      ? '⚠️ Needs Immediate Watering'
                      : '✅ Soil Hydration Optimal'}
                  </span>
                </div>
                <div className="w-full bg-blue-200/70 h-2.5 rounded-full mt-3 overflow-hidden">
                  <div
                    className="bg-blue-600 h-full rounded-full transition-all duration-700"
                    style={{ width: `${selectedSensor.readings.moisture}%` }}
                  />
                </div>
              </div>

              {/* Temperature Gauge */}
              <div className="bg-gradient-to-br from-amber-50/70 to-orange-50/40 p-5 rounded-2xl border border-amber-200/60 relative overflow-hidden">
                <div className="flex items-center justify-between mb-3">
                  <div className="flex items-center gap-2 text-amber-800 font-bold text-sm">
                    <Thermometer className="w-5 h-5 text-amber-600" />
                    <span>Root / Ambient Temp</span>
                  </div>
                  <span className="text-xs font-bold text-amber-700 bg-amber-100 px-2 py-0.5 rounded-lg">
                    Ideal: 20° - 26°C
                  </span>
                </div>
                <div className="flex items-baseline gap-2">
                  <span className="text-4xl font-black text-amber-950">
                    {selectedSensor.readings.temperature}°C
                  </span>
                  <span className="text-xs text-amber-700 font-semibold">
                    {((selectedSensor.readings.temperature * 9) / 5 + 32).toFixed(1)}°F
                  </span>
                </div>
                <div className="w-full bg-amber-200/70 h-2.5 rounded-full mt-3 overflow-hidden">
                  <div
                    className="bg-amber-500 h-full rounded-full transition-all duration-700"
                    style={{ width: `${(selectedSensor.readings.temperature / 40) * 100}%` }}
                  />
                </div>
              </div>

              {/* Humidity Gauge */}
              <div className="bg-gradient-to-br from-teal-50/70 to-emerald-50/40 p-5 rounded-2xl border border-teal-200/60 relative overflow-hidden">
                <div className="flex items-center justify-between mb-3">
                  <div className="flex items-center gap-2 text-teal-800 font-bold text-sm">
                    <Droplets className="w-5 h-5 text-teal-600" />
                    <span>Air Relative Humidity</span>
                  </div>
                  <span className="text-xs font-bold text-teal-700 bg-teal-100 px-2 py-0.5 rounded-lg">
                    SHT30 Sensor
                  </span>
                </div>
                <div className="flex items-baseline gap-2">
                  <span className="text-4xl font-black text-teal-950">
                    {selectedSensor.readings.humidity}%
                  </span>
                  <span className="text-xs text-teal-700 font-semibold">
                    {selectedSensor.readings.humidity > 50 ? 'Tropical zone' : 'Moderate'}
                  </span>
                </div>
                <div className="w-full bg-teal-200/70 h-2.5 rounded-full mt-3 overflow-hidden">
                  <div
                    className="bg-teal-600 h-full rounded-full transition-all duration-700"
                    style={{ width: `${selectedSensor.readings.humidity}%` }}
                  />
                </div>
              </div>

              {/* Sunlight / Lux Gauge */}
              <div className="bg-gradient-to-br from-yellow-50/70 to-amber-50/40 p-5 rounded-2xl border border-yellow-200/60 relative overflow-hidden">
                <div className="flex items-center justify-between mb-3">
                  <div className="flex items-center gap-2 text-yellow-800 font-bold text-sm">
                    <Sun className="w-5 h-5 text-yellow-600" />
                    <span>Luminosity (PAR / Lux)</span>
                  </div>
                  <span className="text-xs font-bold text-yellow-700 bg-yellow-100 px-2 py-0.5 rounded-lg">
                    BH1750 Optical
                  </span>
                </div>
                <div className="flex items-baseline gap-2">
                  <span className="text-4xl font-black text-yellow-950">
                    {selectedSensor.readings.lightLux.toLocaleString()}
                  </span>
                  <span className="text-xs text-yellow-800 font-semibold">lux</span>
                </div>
                <div className="w-full bg-yellow-200/70 h-2.5 rounded-full mt-3 overflow-hidden">
                  <div
                    className="bg-yellow-500 h-full rounded-full transition-all duration-700"
                    style={{
                      width: `${Math.min(100, (selectedSensor.readings.lightLux / 25000) * 100)}%`
                    }}
                  />
                </div>
              </div>
            </div>

            {/* Microcontroller Code Snippet / Integration Guide */}
            <div className="bg-forest-900 text-cream p-5 rounded-2xl space-y-3">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <Cpu className="w-4 h-4 text-limeAccent" />
                  <span className="text-xs font-bold text-white tracking-wide">
                    ESP32 / Arduino C++ Integration URL
                  </span>
                </div>
                <span className="text-[11px] font-mono text-sage-300">HTTP REST / MQTT</span>
              </div>
              <div className="bg-black/40 p-3 rounded-xl font-mono text-xs text-emerald-300 overflow-x-auto select-all">
                POST /api/sensors/telemetry HTTP/1.1<br />
                Host: plantpal.garden<br />
                Content-Type: application/json<br />
                {`{"deviceId": "${selectedSensor.id}", "moisture": 42.5, "temp": 23.1, "lux": 2800}`}
              </div>
              <p className="text-xs text-sage-300">
                Directly flash your ESP32 with the PlantPal open-source firmware or use Home Assistant ESPHome integration.
              </p>
            </div>
          </div>
        </div>
      </div>

      {/* Pair Probe Modal */}
      {showPairModal && (
        <div className="fixed inset-0 z-50 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-md w-full p-6 border border-sage-300 shadow-2xl space-y-5">
            <div className="flex items-center justify-between border-b border-sage-200 pb-3">
              <div className="flex items-center gap-2">
                <Cpu className="w-5 h-5 text-forest-700" />
                <h3 className="text-lg font-bold text-forest-900">Pair Garden Probe</h3>
              </div>
              <button
                onClick={() => setShowPairModal(false)}
                className="text-sage-500 hover:text-forest-900 text-sm font-bold"
              >
                ✕
              </button>
            </div>

            <form onSubmit={handlePairDevice} className="space-y-4">
              <div>
                <label className="text-xs font-bold text-forest-900 block mb-1">
                  Device Name / Location
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g., Office Fern Sensor (ESP32)"
                  value={newDeviceName}
                  onChange={(e) => setNewDeviceName(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-sage-300 focus:outline-none focus:ring-2 focus:ring-forest-600 text-sm"
                />
              </div>

              <div>
                <label className="text-xs font-bold text-forest-900 block mb-1">
                  Assigned Plant
                </label>
                <select
                  value={newDevicePlant}
                  onChange={(e) => setNewDevicePlant(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-sage-300 focus:outline-none focus:ring-2 focus:ring-forest-600 text-sm bg-white"
                >
                  <option value="Monstera Deliciosa">Monstera Deliciosa</option>
                  <option value="Sweet Genovese Basil">Sweet Genovese Basil</option>
                  <option value="Roma Tomato">Roma Tomato</option>
                  <option value="Snake Plant">Snake Plant</option>
                  <option value="Peace Lily">Peace Lily</option>
                  <option value="Fiddle Leaf Fig">Fiddle Leaf Fig</option>
                </select>
              </div>

              <div className="p-3 bg-sage-50 rounded-xl text-xs text-sage-700 space-y-1">
                <p className="font-bold text-forest-900">Supported Hardware Protocols:</p>
                <p>• ESP32 / ESP8266 capacitive moisture probes</p>
                <p>• Tuya / Sonoff Zigbee 3.0 soil sensors</p>
                <p>• Xiaomi Mi Flora BLE plant monitors</p>
              </div>

              <div className="flex items-center justify-end gap-3 pt-2">
                <button
                  type="button"
                  onClick={() => setShowPairModal(false)}
                  className="px-4 py-2 rounded-xl text-xs font-bold text-sage-700 hover:bg-sage-100"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl text-xs font-bold bg-forest-700 hover:bg-forest-900 text-white shadow-md transition-all"
                >
                  Complete Pairing
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
