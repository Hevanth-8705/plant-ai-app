import React, { useState, useRef, useEffect } from 'react';
import { Camera, RefreshCw, Upload, Search, Activity, X } from 'lucide-react';

export const ScanScreen: React.FC = () => {
  const [stream, setStream] = useState<MediaStream | null>(null);
  const [capturedImage, setCapturedImage] = useState<string | null>(null);
  const [result, setResult] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const videoRef = useRef<HTMLVideoElement>(null);

  useEffect(() => {
    return () => stopCamera();
  }, []);

  const startCamera = async () => {
    try {
      const mediaStream = await navigator.mediaDevices.getUserMedia({ video: { facingMode: 'environment' } });
      setStream(mediaStream);
      if (videoRef.current) videoRef.current.srcObject = mediaStream;
    } catch (err) {
      setError('Camera access denied or unavailable.');
    }
  };

  const stopCamera = () => {
    stream?.getTracks().forEach(track => track.stop());
    setStream(null);
  };

  const captureImage = () => {
    if (videoRef.current) {
      const canvas = document.createElement('canvas');
      canvas.width = videoRef.current.videoWidth;
      canvas.height = videoRef.current.videoHeight;
      canvas.getContext('2d')?.drawImage(videoRef.current, 0, 0);
      setCapturedImage(canvas.toDataURL('image/jpeg', 0.8));
      stopCamera();
    }
  };

  const scanPlant = async (mode: 'identify' | 'health') => {
    if (!capturedImage) return;
    setLoading(true);
    setError(null);
    try {
      const token = localStorage.getItem('plantpal_token');
      const response = await fetch('http://localhost:5000/api/plants/scan', {
        method: 'POST',
        headers: { 
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ base64Image: capturedImage.split(',')[1], mode })
      });
      const data = await response.json();
      if (!data.success) throw new Error(data.message || 'Scan failed');
      setResult(data.result);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 max-w-2xl mx-auto">
      <h2 className="text-2xl font-bold text-forest-900 mb-6">Plant Scanner</h2>
      
      {!stream && !capturedImage && (
        <button onClick={startCamera} className="w-full py-4 bg-forest-700 text-white rounded-xl font-bold flex items-center justify-center gap-2">
          <Camera /> Open Camera
        </button>
      )}

      {stream && (
        <div className="relative">
          <video ref={videoRef} autoPlay playsInline className="w-full rounded-2xl bg-black" />
          <button onClick={captureImage} className="absolute bottom-4 left-1/2 -translate-x-1/2 p-4 bg-white rounded-full shadow-lg">
            <Camera className="text-forest-900" />
          </button>
        </div>
      )}

      {capturedImage && !result && (
        <div className="space-y-4">
          <img src={capturedImage} alt="Captured" className="w-full rounded-2xl" />
          <div className="flex gap-2">
            <button onClick={() => { setCapturedImage(null); startCamera(); }} className="flex-1 py-3 bg-sage-200 rounded-xl font-bold">Retake</button>
            <button onClick={() => scanPlant('identify')} disabled={loading} className="flex-1 py-3 bg-forest-700 text-white rounded-xl font-bold flex items-center justify-center gap-2">
              {loading ? 'Scanning...' : <><Search size={18} /> Identify</>}
            </button>
            <button onClick={() => scanPlant('health')} disabled={loading} className="flex-1 py-3 bg-forest-700 text-white rounded-xl font-bold flex items-center justify-center gap-2">
              {loading ? 'Analyzing...' : <><Activity size={18} /> Health Check</>}
            </button>
          </div>
        </div>
      )}

      {error && <p className="text-rose-700 mt-4 p-4 bg-rose-50 rounded-xl">{error}</p>}

      {result && (
        <div className="p-6 bg-white rounded-3xl shadow-sm border border-sage-200 mt-6 space-y-4">
            <h3 className="text-xl font-bold text-forest-900">{result.commonName}</h3>
            <pre className="text-xs text-sage-700 bg-sage-50 p-4 rounded-xl overflow-x-auto">{JSON.stringify(result, null, 2)}</pre>
            <button onClick={() => { setResult(null); setCapturedImage(null); }} className="w-full py-3 bg-sage-200 rounded-xl font-bold">Scan Another</button>
        </div>
      )}
    </div>
  );
};
