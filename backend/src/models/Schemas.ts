import mongoose, { Schema, Document } from 'mongoose';

// User Schema
export interface IUser extends Document {
  email: string;
  passwordHash: string;
  name: string;
  avatarUrl?: string;
  xpPoints: number;
  level: number;
  preferences: {
    notificationsEnabled: boolean;
    wateringTimePreference: string;
  };
  createdAt: Date;
}

const UserSchema = new Schema<IUser>({
  email: { type: String, required: true, unique: true, lowercase: true, trim: true },
  passwordHash: { type: String, required: true },
  name: { type: String, required: true },
  avatarUrl: { type: String },
  xpPoints: { type: Number, default: 120 },
  level: { type: Number, default: 2 },
  preferences: {
    notificationsEnabled: { type: Boolean, default: true },
    wateringTimePreference: { type: String, default: '09:00' }
  },
  createdAt: { type: Date, default: Date.now }
});

// Plant Schema
export interface IPlant extends Document {
  userId: mongoose.Types.ObjectId;
  name: string;
  species: string;
  category: 'INDOOR' | 'OUTDOOR' | 'VEGETABLE' | 'HERB' | 'FLOWER' | 'SUCCULENT';
  imageUrl?: string;
  isIndoor: boolean;
  plantedDate: Date;
  wateringIntervalDays: number;
  lastWateredDate: Date;
  fertilizerIntervalDays: number;
  lastFertilizedDate: Date;
  sunlightRequirement: 'FULL_SUN' | 'BRIGHT_INDIRECT' | 'PARTIAL_SHADE' | 'LOW_LIGHT';
  isHarvestable: boolean;
  maturityDays: number;
  healthStatus: string;
  notes?: string;
  isArchived: boolean;
  customPotColor?: string;
  growthStage?: string;
  createdAt: Date;
}

const PlantSchema = new Schema<IPlant>({
  userId: { type: Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  name: { type: String, required: true },
  species: { type: String, required: true },
  category: { type: String, required: true, enum: ['INDOOR', 'OUTDOOR', 'VEGETABLE', 'HERB', 'FLOWER', 'SUCCULENT'] },
  imageUrl: { type: String },
  isIndoor: { type: Boolean, default: true },
  plantedDate: { type: Date, default: Date.now },
  wateringIntervalDays: { type: Number, default: 7 },
  lastWateredDate: { type: Date, default: Date.now },
  fertilizerIntervalDays: { type: Number, default: 30 },
  lastFertilizedDate: { type: Date, default: Date.now },
  sunlightRequirement: { type: String, default: 'BRIGHT_INDIRECT', enum: ['FULL_SUN', 'BRIGHT_INDIRECT', 'PARTIAL_SHADE', 'LOW_LIGHT'] },
  isHarvestable: { type: Boolean, default: false },
  maturityDays: { type: Number, default: 60 },
  healthStatus: { type: String, default: 'Healthy' },
  notes: { type: String },
  isArchived: { type: Boolean, default: false },
  customPotColor: { type: String, default: '#C86D51' },
  growthStage: { type: String, default: 'Vegetative' },
  createdAt: { type: Date, default: Date.now }
});

// WateringLog Schema
const WateringLogSchema = new Schema({
  userId: { type: Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  plantId: { type: Schema.Types.ObjectId, ref: 'Plant', required: true, index: true },
  wateredDate: { type: Date, default: Date.now },
  soilMoistureBefore: { type: String, default: 'Top 2 inches Dry' },
  notes: { type: String }
});

// FertilizerLog Schema
const FertilizerLogSchema = new Schema({
  userId: { type: Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  plantId: { type: Schema.Types.ObjectId, ref: 'Plant', required: true, index: true },
  appliedDate: { type: Date, default: Date.now },
  fertilizerType: { type: String, required: true },
  amount: { type: Number, required: true },
  unit: { type: String, default: 'ml' },
  notes: { type: String }
});

// GrowthRecord Schema
const GrowthRecordSchema = new Schema({
  userId: { type: Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  plantId: { type: Schema.Types.ObjectId, ref: 'Plant', required: true, index: true },
  recordedDate: { type: Date, default: Date.now },
  heightCm: { type: Number, required: true },
  leafCount: { type: Number, required: true },
  hasFlowers: { type: Boolean, default: false },
  hasFruit: { type: Boolean, default: false },
  photoUrl: { type: String },
  milestone: { type: String },
  notes: { type: String }
});

// PlantHealthReport Schema
const PlantHealthReportSchema = new Schema({
  userId: { type: Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  plantId: { type: Schema.Types.ObjectId, ref: 'Plant', index: true },
  plantName: { type: String, required: true },
  scanDate: { type: Date, default: Date.now },
  symptoms: [{ type: String }],
  primaryDiagnosis: { type: String, required: true },
  severity: { type: String, required: true },
  possibleCauses: [{ type: String }],
  environmentalFactors: { type: String },
  organicRemedies: [{ type: String }],
  chemicalOptions: [{ type: String }],
  preventionTips: [{ type: String }],
  needsExpertHelp: { type: Boolean, default: false },
  confidenceNote: { type: String },
  photoUrl: { type: String }
});

// HarvestRecord Schema
const HarvestRecordSchema = new Schema({
  userId: { type: Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  plantId: { type: Schema.Types.ObjectId, ref: 'Plant', required: true, index: true },
  harvestDate: { type: Date, default: Date.now },
  quantity: { type: Number, required: true },
  unit: { type: String, required: true },
  photoUrl: { type: String },
  notes: { type: String }
});

// DiaryEntry Schema
const DiaryEntrySchema = new Schema({
  userId: { type: Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  plantId: { type: Schema.Types.ObjectId, ref: 'Plant', required: true, index: true },
  plantName: { type: String, required: true },
  title: { type: String, required: true },
  description: { type: String },
  eventType: { type: String, required: true },
  photoUrl: { type: String },
  tags: [{ type: String }],
  createdAt: { type: Date, default: Date.now }
});

// Smart IoT Sensor Device Schema
const SensorDeviceSchema = new Schema({
  userId: { type: Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  plantId: { type: Schema.Types.ObjectId, ref: 'Plant', index: true },
  deviceName: { type: String, required: true },
  deviceType: { type: String, default: 'ESP32_SOIL_COMBO' }, // ESP32, BLE, Zigbee
  hardwareId: { type: String, required: true, unique: true },
  batteryPct: { type: Number, default: 95 },
  lastMoistureReading: { type: Number, default: 48 }, // 0-100%
  lastTemperatureC: { type: Number, default: 22.5 },
  lastHumidityPct: { type: Number, default: 58 },
  lastLightLux: { type: Number, default: 1250 },
  lastHeartbeat: { type: Date, default: Date.now },
  isOnline: { type: Boolean, default: true }
});

// Sensor Reading Historical Timeseries Schema
const SensorReadingSchema = new Schema({
  deviceId: { type: Schema.Types.ObjectId, ref: 'SensorDevice', required: true, index: true },
  plantId: { type: Schema.Types.ObjectId, ref: 'Plant', required: true, index: true },
  soilMoisture: { type: Number, required: true },
  temperatureC: { type: Number, required: true },
  humidityPct: { type: Number, required: true },
  lightLux: { type: Number, required: true },
  timestamp: { type: Date, default: Date.now, index: true }
});

// Community Post Schema
const CommunityPostSchema = new Schema({
  userId: { type: Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  authorName: { type: String, required: true },
  title: { type: String, required: true },
  content: { type: String, required: true },
  imageUrl: { type: String },
  plantTag: { type: String },
  likesCount: { type: Number, default: 0 },
  commentsCount: { type: Number, default: 0 },
  createdAt: { type: Date, default: Date.now }
});

// Post Comment Schema
const PostCommentSchema = new Schema({
  postId: { type: Schema.Types.ObjectId, ref: 'CommunityPost', required: true, index: true },
  userId: { type: Schema.Types.ObjectId, ref: 'User', required: true },
  authorName: { type: String, required: true },
  content: { type: String, required: true },
  createdAt: { type: Date, default: Date.now }
});

// Garden Budget Entry Schema
const GardenBudgetEntrySchema = new Schema({
  userId: { type: Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  category: { type: String, required: true }, // 'Plants', 'Pots', 'Soil', 'Fertilizer', 'Tools', 'Sensors'
  itemDescription: { type: String, required: true },
  cost: { type: Number, required: true },
  purchaseDate: { type: Date, default: Date.now },
  plantId: { type: Schema.Types.ObjectId, ref: 'Plant' }
});

// Vacation Plan Schema
const VacationPlanSchema = new Schema({
  userId: { type: Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  startDate: { type: Date, required: true },
  endDate: { type: Date, required: true },
  caretakerName: { type: String },
  caretakerEmail: { type: String },
  specialInstructions: { type: String },
  isCompleted: { type: Boolean, default: false },
  createdAt: { type: Date, default: Date.now }
});

// Garden Achievement Schema
const AchievementSchema = new Schema({
  userId: { type: Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  title: { type: String, required: true },
  description: { type: String, required: true },
  icon: { type: String, required: true },
  earnedAt: { type: Date, default: Date.now }
});

// User Challenge Schema
const UserChallengeSchema = new Schema({
  userId: { type: Schema.Types.ObjectId, ref: 'User', required: true, index: true },
  title: { type: String, required: true },
  description: { type: String, required: true },
  target: { type: Number, required: true },
  progress: { type: Number, default: 0 },
  isCompleted: { type: Boolean, default: false },
  deadline: { type: Date }
});

export const User = mongoose.model<IUser>('User', UserSchema);
export const Plant = mongoose.model<IPlant>('Plant', PlantSchema);
export const WateringLog = mongoose.model('WateringLog', WateringLogSchema);
export const FertilizerLog = mongoose.model('FertilizerLog', FertilizerLogSchema);
export const GrowthRecord = mongoose.model('GrowthRecord', GrowthRecordSchema);
export const PlantHealthReport = mongoose.model('PlantHealthReport', PlantHealthReportSchema);
export const HarvestRecord = mongoose.model('HarvestRecord', HarvestRecordSchema);
export const DiaryEntry = mongoose.model('DiaryEntry', DiaryEntrySchema);
export const SensorDevice = mongoose.model('SensorDevice', SensorDeviceSchema);
export const SensorReading = mongoose.model('SensorReading', SensorReadingSchema);
export const CommunityPost = mongoose.model('CommunityPost', CommunityPostSchema);
export const PostComment = mongoose.model('PostComment', PostCommentSchema);
export const GardenBudgetEntry = mongoose.model('GardenBudgetEntry', GardenBudgetEntrySchema);
export const VacationPlan = mongoose.model('VacationPlan', VacationPlanSchema);
export const Achievement = mongoose.model('Achievement', AchievementSchema);
export const UserChallenge = mongoose.model('UserChallenge', UserChallengeSchema);
