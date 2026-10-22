import React from 'react';
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  AreaChart,
  Area
} from 'recharts';

interface GrowthTrendsChartProps {
  weeklyData: { name: string; growth: number }[];
  monthlyData: { name: string; growth: number }[];
}

export const GrowthTrendsChart: React.FC<GrowthTrendsChartProps> = ({ weeklyData, monthlyData }) => {
  return (
    <div className="space-y-4">
      <div>
        <p className="text-[10px] text-sage-600 font-bold uppercase mb-2">Weekly Trend (cm)</p>
        <div className="h-32">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={weeklyData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="name" fontSize={10} />
              <YAxis fontSize={10} />
              <Tooltip />
              <Line type="monotone" dataKey="growth" stroke="#4a7c59" strokeWidth={2} />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      <div>
        <p className="text-[10px] text-sage-600 font-bold uppercase mb-2">Monthly Trend (cm)</p>
        <div className="h-32">
          <ResponsiveContainer width="100%" height="100%">
            <AreaChart data={monthlyData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
              <XAxis dataKey="name" fontSize={10} />
              <YAxis fontSize={10} />
              <Tooltip />
              <Area type="monotone" dataKey="growth" stroke="#4a7c59" fill="#4a7c59" fillOpacity={0.2} />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
};
