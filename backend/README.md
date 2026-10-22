# PlantPal Backend Service

Node.js, Express, TypeScript, and MongoDB backend for PlantPal — AI-Powered Plant Care Mobile App.

## Features
- **JWT Authentication**: User registration, login, profile management.
- **Plant Management**: Full CRUD operations for registered indoor & outdoor plants.
- **Care Scheduling**: Synchronized watering and fertilizer schedules with logs.
- **Growth Monitoring**: Height, leaf counts, milestones, and photo history.
- **AI Diagnostics Proxy**: Google Gemini 3.5 Flash integration with botanical heuristic fallback.
- **Plant Diary**: Chronological event logs with tags.

## Installation & Setup
1. Enter backend directory:
   ```bash
   cd backend
   npm install
   ```

2. Configure environment variables in `.env`:
   ```env
   PORT=5000
   MONGODB_URI=mongodb://localhost:27017/plantpal
   JWT_SECRET=your_secure_random_jwt_secret
   GEMINI_API_KEY=your_gemini_api_key
   ```

3. Run development server:
   ```bash
   npm run dev
   ```

4. Build and start production bundle:
   ```bash
   npm run build
   npm start
   ```
