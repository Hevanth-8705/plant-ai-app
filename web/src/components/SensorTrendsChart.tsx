import React from 'react';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from 'recharts';

interface SensorReading {
  timestamp: string;
  soilMoisture: number;
  temperatureC: number;
}

interface SensorTrendsChartProps {
  data: SensorReading[];
}

export const SensorTrendsChart: React.FC<SensorTrendsChartProps> = ({ data }) => {
  return (
    <div className="h-48 w-full">
      <ResponsiveContainer width="100%" height="100%">
        <LineChart data={data}>
          <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
          <XAxis dataKey="timestamp" fontSize={10} tickFormatter={(val) => new Date(val).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })} />
          <YAxis fontSize={10} />
          <Tooltip />
          <Line type="monotone" dataKey="soilMoisture" stroke="#10b981" strokeWidth={2} name="Moisture %" />
          <Line type="monotone" dataKey="temperatureC" stroke="#f59e0b" strokeWidth={2} name="Temp °C" />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
};
