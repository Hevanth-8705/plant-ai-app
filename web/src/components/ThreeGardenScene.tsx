import React, { useEffect, useRef, useState } from 'react';
import * as THREE from 'three';

interface ThreeGardenSceneProps {
  potColor?: string;
  leafScale?: number;
  interactive?: boolean;
}

export const ThreeGardenScene: React.FC<ThreeGardenSceneProps> = ({
  potColor = '#C86D51',
  leafScale = 1.0,
  interactive = true
}) => {
  const mountRef = useRef<HTMLDivElement>(null);
  const [webGLFailed, setWebGLFailed] = useState(false);

  useEffect(() => {
    const container = mountRef.current;
    if (!container) return;

    let renderer: THREE.WebGLRenderer;
    try {
      renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    } catch (e) {
      setWebGLFailed(true);
      return;
    }

    const width = container.clientWidth || 400;
    const height = container.clientHeight || 400;
    renderer.setSize(width, height);
    renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
    renderer.shadowMap.enabled = true;
    container.appendChild(renderer.domElement);

    const scene = new THREE.Scene();
    const camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 100);
    camera.position.set(0, 2.5, 5.5);
    camera.lookAt(0, 0.8, 0);

    // Warm Botanical Lighting
    const ambientLight = new THREE.AmbientLight(0xfff8ee, 0.9);
    scene.add(ambientLight);

    const sunLight = new THREE.DirectionalLight(0xfffaed, 1.8);
    sunLight.position.set(4, 8, 4);
    sunLight.castShadow = true;
    scene.add(sunLight);

    const sageFillLight = new THREE.DirectionalLight(0x99bb99, 0.6);
    sageFillLight.position.set(-4, 2, -3);
    scene.add(sageFillLight);

    // Plant Group
    const plantGroup = new THREE.Group();
    scene.add(plantGroup);

    // 1. Ceramic Pot
    const potMat = new THREE.MeshStandardMaterial({
      color: new THREE.Color(potColor),
      roughness: 0.4,
      metalness: 0.05
    });

    const potGeo = new THREE.CylinderGeometry(0.85, 0.65, 1.4, 32);
    const pot = new THREE.Mesh(potGeo, potMat);
    pot.position.y = 0.7;
    pot.castShadow = true;
    pot.receiveShadow = true;
    plantGroup.add(pot);

    // Pot Rim
    const rimGeo = new THREE.TorusGeometry(0.86, 0.07, 16, 32);
    const rim = new THREE.Mesh(rimGeo, potMat);
    rim.rotation.x = Math.PI / 2;
    rim.position.y = 1.4;
    plantGroup.add(rim);

    // 2. Potting Soil
    const soilMat = new THREE.MeshStandardMaterial({ color: 0x24180d, roughness: 0.9 });
    const soilGeo = new THREE.CylinderGeometry(0.82, 0.82, 0.1, 32);
    const soil = new THREE.Mesh(soilGeo, soilMat);
    soil.position.y = 1.34;
    plantGroup.add(soil);

    // 3. Main Stems & Leaves
    const stemMat = new THREE.MeshStandardMaterial({ color: 0x2e6633, roughness: 0.5 });
    const leafMat = new THREE.MeshStandardMaterial({
      color: 0x3d8c47,
      roughness: 0.35,
      side: THREE.DoubleSide
    });
    const leafLightMat = new THREE.MeshStandardMaterial({
      color: 0x58b365,
      roughness: 0.3,
      side: THREE.DoubleSide
    });

    const stemCurves = [
      { x: 0, z: 0, height: 1.8, angleY: 0, leafTilt: 0.6, scale: 1.1 },
      { x: -0.3, z: 0.2, height: 1.5, angleY: 1.2, leafTilt: 0.8, scale: 0.9 },
      { x: 0.35, z: 0.15, height: 1.6, angleY: -1.4, leafTilt: 0.7, scale: 1.0 },
      { x: 0.1, z: -0.3, height: 1.4, angleY: 3.0, leafTilt: 0.85, scale: 0.85 },
      { x: -0.2, z: -0.2, height: 1.2, angleY: -2.6, leafTilt: 0.9, scale: 0.75 }
    ];

    const leafMeshes: THREE.Mesh[] = [];

    stemCurves.forEach((config, idx) => {
      const stemGroup = new THREE.Group();

      // Stem
      const stemGeo = new THREE.CylinderGeometry(0.04, 0.07, config.height, 12);
      const stem = new THREE.Mesh(stemGeo, stemMat);
      stem.position.y = config.height / 2;
      stem.rotation.z = 0.15 * Math.sin(idx);
      stemGroup.add(stem);

      // Heart-shaped botanical leaf
      const leafShape = new THREE.Shape();
      leafShape.moveTo(0, 0);
      leafShape.bezierCurveTo(0.4, 0.3, 0.8, 0.8, 0.4, 1.4);
      leafShape.bezierCurveTo(0.2, 1.7, 0, 1.9, 0, 2.1);
      leafShape.bezierCurveTo(0, 1.9, -0.2, 1.7, -0.4, 1.4);
      leafShape.bezierCurveTo(-0.8, 0.8, -0.4, 0.3, 0, 0);

      const leafGeo = new THREE.ShapeGeometry(leafShape);
      const mat = idx % 2 === 0 ? leafMat : leafLightMat;
      const leaf = new THREE.Mesh(leafGeo, mat);
      leaf.position.set(0, config.height, 0);
      leaf.rotation.x = config.leafTilt;
      leaf.rotation.z = 0.2 * Math.cos(idx);
      const s = config.scale * leafScale;
      leaf.scale.set(s, s, s);
      leaf.castShadow = true;
      leafMeshes.push(leaf);
      stemGroup.add(leaf);

      stemGroup.position.set(config.x, 1.35, config.z);
      stemGroup.rotation.y = config.angleY;
      plantGroup.add(stemGroup);
    });

    // Gentle Shadow Ground
    const shadowGeo = new THREE.PlaneGeometry(5, 5);
    const shadowMat = new THREE.ShadowMaterial({ opacity: 0.15 });
    const shadowPlane = new THREE.Mesh(shadowGeo, shadowMat);
    shadowPlane.rotation.x = -Math.PI / 2;
    shadowPlane.position.y = 0;
    shadowPlane.receiveShadow = true;
    scene.add(shadowPlane);

    // Mouse Parallax Interaction
    let targetRotationY = 0;
    let targetRotationX = 0;

    const onMouseMove = (e: MouseEvent) => {
      if (!interactive) return;
      const rect = container.getBoundingClientRect();
      const x = ((e.clientX - rect.left) / rect.width) * 2 - 1;
      const y = -(((e.clientY - rect.top) / rect.height) * 2 - 1);
      targetRotationY = x * 0.4;
      targetRotationX = y * 0.15;
    };

    container.addEventListener('mousemove', onMouseMove);

    // Animation Loop (Gentle Wind Breeze Sway)
    let animationFrameId: number;
    let clock = new THREE.Clock();

    const animate = () => {
      animationFrameId = requestAnimationFrame(animate);
      const elapsed = clock.getElapsedTime();

      // Smooth parallax dampening
      plantGroup.rotation.y += (targetRotationY - plantGroup.rotation.y) * 0.05;
      plantGroup.rotation.x += (targetRotationX - plantGroup.rotation.x) * 0.05;

      // Natural gentle breeze sway on foliage
      leafMeshes.forEach((leaf, i) => {
        leaf.rotation.z = Math.sin(elapsed * 1.5 + i * 0.8) * 0.04;
      });

      renderer.render(scene, camera);
    };

    animate();

    const handleResize = () => {
      const w = container.clientWidth || 400;
      const h = container.clientHeight || 400;
      camera.aspect = w / h;
      camera.updateProjectionMatrix();
      renderer.setSize(w, h);
    };

    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
      container.removeEventListener('mousemove', onMouseMove);
      cancelAnimationFrame(animationFrameId);
      if (renderer.domElement && container.contains(renderer.domElement)) {
        container.removeChild(renderer.domElement);
      }
      renderer.dispose();
    };
  }, [potColor, leafScale, interactive]);

  if (webGLFailed) {
    return (
      <div className="w-full h-full flex flex-col items-center justify-center bg-forest-50 rounded-2xl p-6 text-center">
        <div className="w-20 h-20 bg-forest-700 text-white rounded-full flex items-center justify-center text-3xl mb-3 shadow-lg">
          🌱
        </div>
        <p className="font-bold text-forest-900">PlantPal 3D Living Companion</p>
        <p className="text-xs text-gray-500 mt-1">Interactive 3D ready (WebGL fallback active)</p>
      </div>
    );
  }

  return <div ref={mountRef} className="w-full h-full min-h-[300px] cursor-grab active:cursor-grabbing" />;
};
