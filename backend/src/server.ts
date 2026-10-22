import express, { Request, Response, NextFunction } from 'express';
import mongoose from 'mongoose';
import cors from 'cors';
import dotenv from 'dotenv';
import bcrypt from 'bcryptjs';
import jwt from 'jsonwebtoken';
import axios from 'axios';
import {
  User,
  Plant,
  WateringLog,
  FertilizerLog,
  GrowthRecord,
  PlantHealthReport,
  HarvestRecord,
  DiaryEntry,
  SensorDevice,
  SensorReading,
  CommunityPost,
  PostComment,
  GardenBudgetEntry,
  VacationPlan,
  Achievement,
  UserChallenge
} from './models/Schemas';

dotenv.config();

const app = express();
const PORT = process.env.PORT || 5000;
const JWT_SECRET = process.env.JWT_SECRET || 'plantpal_super_secure_jwt_secret_key_2026';
const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://localhost:27017/plantpal';

app.use(cors());
app.use(express.json({ limit: '15mb' }));

interface AuthRequest extends Request {
  userId?: string;
}

const authenticateToken = (req: AuthRequest, res: Response, next: NextFunction) => {
  const authHeader = req.headers['authorization'];
  const token = authHeader && authHeader.split(' ')[1];
  if (!token) return res.status(401).json({ error: 'Access token required' });

  jwt.verify(token, JWT_SECRET, (err, decoded: any) => {
    if (err) return res.status(403).json({ error: 'Invalid or expired token' });
    req.userId = decoded.userId;
    next();
  });
};

// 1. Health & Root
app.get('/api/health', (req, res) => {
  res.json({
    status: 'healthy',
    timestamp: new Date(),
    service: 'PlantPal Ecosystem API'
  });
});

// 2. Authentication APIs
app.post('/api/auth/register', async (req, res) => {
  try {
    const { email, password, name } = req.body;
    if (!email || !password || !name) {
      return res.status(400).json({ error: 'Email, password, and name are required' });
    }

    const existing = await User.findOne({ email });
    if (existing) return res.status(409).json({ error: 'Email already registered' });

    const salt = await bcrypt.genSalt(10);
    const passwordHash = await bcrypt.hash(password, salt);

    const newUser = await User.create({ email, passwordHash, name, xpPoints: 100, level: 1 });
    const token = jwt.sign({ userId: newUser._id }, JWT_SECRET, { expiresIn: '7d' });

    res.status(201).json({
      token,
      user: { id: newUser._id, email: newUser.email, name: newUser.name, xpPoints: newUser.xpPoints, level: newUser.level }
    });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/auth/login', async (req, res) => {
  try {
    const { email, password } = req.body;
    const user = await User.findOne({ email });
    if (!user) return res.status(401).json({ error: 'Invalid credentials' });

    const isMatch = await bcrypt.compare(password, user.passwordHash);
    if (!isMatch) return res.status(401).json({ error: 'Invalid credentials' });

    const token = jwt.sign({ userId: user._id }, JWT_SECRET, { expiresIn: '7d' });
    res.json({
      token,
      user: { id: user._id, email: user.email, name: user.name, xpPoints: user.xpPoints, level: user.level }
    });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

// 3. Plants CRUD & 3D Companion Metadata
app.get('/api/plants', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const plants = await Plant.find({ userId: req.userId, isArchived: false }).sort({ createdAt: -1 });
    res.json(plants);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/plants', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const plantData = { ...req.body, userId: req.userId };
    const plant = await Plant.create(plantData);
    await User.findByIdAndUpdate(req.userId, { $inc: { xpPoints: 50 } });
    res.status(201).json(plant);
  } catch (err: any) {
    res.status(400).json({ error: err.message });
  }
});

app.put('/api/plants/:id', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const plant = await Plant.findOneAndUpdate(
      { _id: req.params.id, userId: req.userId },
      req.body,
      { new: true }
    );
    if (!plant) return res.status(404).json({ error: 'Plant not found' });
    res.json(plant);
  } catch (err: any) {
    res.status(400).json({ error: err.message });
  }
});

app.delete('/api/plants/:id', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const plant = await Plant.findOneAndDelete({ _id: req.params.id, userId: req.userId });
    if (!plant) return res.status(404).json({ error: 'Plant not found' });
    res.json({ message: 'Plant removed successfully' });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

// 4. Care Actions (Watering & Fertilizer)
app.post('/api/plants/:id/water', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const { soilMoistureBefore, notes } = req.body;
    const now = new Date();
    await Plant.updateOne({ _id: req.params.id, userId: req.userId }, { lastWateredDate: now });
    const log = await WateringLog.create({
      userId: req.userId,
      plantId: req.params.id,
      wateredDate: now,
      soilMoistureBefore: soilMoistureBefore || 'Top 2 inches Dry',
      notes
    });
    await User.findByIdAndUpdate(req.userId, { $inc: { xpPoints: 15 } });
    
    // Achievement check: 10 waterings
    const wateringsCount = await WateringLog.countDocuments({ userId: req.userId });
    if (wateringsCount === 10) {
      await Achievement.create({
        userId: req.userId,
        title: 'Hydration Hero',
        description: 'Completed 10 watering tasks.',
        icon: 'water_drop'
      });
      await User.findByIdAndUpdate(req.userId, { $inc: { xpPoints: 50 } });
    }

    res.status(201).json(log);
  } catch (err: any) {
    res.status(400).json({ error: err.message });
  }
});

app.post('/api/plants/:id/fertilize', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const { fertilizerType, amount, unit, notes } = req.body;
    const now = new Date();
    await Plant.updateOne({ _id: req.params.id, userId: req.userId }, { lastFertilizedDate: now });
    const log = await FertilizerLog.create({
      userId: req.userId,
      plantId: req.params.id,
      appliedDate: now,
      fertilizerType: fertilizerType || 'Balanced Organic Liquid',
      amount: amount || 5,
      unit: unit || 'ml',
      notes
    });
    await User.findByIdAndUpdate(req.userId, { $inc: { xpPoints: 20 } });
    res.status(201).json(log);
  } catch (err: any) {
    res.status(400).json({ error: err.message });
  }
});

// 5. Growth Tracking & Timelines
app.get('/api/plants/:id/growth', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const records = await GrowthRecord.find({ plantId: req.params.id, userId: req.userId }).sort({ recordedDate: 1 });
    res.json(records);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/plants/:id/growth', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const record = await GrowthRecord.create({
      ...req.body,
      plantId: req.params.id,
      userId: req.userId
    });
    await User.findByIdAndUpdate(req.userId, { $inc: { xpPoints: 25 } });
    res.status(201).json(record);
  } catch (err: any) {
    res.status(400).json({ error: err.message });
  }
});

// 6. Smart IoT Sensors (ESP32 Integration)
app.get('/api/sensors', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const devices = await SensorDevice.find({ userId: req.userId }).populate('plantId', 'name species');
    res.json(devices);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/sensors/device', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const device = await SensorDevice.create({
      ...req.body,
      userId: req.userId
    });
    res.status(201).json(device);
  } catch (err: any) {
    res.status(400).json({ error: err.message });
  }
});

app.post('/api/sensors/telemetry', async (req, res) => {
  try {
    const { hardwareId, soilMoisture, temperatureC, humidityPct, lightLux } = req.body;
    const device = await SensorDevice.findOne({ hardwareId });
    if (!device) return res.status(404).json({ error: 'Device not registered' });

    device.lastMoistureReading = soilMoisture;
    device.lastTemperatureC = temperatureC;
    device.lastHumidityPct = humidityPct;
    device.lastLightLux = lightLux;
    device.lastHeartbeat = new Date();
    device.isOnline = true;
    await device.save();

    if (device.plantId) {
      await SensorReading.create({
        deviceId: device._id,
        plantId: device.plantId,
        soilMoisture,
        temperatureC,
        humidityPct,
        lightLux
      });
    }

    res.json({ status: 'telemetry_recorded' });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});
app.get('/api/sensors/:deviceId/history', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const { deviceId } = req.params;
    const readings = await SensorReading.find({ deviceId }).sort({ timestamp: -1 }).limit(20);
    res.json(readings.reverse());
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/community', async (req, res) => {
  try {
    const posts = await CommunityPost.find().sort({ createdAt: -1 }).limit(20);
    res.json(posts);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/community', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const user = await User.findById(req.userId);
    const post = await CommunityPost.create({
      ...req.body,
      userId: req.userId,
      authorName: user?.name || 'Fellow Gardener'
    });
    res.status(201).json(post);
  } catch (err: any) {
    res.status(400).json({ error: err.message });
  }
});

app.post('/api/plants/scan', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const { base64Image, mode } = req.body;
    const apiKey = process.env.GEMINI_API_KEY;
    if (!apiKey || apiKey === 'MY_GEMINI_API_KEY') {
        throw new Error("Gemini API key not configured.");
    }

    // 1. Plant Validation
    const validationPrompt = `Analyze this image. Does it contain a real, living plant? Reply ONLY with JSON: { "isPlant": boolean, "confidence": number, "reasoning": string }. DO NOT identify the plant, just validate if it is a real plant. If it's a person, animal, car, building, food, artificial object, or landscape without a clearly visible plant, isPlant must be false.`;

    const validationResponse = await axios.post(`https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${apiKey}`, {
        contents: [{ parts: [{ text: validationPrompt }, { inline_data: { mime_type: 'image/jpeg', data: base64Image } }] }]
    });

    const validationResult = JSON.parse(validationResponse.data.candidates[0].content.parts[0].text.replace(/```json/g, '').replace(/```/g, '').trim());

    if (!validationResult.isPlant || validationResult.confidence < 0.85) {
        return res.status(200).json({
            success: false,
            status: 'non_plant_detected',
            message: 'PlantPal Camera scans plants only. Please point the camera at a real plant, leaf, flower, or stem and try again.'
        });
    }

    // 2. Analysis
    const analysisPrompt = mode === 'health' 
        ? `Analyze this plant for health issues. Return JSON with: commonName, primaryDiagnosis, severity, possibleCauses, organicRemedies, preventionTips.`
        : `Identify this plant and provide care instructions. Return JSON with: commonName, scientificName, careRequirements, growingTips.`;

    const analysisResponse = await axios.post(`https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${apiKey}`, {
        contents: [{ parts: [{ text: analysisPrompt }, { inline_data: { mime_type: 'image/jpeg', data: base64Image } }] }]
    });

    const result = JSON.parse(analysisResponse.data.candidates[0].content.parts[0].text.replace(/```json/g, '').replace(/```/g, '').trim());

    res.json({
        success: true,
        status: 'plant_detected',
        plantValidation: validationResult,
        result
    });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/budget', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const entries = await GardenBudgetEntry.find({ userId: req.userId }).sort({ purchaseDate: -1 });
    const totalSpent = entries.reduce((acc, curr) => acc + curr.cost, 0);
    res.json({ entries, totalSpent });
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/budget', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const entry = await GardenBudgetEntry.create({
      ...req.body,
      userId: req.userId
    });
    res.status(201).json(entry);
  } catch (err: any) {
    res.status(400).json({ error: err.message });
  }
});

// 9. Vacation Care Planning
app.get('/api/vacation', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const plans = await VacationPlan.find({ userId: req.userId }).sort({ startDate: -1 });
    res.json(plans);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/vacation', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const plan = await VacationPlan.create({
      ...req.body,
      userId: req.userId
    });
    res.status(201).json(plan);
  } catch (err: any) {
    res.status(400).json({ error: err.message });
  }
});

// 10. Gamification: Achievements & Challenges
app.get('/api/achievements', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const achievements = await Achievement.find({ userId: req.userId }).sort({ earnedAt: -1 });
    res.json(achievements);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.get('/api/challenges', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const challenges = await UserChallenge.find({ userId: req.userId, isCompleted: false });
    res.json(challenges);
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});
app.post('/api/ai/garden-design', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const { spaceType, sunlightExposure, preferredStyle, budget } = req.body;
    const apiKey = process.env.GEMINI_API_KEY;
    if (!apiKey || apiKey === 'MY_GEMINI_API_KEY') {
        throw new Error("Gemini API key not configured.");
    }

    const prompt = `Design a ${preferredStyle || 'modern'} ${spaceType || 'indoor'} garden with ${sunlightExposure || 'bright'} light. Budget: ${budget || 'flexible'}. Return JSON with: conceptTitle, designPhilosophy, recommendedPlants (name, role, pot), estimatedMaterialCost, careComplexity, checklist.`;

    const response = await axios.post(`https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${apiKey}`, {
        contents: [{ parts: [{ text: prompt }] }]
    });

    const result = response.data.candidates[0].content.parts[0].text;
    const cleaned = result.replace(/```json/g, '').replace(/```/g, '').trim();
    res.json(JSON.parse(cleaned));
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

app.post('/api/ai/scan', authenticateToken, async (req: AuthRequest, res) => {
  try {
    const { plantName, symptoms, base64Image } = req.body;
    const apiKey = process.env.GEMINI_API_KEY;
    if (!apiKey || apiKey === 'MY_GEMINI_API_KEY') {
        throw new Error("Gemini API key not configured.");
    }

    const prompt = `Analyze this plant for health issues. Plant: ${plantName}. Symptoms: ${symptoms.join(', ')}. Return JSON with: primaryDiagnosis, severity, possibleCauses, environmentalFactors, organicRemedies, chemicalOptions, preventionTips, needsExpertHelp, confidenceNote.`;

    const parts: any[] = [{ text: prompt }];
    if (base64Image) {
        parts.push({ inline_data: { mime_type: 'image/jpeg', data: base64Image } });
    }

    const response = await axios.post(`https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=${apiKey}`, {
        contents: [{ parts }]
    });

    const result = response.data.candidates[0].content.parts[0].text;
    const cleaned = result.replace(/```json/g, '').replace(/```/g, '').trim();
    res.json(JSON.parse(cleaned));
  } catch (err: any) {
    res.status(500).json({ error: err.message });
  }
});

// Weather API Proxy
app.get('/api/weather', async (req, res) => {
  const city = req.query.city || 'San Francisco';
  const apiKey = process.env.WEATHER_API_KEY; // User must set this
  
  if (!apiKey || apiKey === 'YOUR_OPENWEATHER_KEY') {
    // Demonstration/Fallback Mode
    return res.json({
        city,
        temperatureC: 21,
        condition: 'Partly Sunny (Demo)',
        humidityPct: 62,
        rainProbability: 10,
        gardeningAdvice: 'Ideal conditions for outdoor watering.'
    });
  }

  try {
    const response = await axios.get(`https://api.openweathermap.org/data/2.5/weather?q=${city}&units=metric&appid=${apiKey}`);
    const data = response.data;
    res.json({
      city: data.name,
      temperatureC: data.main.temp,
      condition: data.weather[0].description,
      humidityPct: data.main.humidity,
      rainProbability: data.rain ? data.rain['1h'] : 0,
      gardeningAdvice: data.main.temp > 25 ? 'High heat: ensure adequate water.' : 'Moderate conditions.'
    });
  } catch (err: any) {
    res.status(502).json({ error: 'Weather service unavailable' });
  }
});

if (process.env.NODE_ENV !== 'test') {
  mongoose
    .connect(MONGODB_URI)
    .then(() => {
      console.log('PlantPal MongoDB connected.');
      app.listen(PORT, () => console.log(`PlantPal API running on port ${PORT}`));
    })
    .catch((err) => {
      console.warn('MongoDB deferred:', err.message);
      app.listen(PORT, () => console.log(`PlantPal API running on port ${PORT} (Standalone)`));
    });
}

export default app;
